package com.moviestore.app.repository;

import com.moviestore.app.model.Reservation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
  @EntityGraph(attributePaths = {"movie", "cinema", "screen"})
  List<Reservation> findByScreenIdAndDateAndStartAt(Long screenId, LocalDate date, LocalTime startAt);

  @EntityGraph(attributePaths = {"movie", "cinema", "screen"})
  List<Reservation> findByUsername(String username);

  @Override
  @EntityGraph(attributePaths = {"movie", "cinema", "screen"})
  List<Reservation> findAll();

  @EntityGraph(attributePaths = {"movie", "cinema", "screen"})
  Optional<Reservation> findWithDetailsById(Long id);

  @EntityGraph(attributePaths = {"movie", "cinema", "screen"})
  List<Reservation> findByCinemaOwnerId(Long ownerId);
}
