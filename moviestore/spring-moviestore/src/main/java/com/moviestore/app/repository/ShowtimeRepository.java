package com.moviestore.app.repository;

import com.moviestore.app.model.Showtime;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalTime;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
  @EntityGraph(attributePaths = {"cinema", "movie", "screen"})
  List<Showtime> findByMovieId(Long movieId);

  @EntityGraph(attributePaths = {"cinema", "movie", "screen"})
  List<Showtime> findByCinemaOwnerId(Long ownerId);

  @EntityGraph(attributePaths = {"cinema", "movie", "screen"})
  Optional<Showtime> findFirstByMovieIdAndCinemaIdAndScreenIdAndStartAtAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
      Long movieId, Long cinemaId, Long screenId, LocalTime startAt, LocalDate startDate, LocalDate endDate);
}
