package com.example.todoserver.repository;

import com.example.todoserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A repository for the {@link User} entity. It extends {@link JpaRepository}
 */
public interface UserRepository extends JpaRepository<User, String> {
}
