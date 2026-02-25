package com.moviestore.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class BookingForm {
  @NotNull
  private Long movieId;

  @NotNull
  private Long cinemaId;

  @NotNull
  private Long screenId;

  @NotNull
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate date;

  @NotNull
  @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
  private LocalTime time;

  @NotBlank
  private String seatLabels;
}
