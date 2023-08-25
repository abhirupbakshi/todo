package com.example.todo.repository;

import com.example.todo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A repository for {@link Role} entity that extends {@link JpaRepository}
 */
public interface RoleRepository extends JpaRepository<Role, String> {
}
