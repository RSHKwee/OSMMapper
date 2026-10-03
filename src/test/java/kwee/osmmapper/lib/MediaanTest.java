package kwee.osmmapper.lib;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class MediaanTest {

  @Test
  void nullOfLeegGeeftNaN() {
    assertThat(Mediaan.mediaanList(null)).isNaN();
    assertThat(Mediaan.mediaanList(Collections.emptyList())).isNaN();
    assertThat(Mediaan.mediaanEenvoudig(null)).isNaN();
    assertThat(Mediaan.mediaanEenvoudig(Collections.emptyList())).isNaN();
  }

  @Test
  void onevenAantalElementen() {
    List<Double> input = Arrays.asList(3.0, 1.0, 2.0);
    assertThat(Mediaan.mediaanList(input)).isEqualTo(2.0);
    assertThat(Mediaan.mediaanEenvoudig(input)).isEqualTo(2.0);
  }

  @Test
  void evenAantalElementen() {
    List<Double> input = Arrays.asList(1.0, 2.0, 3.0, 4.0);
    assertThat(Mediaan.mediaanList(input)).isEqualTo(2.5);
    assertThat(Mediaan.mediaanEenvoudig(input)).isEqualTo(2.5);
  }

  @Test
  void eenElement() {
    List<Double> input = Collections.singletonList(42.0);
    assertThat(Mediaan.mediaanList(input)).isEqualTo(42.0);
    assertThat(Mediaan.mediaanEenvoudig(input)).isEqualTo(42.0);
  }

  @Test
  void beideImplementatiesGevenZelfdeResultaat() {
    List<Double> input = Arrays.asList(5.0, 1.0, 9.0, 3.0, 7.0, 2.0);
    assertThat(Mediaan.mediaanList(input))
        .isEqualTo(Mediaan.mediaanEenvoudig(input));
  }
}
