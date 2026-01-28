package kwee.osmmapper.report.image;

import java.io.File;
import java.util.*;
import java.util.regex.*;

public class StraatFotoOrganisator {

  /**
   * Extraheert huisnummer uit mapnaam zoals "3871TD15"
   * 
   * @return het huisnummer (15) of null als format ongeldig is
   */
  public static Integer extractHuisnummer(String mapNaam) {
    // Patroon voor Nederlandse postcodes: 1234AB123
    Pattern patroon = Pattern.compile("^\\d{4}[A-Z]{2}(\\d+)$");
    Matcher matcher = patroon.matcher(mapNaam);

    if (matcher.matches()) {
      try {
        return Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException e) {
        System.err.println("Ongeldig huisnummer in map: " + mapNaam);
        return null;
      }
    } else {
      System.err.println("Ongeldig mapnaam formaat: " + mapNaam);
      System.err.println("Verwacht formaat: 1234AB123 (bijv. 3871TD15)");
      return null;
    }
  }

  /**
   * Extraheert postcode uit mapnaam zoals "3871TD15"
   * 
   * @return de postcode (3871TD) of null als format ongeldig is
   */
  public static String extractPostcode(String mapNaam) {
    Pattern patroon = Pattern.compile("^(\\d{4}[A-Z]{2})\\d+$");
    Matcher matcher = patroon.matcher(mapNaam);

    if (matcher.matches()) {
      return matcher.group(1);
    }
    return null;
  }

  /**
   * Organiseert alle foto's uit submappen op straatkant (oneven/even)
   * 
   * @param hoofdMap De hoofddirectory met alle adres-mappen
   * @return Map met "ONEVEN" en "EVEN" als keys, elk met lijst van foto's
   */
  public static Map<String, List<FotoInfo>> organiseerFotoPerStraatkant(File hoofdMap) {
    Map<String, List<FotoInfo>> resultaat = new HashMap<>();
    resultaat.put("ONEVEN", new ArrayList<>());
    resultaat.put("EVEN", new ArrayList<>());

    if (!hoofdMap.exists() || !hoofdMap.isDirectory()) {
      System.err.println("Map bestaat niet of is geen directory: " + hoofdMap.getPath());
      return resultaat;
    }

    System.out.println("Scannen van map: " + hoofdMap.getAbsolutePath());
    System.out.println("Zoeken naar mappen met formaat 1234AB123...");

    File[] submappen = hoofdMap.listFiles(File::isDirectory);
    if (submappen == null || submappen.length == 0) {
      System.err.println("Geen submappen gevonden in: " + hoofdMap.getPath());
      return resultaat;
    }

    int geldigeMappen = 0;
    int totaleFoto = 0;

    for (File map : submappen) {
      String mapNaam = map.getName();
      Integer huisnummer = extractHuisnummer(mapNaam);

      if (huisnummer != null) {
        geldigeMappen++;

        // Postcode extraheren
        String postcode = extractPostcode(mapNaam);

        // Alle afbeeldingen uit deze map vinden
        File[] fotoBestanden = map.listFiles((dir, naam) -> {
          String lowercase = naam.toLowerCase();
          return lowercase.endsWith(".jpg") || lowercase.endsWith(".jpeg") || lowercase.endsWith(".png")
              || lowercase.endsWith(".gif") || lowercase.endsWith(".bmp");
        });

        if (fotoBestanden != null && fotoBestanden.length > 0) {
          // Bepalen of oneven of even
          String straatkant = (huisnummer % 2 == 0) ? "EVEN" : "ONEVEN";
          List<FotoInfo> doelLijst = resultaat.get(straatkant);

          // Foto's toevoegen met metadata
          for (File foto : fotoBestanden) {
            FotoInfo fotoInfo = new FotoInfo(foto, postcode, huisnummer, mapNaam);
            doelLijst.add(fotoInfo);
            totaleFoto++;
          }

          System.out.println("  ✓ " + mapNaam + ": " + fotoBestanden.length + " foto's -> " + straatkant);
        } else {
          System.out.println("  ⚠ " + mapNaam + ": geen foto's gevonden");
        }
      }
    }

    // Sorteren op huisnummer binnen elke groep
    sorteerlijsten(resultaat);

    System.out.println("\nSamenvatting:");
    System.out.println("  Geldige mappen gevonden: " + geldigeMappen);
    System.out.println("  Totale foto's: " + totaleFoto);
    System.out.println("  Oneven huisnummers: " + resultaat.get("ONEVEN").size() + " foto's");
    System.out.println("  Even huisnummers: " + resultaat.get("EVEN").size() + " foto's");

    return resultaat;
  }

  /**
   * Helper class om foto metadata bij te houden
   */
  public static class FotoInfo {
    private File fotoBestand;
    private String postcode;
    private int huisnummer;
    private String mapNaam;

    public FotoInfo(File fotoBestand, String postcode, int huisnummer, String mapNaam) {
      this.fotoBestand = fotoBestand;
      this.postcode = postcode;
      this.huisnummer = huisnummer;
      this.mapNaam = mapNaam;
    }

    public File getFotoBestand() {
      return fotoBestand;
    }

    public String getPostcode() {
      return postcode;
    }

    public int getHuisnummer() {
      return huisnummer;
    }

    public String getMapNaam() {
      return mapNaam;
    }

    @Override
    public String toString() {
      return mapNaam + "/" + fotoBestand.getName();
    }
  }

  /**
   * Sorteert de lijsten op huisnummer (oplopend)
   */
  private static void sorteerlijsten(Map<String, List<FotoInfo>> groepen) {
    for (List<FotoInfo> lijst : groepen.values()) {
      lijst.sort(Comparator.comparingInt(FotoInfo::getHuisnummer));
    }
  }

  /**
   * Alternatieve methode die Map<String, List<File>> teruggeeft (voor
   * compatibiliteit met eerder voorbeeld)
   */
  public static Map<String, List<File>> organiseerFotoPerStraatkantSimpel(File hoofdMap) {
    Map<String, List<FotoInfo>> georganiseerd = organiseerFotoPerStraatkant(hoofdMap);

    // Converteren naar Map<String, List<File>>
    Map<String, List<File>> resultaat = new HashMap<>();
    resultaat.put("ONEVEN", new ArrayList<>());
    resultaat.put("EVEN", new ArrayList<>());

    for (Map.Entry<String, List<FotoInfo>> entry : georganiseerd.entrySet()) {
      for (FotoInfo fotoInfo : entry.getValue()) {
        resultaat.get(entry.getKey()).add(fotoInfo.getFotoBestand());
      }
    }

    return resultaat;
  }
}