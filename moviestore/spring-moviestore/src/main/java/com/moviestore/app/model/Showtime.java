package com.moviestore.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
public class Showtime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(nullable = false)
  private LocalTime startAt;

  @NotNull
  @Column(nullable = false)
  private LocalDate startDate;

  @NotNull
  @Column(nullable = false)
  private LocalDate endDate;

  @NotNull
  @Column
  private Double ticketPrice;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private Movie movie;

  @ManyToOne(fetch = FetchType.EAGER)
  private Screen screen;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private Cinema cinema;
}
