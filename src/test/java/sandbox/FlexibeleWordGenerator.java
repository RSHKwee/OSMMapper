package sandbox;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import kwee.osmmapper.report.image.StraatFotoOrganisatorPerPostcode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FlexibeleWordGenerator {

  // Configuratie-instellingen
  public static class Config {
    public int fotoPerRij = 2; // 1, 2, 3 of 4 foto's per rij
    public boolean paginaPerPostcode = true;
    public boolean toonHuisnummer = true;
    public boolean toonBestandsnaam = true;
    public int fotoBreedteEMU = 300; // Standaard breedte voor 2 per rij
    public int fotoHoogteEMU = 200; // Standaard hoogte voor 2 per rij
    public String documentTitel = "Straatfoto Overzicht";
  }

  /**
   * Genereert Word document met instelbare layout
   */
  public static void genereerWordDocument(
      Map<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> data, String uitvoerPad, Config config)
      throws IOException {

    // Pas foto grootte aan op basis van aantal per rij
    pasFotoGrootteAan(config);

    try (XWPFDocument document = new XWPFDocument()) {
      System.out.println("Word document genereren...");
      System.out.println("Configuratie: " + config.fotoPerRij + " foto's per rij");
      System.out.println("Foto grootte: " + Units.toPoints(config.fotoBreedteEMU) + "pt x "
          + Units.toPoints(config.fotoHoogteEMU) + "pt");

      // Voeg voorpagina toe
      voegVoorpaginaToe(document, config, data);

      // Voeg elke postcode toe
      int postcodeNummer = 1;
      for (Map.Entry<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> postcodeEntry : data
          .entrySet()) {

        String postcode = postcodeEntry.getKey();
        Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> straatkantData = postcodeEntry.getValue();

        System.out.println("  Verwerken postcode " + postcodeNummer + ": " + postcode);

        // Voeg postcode sectie toe
        voegPostcodeSectieToe(document, postcode, postcodeNummer, straatkantData, config);

        // Pagina-einde tussen postcodes (indien gewenst)
        if (config.paginaPerPostcode && postcodeNummer < data.size()) {
          voegPaginaEindeToe(document);
        }

        postcodeNummer++;
      }

      // Opslaan
      try (FileOutputStream out = new FileOutputStream(uitvoerPad)) {
        document.write(out);
      }

      System.out.println("✅ Word document opgeslagen als: " + uitvoerPad);

    } catch (Exception e) {
      System.err.println("❌ Fout bij maken Word document: " + e.getMessage());
      e.printStackTrace();
      throw new IOException(e);
    }
  }

  /**
   * Past foto grootte aan op basis van aantal per rij
   */
  private static void pasFotoGrootteAan(Config config) {
    switch (config.fotoPerRij) {
    case 1:
      // 1 foto per rij = heel groot
      config.fotoBreedteEMU = Units.toEMU(400); // ~14 cm
      config.fotoHoogteEMU = Units.toEMU(300); // ~10.5 cm
      break;
    case 2:
      // 2 foto's per rij = groot
      config.fotoBreedteEMU = Units.toEMU(250); // ~8.8 cm
      config.fotoHoogteEMU = Units.toEMU(188); // ~6.6 cm
      break;
    case 3:
      // 3 foto's per rij = medium
      config.fotoBreedteEMU = Units.toEMU(180); // ~6.3 cm
      config.fotoHoogteEMU = Units.toEMU(135); // ~4.7 cm
      break;
    case 4:
      // 4 foto's per rij = klein
      config.fotoBreedteEMU = Units.toEMU(135); // ~4.7 cm
      config.fotoHoogteEMU = Units.toEMU(101); // ~3.5 cm
      break;
    default:
      // Standaard 2 per rij
      config.fotoPerRij = 2;
      config.fotoBreedteEMU = Units.toEMU(250);
      config.fotoHoogteEMU = Units.toEMU(188);
    }
  }

  /**
   * Voegt een voorpagina toe
   */
  private static void voegVoorpaginaToe(XWPFDocument document, Config config,
      Map<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> data) {

    XWPFParagraph titel = document.createParagraph();
    titel.setAlignment(ParagraphAlignment.CENTER);

    XWPFRun titelRun = titel.createRun();
    titelRun.setText(config.documentTitel);
    titelRun.setBold(true);
    titelRun.setFontSize(28);
    titelRun.setColor("1F4E79");
    titelRun.addBreak();
    titelRun.addBreak();

    // Configuratie info
    XWPFRun configRun = titel.createRun();
    configRun.setText("Configuratie: " + config.fotoPerRij + " foto's per rij");
    configRun.setFontSize(14);
    configRun.setItalic(true);
    configRun.addBreak();
    configRun.addBreak();

    // Datum
    XWPFRun datumRun = titel.createRun();
    datumRun.setText("Gegenereerd op: " + java.time.LocalDate.now());
    datumRun.setFontSize(12);
    datumRun.addBreak();
    datumRun.addBreak();
    datumRun.addBreak();

    // Statistieken
    int totaalPostcodes = data.size();
    int totaalFoto = 0;
    for (Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> postcodeData : data.values()) {
      totaalFoto += postcodeData.get("ONEVEN").size() + postcodeData.get("EVEN").size();
    }

    XWPFParagraph stats = document.createParagraph();
    stats.setAlignment(ParagraphAlignment.CENTER);

    XWPFRun statsRun = stats.createRun();
    statsRun.setText(String.format("Totaal %d postcodes%n" + "Totaal %d foto's", totaalPostcodes, totaalFoto));
    statsRun.setFontSize(14);
    statsRun.setBold(true);

    // Pagina-einde na voorpagina
    voegPaginaEindeToe(document);
  }

  /**
   * Voegt een postcode sectie toe
   */
  private static void voegPostcodeSectieToe(XWPFDocument document, String postcode, int postcodeNummer,
      Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> straatkantData, Config config) {

    // POSTCODE TITEL
    XWPFParagraph postcodeTitel = document.createParagraph();
    postcodeTitel.setAlignment(ParagraphAlignment.LEFT);

    XWPFRun titelRun = postcodeTitel.createRun();
    titelRun.setText(postcodeNummer + ". POSTCODE: " + postcode);
    titelRun.setBold(true);
    titelRun.setFontSize(20);
    titelRun.setColor("2E74B5");
    titelRun.addBreak();

    // SAMENVATTING
    int totaalOneven = straatkantData.get("ONEVEN").size();
    int totaalEven = straatkantData.get("EVEN").size();

    XWPFParagraph samenvatting = document.createParagraph();

    XWPFRun samenvattingRun = samenvatting.createRun();
    samenvattingRun.setText(String.format("Oneven: %d foto's | Even: %d foto's | Totaal: %d foto's", totaalOneven,
        totaalEven, (totaalOneven + totaalEven)));
    samenvattingRun.setFontSize(11);
    samenvattingRun.setItalic(true);
    samenvattingRun.addBreak();
    samenvattingRun.addBreak();

    // ON EVEN HUISNUMMERS
    if (!straatkantData.get("ONEVEN").isEmpty()) {
      voegStraatkantSectieToe(document, "ON EVEN HUISNUMMERS", straatkantData.get("ONEVEN"), config);
    }

    // EVEN HUISNUMMERS
    if (!straatkantData.get("EVEN").isEmpty()) {
      voegStraatkantSectieToe(document, "EVEN HUISNUMMERS", straatkantData.get("EVEN"), config);
    }
  }

  /**
   * Voegt een straatkant sectie toe
   */
  private static void voegStraatkantSectieToe(XWPFDocument document, String straatkant,
      List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst, Config config) {

    if (fotoLijst.isEmpty())
      return;

    // SECTIE TITEL
    XWPFParagraph sectieTitel = document.createParagraph();

    XWPFRun titelRun = sectieTitel.createRun();
    titelRun.setText(straatkant + " (" + fotoLijst.size() + " foto's):");
    titelRun.setBold(true);
    titelRun.setFontSize(14);
    titelRun.setColor("C65911");
    titelRun.addBreak();

    // GROEPEER PER HUISNUMMER
    Map<Integer, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> perHuisnummer = groepeerFotoPerHuisnummer(fotoLijst);

    for (Map.Entry<Integer, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> entry : perHuisnummer.entrySet()) {

      int huisnummer = entry.getKey();
      List<StraatFotoOrganisatorPerPostcode.FotoInfo> huisnummerFoto = entry.getValue();

      // HUISNUMMER SUBTITEL (optioneel)
      if (config.toonHuisnummer) {
        XWPFParagraph huisnummerTitel = document.createParagraph();

        XWPFRun huisnummerRun = huisnummerTitel.createRun();
        huisnummerRun.setText("• Huisnummer " + huisnummer + " (" + huisnummerFoto.size() + " foto's)");
        huisnummerRun.setBold(true);
        huisnummerRun.setFontSize(12);
        huisnummerRun.addBreak();
      }

      // MAAK TABEL VOOR FOTO'S
      maakFotoTabel(document, huisnummerFoto, config);

      // RUIMTE TUSSEN HUISNUMMERS
      document.createParagraph().createRun().addBreak();
    }

    // RUIMTE NA SECTIE
    document.createParagraph().createRun().addBreak();
  }

  /**
   * Maakt een tabel met foto's volgens configuratie
   */
  private static void maakFotoTabel(XWPFDocument document, List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst,
      Config config) {

    // Bepaal aantal rijen nodig
    int rijenNodig = (int) Math.ceil((double) fotoLijst.size() / config.fotoPerRij);

    // Maak tabel met juiste aantal rijen
    XWPFTable tabel = document.createTable(rijenNodig, config.fotoPerRij);
    tabel.setWidth("100%");

    // Configureer kolombreedtes
    for (int kolom = 0; kolom < config.fotoPerRij; kolom++) {
      XWPFTableCell cel = tabel.getRow(0).getCell(kolom);
      cel.setWidth(String.valueOf(100 / config.fotoPerRij) + "%");
    }

    // Vul de tabel met foto's
    int fotoIndex = 0;
    for (int rij = 0; rij < rijenNodig; rij++) {
      XWPFTableRow tabelRij = tabel.getRow(rij);

      for (int kolom = 0; kolom < config.fotoPerRij; kolom++) {
        if (fotoIndex < fotoLijst.size()) {
          StraatFotoOrganisatorPerPostcode.FotoInfo fotoInfo = fotoLijst.get(fotoIndex);
          XWPFTableCell cel = tabelRij.getCell(kolom);
          voegFotoToeAanCel(cel, fotoInfo, config);
          fotoIndex++;
        } else {
          // Lege cel voor de rest van de rij
          tabelRij.getCell(kolom).setText("");
        }
      }
    }
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
   * Voegt een foto toe aan een cel
   */
  private static void voegFotoToeAanCel(XWPFTableCell cel, StraatFotoOrganisatorPerPostcode.FotoInfo fotoInfo,
      Config config) {

    cel.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.TOP);

    XWPFParagraph paragraph = cel.addParagraph();
    paragraph.setAlignment(ParagraphAlignment.CENTER);

    // HUISNUMMER BOVEN FOTO (optioneel)
    if (config.toonHuisnummer) {
      XWPFRun huisnummerRun = paragraph.createRun();
      huisnummerRun.setText("Nr. " + fotoInfo.getHuisnummer());
      huisnummerRun.setBold(true);

      // Pas fontgrootte aan op basis van foto grootte
      if (config.fotoPerRij == 1) {
        huisnummerRun.setFontSize(14);
      } else if (config.fotoPerRij == 2) {
        huisnummerRun.setFontSize(12);
      } else {
        huisnummerRun.setFontSize(10);
      }

      huisnummerRun.addBreak();
    }

    // FOTO
    try (FileInputStream fis = new FileInputStream(fotoInfo.getFotoBestand())) {
      XWPFRun fotoRun = paragraph.createRun();

      int pictureType = bepaalPictureType(fotoInfo.getFotoBestand().getName());
      fotoRun.addPicture(fis, pictureType, fotoInfo.getFotoBestand().getName(), config.fotoBreedteEMU,
          config.fotoHoogteEMU);

      fotoRun.addBreak();

    } catch (Exception e) {
      // Plaatshouder als foto niet geladen kan worden
      XWPFRun foutRun = paragraph.createRun();
      foutRun.setText("[FOTO]");
      foutRun.setColor("FF0000");
      foutRun.setBold(true);
      foutRun.addBreak();

      // Teken een kader als plaatshouder
      foutRun.setText("───────");
      foutRun.addBreak();
    }

    // BESTANDSNAAM ONDER FOTO (optioneel)
    if (config.toonBestandsnaam) {
      XWPFRun naamRun = paragraph.createRun();
      String bestandsNaam = fotoInfo.getFotoBestand().getName();

      // Kort de naam in als nodig
      int maxLengte;
      if (config.fotoPerRij == 1) {
        maxLengte = 30;
        naamRun.setFontSize(9);
      } else if (config.fotoPerRij == 2) {
        maxLengte = 20;
        naamRun.setFontSize(8);
      } else {
        maxLengte = 15;
        naamRun.setFontSize(7);
      }

      if (bestandsNaam.length() > maxLengte) {
        bestandsNaam = bestandsNaam.substring(0, maxLengte - 3) + "...";
      }

      naamRun.setText(bestandsNaam);
      naamRun.setColor("666666");
    }
  }

  /**
   * Bepaalt het picture type
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
      return XWPFDocument.PICTURE_TYPE_JPEG;
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
   * Helper om EMU naar punten te converteren voor debugging
   */
  private static double emuNaarCm(long emu) {
    return emu / 360000.0; // 1 cm = 360000 EMU
  }
}
