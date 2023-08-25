package com.example.todo;

import com.example.todo.model.Role;
import com.example.todo.repository.RoleRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TodoServer {

	public static void main(String[] args) {
		SpringApplication.run(TodoServer.class, args);
	}
}
