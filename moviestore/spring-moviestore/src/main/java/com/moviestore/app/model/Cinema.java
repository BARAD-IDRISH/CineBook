package com.moviestore.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Cinema {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable = false)
  private String name;

  @NotBlank
  @Column(nullable = false)
  private String city;

  // Legacy field kept for DB compatibility; pricing is now per showtime.
  @Column
  private Double ticketPrice;

  // Legacy fields kept for DB compatibility; seating is now per screen.
  @Column
  private Integer rowsCount;

  @Column
  private Integer colsCount;

  private String image;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private User owner;
}
