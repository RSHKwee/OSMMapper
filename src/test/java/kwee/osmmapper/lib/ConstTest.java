
package kwee.osmmapper.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConstTest {

  @Test
  @DisplayName("compareDouble geeft true voor exact gelijke doubles")
  void compareDoubleEqual() {
    assertTrue(Const.compareDouble(1.5, 1.5));
  }

  @Test
  @DisplayName("compareDouble geeft false voor ongelijke doubles")
  void compareDoubleNotEqual() {
    assertFalse(Const.compareDouble(1.5, 1.6));
  }

  @Test
  @DisplayName("compareDouble werkt met de undefined constante")
  void compareDoubleUndefined() {
    assertTrue(Const.compareDouble(Const.c_LongLatUndefined, Const.c_LongLatUndefined));
    assertFalse(Const.compareDouble(52.0, Const.c_LongLatUndefined));
  }

  @Test
  @DisplayName("Constanten hebben de verwachte waarden")
  void constantValues() {
    assertEquals(-500.0, Const.c_LongLatUndefined, 1e-9);
    assertEquals(-1, Const.c_ZoomUndefined);
  }
}