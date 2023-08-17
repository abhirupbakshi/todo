package com.example.todoserver.repository;

import com.example.todoserver.model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * A repository for {@link Todo} entity. It extends {@link JpaRepository}
 */
public interface TodoRepository extends JpaRepository<Todo, UUID> {

    /**
     * Gets a page of {@link Todo}s associated with a username.
     * @param username The username
     * @param pageable A {@link Pageable} object containing pagination information.
     * @return A {@link Page} of todos
     */
    @Query("SELECT t FROM Todo t WHERE t.user.username = :username")
    Page<Todo> findByUsername(@Param("username") String username, Pageable pageable);
}
