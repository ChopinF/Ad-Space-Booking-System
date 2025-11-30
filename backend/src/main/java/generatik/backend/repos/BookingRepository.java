package generatik.backend.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import generatik.backend.entities.booking.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
}
