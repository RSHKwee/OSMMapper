package kwee.osmmapper.report;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import kwee.logger.MyLogger;
import kwee.osmmapper.lib.MemoContent;
import kwee.osmmapper.lib.OSMMapExcel;
import kwee.osmmapper.report.image.PostcodePdfGenerator;
import kwee.osmmapper.report.image.StraatFotoOrganisatorPerPostcode;

public class ReportMenu {
  private static final Logger LOGGER = MyLogger.getLogger();

  /**
   * Genereert de geselecteerde rapporten.
   *
   * @param a_Tabname         naam van de tab (voor de bestandsnaam)
   * @param a_fotoHoofdmap    map met foto's
   * @param a_ExcelFile       Excel-bestand
   * @param a_ReportDirectory doelmap
   * @param a_types           welke rapporten te genereren; leeg = niets
   */
  static public void generateReport(String a_Tabname, File a_fotoHoofdmap, String a_ExcelFile, String a_ReportDirectory,
      Set<ReportType> a_types) {
    if (a_types == null || a_types.isEmpty()) {
      LOGGER.log(Level.INFO, "Geen rapporten geselecteerd, niets te doen.");
      return;
    }

    String datum = java.time.LocalDate.now().toString().replace("-", "");
    List<String> gegenereerd = new java.util.ArrayList<>();

    try {
      OSMMapExcel osmMapExcel = new OSMMapExcel(a_ExcelFile);
      osmMapExcel.ReadExcel();

      // ---- Optie A: per postcode + straatkant ----
      if (a_types.contains(ReportType.POSTCODE_PER_STRAATKANT)) {
        String pad = a_ReportDirectory + File.separator + a_Tabname + "_StraatOverzicht_Postcode_" + datum + ".pdf";
        genereerPerPostcodeEnStraatkant(a_fotoHoofdmap, osmMapExcel, pad);
        gegenereerd.add(pad + " (per postcode en straatkant)");
      }

      // ---- Optie B: alleen per postcode ----
      if (a_types.contains(ReportType.POSTCODE_EENVOUDIG)) {
        String pad = a_ReportDirectory + File.separator + a_Tabname + "_StraatOverzicht_Postcode_Eenvoudig_" + datum
            + ".pdf";
        genereerAlleenPerPostcode(a_fotoHoofdmap, osmMapExcel, pad);
        gegenereerd.add(pad + " (alleen per postcode)");
      }

      // ---- Memo overzicht ----
      if (a_types.contains(ReportType.MEMO_OVERZICHT)) {
        String pad = a_ReportDirectory + File.separator + a_Tabname + "_Memo_overzicht_" + datum + ".pdf";
        genereerMemoOverzicht(a_ExcelFile, pad);
        gegenereerd.add(pad + " (memo overzicht)");
      }

      LOGGER.log(Level.INFO, gegenereerd.size() + " PDF('s) gegenereerd:");
      for (String s : gegenereerd) {
        LOGGER.log(Level.INFO, " - " + s);
      }

    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "FOUT: " + e.getMessage());
    }
  }

  // ============================================================
  // Private helpers per rapport
  // ============================================================

  private static void genereerPerPostcodeEnStraatkant(File fotoHoofdmap, OSMMapExcel osmMapExcel, String pad)
      throws Exception {
    LOGGER.log(Level.INFO, "Rapport A: per postcode en straatkant");
    Map<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> data = StraatFotoOrganisatorPerPostcode
        .organiseerPerPostcodeEnStraatkant(fotoHoofdmap, osmMapExcel);

    if (data.isEmpty()) {
      LOGGER.log(Level.INFO, "Geen geldige mappen gevonden, rapport A overgeslagen.");
      return;
    }
    StraatFotoOrganisatorPerPostcode.toonStructuur(data);
    PostcodePdfGenerator.genereerPdfPerPostcode(data, osmMapExcel, pad);
  }

  private static void genereerAlleenPerPostcode(File fotoHoofdmap, OSMMapExcel osmMapExcel, String pad)
      throws Exception {
    LOGGER.log(Level.INFO, "Rapport B: alleen per postcode");
    Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> data = StraatFotoOrganisatorPerPostcode
        .organiseerAlleenPerPostcode(fotoHoofdmap, osmMapExcel);
    PostcodePdfGenerator.genereerPdfPerPostcodeEenvoudig(data, osmMapExcel, pad);
  }

  private static void genereerMemoOverzicht(String excelFile, String pad) throws Exception {
    LOGGER.log(Level.INFO, "Rapport C: memo overzicht");
    OSMMapExcel osmMapExcel = new OSMMapExcel(excelFile);
    List<MemoContent> memos = osmMapExcel.ReadExcel();

    if (memos == null || memos.isEmpty()) {
      LOGGER.log(Level.INFO, "Geen memo's om te rapporteren, rapport C overgeslagen.");
      return;
    }
    MemoPdfReporter.generateReport(memos, pad, "OSM Mapper - Memo overzicht");
  }
}