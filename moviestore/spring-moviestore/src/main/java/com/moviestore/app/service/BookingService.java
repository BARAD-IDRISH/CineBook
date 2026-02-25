package com.moviestore.app.service;

import com.moviestore.app.dto.BookingForm;
import com.moviestore.app.model.Cinema;
import com.moviestore.app.model.Movie;
import com.moviestore.app.model.Reservation;
import com.moviestore.app.model.Screen;
import com.moviestore.app.model.Showtime;
import com.moviestore.app.model.User;
import com.moviestore.app.repository.CinemaRepository;
import com.moviestore.app.repository.MovieRepository;
import com.moviestore.app.repository.ReservationRepository;
import com.moviestore.app.repository.ScreenRepository;
import com.moviestore.app.repository.ShowtimeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
  private final ReservationRepository reservationRepository;
  private final MovieRepository movieRepository;
  private final CinemaRepository cinemaRepository;
  private final ScreenRepository screenRepository;
  private final ShowtimeRepository showtimeRepository;
  private final QrCodeService qrCodeService;

  public BookingService(ReservationRepository reservationRepository,
                        MovieRepository movieRepository,
                        CinemaRepository cinemaRepository,
                        ScreenRepository screenRepository,
                        ShowtimeRepository showtimeRepository,
                        QrCodeService qrCodeService) {
    this.reservationRepository = reservationRepository;
    this.movieRepository = movieRepository;
    this.cinemaRepository = cinemaRepository;
    this.screenRepository = screenRepository;
    this.showtimeRepository = showtimeRepository;
    this.qrCodeService = qrCodeService;
  }

  public List<String> getTakenSeats(Long screenId, LocalDate date, LocalTime time) {
    return reservationRepository.findByScreenIdAndDateAndStartAt(screenId, date, time)
        .stream()
        .flatMap(r -> Arrays.stream(r.getSeatLabels().split(",")))
        .map(String::trim)
        .filter(s -> !s.isBlank())
        .distinct()
        .collect(Collectors.toList());
  }

  public Reservation createReservation(BookingForm form, User user) {
    Movie movie = movieRepository.findById(form.getMovieId())
        .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
    Cinema cinema = cinemaRepository.findById(form.getCinemaId())
        .orElseThrow(() -> new IllegalArgumentException("Cinema not found"));
    Screen screen = screenRepository.findById(form.getScreenId())
        .orElseThrow(() -> new IllegalArgumentException("Screen not found"));
    if (!screen.getCinema().getId().equals(cinema.getId())) {
      throw new IllegalArgumentException("Screen does not belong to selected cinema");
    }
    Showtime showtime = showtimeRepository
        .findFirstByMovieIdAndCinemaIdAndScreenIdAndStartAtAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            form.getMovieId(), form.getCinemaId(), form.getScreenId(), form.getTime(), form.getDate(), form.getDate())
        .orElseThrow(() -> new IllegalArgumentException("Showtime not found for selected date/time"));
    if (showtime.getTicketPrice() == null) {
      throw new IllegalStateException("Ticket price not set for selected showtime");
    }

    List<String> requestedSeats = Arrays.stream(form.getSeatLabels().split(","))
        .map(String::trim)
        .filter(s -> !s.isBlank())
        .distinct()
        .toList();

    if (requestedSeats.isEmpty()) {
      throw new IllegalArgumentException("Select at least one seat");
    }

    List<String> taken = getTakenSeats(screen.getId(), form.getDate(), form.getTime());
    boolean conflict = requestedSeats.stream().anyMatch(taken::contains);
    if (conflict) {
      throw new IllegalStateException("One or more selected seats are already reserved");
    }

    Reservation reservation = new Reservation();
    reservation.setMovie(movie);
    reservation.setCinema(cinema);
    reservation.setScreen(screen);
    reservation.setDate(form.getDate());
    reservation.setStartAt(form.getTime());
    reservation.setSeatLabels(String.join(", ", requestedSeats));
    reservation.setTicketPrice(showtime.getTicketPrice());
    reservation.setTotal(showtime.getTicketPrice() * requestedSeats.size());
    reservation.setUsername(user.getUsername());
    reservation.setPhone(user.getPhone());

    Reservation saved = reservationRepository.save(reservation);
    String qrPath = qrCodeService.createReservationQr(saved);
    saved.setQrCodePath(qrPath);
    return reservationRepository.save(saved);
  }
}
