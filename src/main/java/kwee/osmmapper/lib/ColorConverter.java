package kwee.osmmapper.lib;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import kwee.logger.MyLogger;

public class ColorConverter {
  private static final Logger LOGGER = MyLogger.getLogger();
  private static final Map<String, Color> COMPLETE_COLOR_MAP = new HashMap<>();

  static {
    // Laad alle standaard Color constants via reflectie
    Field[] fields = Color.class.getFields();

    for (Field field : fields) {
      if (field.getType() == Color.class) {
        try {
          Color color = (Color) field.get(null);
          String name = field.getName().toUpperCase();
          COMPLETE_COLOR_MAP.put(name, color);

          // Voeg ook variaties toe
          COMPLETE_COLOR_MAP.put(name.replace("_", ""), color);
        } catch (IllegalAccessException e) {
          // Negeer velden die niet toegankelijk zijn
        }
      }
    }

    // Voeg handmatig extra kleuren toe die je wilt ondersteunen
    addAdditionalColors();
  }

  private static void addAdditionalColors() {
    // Web kleuren
    COMPLETE_COLOR_MAP.put("MAROON", new Color(128, 0, 0));
    COMPLETE_COLOR_MAP.put("PURPLE", new Color(128, 0, 128));
    COMPLETE_COLOR_MAP.put("OLIVE", new Color(128, 128, 0));
    COMPLETE_COLOR_MAP.put("TEAL", new Color(0, 128, 128));
    COMPLETE_COLOR_MAP.put("NAVY", new Color(0, 0, 128));
    COMPLETE_COLOR_MAP.put("LIME", new Color(0, 255, 0));
    COMPLETE_COLOR_MAP.put("FUCHSIA", Color.MAGENTA);
    COMPLETE_COLOR_MAP.put("AQUA", Color.CYAN);

    // Alternatieve spelling
    COMPLETE_COLOR_MAP.put("GREY", Color.GRAY);
    COMPLETE_COLOR_MAP.put("LIGHTGREY", Color.LIGHT_GRAY);
    COMPLETE_COLOR_MAP.put("DARKGREY", Color.DARK_GRAY);
    COMPLETE_COLOR_MAP.put("SLATEGREY", new Color(112, 128, 144));
  }

  public static Color getColor(String colorName) {
    if (colorName == null || colorName.trim().isEmpty()) {
      return null;
    }

    String key = colorName.trim().toUpperCase().replace(" ", "_").replace("-", "_");

    return COMPLETE_COLOR_MAP.get(key);
  }

  public static void printAllColors() {
    LOGGER.log(Level.INFO, "Beschikbare kleuren (" + COMPLETE_COLOR_MAP.size() + "):");
    COMPLETE_COLOR_MAP.keySet().stream().sorted().forEach(key -> LOGGER.log(Level.INFO, "  " + key));
  }

}
