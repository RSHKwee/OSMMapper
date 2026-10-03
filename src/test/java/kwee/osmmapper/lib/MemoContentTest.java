package kwee.osmmapper.lib;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;

import org.junit.jupiter.api.Test;

class MemoContentTest {

  @Test
  void nieuwObjectIsLeeg() {
    MemoContent memo = new MemoContent();
    assertThat(memo.isEmpty()).isTrue();
  }

  @Test
  void nietLeegNaSetVanEenVeld() {
    MemoContent memo = new MemoContent();
    memo.setStreet("Kerkstraat");
    assertThat(memo.isEmpty()).isFalse();
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

    assertThat(address.getStreet()).isEqualTo("Kerkstraat");
    assertThat(address.getHousenumber()).isEqualTo("12");
    assertThat(address.getPostalcode()).isEqualTo("1234AB");
    assertThat(address.getCity()).isEqualTo("Amsterdam");
    assertThat(address.getCountry()).isEqualTo("Nederland");
  }

  @Test
  void setColorMetStringGebruiktColorConverter() {
    MemoContent memo = new MemoContent();
    memo.setColor("RED");
    assertThat(memo.getColor()).isEqualTo(Color.RED);

    memo.setColor("onbekend");
    assertThat(memo.getColor()).isNull();
  }

  @Test
  void setClorWerkt() {
    MemoContent memo = new MemoContent();
    memo.setClor(Color.BLUE);
    assertThat(memo.getColor()).isEqualTo(Color.BLUE);
  }

  @Test
  void standaardCoordinatenZijnUndefined() {
    MemoContent memo = new MemoContent();
    assertThat(memo.getLatitude()).isEqualTo(Const.c_LongLatUndefined);
    assertThat(memo.getLongitude()).isEqualTo(Const.c_LongLatUndefined);
  }

  @Test
  void pictureIdxIsNooitNull() {
    MemoContent memo = new MemoContent();
    assertThat(memo.getPicturIdx()).isNotNull().isEmpty();
  }
}