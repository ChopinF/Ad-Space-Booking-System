package generatik.backend.entities.adspace;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "ad_spaces")
public class AdSpace {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "name", length = 50, nullable = false, unique = true)
  private String name;

  @Column(name = "price_per_day", nullable = false)
  private Integer pricePerDay;

  @Column(name = "city", nullable = false)
  @Enumerated(EnumType.STRING)
  private City city;

  @Column(name = "address", nullable = false)
  private String address;

  @Column(name = "availability_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private AvailabilityStatus availabilityStatus;

  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private AdSpaceType type;

  public AdSpace() {
    // empty constructor for jpa
  }

  public AdSpace(
      String name,
      AdSpaceType type,
      Integer pricePerDay,
      City city,
      String address,
      AvailabilityStatus availabilityStatus) {
    this.name = name;
    this.type = type;
    this.pricePerDay = pricePerDay;
    this.city = city;
    this.address = address;
    this.availabilityStatus = availabilityStatus;
  }

  // getters setters cu lsp-ul preferat jdtls

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getPricePerDay() {
    return pricePerDay;
  }

  public void setPricePerDay(Integer pricePerDay) {
    this.pricePerDay = pricePerDay;
  }

  public City getCity() {
    return city;
  }

  public void setCity(City city) {
    this.city = city;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public AvailabilityStatus getAvailabilityStatus() {
    return availabilityStatus;
  }

  public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
    this.availabilityStatus = availabilityStatus;
  }

  public AdSpaceType getType() {
    return type;
  }

  public void setType(AdSpaceType type) {
    this.type = type;
  }
}
