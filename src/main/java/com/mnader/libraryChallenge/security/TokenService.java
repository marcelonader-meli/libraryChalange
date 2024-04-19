package com.mnader.libraryChallenge.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.mnader.libraryChallenge.model.AuthRefreshToken;
import com.mnader.libraryChallenge.model.User;
import com.mnader.libraryChallenge.repository.AuthRepository;
import com.mnader.libraryChallenge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Objects;

@Service
public class TokenService {

    private static final String ISSUER = "API Library Challenge";

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.refresh_secret}")
    private String refreshSecret;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private UserRepository userRepository;

    public String generateToken(User user) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
           return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.getLogin())
                .withClaim("id", user.getId())
                .withExpiresAt(LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00")))
                .sign(algorithm);
        } catch (JWTCreationException ex){
           throw new RuntimeException("Generate JWT Token Error", ex);
        }
    }

    public String generateOrUpdateRefreshToken(User user) {
        try {
            var algorithm = Algorithm.HMAC256(refreshSecret);
            var refreshToken = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.getId().toString())
                .withExpiresAt(LocalDateTime.now().plusDays(20).toInstant(ZoneOffset.of("-03:00")))
                .sign(algorithm);
            var splitToken = Arrays.stream(refreshToken.split("\\.")).toList();
            var userPersisted = userRepository.findById(user.getId()).orElseThrow();
            var authRefreshTokenPersisted = authRepository.findById(userPersisted.getId()).orElse(null);
            if (authRefreshTokenPersisted == null) {
                var authRefreshToken = AuthRefreshToken
                    .builder()
                    .refreshToken(splitToken.get(1))
                    .user(userPersisted)
                    .build();
                userPersisted.setRefreshToken(authRefreshToken);
                authRepository.saveAndFlush(authRefreshToken);
                userRepository.saveAndFlush(userPersisted);
            } else {
                authRefreshTokenPersisted.setRefreshToken(splitToken.get(1));
                authRepository.saveAndFlush(authRefreshTokenPersisted);
            }

            return refreshToken;
        } catch (JWTCreationException ex){
            throw new RuntimeException("Generate Refresh JWT Token Error", ex);
        }
    }

    public String getSubjectFromJwt(String tokenJwt) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build()
                .verify(tokenJwt)
                .getSubject();
        } catch (JWTVerificationException ex){
            throw new RuntimeException("Invalid or expired JWT Token", ex);
        }
    }

    public String validateRefreshToken(String refreshTokenJwt) {
        try {
            var algorithm = Algorithm.HMAC256(refreshSecret);
            var subject = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build()
                .verify(refreshTokenJwt)
                .getSubject();
            var splitToken = Arrays.stream(refreshTokenJwt.split("\\.")).toList();
            var userPersisted = userRepository.findById(Long.valueOf(subject)).orElseThrow();
            var authRefreshToken = authRepository.findByUser(userPersisted);
            if (Objects.equals(splitToken.get(1), authRefreshToken.getRefreshToken())){
                return subject;
            } else {
                throw new JWTVerificationException("Invalid or expired JWT Token");
            }
        } catch (JWTVerificationException ex){
            throw new RuntimeException("Invalid or expired JWT Token", ex);
        }
    }
}
