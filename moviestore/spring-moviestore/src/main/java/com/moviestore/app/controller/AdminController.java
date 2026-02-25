package com.moviestore.app.controller;

import com.moviestore.app.model.*;
import com.moviestore.app.repository.*;
import com.moviestore.app.service.FileStorageService;
import com.moviestore.app.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/admin")
public class AdminController {
  private final MovieRepository movieRepository;
  private final CinemaRepository cinemaRepository;
  private final ScreenRepository screenRepository;
  private final ShowtimeRepository showtimeRepository;
  private final ReservationRepository reservationRepository;
  private final UserRepository userRepository;
  private final UserService userService;
  private final FileStorageService fileStorageService;

  public AdminController(MovieRepository movieRepository,
                         CinemaRepository cinemaRepository,
                         ScreenRepository screenRepository,
                         ShowtimeRepository showtimeRepository,
                         ReservationRepository reservationRepository,
                         UserRepository userRepository,
                         UserService userService,
                         FileStorageService fileStorageService) {
    this.movieRepository = movieRepository;
    this.cinemaRepository = cinemaRepository;
    this.screenRepository = screenRepository;
    this.showtimeRepository = showtimeRepository;
    this.reservationRepository = reservationRepository;
    this.userRepository = userRepository;
    this.userService = userService;
    this.fileStorageService = fileStorageService;
  }

  @GetMapping("/dashboard")
  public String dashboard(Model model) {
    model.addAttribute("users", userRepository.count());
    model.addAttribute("movies", movieRepository.count());
    model.addAttribute("cinemas", cinemaRepository.count());
    model.addAttribute("screens", screenRepository.count());
    model.addAttribute("reservations", reservationRepository.count());
    return "admin/dashboard";
  }

  @GetMapping({"", "/", "/home"})
  public String adminRoot() {
    return "redirect:/admin/dashboard";
  }

  @GetMapping({"/movies", "/movie"})
  public String movies(Model model) {
    model.addAttribute("movies", movieRepository.findAll());
    return "admin/movies";
  }

  @PostMapping("/movies")
  public String createMovie(@RequestParam String title,
                            @RequestParam String language,
                            @RequestParam String genre,
                            @RequestParam String director,
                            @RequestParam String cast,
                            @RequestParam String description,
                            @RequestParam Integer duration,
                            @RequestParam LocalDate releaseDate,
                            @RequestParam LocalDate endDate,
                            RedirectAttributes redirectAttributes,
                            Model model) {
    try {
      if (title == null || title.trim().isEmpty() ||
          language == null || language.trim().isEmpty() ||
          genre == null || genre.trim().isEmpty() ||
          director == null || director.trim().isEmpty() ||
          cast == null || cast.trim().isEmpty() ||
          duration == null || duration <= 0) {
        model.addAttribute("error", "Please fill in all required fields with valid data");
        model.addAttribute("movies", movieRepository.findAll());
        return "admin/movies";
      }
      Movie movie = new Movie();
      movie.setTitle(title);
      movie.setLanguage(language);
      movie.setGenre(genre);
      movie.setDirector(director);
      movie.setCast(cast);
      movie.setDescription(description);
      movie.setDuration(duration);
      movie.setReleaseDate(releaseDate);
      movie.setEndDate(endDate);
      movieRepository.save(movie);
      redirectAttributes.addFlashAttribute("success", "Movie added successfully!");
      return "redirect:/admin/movies";
    } catch (DataIntegrityViolationException e) {
      model.addAttribute("error", "Error adding movie: " + e.getMessage());
      model.addAttribute("movies", movieRepository.findAll());
      return "admin/movies";
    } catch (Exception e) {
      model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
      model.addAttribute("movies", movieRepository.findAll());
      return "admin/movies";
    }
  }

