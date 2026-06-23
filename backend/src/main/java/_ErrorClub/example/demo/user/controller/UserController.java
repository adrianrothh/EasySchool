package _ErrorClub.example.demo.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CRUD de usuários. Acesso restrito a ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

}
