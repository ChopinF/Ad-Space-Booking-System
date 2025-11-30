package generatik.backend.dtos;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BookingCreationDTO(
    @JsonProperty("adSpaceId") Long adSpaceId,
    @JsonProperty("advertiserName") String advertiserName,
    @JsonProperty("advertiserEmail") String advertiserEmail,
    @JsonProperty("startDate") LocalDate startDate,
    @JsonProperty("endDate") LocalDate endDate) {
}