  @PostMapping("/movies/{id}/image")
  public String uploadMovieImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
    Movie movie = movieRepository.findById(id).orElseThrow();
    String path = fileStorageService.store(file, "movies");
    movie.setImage(path);
    movieRepository.save(movie);
    return "redirect:/admin/movies";
  }

  @PostMapping("/movies/{id}/delete")
  public String deleteMovie(@PathVariable Long id, 
                            RedirectAttributes redirectAttributes,
                            Model model) {
    try {
      Movie movie = movieRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Movie not found"));
      
      // Check if there are associated showtimes
      long showtimeCount = showtimeRepository.findByMovieId(id).size();
      if (showtimeCount > 0) {
        model.addAttribute("error", "Cannot delete movie because it has " + showtimeCount + " associated showtime(s). Please delete the showtimes first.");
        model.addAttribute("movies", movieRepository.findAll());
        return "admin/movies";
      }
      
      movieRepository.deleteById(id);
      redirectAttributes.addFlashAttribute("success", "Movie deleted successfully!");
      return "redirect:/admin/movies";
    } catch (DataIntegrityViolationException e) {
      model.addAttribute("error", "Cannot delete movie: It is still referenced by other records. Please delete associated showtimes first.");
      model.addAttribute("movies", movieRepository.findAll());
      return "admin/movies";
    } catch (Exception e) {
      model.addAttribute("error", "An error occurred while deleting the movie: " + e.getMessage());
      model.addAttribute("movies", movieRepository.findAll());
      return "admin/movies";
    }
  }

  @GetMapping({"/cinemas", "/cinema"})
  public String cinemas(Model model) {
    User current = userService.getCurrentUser();
    if (current == null) {
      return "redirect:/login";
    }
    if (current.getRole() == Role.SUPERADMIN) {
      model.addAttribute("cinemas", cinemaRepository.findAll());
    } else {
      model.addAttribute("cinemas", cinemaRepository.findByOwnerId(current.getId()));
    }
    return "admin/cinemas";
  }

  @PostMapping("/cinemas")
  public String createCinema(@RequestParam String name,
                             @RequestParam String city) {
    User current = userService.getCurrentUser();
    if (current == null) {
      return "redirect:/login";
    }
    Cinema cinema = new Cinema();
    cinema.setName(name);
    cinema.setCity(city);
    cinema.setTicketPrice(0.0);
    cinema.setRowsCount(0);
    cinema.setColsCount(0);
    cinema.setOwner(current);
    cinemaRepository.save(cinema);
    Screen screen = new Screen();
    screen.setName("Screen 1");
    screen.setRowsCount(8);
    screen.setColsCount(10);
    screen.setCinema(cinema);
    screenRepository.save(screen);
    return "redirect:/admin/cinemas";
  }

  @GetMapping({"/screens", "/screen"})
  public String screens(Model model) {
    User current = userService.getCurrentUser();
    if (current == null) {
      return "redirect:/login";
    }
    if (current.getRole() == Role.SUPERADMIN) {
      model.addAttribute("screens", screenRepository.findAll());
      model.addAttribute("cinemas", cinemaRepository.findAll());
    } else {
      model.addAttribute("screens", screenRepository.findByCinemaOwnerId(current.getId()));
      model.addAttribute("cinemas", cinemaRepository.findByOwnerId(current.getId()));
    }
    return "admin/screens";
  }

  @PostMapping("/screens")
  public String createScreen(@RequestParam String name,
                             @RequestParam Integer rowsCount,
                             @RequestParam Integer colsCount,
                             @RequestParam Long cinemaId) {
    User current = userService.getCurrentUser();
    if (current == null) {
      return "redirect:/login";
    }
    Cinema cinema = cinemaRepository.findById(cinemaId).orElseThrow();
    if (!canManageCinema(current, cinema)) {
      return "redirect:/admin/screens?error=cinemaForbidden";
    }
    Screen screen = new Screen();
    screen.setName(name);
    screen.setRowsCount(rowsCount);
    screen.setColsCount(colsCount);
    screen.setCinema(cinema);
    screenRepository.save(screen);
    return "redirect:/admin/screens";
  }

  @PostMapping("/screens/{id}/delete")
  public String deleteScreen(@PathVariable Long id) {
    Screen screen = screenRepository.findById(id).orElseThrow();
    User current = userService.getCurrentUser();
    if (!canManageCinema(current, screen.getCinema())) {
      return "redirect:/admin/screens?error=cinemaForbidden";
    }
    screenRepository.deleteById(id);
    return "redirect:/admin/screens";
  }

  @PostMapping("/cinemas/{id}/image")
  public String uploadCinemaImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
    Cinema cinema = cinemaRepository.findById(id).orElseThrow();
    User current = userService.getCurrentUser();
    if (!canManageCinema(current, cinema)) {
      return "redirect:/admin/cinemas?error=forbidden";
    }
    String path = fileStorageService.store(file, "cinemas");
    cinema.setImage(path);
    cinemaRepository.save(cinema);
    return "redirect:/admin/cinemas";
  }

  @PostMapping("/cinemas/{id}/delete")
  public String deleteCinema(@PathVariable Long id) {
    Cinema cinema = cinemaRepository.findById(id).orElseThrow();
    User current = userService.getCurrentUser();
    if (!canManageCinema(current, cinema)) {
      return "redirect:/admin/cinemas?error=forbidden";
    }
    cinemaRepository.deleteById(id);
    return "redirect:/admin/cinemas";
  }

  @GetMapping({"/showtimes", "/showtime"})
  public String showtimes(Model model, @RequestParam(required = false) String error) {
    User current = userService.getCurrentUser();
    if (current == null) {
      return "redirect:/login";
    }
    if (current.getRole() == Role.SUPERADMIN) {
      model.addAttribute("showtimes", showtimeRepository.findAll());
    } else {
      model.addAttribute("showtimes", showtimeRepository.findByCinemaOwnerId(current.getId()));
    }
    model.addAttribute("movies", movieRepository.findAll());
    if (current.getRole() == Role.SUPERADMIN) {
      model.addAttribute("cinemas", cinemaRepository.findAll());
      model.addAttribute("screens", screenRepository.findAll());
    } else {
      model.addAttribute("cinemas", cinemaRepository.findByOwnerId(current.getId()));
      model.addAttribute("screens", screenRepository.findByCinemaOwnerId(current.getId()));
    }
    if (error != null) {
      if ("cinemaForbidden".equals(error)) {
        model.addAttribute("error", "You don't have permission to manage this showtime.");
      }
    }
    return "admin/showtimes";
  }

  @PostMapping("/showtimes")
  public String createShowtime(@RequestParam Long movieId,
                               @RequestParam Long cinemaId,
                               @RequestParam Long screenId,
                               @RequestParam LocalTime startAt,
                               @RequestParam LocalDate startDate,
                               @RequestParam LocalDate endDate,
                               @RequestParam Double ticketPrice) {
    Showtime showtime = new Showtime();
    showtime.setMovie(movieRepository.findById(movieId).orElseThrow());
    Cinema cinema = cinemaRepository.findById(cinemaId).orElseThrow();
    Screen screen = screenRepository.findById(screenId).orElseThrow();
    User current = userService.getCurrentUser();
    if (!canManageCinema(current, cinema)) {
      return "redirect:/admin/showtimes?error=cinemaForbidden";
    }
    if (!screen.getCinema().getId().equals(cinema.getId())) {
      return "redirect:/admin/showtimes?error=screenMismatch";
    }
    showtime.setCinema(cinema);
    showtime.setScreen(screen);
    showtime.setStartAt(startAt);
    showtime.setStartDate(startDate);
    showtime.setEndDate(endDate);
    showtime.setTicketPrice(ticketPrice);
    showtimeRepository.save(showtime);
    return "redirect:/admin/showtimes";
  }

  @PostMapping("/showtimes/{id}/delete")
  public String deleteShowtime(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
      Showtime showtime = showtimeRepository.findById(id).orElseThrow();
      User current = userService.getCurrentUser();
      if (!canManageCinema(current, showtime.getCinema())) {
        return "redirect:/admin/showtimes?error=cinemaForbidden";
      }
      showtimeRepository.deleteById(id);
      return "redirect:/admin/showtimes";
    } catch (DataIntegrityViolationException e) {
      redirectAttributes.addFlashAttribute("error", "Cannot delete showtime. There are reservations associated with this showtime.");
      return "redirect:/admin/showtimes";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the showtime: " + e.getMessage());
      return "redirect:/admin/showtimes";
    }
  }

  @GetMapping({"/reservations", "/reservation"})
  public String reservations(Model model) {
    User current = userService.getCurrentUser();
    if (current == null) {
      return "redirect:/login";
    }
    if (current.getRole() == Role.SUPERADMIN) {
      model.addAttribute("reservations", reservationRepository.findAll());
    } else {
      model.addAttribute("reservations", reservationRepository.findByCinemaOwnerId(current.getId()));
    }
    return "admin/reservations";
  }

  @PostMapping("/reservations/{id}/delete")
  public String deleteReservation(@PathVariable Long id) {
    Reservation reservation = reservationRepository.findWithDetailsById(id).orElseThrow();
    User current = userService.getCurrentUser();
    if (!canManageCinema(current, reservation.getCinema())) {
      return "redirect:/admin/reservations?error=cinemaForbidden";
    }
    reservationRepository.deleteById(id);
    return "redirect:/admin/reservations";
  }

  @GetMapping({"/users", "/user"})
  public String users(Model model) {
    User current = userService.getCurrentUser();
    if (current == null || current.getRole() != Role.SUPERADMIN) {
      return "redirect:/admin/dashboard";
    }
    model.addAttribute("users", userRepository.findAll());
    return "admin/users";
  }

  @GetMapping({"/account", "/accounts"})
  public String account(Model model) {
    User current = userService.getCurrentUser();
    if (current == null) {
      return "redirect:/login";
    }
    model.addAttribute("user", current);
    return "admin/account";
  }

  @PostMapping("/users/{id}/role")
  public String updateUserRole(@PathVariable Long id, @RequestParam Role role) {
    User current = userService.getCurrentUser();
    if (current == null || current.getRole() != Role.SUPERADMIN) {
      return "redirect:/admin/dashboard";
    }
    User user = userRepository.findById(id).orElseThrow();
    user.setRole(role);
    userRepository.save(user);
    return "redirect:/admin/users";
  }

  private boolean canManageCinema(User current, Cinema cinema) {
    if (current == null || cinema == null) {
      return false;
    }
    if (current.getRole() == Role.SUPERADMIN) {
      return true;
    }
    User owner = cinema.getOwner();
    return owner != null && owner.getId().equals(current.getId());
  }
}
