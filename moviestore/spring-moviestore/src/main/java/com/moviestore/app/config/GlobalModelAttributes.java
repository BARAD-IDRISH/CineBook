package com.moviestore.app.config;

import com.moviestore.app.model.User;
import com.moviestore.app.service.UserService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {
  private final UserService userService;

  public GlobalModelAttributes(UserService userService) {
    this.userService = userService;
  }

  @ModelAttribute("currentUser")
  public User currentUser() {
    return userService.getCurrentUser();
  }

  @ModelAttribute("isAdmin")
  public boolean isAdmin() {
    User user = userService.getCurrentUser();
    if (user == null) return false;
    return user.getRole().name().equals("ADMIN") || user.getRole().name().equals("SUPERADMIN");
  }

  @ModelAttribute("isSuperadmin")
  public boolean isSuperadmin() {
    User user = userService.getCurrentUser();
    if (user == null) return false;
    return user.getRole().name().equals("SUPERADMIN");
  }
}
