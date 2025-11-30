package generatik.backend.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import generatik.backend.entities.booking.Status;

public record BookingDTO(
    @JsonProperty("id") Long id,
    @JsonProperty("adSpaceId") Long adSpaceId,
    @JsonProperty("advertiserName") String advertiserName,
    @JsonProperty("advertiserEmail") String advertiserEmail,
    @JsonProperty("startDate") LocalDate startDate,
    @JsonProperty("endDate") LocalDate endDate,
    @JsonProperty("createdAt") LocalDateTime createdAt,
    @JsonProperty("status") Status status,
    @JsonProperty("totalCost") Integer totalCost) {
}
