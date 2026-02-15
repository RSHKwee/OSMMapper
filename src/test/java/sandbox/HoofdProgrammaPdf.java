package sandbox;

import java.io.File;
import java.util.Scanner;

import kwee.osmmapper.lib.OSMMapExcel;
import kwee.osmmapper.report.image.FlexibelePdfGenerator;
import kwee.osmmapper.report.image.StraatFotoOrganisatorPerPostcode;

public class HoofdProgrammaPdf {

  public static void main(String[] args) {
    System.out.println("=== PDF GENERATOR VOOR STRAATFOTO'S ===\n");

    try (Scanner scanner = new Scanner(System.in)) {

      // 1. Kies layout
      System.out.println("Hoeveel foto's per rij?");
      System.out.println("1. 1 foto per rij   (zeer groot)");
      System.out.println("2. 2 foto's per rij (aanbevolen)");
      System.out.println("3. 3 foto's per rij (compact)");
      System.out.println("4. 4 foto's per rij (klein)");

      int keuze = vraagKeuze(scanner, 1, 4);

      // 2. Vraag pad
      System.out.print("\nPad naar foto map: ");
      String pad = scanner.nextLine().trim();
      if (pad.isEmpty()) {
        pad = "D:\\Data\\Hoevelaken\\Fotos";
      }

      OSMMapExcel osmMapExcel = new OSMMapExcel("D:\\Data\\Hoevelaken\\hoevelaken-contacten_202601220957_met_geo.xlsx"); // TODO
      osmMapExcel.ReadExcel();

      File fotoMap = new File(pad);
      if (!fotoMap.exists()) {
        System.err.println("ERROR: Map niet gevonden: " + fotoMap.getAbsolutePath());
        return;
      }

      // 3. Organiseer foto's
      System.out.println("\nFoto's organiseren...");
      var georganiseerdeData = StraatFotoOrganisatorPerPostcode.organiseerPerPostcodeEnStraatkant(fotoMap, osmMapExcel);

      if (georganiseerdeData.isEmpty()) {
        System.out.println("Geen foto's gevonden!");
        return;
      }

      // 4. Toon samenvatting
      int totaalFoto = 0;
      for (var postcodeData : georganiseerdeData.values()) {
        totaalFoto += postcodeData.get("ONEVEN").size() + postcodeData.get("EVEN").size();
      }

      System.out.println("\nGevonden: " + totaalFoto + " foto's in " + georganiseerdeData.size() + " postcodes");

      // 5. Configureer PDF
      FlexibelePdfGenerator.PdfConfig config = new FlexibelePdfGenerator.PdfConfig();
      config.fotoPerRij = keuze;

      // 6. Genereer PDF
      String tijdstip = java.time.LocalDateTime.now()
          .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

      String pdfBestand = "D:\\Data\\Hoevelaken\\" + String.format("StraatFoto_%dperRij_%s.pdf", keuze, tijdstip);

      System.out.println("\nPDF genereren...");
      FlexibelePdfGenerator.genereerPdf(georganiseerdeData, pdfBestand, config);

      // 7. Toon resultaat
      System.out.println("\n✅ KLAAR!");
      System.out.println("Bestand: " + new File(pdfBestand).getAbsolutePath());
      System.out.println("\nLayout overzicht:");
      toonLayoutOverzicht(keuze);

    } catch (Exception e) {
      System.err.println("\n❌ ERROR: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static int vraagKeuze(Scanner scanner, int min, int max) {
    while (true) {
      System.out.print("\nJouw keuze (" + min + "-" + max + "): ");
      try {
        int keuze = Integer.parseInt(scanner.nextLine().trim());
        if (keuze >= min && keuze <= max) {
          return keuze;
        }
      } catch (NumberFormatException e) {
        // ignore
      }
      System.out.println("Ongeldige keuze. Probeer opnieuw.");
    }
  }

  private static void toonLayoutOverzicht(int fotoPerRij) {
    switch (fotoPerRij) {
    case 1:
      System.out.println("┌─────────────────────────────┐");
      System.out.println("│        [GROTE FOTO]         │");
      System.out.println("│     ≈ 14 cm breed          │");
      System.out.println("│     ≈ 10.5 cm hoog         │");
      System.out.println("│     ±4 foto's per pagina   │");
      System.out.println("└─────────────────────────────┘");
      break;

    case 2:
      System.out.println("┌─────────────────────────────┐");
      System.out.println("│   [FOTO]      [FOTO]       │");
      System.out.println("│   ≈ 8.8 cm breed           │");
      System.out.println("│   ≈ 6.6 cm hoog            │");
      System.out.println("│   ±8 foto's per pagina     │");
      System.out.println("└─────────────────────────────┘");
      break;

    case 3:
      System.out.println("┌─────────────────────────────┐");
      System.out.println("│ [FOTO] [FOTO] [FOTO]       │");
      System.out.println("│ ≈ 6.3 cm breed             │");
      System.out.println("│ ≈ 4.7 cm hoog              │");
      System.out.println("│ ±12 foto's per pagina      │");
      System.out.println("└─────────────────────────────┘");
      break;

    case 4:
      System.out.println("┌─────────────────────────────┐");
      System.out.println("│[F][F][F][F]                │");
      System.out.println("│≈ 4.7 cm breed              │");
      System.out.println("│≈ 3.5 cm hoog               │");
      System.out.println("│±16 foto's per pagina       │");
      System.out.println("└─────────────────────────────┘");
      break;
    }
  }
}