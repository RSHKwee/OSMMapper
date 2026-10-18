package kwee.osmmapper.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    assertTrue((address.getStreet()).isEmpty());
    assertTrue((address.getHousenumber()).isEmpty());
    assertTrue((address.getPostalcode()).isEmpty());
    assertTrue((address.getCity()).isEmpty());
    assertTrue((address.getCountry()).isEmpty());
  }

  @Test
  void settersEnGettersWerken() {
    address.setStreet("Kerkstraat");
    address.setHousenumber("12a");
    address.setPostalcode("1234AB");
    address.setCity("Amsterdam");
    address.setCountry("Nederland");

    assertEquals("Kerkstraat", address.getStreet());
    assertEquals("12a", address.getHousenumber());
    assertEquals("1234AB", address.getPostalcode());
    assertEquals("Amsterdam", address.getCity());
    assertEquals("Nederland", address.getCountry());
  }

  @Test
  void toStringBevatAlleVeldenGescheidenDoorPuntkomma() {
    address.setStreet("Kerkstraat");
    address.setHousenumber("12a");
    address.setPostalcode("1234AB");
    address.setCity("Amsterdam");
    address.setCountry("Nederland");

    assertEquals("Kerkstraat; 12a; 1234AB; Amsterdam; Nederland", address.toString());
  }
}
