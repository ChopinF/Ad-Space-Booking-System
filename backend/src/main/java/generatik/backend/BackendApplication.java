package generatik.backend;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import generatik.backend.entities.adspace.AdSpace;
import generatik.backend.entities.adspace.AdSpaceType;
import generatik.backend.entities.adspace.AvailabilityStatus;
import generatik.backend.entities.adspace.City;
import generatik.backend.entities.booking.Booking;
import generatik.backend.entities.booking.Status;
import generatik.backend.repos.AdSpaceRepository;
import generatik.backend.repos.BookingRepository;

@SpringBootApplication
public class BackendApplication {

  public static void main(String[] args) {
    System.out.println("Starting");
    SpringApplication.run(BackendApplication.class, args);
  }

  @Bean
  CommandLineRunner seedDatabase(AdSpaceRepository adSpaceRepository, BookingRepository bookingRepository) {
    return args -> {
      if (adSpaceRepository.count() > 0 || bookingRepository.count() > 0) {
        return;
      }

      AdSpace s1 = new AdSpace(
          "Times Square",
          AdSpaceType.Billboard,
          300,
          City.Bucuresti,
          "Piata Unirii 1",
          AvailabilityStatus.Available);
      AdSpace s2 = new AdSpace(
          "Mall Entrance",
          AdSpaceType.MallDisplay,
          200,
          City.Cluj,
          "Iulius Mall, intrarea principala",
          AvailabilityStatus.Available);
      AdSpace s3 = new AdSpace(
          "Bus Stop",
          AdSpaceType.BusStop,
          100,
          City.Iasi,
          "Str. Victoriei, statia 3",
          AvailabilityStatus.Available);
      AdSpace s4 = new AdSpace(
          "Transit ad",
          AdSpaceType.TransitAd,
          100,
          City.Iasi,
          "Str. Roman Musat",
          AvailabilityStatus.Available);

      AdSpace s5 = new AdSpace(
          "Times Square 2",
          AdSpaceType.Billboard,
          300,
          City.Bucuresti,
          "Piata Unirii 1",
          AvailabilityStatus.Available);
      AdSpace s6 = new AdSpace(
          "Mall Entrance 2",
          AdSpaceType.MallDisplay,
          200,
          City.Cluj,
          "Iulius Mall, intrarea principala",
          AvailabilityStatus.Available);
      AdSpace s7 = new AdSpace(
          "Bus Stop 2",
          AdSpaceType.BusStop,
          100,
          City.Iasi,
          "Str. Victoriei, statia 3",
          AvailabilityStatus.Available);
      AdSpace s8 = new AdSpace(
          "Transit ad 2",
          AdSpaceType.TransitAd,
          100,
          City.Iasi,
          "Str. Roman Musat",
          AvailabilityStatus.Available);

      adSpaceRepository.save(s1);
      adSpaceRepository.save(s2);
      adSpaceRepository.save(s3);
      adSpaceRepository.save(s4);
      adSpaceRepository.save(s5);
      adSpaceRepository.save(s6);
      adSpaceRepository.save(s7);
      adSpaceRepository.save(s8);

      Booking b1 = new Booking(
          "Acme Corp",
          "contact@acme.com",
          LocalDate.now().plusDays(3),
          LocalDate.now().plusDays(10),
          300 * 7 // 7 zile * 300
      );
      b1.setAdSpace(s1);
      b1.setStatus(Status.Pending);

      // booking pentru s2 (Mall Entrance)
      Booking b2 = new Booking(
          "Cool Startup",
          "hello@cool.io",
          LocalDate.now().plusDays(5),
          LocalDate.now().plusDays(8),
          200 * 3 // 3 zile * 200
      );
      b2.setAdSpace(s2);
      b2.setStatus(Status.Pending);

      bookingRepository.save(b1);
      bookingRepository.save(b2);
    };
  }
}
