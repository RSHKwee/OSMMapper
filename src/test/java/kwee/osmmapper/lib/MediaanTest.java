package kwee.osmmapper.lib;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class MediaanTest {
  // Double.NaN
  @Test
  void nullOfLeegGeeftNaN() {
    assertEquals(Double.NaN, Mediaan.mediaanList(null), 1e-9);
    assertEquals(Double.NaN, Mediaan.mediaanList(Collections.emptyList()), 1e-9);
    assertEquals(Double.NaN, Mediaan.mediaanEenvoudig(null), 1e-9);
    assertEquals(Double.NaN, Mediaan.mediaanEenvoudig(Collections.emptyList()), 1e-9);
  }

  @Test
  void onevenAantalElementen() {
    List<Double> input = Arrays.asList(3.0, 1.0, 2.0);
    assertEquals(2.0, Mediaan.mediaanList(input), 1e-9);
    assertEquals(2.0, Mediaan.mediaanEenvoudig(input), 1e-9);
  }

  @Test
  void evenAantalElementen() {
    List<Double> input = Arrays.asList(1.0, 2.0, 3.0, 4.0);
    assertEquals(2.5, Mediaan.mediaanList(input), 1e-9);
    assertEquals(2.5, Mediaan.mediaanEenvoudig(input), 1e-9);
  }

  @Test
  void eenElement() {
    List<Double> input = Collections.singletonList(42.0);
    assertEquals(42.0, Mediaan.mediaanList(input), 1e-9);
    assertEquals(42.0, Mediaan.mediaanEenvoudig(input), 1e-9);
  }

  @Test
  void beideImplementatiesGevenZelfdeResultaat() {
    List<Double> input = Arrays.asList(5.0, 1.0, 9.0, 3.0, 7.0, 2.0);
    assertEquals(Mediaan.mediaanEenvoudig(input), Mediaan.mediaanList(input), 1e-9);
  }
}
