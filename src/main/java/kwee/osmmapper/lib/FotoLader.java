package kwee.osmmapper.lib;

import java.nio.file.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FotoLader {

  /**
   * Haalt alle bestandsnamen op uit een directory (recursief of niet-recursief)
   * 
   * @param directoryPad Pad naar de foto directory
   * @param recursief    Of subdirectories ook doorzocht moeten worden
   * @param extensies    Lijst van toegestane extensies (jpg, jpeg, png, etc.)
   * @return Lijst van bestandsnamen (zonder pad)
   */
  public static List<String> haalBestandsnamen(String directoryPad, boolean recursief, String... extensies) {
    List<String> resultaat = new ArrayList<>();
    Path pad = Paths.get(directoryPad);

    try {
      // Gebruik Files.walk voor recursief of Files.list voor niet-recursief
      Stream<Path> stream = recursief ? Files.walk(pad) : Files.list(pad);

      resultaat = stream.filter(Files::isRegularFile) // Alleen bestanden, geen directories
          .filter(p -> heeftJuisteExtensie(p, extensies)) // Filter op extensie
          .map(p -> p.getFileName().toString()) // Haal alleen bestandsnaam
          .sorted() // Sorteer alfabetisch
          .collect(Collectors.toList());

      stream.close();

    } catch (IOException e) {
      System.err.println("Fout bij lezen directory: " + directoryPad);
      e.printStackTrace();
    }

    return resultaat;
  }

  private static boolean heeftJuisteExtensie(Path pad, String... extensies) {
    if (extensies.length == 0) {
      return true; // Alle bestanden als geen extensies gespecificeerd
    }

    String bestandsnaam = pad.getFileName().toString().toLowerCase();
    for (String ext : extensies) {
      if (bestandsnaam.endsWith("." + ext.toLowerCase())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Specifiek voor foto's: haal alleen beeldbestanden op
   */
  public static List<String> haalFotoBestandsnamen(String directoryPad, boolean recursief) {
    return haalBestandsnamen(directoryPad, recursief, "jpg", "jpeg", "png", "gif", "bmp");
  }
}
