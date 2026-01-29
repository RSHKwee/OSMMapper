package kwee.osmmapper.report.image;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PostcodeWordGenerator {

  /**
   * Genereert Word document gegroepeerd per postcode en straatkant
   */
  public static void genereerWordDocument(
      Map<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> data, String uitvoerPad)
      throws IOException {

    try (XWPFDocument document = new XWPFDocument()) {
      System.out.println("Word document genereren...");

      // Voeg elke postcode toe
      for (Map.Entry<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> postcodeEntry : data
          .entrySet()) {

        String postcode = postcodeEntry.getKey();
        Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> straatkantData = postcodeEntry.getValue();

        System.out.println("  Verwerken postcode: " + postcode);

        // Voeg postcode sectie toe
        voegPostcodeSectieToe(document, postcode, straatkantData);

        // Pagina-einde tussen postcodes
        voegPaginaEindeToe(document);
      }

      // Verwijder laatste pagina-einde
      if (document.getParagraphs().size() > 0) {
        document.removeBodyElement(document.getParagraphs().size() - 1);
      }

      // Opslaan
      try (FileOutputStream out = new FileOutputStream(uitvoerPad)) {
        document.write(out);
      }

      System.out.println("Word document opgeslagen als: " + uitvoerPad);

    } catch (Exception e) {
      System.err.println("Fout bij maken Word document: " + e.getMessage());
      e.printStackTrace();
      throw new IOException(e);
    }
  }

  /**
   * Voegt een complete postcode sectie toe aan het Word document
   */
  private static void voegPostcodeSectieToe(XWPFDocument document, String postcode,
      Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> straatkantData) {

    // 1. POSTCODE TITEL
    XWPFParagraph postcodeTitel = document.createParagraph();
    postcodeTitel.setAlignment(ParagraphAlignment.CENTER);

    XWPFRun titelRun = postcodeTitel.createRun();
    titelRun.setText("POSTCODE: " + postcode);
    titelRun.setBold(true);
    titelRun.setFontSize(24);
    titelRun.setColor("2E74B5");
    titelRun.addBreak();

    // 2. SAMENVATTING
    int totaalOneven = straatkantData.get("ONEVEN").size();
    int totaalEven = straatkantData.get("EVEN").size();

    XWPFParagraph samenvatting = document.createParagraph();
    samenvatting.setAlignment(ParagraphAlignment.LEFT);

    XWPFRun samenvattingRun = samenvatting.createRun();
    samenvattingRun.setText("Samenvatting:");
    samenvattingRun.setBold(true);
    samenvattingRun.setFontSize(12);
    samenvattingRun.addBreak();

    XWPFRun detailsRun = samenvatting.createRun();
    detailsRun.setText(String.format("• %d oneven huisnummers%n" + "• %d even huisnummers%n" + "• %d foto's totaal",
        totaalOneven, totaalEven, (totaalOneven + totaalEven)));
    detailsRun.setFontSize(11);
    detailsRun.addBreak();
    detailsRun.addBreak();

    // 3. ON EVEN HUISNUMMERS
    if (!straatkantData.get("ONEVEN").isEmpty()) {
      voegStraatkantSectieToe(document, postcode, "ON EVEN HUISNUMMERS", straatkantData.get("ONEVEN"));
    }

    // 4. EVEN HUISNUMMERS
    if (!straatkantData.get("EVEN").isEmpty()) {
      voegStraatkantSectieToe(document, postcode, "EVEN HUISNUMMERS", straatkantData.get("EVEN"));
    }
  }

  /**
   * Voegt een straatkant sectie toe voor een specifieke postcode
   */
  private static void voegStraatkantSectieToe(XWPFDocument document, String postcode, String straatkant,
      List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst) {

    // SECTIE TITEL
    XWPFParagraph sectieTitel = document.createParagraph();
    sectieTitel.setAlignment(ParagraphAlignment.LEFT);

    XWPFRun titelRun = sectieTitel.createRun();
    titelRun.setText(postcode + " - " + straatkant);
    titelRun.setBold(true);
    titelRun.setFontSize(16);
    titelRun.setColor("1F4E79");
    titelRun.addBreak();

    // Maak een tabel voor de foto's (2 kolommen)
    XWPFTable fotoTabel = document.createTable();

    // Configureer tabel layout
    fotoTabel.setWidth("100%");

    // Groepeer foto's per huisnummer
    Map<Integer, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> fotoPerHuisnummer = groepeerFotoPerHuisnummer(
        fotoLijst);

    int rijIndex = 0;

    // Verwerk elk huisnummer
    for (Map.Entry<Integer, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> entry : fotoPerHuisnummer.entrySet()) {

      int huisnummer = entry.getKey();
      List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoVoorDitHuisnummer = entry.getValue();

      // Nieuwe rij voor elk huisnummer
      XWPFTableRow huidigeRij = fotoTabel.createRow();
      rijIndex++;

      // Cel voor huisnummer label (linkerkolom)
      XWPFTableCell huisnummerCel = huidigeRij.getCell(0);
      huisnummerCel.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

      // Huisnummer label
      XWPFParagraph huisnummerParagraaf = huisnummerCel.addParagraph();
      huisnummerParagraaf.setAlignment(ParagraphAlignment.CENTER);

      XWPFRun huisnummerRun = huisnummerParagraaf.createRun();
      huisnummerRun.setText("Huisnummer " + huisnummer);
      huisnummerRun.setBold(true);
      huisnummerRun.setFontSize(14);
      huisnummerRun.setColor("C65911");

      // Cel voor foto's (rechterkolom)
      XWPFTableCell fotoCel = huidigeRij.getCell(1);
      fotoCel.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.TOP);

      // Voeg alle foto's voor dit huisnummer toe
      voegFotoToeAanCel(fotoCel, fotoVoorDitHuisnummer);

      // Voeg lege rij toe voor ruimte tussen huisnummers (als niet de laatste)
      if (rijIndex < fotoPerHuisnummer.size()) {
        XWPFTableRow legeRij = fotoTabel.createRow();
        legeRij.getCell(0).setText("");
        legeRij.getCell(1).setText("");

        // Stel minimale rijhoogte in voor ruimte
        legeRij.setHeight(100);
      }
    }

    // Ruimte na tabel
    XWPFParagraph ruimte = document.createParagraph();
    ruimte.createRun().addBreak();
  }

  /**
   * Groepeer foto's per huisnummer
   */
  private static Map<Integer, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> groepeerFotoPerHuisnummer(
      List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst) {

    Map<Integer, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> groepering = new TreeMap<>();

    for (StraatFotoOrganisatorPerPostcode.FotoInfo fotoInfo : fotoLijst) {
      groepering.computeIfAbsent(fotoInfo.getHuisnummer(), k -> new java.util.ArrayList<>()).add(fotoInfo);
    }

    return groepering;
  }

  /**
   * Voegt foto's toe aan een tabelcel
   */
  private static void voegFotoToeAanCel(XWPFTableCell cel, List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst) {

    if (fotoLijst.isEmpty())
      return;

    // Maak een tabel binnen de cel voor de foto's (max 2 per rij)
    XWPFTable fotoTabelInCel = cel.addParagraph().getDocument().createTable();
    fotoTabelInCel.setWidth("100%");

    int fotoIndex = 0;
    XWPFTableRow huidigeRij = null;

    for (StraatFotoOrganisatorPerPostcode.FotoInfo fotoInfo : fotoLijst) {
      // Nieuwe rij elke 2 foto's
      if (fotoIndex % 2 == 0) {
        huidigeRij = fotoTabelInCel.createRow();
      }

      if (huidigeRij != null) {
        XWPFTableCell fotoCel = huidigeRij.getCell(fotoIndex % 2);
        voegIndividueleFotoToe(fotoCel, fotoInfo);
      }

      fotoIndex++;
    }
  }

  /**
   * Voegt een individuele foto toe aan een cel
   */
  private static void voegIndividueleFotoToe(XWPFTableCell cel, StraatFotoOrganisatorPerPostcode.FotoInfo fotoInfo) {

    // Foto toevoegen
    try (FileInputStream fis = new FileInputStream(fotoInfo.getFotoBestand())) {
      XWPFParagraph fotoParagraaf = cel.addParagraph();
      fotoParagraaf.setAlignment(ParagraphAlignment.CENTER);

      XWPFRun fotoRun = fotoParagraaf.createRun();

      // Bepaal het juiste picture type
      int pictureType = bepaalPictureType(fotoInfo.getFotoBestand().getName());

      String fotoPath = fotoInfo.getFotoBestand().getPath();
      fotoRun.addPicture(fis, pictureType, fotoPath, Units.toEMU(150), // breedte
          Units.toEMU(100)); // hoogte

      fotoRun.addBreak();

      // Bestandsnaam onder foto
      XWPFRun naamRun = fotoParagraaf.createRun();
      String bestandsNaam = fotoInfo.getFotoBestand().getName();
      if (bestandsNaam.length() > 20) {
        bestandsNaam = bestandsNaam.substring(0, 17) + "...";
      }
      naamRun.setText(bestandsNaam);
      naamRun.setFontSize(8);
      naamRun.setColor("666666");

    } catch (Exception e) {
      // Alternatieve tekst als foto niet geladen kan worden
      XWPFParagraph foutParagraaf = cel.addParagraph();
      foutParagraaf.setAlignment(ParagraphAlignment.CENTER);

      XWPFRun foutRun = foutParagraaf.createRun();
      foutRun.setText("[Foto niet beschikbaar]");
      foutRun.setColor("FF0000");
      foutRun.setItalic(true);

      XWPFRun naamRun = foutParagraaf.createRun();
      naamRun.addBreak();
      naamRun.setText(fotoInfo.getFotoBestand().getName());
      naamRun.setFontSize(8);
    }
  }

  /**
   * Bepaalt het picture type op basis van bestandsextensie
   */
  private static int bepaalPictureType(String bestandsNaam) {
    String lowercase = bestandsNaam.toLowerCase();

    if (lowercase.endsWith(".jpg") || lowercase.endsWith(".jpeg")) {
      return XWPFDocument.PICTURE_TYPE_JPEG;
    } else if (lowercase.endsWith(".png")) {
      return XWPFDocument.PICTURE_TYPE_PNG;
    } else if (lowercase.endsWith(".gif")) {
      return XWPFDocument.PICTURE_TYPE_GIF;
    } else if (lowercase.endsWith(".bmp")) {
      return XWPFDocument.PICTURE_TYPE_BMP;
    } else {
      return XWPFDocument.PICTURE_TYPE_JPEG; // Standaard
    }
  }

  /**
   * Voegt een pagina-einde toe
   */
  private static void voegPaginaEindeToe(XWPFDocument document) {
    XWPFParagraph pageBreak = document.createParagraph();
    XWPFRun run = pageBreak.createRun();
    run.addBreak(BreakType.PAGE);
  }

  /**
   * Eenvoudigere versie: alle foto's per postcode in een lijst
   */
  public static void genereerWordDocumentEenvoudig(Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> data,
      String uitvoerPad) throws IOException {

    try (XWPFDocument document = new XWPFDocument()) {

      System.out.println("Word document genereren (eenvoudige versie)...");

      // Voeg elke postcode toe
      for (Map.Entry<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> entry : data.entrySet()) {

        String postcode = entry.getKey();
        List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst = entry.getValue();

        System.out.println("  Verwerken postcode: " + postcode);

        // Postcode titel
        XWPFParagraph titel = document.createParagraph();
        titel.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titelRun = titel.createRun();
        titelRun.setText("POSTCODE: " + postcode);
        titelRun.setBold(true);
        titelRun.setFontSize(20);
        titelRun.setColor("2E74B5");
        titelRun.addBreak();

        // Toon aantal foto's
        XWPFParagraph info = document.createParagraph();
        XWPFRun infoRun = info.createRun();
        infoRun.setText(fotoLijst.size() + " foto's, gesorteerd op huisnummer");
        infoRun.setFontSize(11);
        infoRun.setItalic(true);
        infoRun.addBreak();
        infoRun.addBreak();

        // Maak een tabel voor de foto's (3 kolommen)
        XWPFTable tabel = document.createTable();
        tabel.setWidth("100%");

        int kolomIndex = 0;
        XWPFTableRow huidigeRij = tabel.getRow(0);

        for (StraatFotoOrganisatorPerPostcode.FotoInfo fotoInfo : fotoLijst) {
          // Nieuwe rij elke 3 foto's
          if (kolomIndex > 0 && kolomIndex % 3 == 0) {
            huidigeRij = tabel.createRow();
          }

          XWPFTableCell cel = huidigeRij.getCell(kolomIndex % 3);
          voegFotoMetHuisnummerToe(cel, fotoInfo);

          kolomIndex++;
        }

        // Pagina-einde na elke postcode (behalve de laatste)
        if (data.entrySet().size() > 1) {
          XWPFParagraph pageBreak = document.createParagraph();
          pageBreak.createRun().addBreak(BreakType.PAGE);
        }
      }

      // Opslaan
      try (FileOutputStream out = new FileOutputStream(uitvoerPad)) {
        document.write(out);
      }

      System.out.println("Word document opgeslagen als: " + uitvoerPad);

    } catch (Exception e) {
      System.err.println("Fout: " + e.getMessage());
      e.printStackTrace();
      throw new IOException(e);
    }
  }

  /**
   * Voegt een foto toe met huisnummer label
   */
  private static void voegFotoMetHuisnummerToe(XWPFTableCell cel, StraatFotoOrganisatorPerPostcode.FotoInfo fotoInfo) {

    cel.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.TOP);

    XWPFParagraph paragraph = cel.addParagraph();
    paragraph.setAlignment(ParagraphAlignment.CENTER);

    // Huisnummer boven foto
    XWPFRun huisnummerRun = paragraph.createRun();
    huisnummerRun.setText("Nr. " + fotoInfo.getHuisnummer());
    huisnummerRun.setBold(true);
    huisnummerRun.setFontSize(10);
    huisnummerRun.addBreak();

    // Foto
    try (FileInputStream fis = new FileInputStream(fotoInfo.getFotoBestand())) {
      XWPFRun fotoRun = paragraph.createRun();

      int pictureType = bepaalPictureType(fotoInfo.getFotoBestand().getName());
      fotoRun.addPicture(fis, pictureType, fotoInfo.getFotoBestand().getName(), Units.toEMU(120), Units.toEMU(80));

      fotoRun.addBreak();

    } catch (Exception e) {
      XWPFRun foutRun = paragraph.createRun();
      foutRun.setText("[Geen foto]");
      foutRun.setColor("999999");
      foutRun.setItalic(true);
      foutRun.addBreak();
    }

    // Bestandsnaam
    XWPFRun naamRun = paragraph.createRun();
    String naam = fotoInfo.getFotoBestand().getName();
    if (naam.length() > 15) {
      naam = naam.substring(0, 12) + "...";
    }
    naamRun.setText(naam);
    naamRun.setFontSize(7);
    naamRun.setColor("666666");
  }
}