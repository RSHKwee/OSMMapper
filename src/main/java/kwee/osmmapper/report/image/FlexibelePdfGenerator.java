package kwee.osmmapper.report.image;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FlexibelePdfGenerator {

  // Configuratie klasse
  public static class PdfConfig {
    public int fotoPerRij = 2; // 1, 2, 3 of 4 foto's per rij
    public boolean toonHuisnummer = true;
    public boolean toonBestandsnaam = true;
    public boolean toonPostcode = true;
    public boolean groepeerPerPostcode = true;
    public float marge = 50; // marge in punten
    public float ruimteTussenFoto = 15; // ruimte tussen foto's in punten
    public float ruimteOnderTitel = 10; // ruimte onder titel in punten

    // Automatisch berekend op basis van fotoPerRij
    public float fotoBreedte = 200; // in punten
    public float fotoHoogte = 150; // in punten
  }

  /**
   * Genereert PDF met instelbare layout
   */
  public static void genereerPdf(Map<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> data,
      String uitvoerPad, PdfConfig config) throws IOException {

    // Bereken foto grootte op basis van configuratie
    berekenFotoGrootte(config);

    try (PDDocument document = new PDDocument()) {
      System.out.println("PDF genereren...");
      System.out.println("Configuratie: " + config.fotoPerRij + " foto's per rij");
      System.out.printf("Foto grootte: %.1f x %.1f punten%n", config.fotoBreedte, config.fotoHoogte);

      if (config.groepeerPerPostcode) {
        // Gegroepeerd per postcode
        genereerPdfPerPostcode(document, data, config);
      } else {
        // Alle foto's op volgorde (alleen oneven/even scheiding)
        genereerPdfSimpel(document, data, config);
      }

      document.save(uitvoerPad);
      System.out.println("✅ PDF opgeslagen als: " + uitvoerPad);

    } catch (IOException e) {
      System.err.println("❌ Fout bij maken PDF: " + e.getMessage());
      throw e;
    }
  }

  /**
   * Bereken optimale foto grootte op basis van aantal per rij
   */
  private static void berekenFotoGrootte(PdfConfig config) {
    float beschikbareBreedte = PDRectangle.A4.getWidth() - (2 * config.marge);
    float beschikbareHoogte = PDRectangle.A4.getHeight() - (2 * config.marge);

    // Bereken breedte per foto
    float breedtePerFoto = (beschikbareBreedte - ((config.fotoPerRij - 1) * config.ruimteTussenFoto))
        / config.fotoPerRij;

    // Houd aspect ratio van 4:3 aan (standaard foto formaat)
    float hoogtePerFoto = breedtePerFoto * 0.75f;

    // Zorg dat foto's niet te groot worden voor de pagina
    float maxHoogte = beschikbareHoogte / 5; // Max 5 rijen per pagina

    if (hoogtePerFoto > maxHoogte) {
      hoogtePerFoto = maxHoogte;
      breedtePerFoto = hoogtePerFoto / 0.75f;
    }

    config.fotoBreedte = breedtePerFoto;
    config.fotoHoogte = hoogtePerFoto;

    System.out.printf("Berekende foto grootte: %.1f x %.1f punten%n", breedtePerFoto, hoogtePerFoto);
  }

  /**
   * Genereert PDF gegroepeerd per postcode
   */
  private static void genereerPdfPerPostcode(PDDocument document,
      Map<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> data, PdfConfig config)
      throws IOException {

    int postcodeNummer = 1;

    for (Map.Entry<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> postcodeEntry : data
        .entrySet()) {

      String postcode = postcodeEntry.getKey();
      Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> straatkantData = postcodeEntry.getValue();

      System.out.println("  Verwerken postcode " + postcodeNummer + ": " + postcode);

      // Start nieuwe pagina voor elke postcode
      PDPage pagina = new PDPage(PDRectangle.A4);
      document.addPage(pagina);

      PDPageContentStream cs = new PDPageContentStream(document, pagina);
      try {
        // Postcode titel
        voegPostcodeTitelToe(cs, postcode, postcodeNummer, config);

        float y = PDRectangle.A4.getHeight() - config.marge - 50;

        // Oneven huisnummers
        if (!straatkantData.get("ONEVEN").isEmpty()) {
          y = voegStraatkantSectieToe(cs, document, pagina, "ON EVEN HUISNUMMERS", straatkantData.get("ONEVEN"), y,
              config, postcode);
        }

        // Even huisnummers
        if (!straatkantData.get("EVEN").isEmpty()) {
          y = voegStraatkantSectieToe(cs, document, pagina, "EVEN HUISNUMMERS", straatkantData.get("EVEN"), y, config,
              postcode);
        }
      } finally {
        cs.close();
      }

      postcodeNummer++;
    }
  }

  /**
   * Voegt postcode titel toe
   */
  private static void voegPostcodeTitelToe(PDPageContentStream cs, String postcode, int postcodeNummer,
      PdfConfig config) throws IOException {

    cs.beginText();
    cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
    cs.newLineAtOffset(config.marge, PDRectangle.A4.getHeight() - config.marge - 30);
    cs.showText(postcodeNummer + ". POSTCODE: " + postcode);
    cs.endText();

    // Samenvatting onder titel
    cs.beginText();
    cs.setFont(PDType1Font.HELVETICA, 10);
    cs.newLineAtOffset(config.marge, PDRectangle.A4.getHeight() - config.marge - 55);
    cs.showText("Foto's gegroepeerd op huisnummer - " + config.fotoPerRij + " per rij");
    cs.endText();
  }

  /**
   * Voegt een straatkant sectie toe
   */
  private static float voegStraatkantSectieToe(PDPageContentStream cs, PDDocument document, PDPage pagina,
      String straatkantTitel, List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst, float startY, PdfConfig config,
      String postcode) throws IOException {

    if (fotoLijst.isEmpty())
      return startY;

    float y = startY - 40; // Ruimte voor sectie titel

    // Sectie titel
    cs.beginText();
    cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
    cs.newLineAtOffset(config.marge, y);
    cs.showText(straatkantTitel + " (" + fotoLijst.size() + " foto's)");
    cs.endText();

    y -= 25;

    // Groepeer foto's per huisnummer
    Map<Integer, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> perHuisnummer = groepeerFotoPerHuisnummer(fotoLijst);

    // Verwerk elk huisnummer
    for (Map.Entry<Integer, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> entry : perHuisnummer.entrySet()) {

      int huisnummer = entry.getKey();
      List<StraatFotoOrganisatorPerPostcode.FotoInfo> huisnummerFoto = entry.getValue();

      // Check of we nieuwe pagina nodig hebben
      float benodigdeRuimte = config.fotoHoogte + 40; // Foto + tekst ruimte
      if (y < config.marge + benodigdeRuimte) {
        // Nieuwe pagina maken
        cs.close();
        pagina = new PDPage(PDRectangle.A4);
        document.addPage(pagina);
        cs = new PDPageContentStream(document, pagina);
        y = PDRectangle.A4.getHeight() - config.marge - 40;

        // Sectie titel op nieuwe pagina
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
        cs.newLineAtOffset(config.marge, y);
        cs.showText(straatkantTitel + " (vervolg)");
        cs.endText();
        y -= 25;

      }

      // Huisnummer label (optioneel)
      if (config.toonHuisnummer) {
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
        cs.newLineAtOffset(config.marge, y);
        cs.showText("Huisnummer " + huisnummer + ":");
        cs.endText();
        y -= 20;
      }

      // Foto's voor dit huisnummer
      y = voegFotoRijToe(cs, document, huisnummerFoto, config, y, postcode);

      // Ruimte tussen huisnummers
      y -= 15;
    }

    return y;
  }

  /**
   * Voegt een rij foto's toe
   */
  private static float voegFotoRijToe(PDPageContentStream cs, PDDocument document,
      List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst, PdfConfig config, float startY, String postcode)
      throws IOException {

    float x = config.marge;
    float y = startY;
    int fotoIndex = 0;

    while (fotoIndex < fotoLijst.size()) {
      // Check of we nieuwe rij moeten beginnen
      if (fotoIndex > 0 && fotoIndex % config.fotoPerRij == 0) {
        // Nieuwe rij
        x = config.marge;
        y -= (config.fotoHoogte + 35); // Foto hoogte + tekst ruimte

        // Check of nieuwe pagina nodig is
        if (y < config.marge + config.fotoHoogte + 20) {
          // We kunnen geen nieuwe foto's meer op deze pagina plaatsen
          // De resterende foto's worden op volgende pagina geplaatst
          // Dit wordt afgehandeld door de aanroepende methode
          break;
        }
      }

      StraatFotoOrganisatorPerPostcode.FotoInfo fotoInfo = fotoLijst.get(fotoIndex);

      // Bereken positie voor deze foto
      float fotoX = x;
      float fotoY = y - config.fotoHoogte;

      try {
        // Laad en plaats foto
        PDImageXObject image = PDImageXObject.createFromFileByContent(fotoInfo.getFotoBestand(), document);

        cs.drawImage(image, fotoX, fotoY, config.fotoBreedte, config.fotoHoogte);

        // Toon huisnummer onder foto (optioneel)
        if (config.toonHuisnummer) {
          cs.beginText();
          cs.setFont(PDType1Font.HELVETICA, 9);
          cs.newLineAtOffset(fotoX, fotoY - 12);
          cs.showText("Nr: " + fotoInfo.getHuisnummer());
          cs.endText();
        }

        // Toon bestandsnaam (optioneel)
        if (config.toonBestandsnaam) {
          cs.beginText();
          cs.setFont(PDType1Font.HELVETICA, 7);
          cs.newLineAtOffset(fotoX, fotoY - 24);

          String bestandsNaam = fotoInfo.getFotoBestand().getName();
          if (bestandsNaam.length() > 20) {
            bestandsNaam = bestandsNaam.substring(0, 17) + "...";
          }
          cs.showText(bestandsNaam);
          cs.endText();
        }

      } catch (IOException e) {
        System.err.println("Foto overslaan: " + fotoInfo.getFotoBestand().getName());
        // Teken een placeholder
        cs.setStrokingColor(200, 200, 200);
        cs.addRect(fotoX, fotoY, config.fotoBreedte, config.fotoHoogte);
        cs.stroke();

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 8);
        cs.newLineAtOffset(fotoX + 5, fotoY + config.fotoHoogte / 2);
        cs.showText("Geen foto");
        cs.endText();
      }

      // Volgende positie
      x += config.fotoBreedte + config.ruimteTussenFoto;
      fotoIndex++;
    }

    return y - (config.fotoHoogte + 35);
  }

  /**
   * Genereert eenvoudige PDF zonder postcode groepering
   */
  private static void genereerPdfSimpel(PDDocument document,
      Map<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> data, PdfConfig config)
      throws IOException {

    // Verzamel alle foto's
    List<StraatFotoOrganisatorPerPostcode.FotoInfo> alleOneven = new java.util.ArrayList<>();
    List<StraatFotoOrganisatorPerPostcode.FotoInfo> alleEven = new java.util.ArrayList<>();

    for (Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> postcodeData : data.values()) {
      alleOneven.addAll(postcodeData.get("ONEVEN"));
      alleEven.addAll(postcodeData.get("EVEN"));
    }

    // Sorteer op huisnummer
    alleOneven.sort(java.util.Comparator.comparingInt(StraatFotoOrganisatorPerPostcode.FotoInfo::getHuisnummer));
    alleEven.sort(java.util.Comparator.comparingInt(StraatFotoOrganisatorPerPostcode.FotoInfo::getHuisnummer));

    // Start eerste pagina
    PDPage pagina = new PDPage(PDRectangle.A4);
    document.addPage(pagina);

    PDPageContentStream cs = new PDPageContentStream(document, pagina);
    try {
      float y = PDRectangle.A4.getHeight() - config.marge;

      // Titel
      cs.beginText();
      cs.setFont(PDType1Font.HELVETICA_BOLD, 20);
      cs.newLineAtOffset(config.marge, y - 30);
      cs.showText("STRAATFOTO OVERZICHT");
      cs.endText();

      cs.beginText();
      cs.setFont(PDType1Font.HELVETICA, 12);
      cs.newLineAtOffset(config.marge, y - 55);
      cs.showText(config.fotoPerRij + " foto's per rij | " + (alleOneven.size() + alleEven.size()) + " foto's totaal");
      cs.endText();

      y -= 100;

      // Oneven huisnummers
      if (!alleOneven.isEmpty()) {
        y = voegFotoLijstToe(cs, document, pagina, "ON EVEN HUISNUMMERS", alleOneven, y, config);
      }

      // Even huisnummers
      if (!alleEven.isEmpty()) {
        voegFotoLijstToe(cs, document, pagina, "EVEN HUISNUMMERS", alleEven, y, config);
      }
    } finally {
      cs.close();
    }
  }

  /**
   * Voegt een lijst foto's toe (zonder huisnummer groepering)
   */
  private static float voegFotoLijstToe(PDPageContentStream cs, PDDocument document, PDPage pagina, String titel,
      List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst, float startY, PdfConfig config) throws IOException {

    float y = startY;

    // Sectie titel
    cs.beginText();
    cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
    cs.newLineAtOffset(config.marge, y);
    cs.showText(titel + " (" + fotoLijst.size() + " foto's)");
    cs.endText();

    y -= 30;

    // Voeg foto's toe in raster
    float x = config.marge;
    int fotoIndex = 0;

    while (fotoIndex < fotoLijst.size()) {
      // Check of nieuwe rij nodig is
      if (fotoIndex > 0 && fotoIndex % config.fotoPerRij == 0) {
        x = config.marge;
        y -= (config.fotoHoogte + 40);
      }

      // Check of nieuwe pagina nodig is
      if (y < config.marge + config.fotoHoogte + 50) {
        cs.close();
        pagina = new PDPage(PDRectangle.A4);
        document.addPage(pagina);
        cs = new PDPageContentStream(document, pagina);

        // Titel op nieuwe pagina
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
        cs.newLineAtOffset(config.marge, PDRectangle.A4.getHeight() - config.marge - 30);
        cs.showText(titel + " (vervolg)");
        cs.endText();

        x = config.marge;
        y = PDRectangle.A4.getHeight() - config.marge - 70;
      }

      StraatFotoOrganisatorPerPostcode.FotoInfo fotoInfo = fotoLijst.get(fotoIndex);
      float fotoX = x;
      float fotoY = y - config.fotoHoogte;

      try {
        // Foto
        // PDImageXObject image =
        // PDImageXObject.createFromFileByContent(fotoInfo.getFotoBestand(), document);
        PDImageXObject image = PDImageXObject.createFromFile(fotoInfo.getFotoBestand().getAbsolutePath(), document);
        cs.drawImage(image, fotoX, fotoY, config.fotoBreedte, config.fotoHoogte);

        // Huisnummer
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
        cs.newLineAtOffset(fotoX, fotoY - 15);
        cs.showText("" + fotoInfo.getHuisnummer());
        cs.endText();

        // Postcode (optioneel)
        if (config.toonPostcode) {
          cs.beginText();
          cs.setFont(PDType1Font.HELVETICA, 8);
          cs.newLineAtOffset(fotoX, fotoY - 28);
          cs.showText(fotoInfo.getPostcode());
          cs.endText();
        }

      } catch (IOException e) {
        System.err.println("Foto overslaan: " + fotoInfo.getFotoBestand().getName());
      }

      x += config.fotoBreedte + config.ruimteTussenFoto;
      fotoIndex++;
    }

    return y - (config.fotoHoogte + 50);
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
   * Berekent maximaal aantal foto's dat op een pagina past
   */
  public static int berekenMaxFotoPerPagina(PdfConfig config) {
    float beschikbareHoogte = PDRectangle.A4.getHeight() - (2 * config.marge) - 100;
    float hoogtePerRij = config.fotoHoogte + 40;

    int rijenPerPagina = (int) (beschikbareHoogte / hoogtePerRij);
    return rijenPerPagina * config.fotoPerRij;
  }
}