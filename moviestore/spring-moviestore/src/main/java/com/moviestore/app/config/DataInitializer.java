package com.moviestore.app.config;

import com.moviestore.app.model.*;
import com.moviestore.app.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class DataInitializer {

  @Bean
  CommandLineRunner seedData(UserRepository userRepository,
                             MovieRepository movieRepository,
                             CinemaRepository cinemaRepository,
                             ScreenRepository screenRepository,
                             ShowtimeRepository showtimeRepository,
                             PasswordEncoder passwordEncoder) {
    return args -> {
      User defaultOwner = null;
      if (userRepository.count() == 0) {
        User superadmin = new User();
        superadmin.setName("Super Admin");
        superadmin.setUsername("superadmin");
        superadmin.setEmail("superadmin@moviestore.local");
        superadmin.setPhone("+15550000001");
        superadmin.setPassword(passwordEncoder.encode("password123"));
        superadmin.setRole(Role.SUPERADMIN);
        userRepository.save(superadmin);

        User admin = new User();
        admin.setName("Cinema Admin");
        admin.setUsername("admin");
        admin.setEmail("admin@moviestore.local");
        admin.setPhone("+15550000002");
        admin.setPassword(passwordEncoder.encode("password123"));
        admin.setRole(Role.ADMIN);
        admin = userRepository.save(admin);
        defaultOwner = admin;

        User guest = new User();
        guest.setName("Guest User");
        guest.setUsername("guest");
        guest.setEmail("guest@moviestore.local");
        guest.setPhone("+15550000003");
        guest.setPassword(passwordEncoder.encode("password123"));
        guest.setRole(Role.GUEST);
        userRepository.save(guest);
      }

      if (defaultOwner == null) {
        defaultOwner = userRepository.findByUsername("admin").orElse(null);
      }

      if (movieRepository.count() == 0) {
        Movie m1 = new Movie();
        m1.setTitle("The Hidden City");
        m1.setLanguage("english");
        m1.setGenre("thriller");
        m1.setDirector("A. Rivera");
        m1.setCast("M. Stone, K. Ali");
        m1.setDescription("A city-wide mystery unfolds over one night.");
        m1.setDuration(124);
        m1.setReleaseDate(LocalDate.now().minusDays(5));
        m1.setEndDate(LocalDate.now().plusDays(25));
        movieRepository.save(m1);

        Movie m2 = new Movie();
        m2.setTitle("Orbit Dawn");
        m2.setLanguage("english");
        m2.setGenre("science fiction");
        m2.setDirector("C. Park");
        m2.setCast("D. Park, T. King");
        m2.setDescription("A rescue mission to a failing orbital colony.");
        m2.setDuration(137);
        m2.setReleaseDate(LocalDate.now().plusDays(6));
        m2.setEndDate(LocalDate.now().plusDays(45));
        movieRepository.save(m2);
      }

      if (cinemaRepository.count() == 0) {
        Cinema c1 = new Cinema();
        c1.setName("Downtown Multiplex");
        c1.setCity("new york");
        c1.setTicketPrice(0.0);
        c1.setRowsCount(0);
        c1.setColsCount(0);
        c1.setOwner(defaultOwner);
        cinemaRepository.save(c1);

        Cinema c2 = new Cinema();
        c2.setName("Lakeside Cinema");
        c2.setCity("chicago");
        c2.setTicketPrice(0.0);
        c2.setRowsCount(0);
        c2.setColsCount(0);
        c2.setOwner(defaultOwner);
        cinemaRepository.save(c2);
      }

      if (screenRepository.count() == 0 && cinemaRepository.count() > 0) {
        Cinema c1 = cinemaRepository.findAll().get(0);
        Screen s1 = new Screen();
        s1.setName("Screen 1");
        s1.setRowsCount(8);
        s1.setColsCount(10);
        s1.setCinema(c1);
        screenRepository.save(s1);

        Screen s2 = new Screen();
        s2.setName("Screen 2");
        s2.setRowsCount(7);
        s2.setColsCount(9);
        s2.setCinema(c1);
        screenRepository.save(s2);
      }

      if (showtimeRepository.count() == 0) {
        Movie movie = movieRepository.findAll().get(0);
        Cinema cinema = cinemaRepository.findAll().get(0);
        Screen screen = screenRepository.findByCinemaId(cinema.getId()).get(0);

        Showtime s1 = new Showtime();
        s1.setMovie(movie);
        s1.setCinema(cinema);
        s1.setScreen(screen);
        s1.setStartAt(LocalTime.of(14, 0));
        s1.setStartDate(LocalDate.now());
        s1.setEndDate(LocalDate.now().plusDays(10));
        s1.setTicketPrice(12.5);
        showtimeRepository.save(s1);

        Showtime s2 = new Showtime();
        s2.setMovie(movie);
        s2.setCinema(cinema);
        s2.setScreen(screen);
        s2.setStartAt(LocalTime.of(19, 30));
        s2.setStartDate(LocalDate.now());
        s2.setEndDate(LocalDate.now().plusDays(10));
        s2.setTicketPrice(14.0);
        showtimeRepository.save(s2);
      }
    };
  }
}


