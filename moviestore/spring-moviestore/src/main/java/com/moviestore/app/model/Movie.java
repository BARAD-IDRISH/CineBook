package com.moviestore.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Movie {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable = false)
  private String title;

  private String image;

  @NotBlank
  @Column(nullable = false)
  private String language;

  @NotBlank
  @Column(nullable = false)
  private String genre;

  @NotBlank
  @Column(nullable = false)
  private String director;

  @NotBlank
  @Column(name = "cast_name", nullable = false, length = 500)
  private String cast;

  @Lob
  private String description;

  @NotNull
  @Column(nullable = false)
  private Integer duration;

  @NotNull
  @Column(nullable = false)
  private LocalDate releaseDate;

  @NotNull
  @Column(nullable = false)
  private LocalDate endDate;
}
