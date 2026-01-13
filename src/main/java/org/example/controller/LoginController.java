package org.example.controller;

import org.example.model.User;
import org.example.repository.UserRepository;

import java.util.Optional;

public class LoginController {

    private UserRepository userRepository;

    public LoginController() {
        // initializam legatura cu bd
        this.userRepository = new UserRepository();
    }

 //verifica daca userul si parola exista in bd
    public User login(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }

        // cautam in baza de date
        Optional<User> userOpt = userRepository.findByUsernameAndPassword(username, password);

        // dacă l-am gasit il returnăm dacă nu, returnam null.
        if (userOpt.isPresent()) {
            return userOpt.get();
        } else {
            return null;
        }
    }
}