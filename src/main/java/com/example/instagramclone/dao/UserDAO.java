package com.example.instagramclone.dao;

import com.example.instagramclone.models.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<LocalUser, Long> {
    Optional<LocalUser> findByUsername(String username);

    Optional<LocalUser> findByEmailIgnoreCase(String email);

}
