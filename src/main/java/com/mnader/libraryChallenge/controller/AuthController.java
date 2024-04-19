package com.mnader.libraryChallenge.controller;

import com.mnader.libraryChallenge.controller.DTO.DataTokenJwtDTO;
import com.mnader.libraryChallenge.controller.DTO.RefreshTokenJwtDTO;
import com.mnader.libraryChallenge.controller.DTO.UserDTO;
import com.mnader.libraryChallenge.controller.exceptions.BadRequestException;
import com.mnader.libraryChallenge.model.User;
import com.mnader.libraryChallenge.security.TokenService;
import com.mnader.libraryChallenge.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<DataTokenJwtDTO> login(
        @Valid @RequestBody UserDTO user
    ) throws BadRequestException {
        var authToken = new UsernamePasswordAuthenticationToken(user.login(), user.password());
        var authentication = authenticationManager.authenticate(authToken);
        var tokenJwt= tokenService.generateToken((User) authentication.getPrincipal());
        var refreshToken = tokenService.generateOrUpdateRefreshToken((User) authentication.getPrincipal());

        return ResponseEntity.ok(new DataTokenJwtDTO(tokenJwt, refreshToken));
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<DataTokenJwtDTO> refreshToken(
        @RequestBody RefreshTokenJwtDTO refreshTokenJwtDTO
    ) throws BadRequestException {
        var subject = tokenService.validateRefreshToken(refreshTokenJwtDTO.refreshToken());
        var user = userService.findUserByID(Long.valueOf(subject));
        var tokenJwt= tokenService.generateToken(user);
        var newRefreshToken = tokenService.generateOrUpdateRefreshToken(user);
        return ResponseEntity.ok(new DataTokenJwtDTO(tokenJwt, newRefreshToken));
    }
}
