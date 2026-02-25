package com.moviestore.app.repository;

import com.moviestore.app.model.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {
  List<Cinema> findByOwnerId(Long ownerId);
}
