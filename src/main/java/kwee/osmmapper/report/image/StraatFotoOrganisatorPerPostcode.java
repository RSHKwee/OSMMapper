package kwee.osmmapper.report.image;

import java.io.File;
import java.util.*;
import java.util.regex.*;

import kwee.osmmapper.lib.OSMMapExcel;

public class StraatFotoOrganisatorPerPostcode {

  /**
   * Extraheert postcode en huisnummer uit mapnaam zoals "3871TD15"
   * 
   * @return Map met "postcode" en "huisnummer" of null als ongeldig
   */
  public static Map<String, Object> extractAdresInfo(String mapNaam) {
    // Patroon: 4 cijfers + 2 letters + huisnummer
    Pattern patroon = Pattern.compile("^(\\d{4}[A-Z]{2})(\\d+)$");
    Matcher matcher = patroon.matcher(mapNaam);

    if (matcher.matches()) {
      try {
        Map<String, Object> info = new HashMap<>();
        info.put("postcode", matcher.group(1)); // "3871TD"
        info.put("huisnummer", Integer.parseInt(matcher.group(2))); // 15
        info.put("volledigeNaam", mapNaam); // "3871TD15"
        return info;
      } catch (NumberFormatException e) {
        return null;
      }
    }
    return null;
  }

  /**
   * Bepaalt of een huisnummer oneven of even is
   */
  public static String bepaalStraatkant(int huisnummer) {
    return (huisnummer % 2 == 0) ? "EVEN" : "ONEVEN";
  }

  /**
   * Organiseert foto's: Eerst per postcode, dan per straatkant, gesorteerd op
   * huisnummer
   * 
   * @param hoofdMap De hoofddirectory met adres-mappen
   * @return Map structuur: Postcode -> Straatkant -> Lijst van FotoInfo
   *         (gesorteerd op huisnummer)
   */
  public static Map<String, Map<String, List<FotoInfo>>> organiseerPerPostcodeEnStraatkant(File hoofdMap,
      OSMMapExcel osmMapExcel) {
    Map<String, Map<String, List<FotoInfo>>> resultaat = new TreeMap<>(); // TreeMap sorteert postcodes automatisch

    if (!hoofdMap.exists() || !hoofdMap.isDirectory()) {
      System.err.println("Map bestaat niet of is geen directory: " + hoofdMap.getPath());
      return resultaat;
    }

    System.out.println("Scannen van map: " + hoofdMap.getAbsolutePath());

    File[] submappen = hoofdMap.listFiles(File::isDirectory);
    if (submappen == null) {
      return resultaat;
    }

    int verwerkteMappen = 0;
    int totaalFoto = 0;

    for (File map : submappen) {
      String mapNaam = map.getName();
      Map<String, Object> adresInfo = extractAdresInfo(mapNaam);

      if (adresInfo != null) {
        verwerkteMappen++;

        String postcode = (String) adresInfo.get("postcode");
        int huisnummer = (int) adresInfo.get("huisnummer");
        String straatkant = bepaalStraatkant(huisnummer);
        String straatnaam = osmMapExcel.getStreet4ZipCode(postcode); // TODO

        // Haal alle foto's uit deze map
        File[] fotoBestanden = map.listFiles((dir, naam) -> {
          String lowercase = naam.toLowerCase();
          return lowercase.endsWith(".jpg") || lowercase.endsWith(".jpeg") || lowercase.endsWith(".png")
              || lowercase.endsWith(".gif") || lowercase.endsWith(".bmp");
        });

        if (fotoBestanden != null && fotoBestanden.length > 0) {
          // Zorg dat postcode entry bestaat
          resultaat.putIfAbsent(postcode, new HashMap<>());
          Map<String, List<FotoInfo>> postcodeMap = resultaat.get(postcode);

          // Zorg dat straatkant entry bestaat
          postcodeMap.putIfAbsent("ONEVEN", new ArrayList<>());
          postcodeMap.putIfAbsent("EVEN", new ArrayList<>());

          // Maak FotoInfo objecten en voeg toe aan juiste lijst
          List<FotoInfo> doelLijst = postcodeMap.get(straatkant);

          for (File foto : fotoBestanden) {
            FotoInfo fotoInfo = new FotoInfo(foto, postcode, huisnummer, straatnaam, mapNaam);
            doelLijst.add(fotoInfo);
            totaalFoto++;
          }

          System.out.printf("  ✓ %s: %d foto's -> %s/%s%n", mapNaam, fotoBestanden.length, postcode, straatkant);
        }
      }
    }

    // Sorteer alle lijsten op huisnummer
    sorteerlijstenPerPostcode(resultaat);

    // Toon samenvatting
    System.out.println("\n=== SAMENVATTING ===");
    System.out.println("Verwerkte mappen: " + verwerkteMappen);
    System.out.println("Totaal foto's: " + totaalFoto);
    System.out.println("Aantal postcodes: " + resultaat.size());

    for (Map.Entry<String, Map<String, List<FotoInfo>>> entry : resultaat.entrySet()) {
      String postcode = entry.getKey();
      int oneven = entry.getValue().get("ONEVEN").size();
      int even = entry.getValue().get("EVEN").size();
      System.out.printf("  %s: %d oneven, %d even%n", postcode, oneven, even);
    }

    return resultaat;
  }

