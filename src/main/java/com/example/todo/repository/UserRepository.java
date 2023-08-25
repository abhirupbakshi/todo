package com.example.todo.repository;

import com.example.todo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A repository for the {@link User} entity. It extends {@link JpaRepository}
 */
public interface UserRepository extends JpaRepository<User, String> {
}
