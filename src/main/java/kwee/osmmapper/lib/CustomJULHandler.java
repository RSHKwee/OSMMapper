package kwee.osmmapper.lib;

import java.util.logging.*;

public class CustomJULHandler extends Handler {

  @Override
  public void publish(LogRecord record) {
    // Verwerk LogRecord naar wens
    // System.out.println("JUL Handler: " + record.getLevel() + " - " + record.getMessage());
  }

  @Override
  public void flush() {
  }

  @Override
  public void close() throws SecurityException {
  }
}
