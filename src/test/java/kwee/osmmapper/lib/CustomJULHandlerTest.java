package kwee.osmmapper.lib;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.jupiter.api.Test;

class CustomJULHandlerTest {

  @Test
  void publishGooitGeenExceptie() {
    CustomJULHandler handler = new CustomJULHandler();
    LogRecord record = new LogRecord(Level.INFO, "test");
    assertDoesNotThrow(() -> handler.publish(record));
  }

  @Test
  void flushEnCloseGooienGeenExceptie() {
    CustomJULHandler handler = new CustomJULHandler();
    assertDoesNotThrow(handler::flush);
    assertDoesNotThrow(handler::close);
  }
}