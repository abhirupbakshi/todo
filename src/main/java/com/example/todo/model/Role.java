package com.example.todo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Table(name = "auth_roles")
@NoArgsConstructor(force = true)
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Role {

    @Id
    @Column(name = "name")
    private String name;
}
