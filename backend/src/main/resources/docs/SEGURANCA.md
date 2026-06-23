# Documentação de Segurança — EasySchool

## Visão Geral

A autenticação do EasySchool utiliza **JWT (JSON Web Token)** com **OAuth2 Resource Server stateless**. Toda requisição (exceto login e refresh) deve incluir um token válido no header `Authorization`. As senhas são armazenadas com hash BCrypt no banco de dados.

### Fluxo de Autenticação

```
1. Cliente envia POST /api/auth/login com email e senha
2. Servidor valida credenciais contra o banco (BCrypt)
3. Servidor retorna accessToken (JWT 15min) + refreshToken (UUID, 7 dias)
4. Cliente envia o accessToken em todas as requisições: Authorization: Bearer <token>
5. Quando o accessToken expira, cliente chama POST /api/auth/refresh com o refreshToken
6. Servidor revoga o refreshToken antigo e retorna um novo par de tokens
```

---

## 1. Dependências (pom.xml)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

- `spring-boot-starter-security`: Habilita o Spring Security no projeto (filtros, autenticação, autorização).
- `jjwt-api / jjwt-impl / jjwt-jackson`: Biblioteca para criar e validar tokens JWT assinados com HMAC-SHA256.

---

## 2. Propriedades (application.properties)

```properties
jwt.secret=EasySchool-jwt-secret-key-troque-em-producao-pelo-menos-256-bits!
jwt.access-token-expiration=900000
```

| Propriedade | Descrição |
|---|---|
| `jwt.secret` | Chave secreta usada para assinar e verificar os tokens JWT. Deve ter no mínimo 32 bytes (256 bits) para o algoritmo HS256. **Trocar em produção.** |
| `jwt.access-token-expiration` | Tempo de vida do access token em milissegundos. 900000ms = 15 minutos. |

---

## 3. Configuração do Spring Security (SecurityConfig.java)

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### O que cada parte faz:

- **`@EnableMethodSecurity`**: Habilita o uso de `@PreAuthorize` nos controllers para controle de acesso por role.
- **`passwordEncoder()`**: Define o BCrypt com strength 12 como algoritmo de hash para senhas. Quanto maior o strength, mais lento (e seguro) o hash.
- **`authenticationManager()`**: Expõe o AuthenticationManager do Spring para ser injetado onde necessário.
- **`csrf.disable()`**: Desabilita proteção CSRF. APIs stateless com JWT não precisam de CSRF pois não usam cookies de sessão.
- **`SessionCreationPolicy.STATELESS`**: Nenhuma sessão HTTP é criada. Toda autenticação depende exclusivamente do token JWT enviado em cada requisição.
- **`requestMatchers("/api/auth/**").permitAll()`**: Os endpoints de login e refresh são públicos (não exigem token).
- **`anyRequest().authenticated()`**: Todos os demais endpoints exigem um token JWT válido.
- **`addFilterBefore(jwtAuthenticationFilter, ...)`**: Registra o filtro JWT antes do filtro padrão do Spring. Assim, o token é validado antes de qualquer outra verificação.

---

