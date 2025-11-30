package generatik.backend.services;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import generatik.backend.dtos.BookingCreationDTO;
import generatik.backend.dtos.BookingDTO;
import generatik.backend.entities.booking.Booking;
import generatik.backend.entities.booking.Status;
import generatik.backend.repos.AdSpaceRepository;
import generatik.backend.repos.BookingRepository;

@Service
public class BookingService {
  private final BookingRepository bookingRepository;
  private final AdSpaceRepository adSpaceRepository;

  public BookingService(BookingRepository bookingRepository, AdSpaceRepository adSpaceRepository) {
    this.bookingRepository = bookingRepository;
    this.adSpaceRepository = adSpaceRepository;
  }

  public BookingDTO createBooking(BookingCreationDTO dto) {
    var adSpaceOptional = this.adSpaceRepository.findById(dto.adSpaceId());// we get first the actual space, to compute
                                                                           // the
    // total price
    if (adSpaceOptional.isEmpty()) {
      throw new IllegalArgumentException("Ad space not found: " + dto.adSpaceId());
    }
    var adSpace = adSpaceOptional.get();
    long days = ChronoUnit.DAYS.between(dto.startDate(), dto.endDate());

    int totalCost = (int) days * adSpace.getPricePerDay();
    var entity = new Booking(dto.advertiserName(), dto.advertiserEmail(), dto.startDate(), dto.endDate(), totalCost);
    entity.setAdSpace(adSpace);

    try {
      var savedBooking = this.bookingRepository.save(entity);
      return new BookingDTO(savedBooking.getId(), savedBooking.getAdSpace().getId(), savedBooking.getAdvertiserName(),
          savedBooking.getAdvertiserEmail(), savedBooking.getStartDate(), savedBooking.getEndDate(),
          savedBooking.getCreatedAt(), savedBooking.getStatus(), totalCost);
    } catch (DataIntegrityViolationException ex) { // in case some trigger of uniquness from db triggers
      if (ex.getMessage().contains("value too long for type character varying(20)"))
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Name is too long - max 20 characters permitted",
            ex);
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Advertiser name or email already exists" + ex.getMessage(),
          ex);

    }
  }

  public Optional<BookingDTO> getById(Long id) {
    return bookingRepository.findById(id)
        .map(b -> new BookingDTO(
            b.getId(),
            b.getAdSpace().getId(),
            b.getAdvertiserName(),
            b.getAdvertiserEmail(),
            b.getStartDate(),
            b.getEndDate(),
            b.getCreatedAt(),
            b.getStatus(),
            b.getTotalCost()));
  }

  public List<BookingDTO> getAll(Optional<Status> status) {
    return bookingRepository.findAll()
        .stream()
        .filter(b -> status.map(s -> b.getStatus() == s).orElse(true))
        .map(b -> new BookingDTO(
            b.getId(),
            b.getAdSpace().getId(),
            b.getAdvertiserName(),
            b.getAdvertiserEmail(),
            b.getStartDate(),
            b.getEndDate(),
            b.getCreatedAt(),
            b.getStatus(),
            b.getTotalCost()))
        .toList();
  }

  public BookingDTO approveBooking(Long id) {
    // edge case not finding the booking
    var booking = bookingRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found: " + id));

    if (booking.getStatus() != Status.Pending) { // edge case status not being pending, it can be
                                                 // already accepted / rejected
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Only pending bookings can be approved");
    }

    booking.setStatus(Status.Approved);
    var saved = bookingRepository.save(booking);
    return new BookingDTO(
        saved.getId(),
        saved.getAdSpace().getId(),
        saved.getAdvertiserName(),
        saved.getAdvertiserEmail(),
        saved.getStartDate(),
        saved.getEndDate(),
        saved.getCreatedAt(),
        saved.getStatus(),
        saved.getTotalCost());
  }

  public BookingDTO rejectBooking(Long id) {
    // edge case not finding the booking
    var booking = bookingRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found: " + id));

    if (booking.getStatus() != Status.Pending) {// edge case status not being pending, it can be
                                                // already accepted / rejected
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Only pending bookings can be rejected");
    }

    booking.setStatus(Status.Rejected);
    var saved = bookingRepository.save(booking);
    return new BookingDTO(
        saved.getId(),
        saved.getAdSpace().getId(),
        saved.getAdvertiserName(),
        saved.getAdvertiserEmail(),
        saved.getStartDate(),
        saved.getEndDate(),
        saved.getCreatedAt(),
        saved.getStatus(),
        saved.getTotalCost());
  }

  // TODO: encapsulate methode toDTO
}
