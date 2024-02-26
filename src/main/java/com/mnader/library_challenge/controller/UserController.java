//package com.mnader.library_challenge.controller;
//
//import com.mnader.library_challenge.controller.exceptions.BadRequestException;
//import com.mnader.library_challenge.controller.exceptions.NotFoundException;
//import com.mnader.library_challenge.controller.exceptions.OutOfRangeRatingException;
//import com.mnader.library_challenge.model.Book;
//import com.mnader.library_challenge.model.DTO.BookRequestDTO;
//import com.mnader.library_challenge.model.DTO.BookResponseDTO;
//import com.mnader.library_challenge.model.User;
//import com.mnader.library_challenge.service.BookService;
//import com.mnader.library_challenge.service.UserService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Controller
//@RequestMapping("/user")
//public class UserController {
//    UserService userService;
//    @Autowired
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @PostMapping
//    public ResponseEntity<String> createUser(
//        @Valid @RequestBody User user
//    ) throws BadRequestException {
//
//        userService.saveUser(user);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
//
//    @DeleteMapping("/{userID}")
//    public ResponseEntity<String> deleteUserByID(
//        @PathVariable Long userID
//    ) throws NotFoundException, BadRequestException {
//        userService.deleteUserByID(userID);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
//}
