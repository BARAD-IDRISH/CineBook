package com.moviestore.app.controller;

import com.moviestore.app.dto.RegisterForm;
import com.moviestore.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/login")
  public String loginPage() {
    return "public/login";
  }

  @GetMapping("/register")
  public String registerPage(Model model) {
    model.addAttribute("form", new RegisterForm());
    return "public/register";
  }

  @PostMapping("/register")
  public String register(@Valid @ModelAttribute("form") RegisterForm form,
                         BindingResult bindingResult,
                         Model model) {
    if (bindingResult.hasErrors()) {
      return "public/register";
    }
    try {
      userService.register(form);
      return "redirect:/login?registered";
    } catch (IllegalArgumentException ex) {
      model.addAttribute("error", ex.getMessage());
      return "public/register";
    }
  }
}
