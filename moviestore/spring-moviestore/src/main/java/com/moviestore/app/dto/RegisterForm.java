package com.moviestore.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterForm {
  @NotBlank
  private String name;

  @NotBlank
  private String username;

  @Email
  @NotBlank
  private String email;

  @NotBlank
  private String phone;

  @NotBlank
  @Size(min = 7)
  private String password;
}
