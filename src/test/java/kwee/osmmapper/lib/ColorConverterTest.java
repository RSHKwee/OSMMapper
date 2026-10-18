package kwee.osmmapper.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.awt.Color;

import org.junit.jupiter.api.Test;

class ColorConverterTest {

  @Test
  void nullOfLeegGeeftNull() {
    assertNull(ColorConverter.getColor(null));
    assertNull(ColorConverter.getColor(""));
    assertNull(ColorConverter.getColor("   "));
  }

  @Test
  void standaardKleurWordtGevonden() {
    assertEquals(Color.RED, ColorConverter.getColor("RED"));
    assertEquals(Color.RED, ColorConverter.getColor("red"));
    assertEquals(Color.RED, ColorConverter.getColor("Red"));
  }

  @Test
  void underscoresEnSpatiesWordenGenegeerd() {
    assertEquals(Color.LIGHT_GRAY, ColorConverter.getColor("LIGHT GRAY"));
    assertEquals(Color.LIGHT_GRAY, ColorConverter.getColor("LIGHT_GRAY"));
    assertEquals(Color.LIGHT_GRAY, ColorConverter.getColor("light-gray"));
  }

  @Test
  void extraWebKleurenWerken() {
    assertEquals(new Color(128, 0, 0), ColorConverter.getColor("MAROON"));
    assertEquals(Color.GRAY, ColorConverter.getColor("GREY"));
    assertEquals(Color.MAGENTA, ColorConverter.getColor("FUCHSIA"));
    assertEquals(Color.CYAN, ColorConverter.getColor("AQUA"));
  }

  @Test
  void onbekendeKleurGeeftNull() {
    assertNull(ColorConverter.getColor("NIETBESTAANDEKLEUR"));
  }
}