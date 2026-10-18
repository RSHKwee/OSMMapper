package kwee.osmmapper.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;

import org.junit.jupiter.api.Test;

class MemoContentTest {

  @Test
  void nieuwObjectIsLeeg() {
    MemoContent memo = new MemoContent();
    assertTrue(memo.isEmpty());
  }

  @Test
  void nietLeegNaSetVanEenVeld() {
    MemoContent memo = new MemoContent();
    memo.setStreet("Kerkstraat");
    assertFalse(memo.isEmpty());

    assertFalse(memo.isEmpty());
  }

  @Test
  void getAddressVultAlleVelden() {
    MemoContent memo = new MemoContent();
    memo.setStreet("Kerkstraat");
    memo.setHousenumber("12");
    memo.setPostcode("1234AB");
    memo.setCity("Amsterdam");
    memo.setCountry("Nederland");

    Address address = memo.getAddress();

    assertEquals("Kerkstraat", address.getStreet());
    assertEquals("12", address.getHousenumber());
    assertEquals("1234AB", address.getPostalcode());
    assertEquals("Amsterdam", address.getCity());
    assertEquals("Nederland", address.getCountry());
  }

  @Test
  void setColorMetStringGebruiktColorConverter() {
    MemoContent memo = new MemoContent();
    memo.setColor("RED");
    assertEquals(Color.RED, memo.getColor());

    memo.setColor("onbekend");
    assertNull(memo.getColor());
  }

  @Test
  void setClorWerkt() {
    MemoContent memo = new MemoContent();
    memo.setClor(Color.BLUE);
    assertEquals(Color.BLUE, memo.getColor());
  }

  @Test
  void standaardCoordinatenZijnUndefined() {
    MemoContent memo = new MemoContent();
    assertEquals(Const.c_LongLatUndefined, memo.getLatitude(), 1e-9);
    assertEquals(Const.c_LongLatUndefined, memo.getLongitude(), 1e-9);
  }

  @Test
  void pictureIdxIsNooitNull() {
    MemoContent memo = new MemoContent();
    assertNotNull(memo.getPicturIdx());
  }
}