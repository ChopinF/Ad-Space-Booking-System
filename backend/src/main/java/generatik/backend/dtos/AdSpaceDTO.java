package generatik.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import generatik.backend.entities.adspace.*;

public record AdSpaceDTO(
    @JsonProperty("id") Long id,
    @JsonProperty("name") String name,
    @JsonProperty("pricePerDay") Integer pricePerDay,
    @JsonProperty("city") City city,
    @JsonProperty("address") String address,
    @JsonProperty("availabilityStatus") AvailabilityStatus availabilityStatus,
    @JsonProperty("type") AdSpaceType type) {
}
