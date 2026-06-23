package _ErrorClub.example.demo.infra.config;

import _ErrorClub.example.demo.user.entity.Perfil;
import _ErrorClub.example.demo.user.entity.User;
import _ErrorClub.example.demo.user.repository.PerfilRepository;
import _ErrorClub.example.demo.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PerfilRepository perfilRepository, PasswordEncoder encoder) {
        return args -> {

            Perfil perfilAluno = perfilRepository.findByNome("ALUNO")
                    .orElseGet(() -> {
                        Perfil p = new Perfil();
                        p.setNome("ALUNO");
                        return perfilRepository.save(p);
                    });

            Perfil perfilProfessor = perfilRepository.findByNome("PROFESSOR")
                    .orElseGet(() -> {
                        Perfil p = new Perfil();
                        p.setNome("PROFESSOR");
                        return perfilRepository.save(p);
                    });

            Perfil perfilAdmin = perfilRepository.findByNome("ADMIN")
                    .orElseGet(() -> {
                        Perfil p = new Perfil();
                        p.setNome("ADMIN");
                        return perfilRepository.save(p);
                    });

            String senhaHash = encoder.encode("123456");

            if (userRepository.findByEmail("aluno@easyschool.com").isEmpty()) {
                User aluno = new User();
                aluno.setName("Aluno de Teste");
                aluno.setEmail("aluno@easyschool.com");
                aluno.setCpf("11122233344");
                aluno.setPassword(senhaHash);
                aluno.setPerfil(perfilAluno);
                aluno.setActivate(true);
                aluno.setCreatedAt(java.time.OffsetDateTime.now());
                userRepository.save(aluno);
            }

            if (userRepository.findByEmail("professor@easyschool.com").isEmpty()) {
                User professor = new User();
                professor.setName("Professor de Teste");
                professor.setEmail("professor@easyschool.com");
                professor.setCpf("55566677788");
                professor.setPassword(senhaHash);
                professor.setPerfil(perfilProfessor);
                professor.setActivate(true);
                professor.setCreatedAt(java.time.OffsetDateTime.now());
                userRepository.save(professor);
            }

            if (userRepository.findByEmail("admin@easyschool.com").isEmpty()) {
                User admin = new User();
                admin.setName("Admin de Teste");
                admin.setEmail("admin@easyschool.com");
                admin.setCpf("99988877766");
                admin.setPassword(senhaHash);
                admin.setPerfil(perfilAdmin);
                admin.setActivate(true);
                admin.setCreatedAt(java.time.OffsetDateTime.now());
                userRepository.save(admin);
            }

            System.out.println("Perfis e Usuarios de teste criados com sucesso!");
        };
    }
}
