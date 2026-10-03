
package kwee.osmmapper.lib;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConstTest {

  @Test
  @DisplayName("compareDouble geeft true voor exact gelijke doubles")
  void compareDoubleEqual() {
    assertThat(Const.compareDouble(1.5, 1.5)).isTrue();
  }

  @Test
  @DisplayName("compareDouble geeft false voor ongelijke doubles")
  void compareDoubleNotEqual() {
    assertThat(Const.compareDouble(1.5, 1.6)).isFalse();
  }

  @Test
  @DisplayName("compareDouble werkt met de undefined constante")
  void compareDoubleUndefined() {
    assertThat(Const.compareDouble(Const.c_LongLatUndefined, Const.c_LongLatUndefined)).isTrue();
    assertThat(Const.compareDouble(52.0, Const.c_LongLatUndefined)).isFalse();
  }

  @Test
  @DisplayName("Constanten hebben de verwachte waarden")
  void constantValues() {
    assertThat(Const.c_LongLatUndefined).isEqualTo(-500.0);
    assertThat(Const.c_ZoomUndefined).isEqualTo(-1);
  }
}