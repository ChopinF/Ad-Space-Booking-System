package generatik.backend.entities.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;

import generatik.backend.entities.adspace.AdSpace;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity(name = "bookings")
@Table(name = "bookings", indexes = {
    @Index(name = "idx_bookings_status", columnList = "status"),
    @Index(name = "idx_bookings_ad_space_id", columnList = "ad_space_id")
})
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "advertiser_name", length = 20, nullable = false, unique = true)
  private String advertiserName;

  @Email
  @NotBlank
  @Column(name = "advertiser_email", nullable = false, unique = true)
  private String advertiserEmail;

  @Column(name = "start_date", columnDefinition = "DATE")
  private LocalDate startDate;

  @Column(name = "end_date", columnDefinition = "DATE")
  private LocalDate endDate;

  @Column(name = "created_at", columnDefinition = "TIMESTAMP")
  private LocalDateTime createdAt;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private Status status;

  @Column(name = "total_cost")
  private Integer totalCost;

  @ManyToOne(optional = false)
  @JoinColumn(name = "ad_space_id", nullable = false)
  private AdSpace adSpace;

  public Booking() {
    // 'empty constructor" nevoie pentru jpa
  }

  @ConsistentDateParameters // validarea pentru startDate si endDate ca ambele sa fie in viitor + endDate sa
                            // fie dupa startDate
  public Booking(
      String name,
      String email,
      LocalDate startDate,
      LocalDate endDate,
      Integer totalCost) {
    this.advertiserName = name;
    this.advertiserEmail = email;
    this.startDate = startDate;
    this.endDate = endDate;
    this.totalCost = totalCost;
    this.status = Status.Pending;// default value "pending"
    this.createdAt = LocalDateTime.now(); // current default time
  }

  // auto generat cu cel mai bun lsp - jdtls
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAdvertiserName() {
    return advertiserName;
  }

  public void setAdvertiserName(String advertiserName) {
    this.advertiserName = advertiserName;
  }

  public String getAdvertiserEmail() {
    return advertiserEmail;
  }

  public void setAdvertiserEmail(String advertiserEmail) {
    this.advertiserEmail = advertiserEmail;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Integer getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(Integer totalCost) {
    this.totalCost = totalCost;
  }

  public AdSpace getAdSpace() {
    return adSpace;
  }

  public void setAdSpace(AdSpace adSpace) {
    this.adSpace = adSpace;
  }

}
