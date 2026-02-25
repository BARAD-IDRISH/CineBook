package com.moviestore.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
public class Reservation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(nullable = false)
  private LocalDate date;

  @NotNull
  @Column(nullable = false)
  private LocalTime startAt;

  @NotBlank
  @Column(nullable = false)
  private String seatLabels;

  @NotNull
  @Column(nullable = false)
  private Double ticketPrice;

  @NotNull
  @Column(nullable = false)
  private Double total;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Movie movie;

  @ManyToOne(fetch = FetchType.LAZY)
  private Screen screen;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Cinema cinema;

  @NotBlank
  @Column(nullable = false)
  private String username;

  @NotBlank
  @Column(nullable = false)
  private String phone;

  @Column(nullable = false)
  private boolean checkin = false;

  @Column(nullable = false)
  private boolean paid = false;

  private String qrCodePath;
}
