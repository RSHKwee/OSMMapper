package sandbox;

import java.io.File;
import java.util.List;
import java.util.Map;

import report.image.PostcodePdfGenerator;
import report.image.StraatFotoOrganisatorPerPostcode;

public class HoofdProgrammaPostcode {

  public static void main(String[] args) {
    System.out.println("=== FOTO ORGANISATOR PER POSTCODE ===\n");

    try {
      // 1. PAS DIT PAD AAN
      File fotoHoofdmap = new File("D:\\Data\\Hoevelaken\\Fotos");
      // File fotoHoofdmap = new File("/home/gebruiker/StraatFoto");

      if (!fotoHoofdmap.exists()) {
        System.err.println("FOUT: Map niet gevonden: " + fotoHoofdmap.getAbsolutePath());
        System.err.println("\nMaak deze map aan of pas het pad aan.");
        return;
      }

      // 2. Toon verwachte structuur
      System.out.println("Verwachte mapstructuur:");
      System.out.println(fotoHoofdmap.getPath() + "/");
      System.out.println("├── 3871TD15/    (postcode 3871TD, huisnummer 15)");
      System.out.println("├── 3871TD16/    (postcode 3871TD, huisnummer 16)");
      System.out.println("├── 3872AB1/     (postcode 3872AB, huisnummer 1)");
      System.out.println("└── 3872AB2/     (postcode 3872AB, huisnummer 2)\n");

      // 3. OPTIE A: Organiseer per postcode EN straatkant
      System.out.println("Optie A: Groeperen per postcode en straatkant");
      Map<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> dataMetStraatkant = StraatFotoOrganisatorPerPostcode
          .organiseerPerPostcodeEnStraatkant(fotoHoofdmap);

      if (dataMetStraatkant.isEmpty()) {
        System.out.println("\nGeen geldige mappen gevonden!");
        return;
      }

      // Toon structuur in console
      StraatFotoOrganisatorPerPostcode.toonStructuur(dataMetStraatkant);

      // 4. Genereer PDF per postcode
      System.out.println("\nPDF genereren...");
      String pdfPadA = "StraatOverzicht_Postcode_" + java.time.LocalDate.now().toString().replace("-", "") + ".pdf";

      PostcodePdfGenerator.genereerPdfPerPostcode(dataMetStraatkant, pdfPadA);

      // 5. OPTIE B: Alleen per postcode (zonder oneven/even scheiding)
      System.out.println("\n---\nOptie B: Alleen groeperen per postcode");
      Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> dataAlleenPostcode = StraatFotoOrganisatorPerPostcode
          .organiseerAlleenPerPostcode(fotoHoofdmap);

      String pdfPadB = "StraatOverzicht_Postcode_Eenvoudig_" + java.time.LocalDate.now().toString().replace("-", "")
          + ".pdf";

      PostcodePdfGenerator.genereerPdfPerPostcodeEenvoudig(dataAlleenPostcode, pdfPadB);

      // 6. Succesbericht
      System.out.println("\n✅ SUCCES!");
      System.out.println("Twee PDF's gegenereerd:");
      System.out.println("1. " + pdfPadA + " (gegroepeerd per postcode en straatkant)");
      System.out.println("2. " + pdfPadB + " (alleen per postcode, alle nummers op volgorde)");
      System.out.println("\nDe foto's zijn nu gegroepeerd per postcode en gesorteerd op huisnummer.");

    } catch (Exception e) {
      System.err.println("\n❌ FOUT: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Voorbeeld van hoe de output eruit ziet:
   */
  public static void toonVoorbeeldOutput() {
    System.out.println("\n=== VOORBEELD OUTPUT ===");
    System.out.println("\nMAP STRUCTUUR:");
    System.out.println("StraatFoto/");
    System.out.println("├── 3871TD15/");
    System.out.println("│   ├── huis.jpg");
    System.out.println("│   └── tuin.jpg");
    System.out.println("├── 3871TD16/");
    System.out.println("│   └── voorkant.jpg");
    System.out.println("├── 3872AB1/");
    System.out.println("│   ├── foto1.jpg");
    System.out.println("│   └── foto2.jpg");
    System.out.println("└── 3872AB2/");
    System.out.println("    └── gevel.jpg");

    System.out.println("\nORGANISATIE IN PDF:");
    System.out.println("=== POSTCODE 3871TD ===");
    System.out.println("Oneven huisnummers:");
    System.out.println("  15 - huis.jpg");
    System.out.println("  15 - tuin.jpg");
    System.out.println("Even huisnummers:");
    System.out.println("  16 - voorkant.jpg");

    System.out.println("\n=== POSTCODE 3872AB ===");
    System.out.println("Oneven huisnummers:");
    System.out.println("  1 - foto1.jpg");
    System.out.println("  1 - foto2.jpg");
    System.out.println("Even huisnummers:");
    System.out.println("  2 - gevel.jpg");
  }
}