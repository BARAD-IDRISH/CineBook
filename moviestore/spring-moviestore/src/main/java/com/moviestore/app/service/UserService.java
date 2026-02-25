package com.moviestore.app.service;

import com.moviestore.app.dto.RegisterForm;
import com.moviestore.app.model.Role;
import com.moviestore.app.model.User;
import com.moviestore.app.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User register(RegisterForm form) {
    if (userRepository.existsByUsername(form.getUsername())) {
      throw new IllegalArgumentException("Username already exists");
    }
    if (userRepository.existsByEmail(form.getEmail())) {
      throw new IllegalArgumentException("Email already exists");
    }

    User user = new User();
    user.setName(form.getName());
    user.setUsername(form.getUsername().toLowerCase());
    user.setEmail(form.getEmail().toLowerCase());
    user.setPhone(form.getPhone());
    user.setPassword(passwordEncoder.encode(form.getPassword()));
    user.setRole(Role.GUEST);
    return userRepository.save(user);
  }

  public User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getName() == null || auth.getName().equals("anonymousUser")) {
      return null;
    }
    return userRepository.findByUsername(auth.getName()).orElse(null);
  }
}
