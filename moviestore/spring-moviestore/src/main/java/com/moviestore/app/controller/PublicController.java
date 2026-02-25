package com.moviestore.app.controller;

import com.moviestore.app.dto.BookingForm;
import com.moviestore.app.model.*;
import com.moviestore.app.repository.*;
import com.moviestore.app.service.BookingService;
import com.moviestore.app.service.FileStorageService;
import com.moviestore.app.service.InvitationService;
import com.moviestore.app.service.QrCodeService;
import com.moviestore.app.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;

@Controller
public class PublicController {
  private static final Logger log = LoggerFactory.getLogger(PublicController.class);
  private final MovieRepository movieRepository;
  private final CinemaRepository cinemaRepository;
  private final ScreenRepository screenRepository;
  private final ShowtimeRepository showtimeRepository;
  private final ReservationRepository reservationRepository;
  private final BookingService bookingService;
  private final UserService userService;
  private final UserRepository userRepository;
  private final FileStorageService fileStorageService;
  private final InvitationService invitationService;
  private final QrCodeService qrCodeService;

  public PublicController(MovieRepository movieRepository,
                          CinemaRepository cinemaRepository,
                          ScreenRepository screenRepository,
                          ShowtimeRepository showtimeRepository,
                          ReservationRepository reservationRepository,
                          BookingService bookingService,
                          UserService userService,
                          UserRepository userRepository,
                          FileStorageService fileStorageService,
                          InvitationService invitationService,
                          QrCodeService qrCodeService) {
    this.movieRepository = movieRepository;
    this.cinemaRepository = cinemaRepository;
    this.screenRepository = screenRepository;
    this.showtimeRepository = showtimeRepository;
    this.reservationRepository = reservationRepository;
    this.bookingService = bookingService;
    this.userService = userService;
    this.userRepository = userRepository;
    this.fileStorageService = fileStorageService;
    this.invitationService = invitationService;
    this.qrCodeService = qrCodeService;
  }

  @GetMapping("/")
  public String home(Model model) {
    LocalDate today = LocalDate.now();
    List<Movie> nowShowing = movieRepository.findByReleaseDateLessThanEqualAndEndDateGreaterThanEqual(today, today);
    model.addAttribute("nowShowing", nowShowing);
    model.addAttribute("comingSoon", movieRepository.findAll().stream().filter(m -> m.getReleaseDate().isAfter(today)).toList());
    return "public/home";
  }

  @GetMapping({"/movies/{id}", "/movie/{id}"})
  public String movieDetails(@PathVariable Long id, Model model) {
    Movie movie = movieRepository.findById(id).orElse(null);
    if (movie == null) {
      return "redirect:/?movieNotFound=true";
    }
    model.addAttribute("movie", movie);
    try {
      List<Showtime> safeShowtimes = showtimeRepository.findByMovieId(id)
          .stream()
          .filter(s -> s != null && s.getCinema() != null && s.getScreen() != null)
          .collect(Collectors.toList());
      model.addAttribute("showtimes", safeShowtimes);
    } catch (Exception ex) {
      model.addAttribute("showtimes", Collections.emptyList());
      model.addAttribute("warning", "Showtimes are temporarily unavailable for this movie.");
    }
    return "public/movie";
  }

  @GetMapping("/cinemas")
  public String cinemas(Model model) {
    model.addAttribute("cinemas", cinemaRepository.findAll());
    return "public/cinemas";
  }

  @GetMapping({"/movies/category/{category}", "/movie/category/{category}"})
  public String movieCategory(@PathVariable String category, Model model) {
    LocalDate today = LocalDate.now();
    List<Movie> allMovies = movieRepository.findAll();
    List<Movie> filtered = switch (category.toLowerCase()) {
      case "nowshowing" -> allMovies.stream()
          .filter(m -> !m.getReleaseDate().isAfter(today) && !m.getEndDate().isBefore(today))
          .toList();
      case "comingsoon" -> allMovies.stream()
          .filter(m -> m.getReleaseDate().isAfter(today))
          .toList();
      default -> allMovies;
    };
    model.addAttribute("movies", filtered);
    model.addAttribute("category", category);
    return "public/movie-category";
  }

