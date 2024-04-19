package com.mnader.libraryChallenge.repository;

import com.mnader.libraryChallenge.model.AuthRefreshToken;
import com.mnader.libraryChallenge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<AuthRefreshToken, Long> {
    AuthRefreshToken findByUser(User user);
}
