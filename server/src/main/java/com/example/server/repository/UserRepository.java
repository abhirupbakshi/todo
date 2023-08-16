package com.example.server.repository;

import com.example.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A repository for the {@link User} entity. It extends {@link JpaRepository}
 */
public interface UserRepository extends JpaRepository<User, String> {
}
