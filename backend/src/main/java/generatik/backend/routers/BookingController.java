package generatik.backend.routers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import generatik.backend.dtos.BookingCreationDTO;
import generatik.backend.dtos.BookingDTO;
import generatik.backend.entities.booking.Status;
import generatik.backend.services.BookingService;

@RestController
@RequestMapping("/api/v1/booking-requests")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" })
@Tag(name = "Bookings", description = "Create and manage booking requests")
public class BookingController {

  private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

  private final BookingService bookingService;

  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @PostMapping(value = "")
  @Operation(summary = "Create a booking request")
  public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingCreationDTO bookingDTO) {
    logger.info("POST /api/v1/booking-requests called to create booking");

    var start = bookingDTO.startDate();
    var end = bookingDTO.endDate();
    var today = LocalDate.now();

    logger.debug("Booking payload received: startDate={}, endDate={}", start, end);

    if (start == null || end == null) { // both must not be null
      logger.warn("Booking creation failed: startDate or endDate is null");
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "startDate and endDate are required");
    }

    if (!start.isAfter(today) || !end.isAfter(today)) { // both must be in the future
      logger.warn("Booking creation failed: dates not in the future. today={}, startDate={}, endDate={}",
          today, start, end);
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "startDate and endDate must both be in the future");
    }

    if (!end.isAfter(start)) { // endDate must be after startDate
      logger.warn("Booking creation failed: endDate {} is not after startDate {}", end, start);
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "endDate must be after startDate");
    }

    var savedBooking = bookingService.createBooking(bookingDTO);
    logger.info("Booking created successfully with id={}", savedBooking.id());

    return ResponseEntity.ok().body(savedBooking);
  }

  @GetMapping(value = "/{id}")
  @Operation(summary = "Get a booking request by ID")
  public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id) {
    logger.info("GET /api/v1/booking-requests/{} called", id);

    return bookingService.getById(id)
        .map(booking -> {
          logger.debug("Booking {} found", id);
          return ResponseEntity.ok(booking);
        })
        .orElseGet(() -> {
          logger.warn("Booking {} not found", id);
          return ResponseEntity.notFound().build();
        });
  }

  @PatchMapping(value = "/{id}/approve")
  @Operation(summary = "Approve a booking request")
  public ResponseEntity<BookingDTO> approveBooking(@PathVariable Long id) {
    logger.info("PATCH /api/v1/booking-requests/{}/approve called", id);

    var booking = bookingService.approveBooking(id);
    logger.info("Booking {} approved", id);

    return ResponseEntity.ok(booking);
  }

  @PatchMapping(value = "/{id}/reject")
  @Operation(summary = "Reject a booking request")
  public ResponseEntity<BookingDTO> rejectBooking(@PathVariable Long id) {
    logger.info("PATCH /api/v1/booking-requests/{}/reject called", id);

    var booking = bookingService.rejectBooking(id);
    logger.info("Booking {} rejected", id);

    return ResponseEntity.ok(booking);
  }

  @GetMapping(value = "")
  @Operation(summary = "List booking requests (optionally filtered by status)")
  public ResponseEntity<List<BookingDTO>> getAll(
      @RequestParam(name = "status", required = false) Status status) {

    logger.info("GET /api/v1/booking-requests called with status={}", status);

    var bookings = bookingService.getAll(Optional.ofNullable(status));
    logger.debug("Found {} booking(s) for status={}", bookings.size(), status);

    return ResponseEntity.ok().body(bookings);
  }
}
