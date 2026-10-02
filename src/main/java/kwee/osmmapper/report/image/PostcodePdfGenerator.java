package kwee.osmmapper.report.image;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import kwee.logger.MyLogger;
import kwee.osmmapper.lib.OSMMapExcel;

public class PostcodePdfGenerator {
  private static final Logger LOGGER = MyLogger.getLogger();

  // A4 in points
  private static final float PAGE_W = PageSize.A4.getWidth(); // 595
  private static final float PAGE_H = PageSize.A4.getHeight(); // 842

  // Layout constanten (identiek aan originele PDFBox-versie)
  private static final float MARGIN = 50f;
  private static final int FOTO_PER_RIJ = 2;
  private static final float FOTO_BREEDTE = 200f;
  private static final float FOTO_HOOGTE = 200f;
  private static final float CEL_BREEDTE = 260f;
  private static final float CEL_HOOGTE = 280f;
  private static final float START_Y = 700f;
  private static final float MIN_Y = 200f;

  // ============================================================
  // Versie 1: per postcode EN straatkant (oneven/even)
  // ============================================================
  public static void genereerPdfPerPostcode(
      Map<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> data, OSMMapExcel osmMapExcel,
      String uitvoerPad) throws IOException {

    LOGGER.log(Level.INFO, "PDF genereren per postcode...");

    Document document = new Document(PageSize.A4, MARGIN, MARGIN, MARGIN, MARGIN);
    try {
      PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(uitvoerPad));
      document.open();

      for (Map.Entry<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> postcodeEntry : data
          .entrySet()) {

        String postcode = postcodeEntry.getKey();
        String straatnaam = osmMapExcel.getStreet4ZipCode(postcode);
        Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> straatkantData = postcodeEntry.getValue();

        voegPostcodeSectieToe(document, writer, postcode, straatnaam, straatkantData);
      }

      document.close();
      LOGGER.log(Level.INFO, "PDF opgeslagen als: " + uitvoerPad);

    } catch (DocumentException e) {
      LOGGER.log(Level.WARNING, "Fout bij maken PDF: " + e.getMessage());
      throw new IOException(e);
    }
  }

  private static void voegPostcodeSectieToe(Document document, PdfWriter writer, String postcode, String straatnaam,
      Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> straatkantData)
      throws DocumentException, IOException {

    LOGGER.log(Level.INFO, "  Verwerken postcode: " + postcode);

    // --- Titelpagina voor deze postcode ---
    document.newPage();
    PdfContentByte cb = writer.getDirectContent();

    schrijfTekst(cb, "POSTCODE: " + postcode + " " + straatnaam, 24, MARGIN, 400);

    int totaalOneven = straatkantData.getOrDefault("ONEVEN", List.of()).size();
    int totaalEven = straatkantData.getOrDefault("EVEN", List.of()).size();

    schrijfTekst(cb, totaalOneven + " oneven huisnummers", 14, MARGIN, 350);
    schrijfTekst(cb, totaalEven + " even huisnummers", 14, MARGIN, 325);
    schrijfTekst(cb, (totaalOneven + totaalEven) + " foto's totaal", 14, MARGIN, 300);

    // --- Oneven sectie ---
    List<StraatFotoOrganisatorPerPostcode.FotoInfo> oneven = straatkantData.get("ONEVEN");
    if (oneven != null && !oneven.isEmpty()) {
      voegStraatkantSectieToe(document, writer, postcode, "ONEVEN HUISNUMMERS", oneven);
    }

    // --- Even sectie ---
    List<StraatFotoOrganisatorPerPostcode.FotoInfo> even = straatkantData.get("EVEN");
    if (even != null && !even.isEmpty()) {
      voegStraatkantSectieToe(document, writer, postcode, "EVEN HUISNUMMERS", even);
    }
  }

  private static void voegStraatkantSectieToe(Document document, PdfWriter writer, String postcode, String straatkant,
      List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst) throws DocumentException, IOException {

    int startIndex = 0;
    int paginaNummer = 1;

    while (startIndex < fotoLijst.size()) {
      document.newPage();
      PdfContentByte cb = writer.getDirectContent();

      // Titel
      String titel = postcode + " - " + straatkant;
      if (paginaNummer > 1) {
        titel += " (vervolg pagina " + paginaNummer + ")";
      }
      schrijfTekst(cb, titel, paginaNummer == 1 ? 18 : 16, MARGIN, 780);

      startIndex = voegFotoToeAanPagina(cb, document, fotoLijst, startIndex);
      paginaNummer++;
    }
  }

  /**
   * Voegt foto's toe aan de huidige pagina vanaf startIndex.
   *
   * @return de nieuwe startIndex (eerste foto die NIET meer op deze pagina paste)
   */
  private static int voegFotoToeAanPagina(PdfContentByte cb, Document document,
      List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst, int startIndex) throws DocumentException, IOException {

    int fotoOpPagina = 0;

    for (int i = startIndex; i < fotoLijst.size(); i++) {
      int rij = fotoOpPagina / FOTO_PER_RIJ;
      int kolom = fotoOpPagina % FOTO_PER_RIJ;

      float huidigeX = MARGIN + kolom * CEL_BREEDTE;
      float huidigeY = START_Y - rij * CEL_HOOGTE;

      // Past deze foto nog op de pagina?
      if (huidigeY - FOTO_HOOGTE < MIN_Y) {
        return i; // volgende pagina
      }

      StraatFotoOrganisatorPerPostcode.FotoInfo fotoInfo = fotoLijst.get(i);

      try {
        Image img = Image.getInstance(fotoInfo.getFotoBestand().getAbsolutePath());
        img.scaleAbsolute(FOTO_BREEDTE, FOTO_HOOGTE);
        img.setAbsolutePosition(huidigeX, huidigeY - FOTO_HOOGTE);
        document.add(img);

        // Huisnummer
        schrijfTekst(cb, String.valueOf(fotoInfo.getHuisnummer()), 12, huidigeX, huidigeY - FOTO_HOOGTE - 15);

        // Bestandsnaam (afgekapt)
        String bestandsNaam = fotoInfo.getFotoBestand().getName();
        if (bestandsNaam.length() > 15) {
          bestandsNaam = bestandsNaam.substring(0, 12) + "...";
        }
        schrijfTekst(cb, bestandsNaam, 8, huidigeX, huidigeY - FOTO_HOOGTE - 30);

      } catch (IOException e) {
        LOGGER.log(Level.WARNING, "Foto overslaan: " + fotoInfo.getFotoBestand().getPath());
      }

      fotoOpPagina++;
    }

    return fotoLijst.size();
  }

  // ============================================================
  // Versie 2: eenvoudig, alleen per postcode
  // ============================================================
  public static void genereerPdfPerPostcodeEenvoudig(Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> data,
      OSMMapExcel osmMapExcel, String uitvoerPad) throws IOException {

    Document document = new Document(PageSize.A4, MARGIN, MARGIN, MARGIN, MARGIN);
    try {
      PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(uitvoerPad));
      document.open();

      for (Map.Entry<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> entry : data.entrySet()) {
        String postcode = entry.getKey();
        String straat = osmMapExcel.getStreet4ZipCode(postcode);
        List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst = entry.getValue();

        document.newPage();
        PdfContentByte cb = writer.getDirectContent();

        schrijfTekst(cb, "POSTCODE: " + postcode + " " + straat, 20, MARGIN, 780);
        schrijfTekst(cb, fotoLijst.size() + " foto's, gesorteerd op huisnummer", 12, MARGIN, 750);

        // Foto's in raster van 4 per rij
        float startY = 700f;
        int fotoTeller = 0;
        int maxPerPagina = 12;

        for (StraatFotoOrganisatorPerPostcode.FotoInfo fotoInfo : fotoLijst) {
          if (fotoTeller >= maxPerPagina) {
            break; // (zie opmerking onderaan — dit is een beperking van de originele code)
          }

          int rij = fotoTeller / 4;
          int kolom = fotoTeller % 4;

          float posX = MARGIN + kolom * 130;
          float posY = startY - rij * 140;

          if (posY - 100 > 100) {
            try {
              Image img = Image.getInstance(fotoInfo.getFotoBestand().getAbsolutePath());
              img.scaleAbsolute(100, 100);
              img.setAbsolutePosition(posX, posY - 100);
              document.add(img);

              schrijfTekst(cb, "Nr: " + fotoInfo.getHuisnummer(), 11, posX, posY - 115);

              String naam = fotoInfo.getFotoBestand().getName();
              if (naam.length() > 12) {
                naam = naam.substring(0, 9) + "...";
              }
              schrijfTekst(cb, naam, 8, posX, posY - 130);

            } catch (IOException e) {
              LOGGER.log(Level.WARNING, "Fout: " + fotoInfo.getFotoBestand().getName());
            }
          }

          fotoTeller++;
        }
      }

      document.close();

    } catch (DocumentException e) {
      LOGGER.log(Level.WARNING, "Fout bij maken PDF: " + e.getMessage());
      throw new IOException(e);
    }
  }

  // ============================================================
  // Helper: tekst schrijven met directe content byte
  // ============================================================
  private static final BaseFont FONT;
  static {
    try {
      FONT = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
    } catch (DocumentException | IOException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private static void schrijfTekst(PdfContentByte cb, String tekst, float grootte, float x, float y) {
    cb.beginText();
    cb.setFontAndSize(FONT, grootte);
    cb.setTextMatrix(x, y);
    cb.showText(tekst);
    cb.endText();
  }
}