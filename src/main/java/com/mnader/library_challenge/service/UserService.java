//package com.mnader.library_challenge.service;
//
//import com.mnader.library_challenge.controller.exceptions.BadRequestException;
//import com.mnader.library_challenge.model.User;
//import com.mnader.library_challenge.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import static com.mnader.library_challenge.utils.Constants.*;
//
//@Service
//public class UserService {
//
//    UserRepository userRepository;
//
//    @Autowired
//    public UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    public void saveUser(User user) {
//        User persistedUser = userRepository.findById(user.getId()).orElse(null);
//        if (persistedUser != null) {
//            throw new BadRequestException(USER_ALREADY_REGISTERED_MESSAGE);
//        }
//        userRepository.saveAndFlush(user);
//    }
//
//    public void deleteUserByID(Long userID) {
//        userRepository.deleteById(userID);
//    }
//}
