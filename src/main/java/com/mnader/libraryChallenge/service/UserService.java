package com.mnader.libraryChallenge.service;

import com.mnader.libraryChallenge.controller.exceptions.BadRequestException;
import com.mnader.libraryChallenge.controller.exceptions.NotFoundException;
import com.mnader.libraryChallenge.model.User;
import com.mnader.libraryChallenge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.mnader.libraryChallenge.utils.Constants.USER_ALREADY_REGISTERED_MESSAGE;
import static com.mnader.libraryChallenge.utils.Constants.USER_NOT_FOUND_MESSAGE;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        User persistedUser = userRepository.findByLogin(user.getLogin());
        if (persistedUser != null) {
            throw new BadRequestException(USER_ALREADY_REGISTERED_MESSAGE);
        }
        return userRepository.saveAndFlush(user);
    }

    public void deleteUserByID(Long userID) {
        User persistedUser = userRepository.findById(userID).orElse(null);
        if (persistedUser == null) {
            throw new NotFoundException(USER_NOT_FOUND_MESSAGE);
        }
        userRepository.deleteById(userID);
    }

    public User findUserByID(Long userID) {
        return userRepository.findById(userID).orElse(null);
    }
}
