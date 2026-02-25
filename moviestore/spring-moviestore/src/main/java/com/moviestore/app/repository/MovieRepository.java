package com.moviestore.app.repository;

import com.moviestore.app.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
  List<Movie> findByReleaseDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate start, LocalDate end);
}
