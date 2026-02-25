package com.moviestore.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable = false)
  private String name;

  @NotBlank
  @Column(nullable = false, unique = true)
  private String username;

  @Email
  @NotBlank
  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(unique = true)
  private String phone;

  private String imageUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role = Role.GUEST;

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
