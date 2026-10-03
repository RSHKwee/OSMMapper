package kwee.osmmapper.lib;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.jupiter.api.Test;

class CustomJULHandlerTest {

  @Test
  void publishGooitGeenExceptie() {
    CustomJULHandler handler = new CustomJULHandler();
    LogRecord record = new LogRecord(Level.INFO, "test");
    assertThatCode(() -> handler.publish(record)).doesNotThrowAnyException();
  }

  @Test
  void flushEnCloseGooienGeenExceptie() {
    CustomJULHandler handler = new CustomJULHandler();
    assertThatCode(handler::flush).doesNotThrowAnyException();
    assertThatCode(handler::close).doesNotThrowAnyException();
  }
}
