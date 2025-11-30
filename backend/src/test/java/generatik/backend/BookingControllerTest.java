package generatik.backend;

//with the help of static import, we can access the static members of a class directly without class name or any object
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {
  @Autowired
  private MockMvc mockMvc;

  // GET /api/v1/booking-requests -> 200 + lista cu seed-ul așteptat
  @Test
  void testsBookingsGetAll() throws Exception {
    // these are for testing the current dates
    String b1Start = LocalDate.now().plusDays(3).toString();
    String b1End = LocalDate.now().plusDays(10).toString();

    String b2Start = LocalDate.now().plusDays(5).toString();
    String b2End = LocalDate.now().plusDays(8).toString();
    // we test to actually get the ones from the seed
    mockMvc.perform(get("/api/v1/booking-requests")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))

        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].adSpaceId").value(1))
        .andExpect(jsonPath("$[0].advertiserName").value("Acme Corp"))
        .andExpect(jsonPath("$[0].advertiserEmail").value("contact@acme.com"))
        .andExpect(jsonPath("$[0].startDate").value(b1Start))
        .andExpect(jsonPath("$[0].endDate").value(b1End))
        .andExpect(jsonPath("$[0].status").value("Pending"))
        .andExpect(jsonPath("$[0].totalCost").value(2100))

        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].adSpaceId").value(2))
        .andExpect(jsonPath("$[1].advertiserName").value("Cool Startup"))
        .andExpect(jsonPath("$[1].advertiserEmail").value("hello@cool.io"))
        .andExpect(jsonPath("$[1].startDate").value(b2Start))
        .andExpect(jsonPath("$[1].endDate").value(b2End))
        .andExpect(jsonPath("$[1].status").value("Pending"))
        .andExpect(jsonPath("$[1].totalCost").value(600));

    // filtering based on status
    mockMvc.perform(get("/api/v1/booking-requests")
        .param("status", "Pending")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].status").value("Pending"))
        .andExpect(jsonPath("$[1].status").value("Pending"));

    // get based on {id} , here the first one
    mockMvc.perform(get("/api/v1/booking-requests/{id}", 1L)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value((int) 1L))
        .andExpect(jsonPath("$.adSpaceId").value(1))
        .andExpect(jsonPath("$.advertiserName").value("Acme Corp"))
        .andExpect(jsonPath("$.advertiserEmail").value("contact@acme.com"))
        .andExpect(jsonPath("$.startDate").value(b1Start))
        .andExpect(jsonPath("$.endDate").value(b1End))
        .andExpect(jsonPath("$.status").value("Pending"))
        .andExpect(jsonPath("$.totalCost").value(2100));

    // edge case : not found when searching by a non existent id
    mockMvc.perform(get("/api/v1/booking-requests/{id}", 99999L)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  // PATCH /api/v1/booking-requests/{id}/approve
  @Test
  void testsBookingPatchApprove() throws Exception {
    // happy scenario
    long bookingId = 1L;

    mockMvc.perform(patch("/api/v1/booking-requests/{id}/approve", bookingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value((int) bookingId))
        .andExpect(jsonPath("$.status").value("Approved"));

    // edge cases
    // try to approve something that doesn't exist
    mockMvc.perform(patch("/api/v1/booking-requests/{id}/approve", 99999L)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
    // now if run again it should not pass, because it's already approved
    mockMvc.perform(patch("/api/v1/booking-requests/{id}/approve", bookingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest()); // 400

    // now if we try to reject should expect error
    mockMvc.perform(patch("/api/v1/booking-requests/{id}/reject", bookingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest()); // 400
  }

  // PATCH /api/v1/booking-requests/{id}/reject
  @Test
  void testsBookingPatchReject() throws Exception {

    long bookingId = 2L;

    mockMvc.perform(patch("/api/v1/booking-requests/{id}/reject", bookingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value((int) bookingId))
        .andExpect(jsonPath("$.status").value("Rejected"));

    // edge cases
    // try to approve something that doesn't exist
    mockMvc.perform(patch("/api/v1/booking-requests/{id}/reject", 99999L)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
    // now if run again it should not pass, because it's already approved
    mockMvc.perform(patch("/api/v1/booking-requests/{id}/reject", bookingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest()); // 400

    // now if we try to reject should expect error
    mockMvc.perform(patch("/api/v1/booking-requests/{id}/approve", bookingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest()); // 400
  }

  // POST /api/v1/booking-requests
  @Test
  void testsBookingsPost() throws Exception {
    String requestBody = """
        {
          "adSpaceId": 1,
          "advertiserName": "Acme Corp new",
          "advertiserEmail": "contactnew@acme.com",
          "startDate": "2025-12-05",
          "endDate": "2025-12-10"
        }
        """;

    // happy case: status 200 + verify the fields from the seed
    mockMvc.perform(post("/api/v1/booking-requests")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.adSpaceId").value(1))
        .andExpect(jsonPath("$.advertiserName").value("Acme Corp new"))
        .andExpect(jsonPath("$.advertiserEmail").value("contactnew@acme.com"))
        .andExpect(jsonPath("$.startDate").value("2025-12-05"))
        .andExpect(jsonPath("$.endDate").value("2025-12-10"))
        .andExpect(jsonPath("$.status").value("Pending"))
        .andExpect(jsonPath("$.totalCost").value(1500));

    // the second request is the same as previous should fail because of the
    // bussiness constraints
    mockMvc.perform(post("/api/v1/booking-requests")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.message").value(
            containsString("Advertiser name or email already exists")));
    // missing startDate / endDate
    String missingDatesBody = """
        {
          "adSpaceId": 1,
          "advertiserName": "No Dates Corp",
          "advertiserEmail": "nodates@acme.com"
        }
        """;

    mockMvc.perform(post("/api/v1/booking-requests")
        .contentType(MediaType.APPLICATION_JSON)
        .content(missingDatesBody)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("startDate and endDate are required"));

    // dates in the past
    String pastDatesBody = """
        {
          "adSpaceId": 1,
          "advertiserName": "Past Corp",
          "advertiserEmail": "past@acme.com",
          "startDate": "2000-01-01",
          "endDate": "2000-01-05"
        }
        """;

    mockMvc.perform(post("/api/v1/booking-requests")
        .contentType(MediaType.APPLICATION_JSON)
        .content(pastDatesBody)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("startDate and endDate must both be in the future"));

    // endDate before startDate
    String endBeforeStartBody = """
        {
          "adSpaceId": 1,
          "advertiserName": "Wrong Range Corp",
          "advertiserEmail": "wrongrange@acme.com",
          "startDate": "2025-12-10",
          "endDate": "2025-12-05"
        }
        """;

    mockMvc.perform(post("/api/v1/booking-requests")
        .contentType(MediaType.APPLICATION_JSON)
        .content(endBeforeStartBody)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("endDate must be after startDate"));
  }
}
