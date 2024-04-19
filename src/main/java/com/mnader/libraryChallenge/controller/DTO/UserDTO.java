package com.mnader.libraryChallenge.controller.DTO;

import com.mnader.libraryChallenge.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public record UserDTO(String login, String password) {

    public User convertToEntity() {
        var passwordEncoded = new BCryptPasswordEncoder().encode(this.password);
        return new User(null, this.login, passwordEncoded, null);
    }
}
