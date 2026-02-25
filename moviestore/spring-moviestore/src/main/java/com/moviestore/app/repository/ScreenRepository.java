package com.moviestore.app.repository;

import com.moviestore.app.model.Screen;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScreenRepository extends JpaRepository<Screen, Long> {
  @Override
  @EntityGraph(attributePaths = {"cinema"})
  List<Screen> findAll();

  @EntityGraph(attributePaths = {"cinema"})
  List<Screen> findByCinemaId(Long cinemaId);

  @EntityGraph(attributePaths = {"cinema"})
  List<Screen> findByCinemaOwnerId(Long ownerId);
}
