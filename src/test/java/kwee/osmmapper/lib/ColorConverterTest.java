package kwee.osmmapper.lib;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;

import org.junit.jupiter.api.Test;

class ColorConverterTest {

  @Test
  void nullOfLeegGeeftNull() {
    assertThat(ColorConverter.getColor(null)).isNull();
    assertThat(ColorConverter.getColor("")).isNull();
    assertThat(ColorConverter.getColor("   ")).isNull();
  }

  @Test
  void standaardKleurWordtGevonden() {
    assertThat(ColorConverter.getColor("RED")).isEqualTo(Color.RED);
    assertThat(ColorConverter.getColor("red")).isEqualTo(Color.RED);
    assertThat(ColorConverter.getColor("Red")).isEqualTo(Color.RED);
  }

  @Test
  void underscoresEnSpatiesWordenGenegeerd() {
    assertThat(ColorConverter.getColor("LIGHT GRAY")).isEqualTo(Color.LIGHT_GRAY);
    assertThat(ColorConverter.getColor("LIGHT_GRAY")).isEqualTo(Color.LIGHT_GRAY);
    assertThat(ColorConverter.getColor("light-gray")).isEqualTo(Color.LIGHT_GRAY);
  }

  @Test
  void extraWebKleurenWerken() {
    assertThat(ColorConverter.getColor("MAROON")).isEqualTo(new Color(128, 0, 0));
    assertThat(ColorConverter.getColor("GREY")).isEqualTo(Color.GRAY);
    assertThat(ColorConverter.getColor("FUCHSIA")).isEqualTo(Color.MAGENTA);
    assertThat(ColorConverter.getColor("AQUA")).isEqualTo(Color.CYAN);
  }

  @Test
  void onbekendeKleurGeeftNull() {
    assertThat(ColorConverter.getColor("NIETBESTAANDEKLEUR")).isNull();
  }
}