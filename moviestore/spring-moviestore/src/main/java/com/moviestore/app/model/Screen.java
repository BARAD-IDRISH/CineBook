package com.moviestore.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Screen {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable = false)
  private String name;

  @NotNull
  @Column(nullable = false)
  private Integer rowsCount;

  @NotNull
  @Column(nullable = false)
  private Integer colsCount;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Cinema cinema;
}