## 4. Filtro JWT (JwtAuthenticationFilter.java)

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (jwtService.isTokenValid(token)) {
            String email = jwtService.getEmailFromToken(token);
            String role = jwtService.getRoleFromToken(token);

            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
            var authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
```

### O que cada parte faz:

- **`OncePerRequestFilter`**: Garante que o filtro executa apenas uma vez por requisição (mesmo com forwards/redirects internos).
- **Extração do header**: Busca o header `Authorization` e verifica se começa com `Bearer `. Se não existir, a requisição segue sem autenticação (será barrada pelo Spring Security se o endpoint exigir).
- **`token = authHeader.substring(7)`**: Remove o prefixo `Bearer ` para obter apenas o token JWT.
- **Validação**: Verifica assinatura e expiração do token via `JwtService`.
- **`SimpleGrantedAuthority("ROLE_" + role)`**: Converte a role do token (ex: `ADMIN`) para o formato do Spring Security (`ROLE_ADMIN`).
- **`SecurityContextHolder.getContext().setAuthentication(...)`**: Registra o usuário autenticado no contexto da requisição. A partir daqui, o Spring Security reconhece o usuário e suas roles.

---

## 5. Serviço JWT (JwtService.java)

```java
@Service
public class JwtService {

    private final SecretKey key;
    private final long accessTokenExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public String generateAccessToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return parseToken(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return parseToken(token).get("role", String.class);
    }
}
```

### O que cada parte faz:

- **Construtor**: Converte a string secreta do `application.properties` em uma `SecretKey` HMAC-SHA256 e carrega o tempo de expiração.
- **`generateAccessToken()`**: Cria um JWT contendo:
  - `subject`: email do usuário (identifica quem é).
  - `claim("role", ...)`: a role do usuário (ADMIN, ALUNO, PROFESSOR).
  - `issuedAt`: data de criação.
  - `expiration`: data de expiração (agora + 15 minutos).
  - `signWith(key)`: assinatura HMAC-SHA256 para garantir integridade.
- **`parseToken()`**: Decodifica e valida o JWT. Se a assinatura estiver errada ou o token estiver expirado, lança exceção.
- **`isTokenValid()`**: Retorna `true` se o token é válido, `false` caso contrário (sem lançar exceção).
- **`getEmailFromToken()` / `getRoleFromToken()`**: Extraem dados específicos dos claims do token.

### Estrutura do JWT gerado:

```json
{
  "sub": "admin@easyschool.com",
  "role": "ADMIN",
  "iat": 1719100000,
  "exp": 1719100900
}
```

---

## 6. Serviço de Autenticação (AuthService.java)

```java
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public record TokenPair(String accessToken, String refreshToken) {}

    public TokenPair login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));

        if (!user.isActivate()) {
            throw new RuntimeException("Usuário desativado");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        return generateTokens(user);
    }

    public TokenPair refresh(String rawRefreshToken) {
        String hash = hashToken(rawRefreshToken);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new RuntimeException("Refresh token inválido"));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("Refresh token expirado ou revogado");
        }

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        User user = userRepository.findById(stored.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return generateTokens(user);
    }

    private TokenPair generateTokens(User user) {
        String role = user.getPerfil().getNome();
        String accessToken = jwtService.generateAccessToken(user.getEmail(), role);

        String rawRefreshToken = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuarioId(user.getId());
        refreshToken.setTokenHash(hashToken(rawRefreshToken));
        refreshToken.setExpiresAt(OffsetDateTime.now().plusDays(7));
        refreshToken.setCreatedAt(OffsetDateTime.now());
        refreshTokenRepository.save(refreshToken);

        return new TokenPair(accessToken, rawRefreshToken);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
```

### O que cada parte faz:

- **`login()`**:
  1. Busca o usuário pelo email no banco.
  2. Verifica se o usuário está ativo.
  3. Compara a senha informada com o hash BCrypt armazenado no banco via `passwordEncoder.matches()`.
  4. Se tudo estiver correto, gera o par de tokens.

- **`refresh()`**:
  1. Recebe o refresh token bruto enviado pelo cliente.
  2. Calcula o hash SHA-256 e busca no banco.
  3. Verifica se não foi revogado e se não expirou.
  4. Revoga o refresh token antigo (impede reutilização).
  5. Gera um novo par de tokens (rotação).

- **`generateTokens()`**:
  1. Gera o access token JWT com email e role via `JwtService`.
  2. Gera um UUID aleatório como refresh token.
  3. Armazena apenas o hash SHA-256 do refresh token no banco (nunca o token bruto).
  4. Retorna ambos os tokens para o cliente.

- **`hashToken()`**: Calcula o hash SHA-256 do refresh token. O token bruto é enviado ao cliente; apenas o hash é persistido no banco. Assim, mesmo que o banco seja comprometido, os refresh tokens não podem ser usados diretamente.

---

## 7. Serviço de Senha (PasswordService.java)

```java
@Component
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
```

### O que cada parte faz:

- **`hash()`**: Recebe a senha em texto plano e retorna o hash BCrypt. Usado ao criar ou alterar senhas de usuários.
- **`matches()`**: Compara uma senha em texto plano com um hash BCrypt. Retorna `true` se correspondem.
- O BCrypt inclui um salt aleatório automaticamente em cada hash, então dois hashes da mesma senha são diferentes. A verificação é feita internamente pelo algoritmo.

---

## 8. UserDetailsService (UserDetailsServiceImpl.java)

```java
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        String role = user.getPerfil().getNome();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isActivate(),
                true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
```

### O que cada parte faz:

- Implementa a interface `UserDetailsService` do Spring Security.
- **`loadUserByUsername()`**: Carrega o usuário do banco pelo email e monta o objeto `UserDetails` com:
  - Email como username.
  - Hash da senha.
  - Flag `enabled` baseada no campo `activate` do usuário.
  - A role do perfil com prefixo `ROLE_` (padrão do Spring Security).

---

## 9. Controller de Autenticação (AuthController.java)

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    record LoginRequest(String email, String password) {}
    record RefreshRequest(String refreshToken) {}
    record TokenResponse(String accessToken, String refreshToken) {}

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        AuthService.TokenPair tokens = authService.login(request.email(), request.password());
        return ResponseEntity.ok(new TokenResponse(tokens.accessToken(), tokens.refreshToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest request) {
        AuthService.TokenPair tokens = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(new TokenResponse(tokens.accessToken(), tokens.refreshToken()));
    }
}
```

### Endpoints:

#### POST /api/auth/login

Autentica o usuário e retorna os tokens.

**Request:**
```json
{
  "email": "admin@easyschool.com",
  "password": "123456"
}
```

**Response (200):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOi...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### POST /api/auth/refresh

Rotaciona o refresh token e retorna um novo par.

**Request:**
```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response (200):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOi...",
  "refreshToken": "7c9e6679-7425-40de-944b-e07fc1f90ae7"
}
```

---

## 10. Entidade User (User.java)

```java
@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    private String name;
    private String email;
    private String cpf;
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    private Perfil perfil;

    private boolean activate = true;
    private short loginAttempt = 0;
    private OffsetDateTime blockedTo;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
```

### O que mudou:

- O campo `UUID profileId` foi substituído por um relacionamento `@ManyToOne` com a entidade `Perfil`. Isso permite acessar diretamente `user.getPerfil().getNome()` para obter a role sem precisar de uma query extra.
- `FetchType.EAGER`: O perfil é carregado junto com o usuário automaticamente, já que é sempre necessário para autenticação.

---

## 11. Roles e Controle de Acesso

### Roles disponíveis:

| Role | Nome no Spring Security | Descrição |
|---|---|---|
| ADMIN | `ROLE_ADMIN` | Acesso total ao sistema |
| ALUNO | `ROLE_ALUNO` | Cria e consulta solicitações |
| PROFESSOR | `ROLE_PROFESSOR` | Responde solicitações dos alunos |

### Como usar nos controllers:

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin-only")
public ResponseEntity<?> adminEndpoint() { ... }

@PreAuthorize("hasRole('PROFESSOR')")
@PostMapping("/responder")
public ResponseEntity<?> responderSolicitacao() { ... }

@PreAuthorize("hasAnyRole('ALUNO', 'PROFESSOR', 'ADMIN')")
@GetMapping("/comum")
public ResponseEntity<?> endpointComum() { ... }
```

---

## 12. Usuários de Teste (DataInitializer.java)

Criados automaticamente na inicialização da aplicação:

| Email | Senha | Role |
|---|---|---|
| aluno@easyschool.com | 123456 | ALUNO |
| professor@easyschool.com | 123456 | PROFESSOR |
| admin@easyschool.com | 123456 | ADMIN |

As senhas são armazenadas com hash BCrypt (strength 12). O texto plano nunca é persistido no banco.

---

## 13. Como usar em requisições

### 1. Fazer login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@easyschool.com","password":"123456"}'
```

### 2. Usar o token em requisições protegidas:
```bash
curl http://localhost:8080/api/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### 3. Renovar o token quando expirar:
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"550e8400-e29b-41d4-a716-446655440000"}'
```
