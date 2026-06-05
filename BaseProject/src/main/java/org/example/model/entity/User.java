package org.example.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id ;

    private String username;
    private String password;
    private String email;
    private String phone ;

    @ManyToOne
    public Role role;
    private LocalDate createdAt ;
}
