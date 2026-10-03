package kwee.osmmapper.lib;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddressTest {

  private Address address;

  @BeforeEach
  void setUp() {
    address = new Address();
  }

  @Test
  void defaultsZijnLegeStrings() {
    assertThat(address.getStreet()).isEmpty();
    assertThat(address.getHousenumber()).isEmpty();
    assertThat(address.getPostalcode()).isEmpty();
    assertThat(address.getCity()).isEmpty();
    assertThat(address.getCountry()).isEmpty();
  }

  @Test
  void settersEnGettersWerken() {
    address.setStreet("Kerkstraat");
    address.setHousenumber("12a");
    address.setPostalcode("1234AB");
    address.setCity("Amsterdam");
    address.setCountry("Nederland");

    assertThat(address.getStreet()).isEqualTo("Kerkstraat");
    assertThat(address.getHousenumber()).isEqualTo("12a");
    assertThat(address.getPostalcode()).isEqualTo("1234AB");
    assertThat(address.getCity()).isEqualTo("Amsterdam");
    assertThat(address.getCountry()).isEqualTo("Nederland");
  }

  @Test
  void toStringBevatAlleVeldenGescheidenDoorPuntkomma() {
    address.setStreet("Kerkstraat");
    address.setHousenumber("12a");
    address.setPostalcode("1234AB");
    address.setCity("Amsterdam");
    address.setCountry("Nederland");

    assertThat(address.toString()).isEqualTo("Kerkstraat; 12a; 1234AB; Amsterdam; Nederland");
  }
}