  @GetMapping({"/book/{movieId}", "/movie/booking/{movieId}"})
  public String bookingPage(@PathVariable Long movieId, Model model) {
    Movie movie = movieRepository.findById(movieId).orElse(null);
    if (movie == null) {
      return "redirect:/?movieNotFound=true";
    }
    model.addAttribute("movie", movie);
    model.addAttribute("cinemas", cinemaRepository.findAll());
    model.addAttribute("screens", screenRepository.findAll());
    model.addAttribute("showtimes", showtimeRepository.findByMovieId(movieId)
        .stream()
        .filter(s -> s != null && s.getCinema() != null && s.getScreen() != null)
        .collect(Collectors.toList()));
    model.addAttribute("form", new BookingForm());
    return "public/booking";
  }

  @GetMapping("/book/seats")
  @ResponseBody
  public Map<String, Object> takenSeats(@RequestParam Long cinemaId,
                                        @RequestParam Long screenId,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {
    Cinema cinema = cinemaRepository.findById(cinemaId).orElseThrow();
    Screen screen = screenRepository.findById(screenId).orElseThrow();
    if (!screen.getCinema().getId().equals(cinema.getId())) {
      return Map.of(
          "takenSeats", List.of(),
          "rowsCount", 0,
          "colsCount", 0
      );
    }
    List<String> taken = bookingService.getTakenSeats(screenId, date, time);
    return Map.of(
        "takenSeats", taken,
        "rowsCount", screen.getRowsCount(),
        "colsCount", screen.getColsCount()
    );
  }

  @PostMapping("/book")
  public String book(@RequestParam(required = false) Long movieId,
                     @RequestParam(required = false) String cinemaId,
                     @RequestParam(required = false) String screenId,
                     @RequestParam(required = false) String date,
                     @RequestParam(required = false) String time,
                     @RequestParam(required = false) String seatLabels,
                     RedirectAttributes redirectAttributes) {
    if (movieId == null) {
      return "redirect:/?movieNotFound=true";
    }
    if (cinemaId == null || screenId == null || date == null || time == null || seatLabels == null || seatLabels.trim().isEmpty()) {
      redirectAttributes.addAttribute("bookingError", "Please select cinema, screen, date, time, and at least one seat.");
      return "redirect:/book/" + movieId;
    }

    User currentUser = userService.getCurrentUser();
    if (currentUser == null) {
      return "redirect:/login";
    }

    try {
      Long cinemaIdValue;
      Long screenIdValue;
      try {
        cinemaIdValue = Long.parseLong(cinemaId);
        screenIdValue = Long.parseLong(screenId);
      } catch (NumberFormatException ex) {
        redirectAttributes.addAttribute("bookingError", "Invalid cinema or screen selection.");
        return "redirect:/book/" + movieId;
      }
      Screen screen = screenRepository.findById(screenIdValue).orElse(null);
      if (screen == null || screen.getCinema() == null || !screen.getCinema().getId().equals(cinemaIdValue)) {
        redirectAttributes.addAttribute("bookingError", "Selected screen does not match the selected cinema.");
        return "redirect:/book/" + movieId;
      }
      BookingForm form = new BookingForm();
      form.setMovieId(movieId);
      form.setCinemaId(cinemaIdValue);
      form.setScreenId(screenIdValue);
      form.setDate(LocalDate.parse(date));
      form.setTime(LocalTime.parse(time));
      form.setSeatLabels(seatLabels);

      Reservation reservation = bookingService.createReservation(form, currentUser);
      return "redirect:/payment/" + reservation.getId();
    } catch (DateTimeParseException ex) {
      redirectAttributes.addAttribute("bookingError", "Invalid date/time format.");
      return "redirect:/book/" + movieId;
    } catch (Exception ex) {
      log.error("Booking failed for movieId={}, cinemaId={}, screenId={}, date={}, time={}",
          movieId, cinemaId, screenId, date, time, ex);
      String message = ex.getMessage() == null ? "Unable to complete reservation." : ex.getMessage();
      redirectAttributes.addAttribute("bookingError", message);
      return "redirect:/book/" + movieId;
    }
  }

  @GetMapping("/mydashboard")
  public String myDashboard(Model model) {
    User user = userService.getCurrentUser();
    if (user == null) return "redirect:/login";
    List<Reservation> reservations = reservationRepository.findByUsername(user.getUsername());
    boolean updated = false;
    for (Reservation reservation : reservations) {
      String newQrPath = qrCodeService.createReservationQr(reservation);
      if (!newQrPath.equals(reservation.getQrCodePath())) {
        reservation.setQrCodePath(newQrPath);
        updated = true;
      }
    }
    if (updated) {
      reservationRepository.saveAll(reservations);
    }
    model.addAttribute("reservations", reservations);
    return "public/mydashboard";
  }

  @PostMapping("/profile/image")
  public String uploadProfileImage(@RequestParam("file") MultipartFile file) {
    User user = userService.getCurrentUser();
    if (user == null) return "redirect:/login";
    String path = fileStorageService.store(file, "users");
    user.setImageUrl(path);
    userRepository.save(user);
    return "redirect:/mydashboard?profileUpdated=true";
  }

  @PostMapping("/reservations/{id}/invite")
  public String invite(@PathVariable Long id, @RequestParam String emails) {
    User user = userService.getCurrentUser();
    if (user == null) return "redirect:/login";

    Reservation reservation = reservationRepository.findWithDetailsById(id).orElseThrow();
    if (!reservation.getUsername().equals(user.getUsername())) {
      return "redirect:/mydashboard?inviteError=unauthorized";
    }

    try {
      List<String> recipients = List.of(emails.split(","));
      invitationService.sendReservationInvitations(reservation, user.getName(), recipients);
      return "redirect:/mydashboard?invite=success";
    } catch (Exception ex) {
      String message = ex.getMessage() == null ? "Unable to send invitations" : ex.getMessage();
      return "redirect:/mydashboard?inviteError=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }
  }

  @PostMapping("/reservations/{id}/checkin")
  public String checkin(@PathVariable Long id) {
    Reservation reservation = reservationRepository.findWithDetailsById(id).orElseThrow();
    reservation.setCheckin(true);
    reservationRepository.save(reservation);
    return "redirect:/mydashboard?checkin=" + id;
  }

  @GetMapping("/checkin/{id}")
  public String publicCheckin(@PathVariable Long id, Model model) {
    Reservation reservation = reservationRepository.findWithDetailsById(id).orElseThrow();
    reservation.setCheckin(true);
    reservationRepository.save(reservation);
    model.addAttribute("reservation", reservation);
    return "public/checkin";
  }

  @GetMapping("/payment/{id}")
  public String paymentPage(@PathVariable Long id, Model model) {
    User user = userService.getCurrentUser();
    if (user == null) return "redirect:/login";
    Reservation reservation = reservationRepository.findWithDetailsById(id).orElseThrow();
    if (!reservation.getUsername().equals(user.getUsername())) {
      return "redirect:/mydashboard?paymentError=unauthorized";
    }
    model.addAttribute("reservation", reservation);
    return "public/payment";
  }

  @PostMapping("/payment/{id}/confirm")
  public String confirmPayment(@PathVariable Long id) {
    User user = userService.getCurrentUser();
    if (user == null) return "redirect:/login";
    Reservation reservation = reservationRepository.findWithDetailsById(id).orElseThrow();
    if (!reservation.getUsername().equals(user.getUsername())) {
      return "redirect:/mydashboard?paymentError=unauthorized";
    }
    if (!reservation.isPaid()) {
      reservation.setPaid(true);
      reservationRepository.save(reservation);
    }
    return "redirect:/mydashboard?payment=success";
  }

  @GetMapping("/dashboard")
  public String dashboardAlias() {
    return "redirect:/mydashboard";
  }
}
