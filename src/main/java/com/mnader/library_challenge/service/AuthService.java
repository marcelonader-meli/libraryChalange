//package com.mnader.library_challenge.service;
//
//import com.mnader.library_challenge.controller.exceptions.BadRequestException;
//import com.mnader.library_challenge.model.User;
//import com.mnader.library_challenge.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import static com.mnader.library_challenge.utils.Constants.USER_ALREADY_REGISTERED_MESSAGE;
//
//@Service
//public class AuthService implements UserDetailsService {
//
//    private UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return userRepository.findByLogin(username);
//    }
//}
