package kwee.osmmapper.report;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import kwee.logger.MyLogger;
import kwee.osmmapper.lib.MemoContent;
import kwee.osmmapper.lib.OSMMapExcel;
import kwee.osmmapper.report.image.PostcodePdfGenerator;
import kwee.osmmapper.report.image.StraatFotoOrganisatorPerPostcode;

public class ReportMenu {
  private static final Logger LOGGER = MyLogger.getLogger();

  // ============================================================
  // Bestaande functionaliteit: foto-overzicht per postcode
  // ============================================================
  static public void generateReport(String a_Tabname, File a_fotoHoofdmap, String a_ExcelFile,
      String a_ReportDirectory) {
    try {
      OSMMapExcel osmMapExcel = new OSMMapExcel(a_ExcelFile);
      osmMapExcel.ReadExcel();

      // Optie A: Groeperen per postcode en straatkant
      LOGGER.log(Level.INFO, "Optie A: Groeperen per postcode en straatkant");
      Map<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> dataMetStraatkant = StraatFotoOrganisatorPerPostcode
          .organiseerPerPostcodeEnStraatkant(a_fotoHoofdmap, osmMapExcel);

      if (dataMetStraatkant.isEmpty()) {
        LOGGER.log(Level.INFO, "Geen geldige mappen gevonden!");
        return;
      }

      StraatFotoOrganisatorPerPostcode.toonStructuur(dataMetStraatkant);
      LOGGER.log(Level.INFO, "PDF genereren...");
      String pdfPadA = a_ReportDirectory + "\\" + a_Tabname + "_StraatOverzicht_Postcode_"
          + java.time.LocalDate.now().toString().replace("-", "") + ".pdf";
      PostcodePdfGenerator.genereerPdfPerPostcode(dataMetStraatkant, osmMapExcel, pdfPadA);

      // Optie B: Alleen groeperen per postcode
      LOGGER.log(Level.INFO, "Optie B: Alleen groeperen per postcode");
      Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> dataAlleenPostcode = StraatFotoOrganisatorPerPostcode
          .organiseerAlleenPerPostcode(a_fotoHoofdmap, osmMapExcel);

      String pdfPadB = a_ReportDirectory + "\\" + a_Tabname + "_StraatOverzicht_Postcode_Eenvoudig_"
          + java.time.LocalDate.now().toString().replace("-", "") + ".pdf";
      PostcodePdfGenerator.genereerPdfPerPostcodeEenvoudig(dataAlleenPostcode, osmMapExcel, pdfPadB);

      // Memo report
      LOGGER.log(Level.INFO, "Memo rapport");
      String pdfPadMemo = a_ReportDirectory + "\\" + a_Tabname + "_OSM Mapper - Memo overzicht_"
          + java.time.LocalDate.now().toString().replace("-", "") + ".pdf";
      generateMemoReport(a_ExcelFile, pdfPadMemo);

      LOGGER.log(Level.INFO, "Drie PDF's gegenereerd:");
      LOGGER.log(Level.INFO, "1. " + pdfPadA + " (gegroepeerd per postcode en straatkant)");
      LOGGER.log(Level.INFO, "2. " + pdfPadB + " (alleen per postcode, alle nummers op volgorde)");
      LOGGER.log(Level.INFO, "3. " + pdfPadMemo + " (uitgangsdata)");
      LOGGER.log(Level.INFO, "De foto's zijn gegroepeerd per postcode en gesorteerd op huisnummer.");

    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "FOUT: " + e.getMessage());
    }
  }

  // ============================================================
  // Nieuw: memo-rapport op basis van List<MemoContent>
  // ============================================================
  /**
   * Genereert een PDF-rapport met alle MemoContent entries.
   *
   * @param a_ExcelFile       Excel bestand
   * @param a_ReportDirectory map waarin de PDF wordt opgeslagen
   */
  static public void generateMemoReport(String a_ExcelFile, String a_pdfmemo) {
    OSMMapExcel osmMapExcel = new OSMMapExcel(a_ExcelFile);
    List<MemoContent> a_memos = osmMapExcel.ReadExcel();

    try {
      if (a_memos == null || a_memos.isEmpty()) {
        LOGGER.log(Level.INFO, "Geen memo's om te rapporteren.");
        return;
      }

      LOGGER.log(Level.INFO, "Memo PDF genereren naar: " + a_pdfmemo);
      MemoPdfReporter.generateReport(a_memos, a_pdfmemo, "OSM Mapper - Memo overzicht");

      LOGGER.log(Level.INFO, "Memo PDF gegenereerd: " + a_pdfmemo);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "FOUT bij genereren memo-rapport: " + e.getMessage());
    }
  }
}