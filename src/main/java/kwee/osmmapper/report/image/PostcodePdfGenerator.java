package kwee.osmmapper.report.image;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import kwee.osmmapper.lib.OSMMapExcel;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PostcodePdfGenerator {

  /**
   * Genereert PDF gegroepeerd per postcode, gesorteerd op huisnummer
   */
  public static void genereerPdfPerPostcode(
      Map<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> data, OSMMapExcel osmMapExcel,
      String uitvoerPad) throws IOException {

    try (PDDocument document = new PDDocument()) {
      System.out.println("\nPDF genereren per postcode...");

      // Verwerk elke postcode
      for (Map.Entry<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> postcodeEntry : data
          .entrySet()) {

        String postcode = postcodeEntry.getKey();
        String straatnaam = osmMapExcel.getStreet4ZipCode(postcode); // TODO
        Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> straatkantData = postcodeEntry.getValue();

        // Voeg sectie toe voor deze postcode
        voegPostcodeSectieToe(document, postcode, straatnaam, straatkantData);
      }

      document.save(uitvoerPad);
      System.out.println("PDF opgeslagen als: " + uitvoerPad);

    } catch (IOException e) {
      System.err.println("Fout bij maken PDF: " + e.getMessage());
      throw e;
    }
  }

  /**
   * Voegt een complete postcode sectie toe aan het document
   */
  private static void voegPostcodeSectieToe(PDDocument document, String postcode, String straatnaam,
      Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> straatkantData) throws IOException {

    System.out.println("  Verwerken postcode: " + postcode);

    // Pagina voor postcode titel
    PDPage titelPagina = new PDPage(PDRectangle.A4);
    document.addPage(titelPagina);

    try (PDPageContentStream cs = new PDPageContentStream(document, titelPagina)) {
      // Grote titel voor postcode
      cs.beginText();
      cs.setFont(PDType1Font.HELVETICA_BOLD, 24);
      cs.newLineAtOffset(50, 400);
      cs.showText("POSTCODE: " + postcode + " " + straatnaam);
      cs.endText();

      // Statistieken
      int totaalOneven = straatkantData.get("ONEVEN").size();
      int totaalEven = straatkantData.get("EVEN").size();

      cs.beginText();
      cs.setFont(PDType1Font.HELVETICA, 14);
      cs.newLineAtOffset(50, 350);
      cs.showText(totaalOneven + " oneven huisnummers");
      cs.newLineAtOffset(0, -25);
      cs.showText(totaalEven + " even huisnummers");
      cs.newLineAtOffset(0, -25);
      cs.showText((totaalOneven + totaalEven) + " foto's totaal");
      cs.endText();
    }

    // Eerst oneven huisnummers voor deze postcode
    if (!straatkantData.get("ONEVEN").isEmpty()) {
      voegStraatkantSectieToe(document, postcode, "ON EVEN HUISNUMMERS", straatkantData.get("ONEVEN"));
    }

    // Dan even huisnummers voor deze postcode
    if (!straatkantData.get("EVEN").isEmpty()) {
      voegStraatkantSectieToe(document, postcode, "EVEN HUISNUMMERS", straatkantData.get("EVEN"));
    }
  }

  /**
   * Voegt een straatkant sectie toe voor een specifieke postcode
   */
  private static void voegStraatkantSectieToe(PDDocument document, String postcode, String straatkant,
      List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst) throws IOException {

    // Eerste pagina voor deze straatkant
    PDPage pagina = new PDPage(PDRectangle.A4);
    document.addPage(pagina);

    try (PDPageContentStream cs = new PDPageContentStream(document, pagina)) {
      // Titel: Postcode + Straatkant
      cs.beginText();
      cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
      cs.newLineAtOffset(50, 750);
      cs.showText(postcode + " - " + straatkant);
      cs.endText();

      // Foto's toevoegen
      voegFotoToeAanPagina(cs, document, fotoLijst, 0);
    }

    // Controleer of er meer foto's zijn (voor paginering)
    int startIndex = berekenAantalFotoVoorPagina(fotoLijst, 0);
    int paginaNummer = 1;

    while (startIndex < fotoLijst.size()) {
      pagina = new PDPage(PDRectangle.A4);
      document.addPage(pagina);
      paginaNummer++;

      try (PDPageContentStream cs = new PDPageContentStream(document, pagina)) {
        // Titel voor vervolgpagina
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
        cs.newLineAtOffset(50, 750);
        cs.showText(postcode + " - " + straatkant + " (vervolg pagina " + paginaNummer + ")");
        cs.endText();

        // Meer foto's toevoegen
        startIndex = voegFotoToeAanPagina(cs, document, fotoLijst, startIndex);
      }
    }
  }

  /**
   * Voegt foto's toe aan een pagina
   */
  private static int voegFotoToeAanPagina(PDPageContentStream cs, PDDocument doc,
      List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst, int startIndex) throws IOException {

    float x = 50;
    float y = 700;
    int fotoPerRij = 4;
    int huidigeIndex = startIndex;

    while (huidigeIndex < fotoLijst.size()) {
      StraatFotoOrganisatorPerPostcode.FotoInfo fotoInfo = fotoLijst.get(huidigeIndex);

      int indexOpPagina = huidigeIndex - startIndex;
      int rij = indexOpPagina / fotoPerRij;
      int kolom = indexOpPagina % fotoPerRij;

      float huidigeX = x + kolom * 130;
      float huidigeY = y - rij * 140;

      // Stop als we onderaan de pagina zijn
      if (huidigeY < 100) {
        return huidigeIndex;
      }

      try {
        // Foto toevoegen
        PDImageXObject pdFoto = PDImageXObject.createFromFileByContent(fotoInfo.getFotoBestand(), doc);

        cs.drawImage(pdFoto, huidigeX, huidigeY - 100, 100, 100);

        // Huisnummer en bestandsnaam
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
        cs.newLineAtOffset(huidigeX, huidigeY - 115);
        cs.showText(String.valueOf(fotoInfo.getHuisnummer()));
        cs.endText();

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 8);
        cs.newLineAtOffset(huidigeX, huidigeY - 130);

        String bestandsNaam = fotoInfo.getFotoBestand().getName();
        if (bestandsNaam.length() > 15) {
          bestandsNaam = bestandsNaam.substring(0, 12) + "...";
        }
        cs.showText(bestandsNaam);
        cs.endText();

      } catch (IOException e) {
        System.err.println("Foto overslaan: " + fotoInfo.getFotoBestand().getPath());
      }

      huidigeIndex++;
    }

    return huidigeIndex;
  }

  private static int berekenAantalFotoVoorPagina(List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst,
      int startIndex) {

    float y = 700;
    int fotoPerRij = 4;
    int maxRijen = (int) ((700 - 100) / 140); // 100px onderkant marge

    int maxFotoPerPagina = maxRijen * fotoPerRij;
    int beschikbareFoto = fotoLijst.size() - startIndex;

    return startIndex + Math.min(maxFotoPerPagina, beschikbareFoto);
  }

  /**
   * Eenvoudigere versie: alle foto's per postcode op volgorde (zonder oneven/even
   * scheiding)
   */
  public static void genereerPdfPerPostcodeEenvoudig(Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> data,
      OSMMapExcel osmMapExcel, String uitvoerPad) throws IOException {

    try (PDDocument document = new PDDocument()) {

      for (Map.Entry<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> entry : data.entrySet()) {

        String postcode = entry.getKey();
        String straat = osmMapExcel.getStreet4ZipCode(postcode);
        List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst = entry.getValue();

        // Start nieuwe pagina voor elke postcode
        PDPage pagina = new PDPage(PDRectangle.A4);
        document.addPage(pagina);

        try (PDPageContentStream cs = new PDPageContentStream(document, pagina)) {
          // Titel
          cs.beginText();
          cs.setFont(PDType1Font.HELVETICA_BOLD, 20);
          cs.newLineAtOffset(50, 750);
          cs.showText("POSTCODE: " + postcode + " " + straat);
          cs.endText();

          // Subtitle
          cs.beginText();
          cs.setFont(PDType1Font.HELVETICA, 12);
          cs.newLineAtOffset(50, 720);
          cs.showText(fotoLijst.size() + " foto's, gesorteerd op huisnummer");
          cs.endText();

          // Foto's in raster (4 per rij)
          float x = 50;
          float y = 680;
          int fotoTeller = 0;

          for (StraatFotoOrganisatorPerPostcode.FotoInfo fotoInfo : fotoLijst) {
            if (fotoTeller >= 12)
              break; // Max 12 per pagina

            int rij = fotoTeller / 4;
            int kolom = fotoTeller % 4;

            float posX = x + kolom * 130;
            float posY = y - rij * 140;

            if (posY > 100) {
              try {
                PDImageXObject img = PDImageXObject.createFromFileByContent(fotoInfo.getFotoBestand(), document);
                cs.drawImage(img, posX, posY - 100, 100, 100);

                // Huisnummer
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
                cs.newLineAtOffset(posX, posY - 115);
                cs.showText("Nr: " + fotoInfo.getHuisnummer());
                cs.endText();

                // Bestandsnaam
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 8);
                cs.newLineAtOffset(posX, posY - 130);
                String naam = fotoInfo.getFotoBestand().getName();
                if (naam.length() > 12)
                  naam = naam.substring(0, 9) + "...";
                cs.showText(naam);
                cs.endText();

              } catch (IOException e) {
                System.err.println("Fout: " + fotoInfo.getFotoBestand().getName());
              }
            }

            fotoTeller++;
          }
        }
      }

      document.save(uitvoerPad);
    }
  }
}