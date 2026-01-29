package sandbox;

import java.io.File;

import kwee.osmmapper.lib.OSMMapExcel;
import kwee.osmmapper.report.image.PostcodeWordGenerator;
import kwee.osmmapper.report.image.StraatFotoOrganisatorPerPostcode;

public class HoofdProgrammaWord {

  public static void main(String[] args) {
    System.out.println("=== WORD DOCUMENT GENERATOR ===\n");

    try {
      // PAS DIT PAD AAN
      File fotoHoofdmap = new File("D:\\Data\\Hoevelaken\\Fotos");
      OSMMapExcel osmMapExcel = new OSMMapExcel("D:\\Data\\Hoevelaken\\hoevelaken-contacten_202601220957_met_geo.xlsx"); // TODO
      osmMapExcel.ReadExcel();

      if (!fotoHoofdmap.exists()) {
        System.err.println("Map niet gevonden!");
        System.err.println("Controleer pad: " + fotoHoofdmap.getAbsolutePath());
        return;
      }

      // Organiseer foto's
      System.out.println("Foto's organiseren...");
      var georganiseerdeData = StraatFotoOrganisatorPerPostcode.organiseerPerPostcodeEnStraatkant(fotoHoofdmap,
          osmMapExcel);

      if (georganiseerdeData.isEmpty()) {
        System.out.println("Geen foto's gevonden!");
        return;
      }

      // Genereer document
      String tijdstip = java.time.LocalDateTime.now()
          .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));

      String docxPad = "StraatFotoOverzicht_" + tijdstip + ".docx";

      // Kies een van deze methodes:

      // Optie 1: Eenvoudige versie (aanbevolen)
      // PostcodeWordGenerator.genereerWordDocument(
      // georganiseerdeData, osmMapExcel, docxPad);

      // Optie 2: Tekstoverzicht (zonder foto's)
      // EenvoudigePostcodeWordGenerator.genereerTekstOverzicht(
      // georganiseerdeData, "TekstOverzicht_" + tijdstip + ".docx");

      // Optie 3: Complexe versie (als POI 5.x werkt)
      PostcodeWordGenerator.genereerWordDocument(georganiseerdeData, docxPad);

      System.out.println("\n✅ KLAAR!");
      System.out.println("Document: " + new File(docxPad).getAbsolutePath());

    } catch (Exception e) {
      System.err.println("\n❌ FOUT: " + e.getMessage());
      e.printStackTrace();

      System.out.println("\nProbleemoplossing:");
      System.out.println("1. Gebruik Apache POI versie 5.2.3 of hoger");
      System.out.println("2. Voeg alle dependencies toe aan pom.xml");
      System.out.println("3. Zorg dat de fotomap correcte mappen bevat");
    }
  }
}
