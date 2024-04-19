package com.mnader.libraryChallenge.controller;

import com.mnader.libraryChallenge.controller.DTO.UserDTO;
import com.mnader.libraryChallenge.controller.exceptions.BadRequestException;
import com.mnader.libraryChallenge.controller.exceptions.NotFoundException;
import com.mnader.libraryChallenge.model.User;
import com.mnader.libraryChallenge.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(
        @Valid @RequestBody UserDTO user
    ) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(user.convertToEntity()));
    }

    @DeleteMapping("/{userID}")
    public ResponseEntity<String> deleteUserByID(
        @PathVariable Long userID
    ) throws NotFoundException, BadRequestException {
        userService.deleteUserByID(userID);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