  /**
   * Helper class voor foto metadata
   */
  public static class FotoInfo {
    private File fotoBestand;
    private String postcode;
    private int huisnummer;
    private String straatnaam;
    private String mapNaam;

    public FotoInfo(File fotoBestand, String postcode, int huisnummer, String straatnaam, String mapNaam) {
      this.fotoBestand = fotoBestand;
      this.postcode = postcode;
      this.huisnummer = huisnummer;
      this.straatnaam = straatnaam;
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

    public String getStraatnaam() {
      return straatnaam;
    }

    public String getMapNaam() {
      return mapNaam;
    }

    @Override
    public String toString() {
      return String.format("%s %s (%d) - %s", postcode, straatnaam, huisnummer, fotoBestand.getName());
    }
  }

  /**
   * Sorteert alle lijsten per postcode op huisnummer
   */
  private static void sorteerlijstenPerPostcode(Map<String, Map<String, List<FotoInfo>>> data) {
    for (Map<String, List<FotoInfo>> postcodeData : data.values()) {
      for (List<FotoInfo> lijst : postcodeData.values()) {
        lijst.sort(Comparator.comparingInt(FotoInfo::getHuisnummer));
      }
    }
  }

  /**
   * Alternatieve versie: groepeer alleen per postcode (zonder oneven/even
   * scheiding)
   */
  public static Map<String, List<FotoInfo>> organiseerAlleenPerPostcode(File hoofdMap, OSMMapExcel osmMapExcel) {
    Map<String, List<FotoInfo>> resultaat = new TreeMap<>();

    File[] submappen = hoofdMap.listFiles(File::isDirectory);
    if (submappen == null)
      return resultaat;

    for (File map : submappen) {
      Map<String, Object> adresInfo = extractAdresInfo(map.getName());
      if (adresInfo == null)
        continue;

      String postcode = (String) adresInfo.get("postcode");
      int huisnummer = (int) adresInfo.get("huisnummer");
      String straatnaam = osmMapExcel.getStreet4ZipCode(postcode); // TODO

      File[] fotoBestanden = map.listFiles(f -> f.getName().toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|bmp)$"));

      if (fotoBestanden != null) {
        resultaat.putIfAbsent(postcode, new ArrayList<>());

        for (File foto : fotoBestanden) {
          resultaat.get(postcode).add(new FotoInfo(foto, postcode, huisnummer, straatnaam, map.getName()));
        }
      }
    }

    // Sorteer per postcode op huisnummer
    for (List<FotoInfo> lijst : resultaat.values()) {
      lijst.sort(Comparator.comparingInt(FotoInfo::getHuisnummer));
    }

    return resultaat;
  }

  /**
   * Toon de georganiseerde structuur in console
   */
  public static void toonStructuur(Map<String, Map<String, List<FotoInfo>>> data) {
    System.out.println("\n=== GESTRUCTUREERD OVERZICHT ===");

    for (Map.Entry<String, Map<String, List<FotoInfo>>> postcodeEntry : data.entrySet()) {
      String postcode = postcodeEntry.getKey();
      Map<String, List<FotoInfo>> straatkantData = postcodeEntry.getValue();

      System.out.println("\n" + postcode + ":");

      System.out.println("  Oneven huisnummers:");
      for (FotoInfo foto : straatkantData.get("ONEVEN")) {
        System.out.println("    " + foto.getHuisnummer() + " - " + foto.getFotoBestand().getName());
      }

      System.out.println("  Even huisnummers:");
      for (FotoInfo foto : straatkantData.get("EVEN")) {
        System.out.println("    " + foto.getHuisnummer() + " - " + foto.getFotoBestand().getName());
      }
    }
  }
}