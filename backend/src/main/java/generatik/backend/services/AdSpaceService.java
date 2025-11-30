package generatik.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import generatik.backend.entities.adspace.*;
import generatik.backend.entities.booking.*;
import generatik.backend.repos.AdSpaceRepository;
import generatik.backend.repos.BookingRepository;
import generatik.backend.dtos.*;

@Service
public class AdSpaceService {
  private final BookingRepository bookingRepository;
  private final AdSpaceRepository adSpaceRepository;

  public AdSpaceService(BookingRepository bookingRepository, AdSpaceRepository adSpaceRepository) {
    this.bookingRepository = bookingRepository;
    this.adSpaceRepository = adSpaceRepository;
  }

  public List<AdSpaceDTO> getAll(Optional<AdSpaceType> type, Optional<City> city) {
    return adSpaceRepository.findAll()
        .stream()
        .filter(ad -> ad.getAvailabilityStatus() == AvailabilityStatus.Available) // only available ones
        .filter(ad -> type.map(t -> ad.getType() == t).orElse(true))
        .filter(ad -> city.map(c -> ad.getCity() == c).orElse(true))
        .map(this::toDTO)
        .toList();
  }

  public Optional<AdSpaceDTO> getById(Long id) {
    return adSpaceRepository.findById(id)
        .map(this::toDTO);
  }

  public void deleteById(Long id) {
    adSpaceRepository.deleteById(id);
  }

  // NEW: update
  public Optional<AdSpaceDTO> updateAdSpace(Long id, AdSpaceDTO dto) {
    return adSpaceRepository.findById(id)
        .map(entity -> {
          entity.setName(dto.name());
          entity.setPricePerDay(dto.pricePerDay());
          entity.setCity(dto.city());
          entity.setAddress(dto.address());
          entity.setAvailabilityStatus(dto.availabilityStatus());
          entity.setType(dto.type());

          var saved = adSpaceRepository.save(entity);
          return toDTO(saved);
        });
  }

  private AdSpaceDTO toDTO(AdSpace ad) {
    return new AdSpaceDTO(
        ad.getId(),
        ad.getName(),
        ad.getPricePerDay(),
        ad.getCity(),
        ad.getAddress(),
        ad.getAvailabilityStatus(),
        ad.getType());
  }

  // TODO: encapsulate methode toDTO
}
