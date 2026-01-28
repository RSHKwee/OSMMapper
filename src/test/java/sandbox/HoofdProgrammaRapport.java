package sandbox;

import java.io.File;
import java.util.Map;

import kwee.osmmapper.lib.OSMMapExcel;
import kwee.osmmapper.report.image.StraatFotoOrganisator;
import kwee.osmmapper.report.image.StraatFotoPdfGenerator;

import java.util.List;

public class HoofdProgrammaRapport {

  public static void main(String[] args) {
    System.out.println("=== STRAATFOTO ORGANISATOR ===\n");

    try {
      // 1. PAS DIT PAD AAN NAAR JOUW FOTO MAP
      File fotoHoofdmap = new File("D:\\Data\\Hoevelaken\\Fotos");

      // Controleer of map bestaat
      if (!fotoHoofdmap.exists()) {
        System.err.println("FOUT: Map niet gevonden!");
        System.err.println("Gezocht in: " + fotoHoofdmap.getAbsolutePath());
        System.err.println("\nMaak deze map aan of pas het pad aan:");
        System.err.println("1. Open HoofdProgramma.java");
        System.err.println("2. Zoek regel: File fotoHoofdmap = new File(...)");
        System.err.println("3. Verander het pad naar jouw map");
        return;
      }

      // 2. Toon map structuur voorbeeld
      System.out.println("Verwachte mapstructuur:");
      System.out.println(fotoHoofdmap.getPath() + "/");
      System.out.println("├── 3871TD15/");
      System.out.println("│   ├── voordeur.jpg");
      System.out.println("│   └── tuin.jpg");
      System.out.println("├── 3871TD16/");
      System.out.println("│   └── huis.jpg");
      System.out.println("└── etc...\n");

      // 3. Organiseer foto's per straatkant
      OSMMapExcel osmMapExcel = new OSMMapExcel("D:\\Data\\Hoevelaken\\hoevelaken-contacten_202601220957_met_geo.xlsx"); // TODO
      System.out.println("Foto's organiseren...");
      Map<String, List<StraatFotoOrganisator.FotoInfo>> georganiseerdeFoto = StraatFotoOrganisator
          .organiseerFotoPerStraatkant(fotoHoofdmap, osmMapExcel);

      // 4. Controleer of er foto's zijn gevonden
      int totaalOneven = georganiseerdeFoto.get("ONEVEN").size();
      int totaalEven = georganiseerdeFoto.get("EVEN").size();

      if (totaalOneven + totaalEven == 0) {
        System.out.println("\nGEEN FOTO'S GEVONDEN!");
        System.out.println("Controleer of:");
        System.out.println("1. Mapnamen het juiste formaat hebben (1234AB123)");
        System.out.println("2. Er foto's in de mappen staan (.jpg, .png, etc.)");
        System.out.println("3. Het juiste pad is opgegeven");
        return;
      }

      // 5. Toon details
      System.out.println("\nDetails per straatkant:");

      System.out.println("Oneven huisnummers (" + totaalOneven + " foto's):");
      for (StraatFotoOrganisator.FotoInfo foto : georganiseerdeFoto.get("ONEVEN")) {
        System.out.println("  " + foto.getMapNaam() + " - " + foto.getFotoBestand().getName());
        if (georganiseerdeFoto.get("ONEVEN").indexOf(foto) >= 4) {
          System.out.println("  ... en " + (totaalOneven - 5) + " meer");
          break;
        }
      }

      System.out.println("\nEven huisnummers (" + totaalEven + " foto's):");
      for (StraatFotoOrganisator.FotoInfo foto : georganiseerdeFoto.get("EVEN")) {
        System.out.println("  " + foto.getMapNaam() + " - " + foto.getFotoBestand().getName());
        if (georganiseerdeFoto.get("EVEN").indexOf(foto) >= 4) {
          System.out.println("  ... en " + (totaalEven - 5) + " meer");
          break;
        }
      }

      // 6. Genereer PDF
      System.out.println("\nPDF genereren...");
      String pdfPad = "StraatOverzicht_" + java.time.LocalDate.now().toString().replace("-", "") + ".pdf";

      StraatFotoPdfGenerator.genereerPdfMetMetadata(georganiseerdeFoto, pdfPad);

      // 7. Succesbericht
      System.out.println("\n✅ SUCCES!");
      System.out.println("PDF gegenereerd: " + new File(pdfPad).getAbsolutePath());
      System.out.println("\nOpen het PDF bestand om het resultaat te bekijken.");
      System.out.println("Oneven huisnummers staan eerst, gevolgd door even huisnummers.");

    } catch (Exception e) {
      System.err.println("\n❌ FOUT OPGETREDEN:");
      e.printStackTrace();

      System.out.println("\nProbleemoplossing:");
      System.out.println("1. Zorg dat Apache PDFBox is geïnstalleerd");
      System.out.println("2. Java moet versie 8 of hoger zijn");
      System.out.println("3. Check of je schrijfrechten hebt in de huidige map");
      System.out.println("4. Zorg dat foto's JPG of PNG formaat hebben");
    }
  }

  /**
   * Voorbeeld om te testen met dummy data
   */
  public static void testMetVoorbeeldData() {
    System.out.println("Test met voorbeeld data...");

    // Maak een tijdelijke map structuur voor testing
    File testMap = new File("test_foto");
    testMap.mkdir();

    // Hier zou je test mappen kunnen maken
    // Maar in jouw geval heb je echte data

    System.out.println("Plaats je foto's in mappen met namen zoals:");
    System.out.println("  3871TD15, 3871TD16, 3871TD17, etc.");
    System.out.println("Elke map moet foto's bevatten (.jpg, .png)");
  }
}
