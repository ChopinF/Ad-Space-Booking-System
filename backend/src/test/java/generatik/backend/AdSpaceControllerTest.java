package generatik.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//with the help of static import, we can access the static members of a class directly without class name or any object
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
class AdSpaceControllerTest {
  @Autowired
  private MockMvc mockMvc;

  // GET /api/v1/ad-spaces -> 200 + list not empty
  @ParameterizedTest
  @ValueSource(strings = {
      "/api/v1/ad-spaces",
      "/api/v1/ad-spaces?type=Billboard",
      "/api/v1/ad-spaces?type=BusStop",
      "/api/v1/ad-spaces?type=MallDisplay",
      "/api/v1/ad-spaces?type=TransitAd",
      "/api/v1/ad-spaces?city=Bucuresti",
      "/api/v1/ad-spaces?city=Cluj",
      "/api/v1/ad-spaces?type=Billboard&city=Cluj"
  })
  void testsBookingGetList(String url) throws Exception {
    mockMvc.perform(get(url)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  // GET /api/v1/ad-spaces/1 -> 200 + not empty
  @Test
  void testsBookingGet() throws Exception {
    long adSpaceId = 1L;

    mockMvc.perform(get("/api/v1/ad-spaces/{id}", adSpaceId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value((int) adSpaceId))
        .andExpect(jsonPath("$.name").value("Times Square"))
        .andExpect(jsonPath("$.pricePerDay").value(300))
        .andExpect(jsonPath("$.city").value("Bucuresti"))
        .andExpect(jsonPath("$.address").value("Piata Unirii 1"))
        .andExpect(jsonPath("$.availabilityStatus").value("Available"))
        .andExpect(jsonPath("$.type").value("Billboard"));

    // edge cases
    // non existing id
    mockMvc.perform(get("/api/v1/ad-spaces/{id}", 99999L)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void testsBookingBadRequest() throws Exception { // edge case, when coming from frontend a bad request it should have
                                                   // 400 as status
    mockMvc.perform(get("/api/v1/ad-spaces")
        .param("type", "INVALID_TYPE")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  // DELETE /api/v1/ad-spaces/{id}
  @Test
  void testDeleteAdSpace() throws Exception {
    long existingId = 3L;
    long nonExistingId = 99999L;

    // happy path: existing ID -> 204 No Content
    mockMvc.perform(delete("/api/v1/ad-spaces/{id}", existingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    // verify it is actually gone -> 404
    mockMvc.perform(get("/api/v1/ad-spaces/{id}", existingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    // edge case: delete non-existing -> 404
    mockMvc.perform(delete("/api/v1/ad-spaces/{id}", nonExistingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  // PUT /api/v1/ad-spaces/{id}
  @Test
  void testUpdateAdSpace() throws Exception {
    long existingId = 2L;
    long nonExistingId = 99999L;

    // AdSpaceDTO shape
    String updateBody = """
        {
          "id": 2,
          "name": "Updated Mall Entrance",
          "pricePerDay": 250,
          "city": "Cluj",
          "address": "Iulius Mall, intrarea secundara",
          "availabilityStatus": "Available",
          "type": "MallDisplay"
        }
        """;

    // happy path: 200 + fields updated
    mockMvc.perform(put("/api/v1/ad-spaces/{id}", existingId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(updateBody)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value((int) existingId))
        .andExpect(jsonPath("$.name").value("Updated Mall Entrance"))
        .andExpect(jsonPath("$.pricePerDay").value(250))
        .andExpect(jsonPath("$.city").value("Cluj"))
        .andExpect(jsonPath("$.address").value("Iulius Mall, intrarea secundara"))
        .andExpect(jsonPath("$.availabilityStatus").value("Available"))
        .andExpect(jsonPath("$.type").value("MallDisplay"));

    // GET afterwards to assert data persisted
    mockMvc.perform(get("/api/v1/ad-spaces/{id}", existingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Mall Entrance"))
        .andExpect(jsonPath("$.pricePerDay").value(250));

    // edge case: update non-existing -> 404
    mockMvc.perform(put("/api/v1/ad-spaces/{id}", nonExistingId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(updateBody)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}
