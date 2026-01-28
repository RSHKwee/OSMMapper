package kwee.osmmapper.report.image;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class StraatFotoPdfGenerator {

  /**
   * Genereert PDF met foto's gesorteerd op straatkant Gebruikt de FotoInfo class
   * voor betere metadata
   */
  public static void genereerPdfMetMetadata(Map<String, List<StraatFotoOrganisator.FotoInfo>> fotoData,
      String uitvoerPad) throws IOException {

    try (PDDocument document = new PDDocument()) {

      // Eerst oneven nummers
      if (!fotoData.get("ONEVEN").isEmpty()) {
        voegSectieToe(document, "ONEVEN HUISNUMMERS", fotoData.get("ONEVEN"));
      }

      // Dan even nummers
      if (!fotoData.get("EVEN").isEmpty()) {
        voegSectieToe(document, "EVEN HUISNUMMERS", fotoData.get("EVEN"));
      }

      document.save(uitvoerPad);
      System.out.println("PDF opgeslagen als: " + uitvoerPad);

    } catch (IOException e) {
      System.err.println("Fout bij maken PDF: " + e.getMessage());
      throw e;
    }
  }

  /**
   * Voegt een sectie met foto's toe aan het document
   */
  private static void voegSectieToe(PDDocument document, String sectieTitel,
      List<StraatFotoOrganisator.FotoInfo> fotoLijst) throws IOException {

    // Eerste pagina voor deze sectie
    PDPage pagina = new PDPage(PDRectangle.A4);
    document.addPage(pagina);

    // Maak een contentStream voor deze pagina
    PDPageContentStream contentStream = new PDPageContentStream(document, pagina);

    try {
      // Titel toevoegen
      contentStream.beginText();
      contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
      contentStream.newLineAtOffset(50, 750);
      contentStream.showText(sectieTitel);
      contentStream.endText();

      // Foto's toevoegen
      voegFotoToeAanPagina(contentStream, document, fotoLijst, 0, 50, 700);

    } finally {
      contentStream.close(); // Altijd sluiten!
    }

    // Controleer of er meer foto's zijn die niet op de eerste pagina passen
    int startIndex = berekenAantalFotoVoorPagina(fotoLijst, 0, 50, 700);

    while (startIndex < fotoLijst.size()) {
      // Nieuwe pagina voor de resterende foto's
      pagina = new PDPage(PDRectangle.A4);
      document.addPage(pagina);

      contentStream = new PDPageContentStream(document, pagina);

      try {
        // Titel voor vervolgpagina
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText(sectieTitel + " (vervolg)");
        contentStream.endText();

        // Meer foto's toevoegen
        int nieuweStart = voegFotoToeAanPagina(contentStream, document, fotoLijst, startIndex, 50, 700);
        startIndex = nieuweStart;

      } finally {
        contentStream.close();
      }
    }
  }

  /**
   * Voegt foto's toe aan een pagina en retourneert volgende start index
   */
  private static int voegFotoToeAanPagina(PDPageContentStream cs, PDDocument doc,
      List<StraatFotoOrganisator.FotoInfo> fotoLijst, int startIndex, float startX, float startY) throws IOException {

    float x = startX;
    float y = startY;
    int fotoPerRij = 4;
    int huidigeIndex = startIndex;

    while (huidigeIndex < fotoLijst.size()) {
      StraatFotoOrganisator.FotoInfo fotoInfo = fotoLijst.get(huidigeIndex);

      // Bereken rij en kolom
      int indexOpPagina = huidigeIndex - startIndex;
      int rij = indexOpPagina / fotoPerRij;
      int kolom = indexOpPagina % fotoPerRij;

      // Bereken positie
      float huidigeX = x + kolom * 130; // 100px breed + 30px ruimte
      float huidigeY = y - rij * 140; // 100px hoog + 40px ruimte voor tekst

      // Stop als we onderaan de pagina zijn
      if (huidigeY < 100) {
        return huidigeIndex; // Retourneer waar we gebleven zijn
      }

      try {
        // Foto toevoegen
        PDImageXObject pdFoto = PDImageXObject.createFromFileByContent(fotoInfo.getFotoBestand(), doc);

        cs.drawImage(pdFoto, huidigeX, huidigeY - 100, 100, 100);

        // Mapnaam onder foto (postcode + huisnummer)
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 10);
        cs.newLineAtOffset(huidigeX, huidigeY - 115);
        cs.showText(fotoInfo.getMapNaam() + " " + fotoInfo.getStraatnaam());
        cs.endText();

        // Bestandsnaam (kleiner)
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 8);
        cs.newLineAtOffset(huidigeX, huidigeY - 128);

        // Kort de bestandsnaam af als te lang
        String bestandsNaam = fotoInfo.getFotoBestand().getName();
        if (bestandsNaam.length() > 15) {
          bestandsNaam = bestandsNaam.substring(0, 12) + "...";
        }
        cs.showText(bestandsNaam);
        cs.endText();

      } catch (IOException e) {
        System.err.println("Kon foto niet laden: " + fotoInfo.getFotoBestand().getPath());
        // Tekst plaatshouder voor ontbrekende foto
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 8);
        cs.newLineAtOffset(huidigeX + 25, huidigeY - 50);
        cs.showText("[Foto niet\nbeschikbaar]");
        cs.endText();
      }

      huidigeIndex++;
    }

    return huidigeIndex; // Alle foto's zijn verwerkt
  }

  /**
   * Berekent hoeveel foto's er op een pagina passen
   */
  private static int berekenAantalFotoVoorPagina(List<StraatFotoOrganisator.FotoInfo> fotoLijst, int startIndex,
      float startX, float startY) {

    float y = startY;
    int fotoPerRij = 4;
    int maxRijen = (int) ((startY - 100) / 140); // 100px onderkant marge

    int maxFotoPerPagina = maxRijen * fotoPerRij;
    int beschikbareFoto = fotoLijst.size() - startIndex;

    return startIndex + Math.min(maxFotoPerPagina, beschikbareFoto);
  }

  /**
   * Eenvoudige PDF generator voor compatibiliteit
   */
  public static void genereerPdfSimpel(Map<String, List<File>> fotoPerStraatkant, String uitvoerPad)
      throws IOException {

    try (PDDocument document = new PDDocument()) {

      // Maak aparte pagina's
      maakPagina(document, "ONEVEN HUISNUMMERS", fotoPerStraatkant.get("ONEVEN"));
      maakPagina(document, "EVEN HUISNUMMERS", fotoPerStraatkant.get("EVEN"));

      document.save(uitvoerPad);
    }
  }

  private static void maakPagina(PDDocument doc, String titel, List<File> fotoLijst) throws IOException {

    if (fotoLijst == null || fotoLijst.isEmpty()) {
      return;
    }

    PDPage pagina = new PDPage(PDRectangle.A4);
    doc.addPage(pagina);

    PDPageContentStream cs = new PDPageContentStream(doc, pagina);

    try {
      // Titel
      cs.beginText();
      cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
      cs.newLineAtOffset(50, 750);
      cs.showText(titel);
      cs.endText();

      // Foto's
      float x = 50;
      float y = 700;
      int index = 0;

      for (File foto : fotoLijst) {
        if (index >= 12) {
          break; // Max 12 per pagina
        }

        int rij = index / 4;
        int kolom = index % 4;

        float posX = x + kolom * 125;
        float posY = y - rij * 140;

        if (posY > 50) {
          try {
            PDImageXObject img = PDImageXObject.createFromFileByContent(foto, doc);
            cs.drawImage(img, posX, posY - 100, 100, 100);

            // Mapnaam (uit parent directory)
            String mapNaam = foto.getParentFile().getName();
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA, 9);
            cs.newLineAtOffset(posX, posY - 115);
            cs.showText(mapNaam);
            cs.endText();

          } catch (Exception e) {
            System.err.println("Fout bij foto: " + foto.getName());
          }
        }

        index++;
      }

    } finally {
      cs.close();
    }
  }
}
