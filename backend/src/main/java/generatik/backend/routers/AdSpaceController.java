package generatik.backend.routers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import generatik.backend.dtos.AdSpaceDTO;
import generatik.backend.entities.adspace.AdSpaceType;
import generatik.backend.entities.adspace.City;
import generatik.backend.services.AdSpaceService;
import generatik.backend.services.BookingService;

@RestController
@RequestMapping("/api/v1/ad-spaces")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" })
public class AdSpaceController {
  private static final Logger logger = LoggerFactory.getLogger(AdSpaceController.class);
  private final AdSpaceService adSpaceService;

  public AdSpaceController(BookingService bookingService, AdSpaceService adSpaceService) {
    this.adSpaceService = adSpaceService;
  }

  @GetMapping("")
  public ResponseEntity<List<AdSpaceDTO>> getAllAdSpaces(
      @RequestParam(name = "type", required = false) AdSpaceType type,
      @RequestParam(name = "city", required = false) City city) {
    logger.info("GET /api/v1/ad-spaces called with type={} and city={}", type, city);
    var spaces = adSpaceService.getAll(
        Optional.ofNullable(type),
        Optional.ofNullable(city));
    logger.debug("Found {} ad spaces", spaces.size());
    return ResponseEntity.ok(spaces);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AdSpaceDTO> getAdSpaceById(@PathVariable Long id) {
    logger.info("GET /api/v1/ad-spaces/{} called", id);
    return adSpaceService.getById(id)
        .map(space -> {
          logger.debug("Ad space {} found", id);
          return ResponseEntity.ok(space);
        })
        .orElseGet(() -> {
          logger.warn("Ad space {} not found", id);
          return ResponseEntity.notFound().build();
        });
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAdSpace(@PathVariable Long id) {
    logger.info("DELETE /api/v1/ad-spaces/{} called", id);
    if (!adSpaceService.getById(id).isPresent()) {
      logger.warn("Tried to delete ad space {}, but it does not exist", id);
      return ResponseEntity.notFound().build();
    }
    adSpaceService.deleteById(id);
    logger.info("Ad space {} deleted", id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<AdSpaceDTO> updateAdSpace(
      @PathVariable Long id,
      @RequestBody AdSpaceDTO adSpaceDTO) {
    logger.info("PUT /api/v1/ad-spaces/{} called", id);
    logger.debug("Payload for {}: {}", id, adSpaceDTO);
    return adSpaceService.updateAdSpace(id, adSpaceDTO)
        .map(updated -> {
          logger.info("Ad space {} updated", id);
          return ResponseEntity.ok(updated);
        })
        .orElseGet(() -> {
          logger.warn("Tried to update ad space {}, but it does not exist", id);
          return ResponseEntity.notFound().build();
        });
  }
}
