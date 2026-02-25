package com.moviestore.app.config;

import com.moviestore.app.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/css/**", "/js/**", "/images/**", "/uploads/**",
                "/", "/login", "/register", "/movies/**", "/movie/**", "/checkin/**", "/book/**"
            ).permitAll()
            .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
            .requestMatchers(HttpMethod.POST, "/book").authenticated()
            .requestMatchers("/mydashboard", "/reservations/**", "/profile/**", "/payment/**").authenticated()
            .anyRequest().permitAll())
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/", false)
            .usernameParameter("username")
            .passwordParameter("password")
            .permitAll())
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .permitAll());

    http.csrf(csrf -> csrf.disable());
    return http.build();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService customUserDetailsService,
                                                          PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(customUserDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
