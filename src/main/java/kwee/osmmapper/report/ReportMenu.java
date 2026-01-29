package kwee.osmmapper.report;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import kwee.logger.MyLogger;
import kwee.osmmapper.lib.OSMMapExcel;
import kwee.osmmapper.report.image.PostcodePdfGenerator;
import kwee.osmmapper.report.image.StraatFotoOrganisatorPerPostcode;

public class ReportMenu {
  private static final Logger LOGGER = MyLogger.getLogger();

  /**
   * 
   * @param a_fotoHoofdmap
   * @param a_ExcelFile
   * @param a_ReportDirectory
   */
  static public void generateReport(File a_fotoHoofdmap, String a_ExcelFile, String a_ReportDirectory) {
    try {
      // 3. OPTIE A: Organiseer per postcode EN straatkant
      OSMMapExcel osmMapExcel = new OSMMapExcel(a_ExcelFile);
      osmMapExcel.ReadExcel();

      LOGGER.log(Level.INFO, "Optie A: Groeperen per postcode en straatkant");
      Map<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> dataMetStraatkant = StraatFotoOrganisatorPerPostcode
          .organiseerPerPostcodeEnStraatkant(a_fotoHoofdmap, osmMapExcel);

      if (dataMetStraatkant.isEmpty()) {
        LOGGER.log(Level.INFO, "Geen geldige mappen gevonden!");
        return;
      }

      // Toon structuur in console
      StraatFotoOrganisatorPerPostcode.toonStructuur(dataMetStraatkant);

      // 4. Genereer PDF per postcode
      LOGGER.log(Level.INFO, "PDF genereren...");
      String pdfPadA = a_ReportDirectory + "\\" + "StraatOverzicht_Postcode_"
          + java.time.LocalDate.now().toString().replace("-", "") + ".pdf";
      PostcodePdfGenerator.genereerPdfPerPostcode(dataMetStraatkant, osmMapExcel, pdfPadA);

      // 5. OPTIE B: Alleen per postcode (zonder oneven/even scheiding)
      LOGGER.log(Level.INFO, "\n---\nOptie B: Alleen groeperen per postcode");
      Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> dataAlleenPostcode = StraatFotoOrganisatorPerPostcode
          .organiseerAlleenPerPostcode(a_fotoHoofdmap, osmMapExcel);

      String pdfPadB = a_ReportDirectory + "\\" + "StraatOverzicht_Postcode_Eenvoudig_"
          + java.time.LocalDate.now().toString().replace("-", "") + ".pdf";
      PostcodePdfGenerator.genereerPdfPerPostcodeEenvoudig(dataAlleenPostcode, osmMapExcel, pdfPadB);

      // 6. Succesbericht
      LOGGER.log(Level.INFO, "Twee PDF's gegenereerd:");
      LOGGER.log(Level.INFO, "1. " + pdfPadA + " (gegroepeerd per postcode en straatkant)");
      LOGGER.log(Level.INFO, "2. " + pdfPadB + " (alleen per postcode, alle nummers op volgorde)");
      LOGGER.log(Level.INFO, "De foto's zijn nu gegroepeerd per postcode en gesorteerd op huisnummer.");

    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "❌ FOUT: " + e.getMessage());
      // e.printStackTrace();
    }
  }
}