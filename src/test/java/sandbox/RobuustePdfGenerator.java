package sandbox;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import kwee.osmmapper.report.image.StraatFotoOrganisatorPerPostcode;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.IOUtils;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class RobuustePdfGenerator {

  public static class PdfConfig {
    public int fotoPerRij = 2;
    public float marge = 50;
    public float ruimteTussenFoto = 15;
    public boolean toonHuisnummer = true;
    public boolean toonBestandsnaam = false; // Uitgezet voor stabiliteit
    public boolean groepeerPerPostcode = true;

    public float fotoBreedte;
    public float fotoHoogte;
  }

  /**
   * Genereert PDF met betere error handling
   */
  public static void genereerPdf(Map<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> data,
      String uitvoerPad, PdfConfig config) throws IOException {

    berekenFotoGrootte(config);

    try (PDDocument document = new PDDocument()) {
      System.out.println("PDF genereren...");
      System.out.println("Foto's per rij: " + config.fotoPerRij);

      if (config.groepeerPerPostcode) {
        genereerPerPostcode(document, data, config);
      } else {
        genereerSimpel(document, data, config);
      }

      // Sla op met try-catch voor betere foutmelding
      try {
        document.save(uitvoerPad);
        System.out.println("✅ PDF opgeslagen: " + uitvoerPad);
      } catch (IOException e) {
        System.err.println("❌ Fout bij opslaan PDF: " + e.getMessage());
        throw new IOException("Kon PDF niet opslaan: " + uitvoerPad, e);
      }

    } catch (Exception e) {
      System.err.println("❌ Algemene fout: " + e.getMessage());
      throw new IOException("PDF generatie mislukt", e);
    }
  }

  private static void berekenFotoGrootte(PdfConfig config) {
    float breedte = PDRectangle.A4.getWidth() - (2 * config.marge);
    config.fotoBreedte = (breedte - ((config.fotoPerRij - 1) * config.ruimteTussenFoto)) / config.fotoPerRij;
    config.fotoHoogte = config.fotoBreedte * 0.75f;
  }

  private static void genereerPerPostcode(PDDocument document,
      Map<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> data, PdfConfig config)
      throws IOException {

    int postcodeNr = 1;

    for (Map.Entry<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> entry : data.entrySet()) {

      String postcode = entry.getKey();
      Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> straatkantData = entry.getValue();

      System.out.println("  Postcode " + postcodeNr + ": " + postcode);

      PDPage pagina = new PDPage(PDRectangle.A4);
      document.addPage(pagina);

      PDPageContentStream cs = null;
      try {
        cs = new PDPageContentStream(document, pagina);

        // Titel
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
        cs.newLineAtOffset(config.marge, PDRectangle.A4.getHeight() - config.marge - 30);
        cs.showText("POSTCODE " + postcode);
        cs.endText();

        float y = PDRectangle.A4.getHeight() - config.marge - 70;

        // Oneven
        List<StraatFotoOrganisatorPerPostcode.FotoInfo> oneven = straatkantData.get("ONEVEN");
        if (!oneven.isEmpty()) {
          y = voegFotoSectieToe(cs, document, pagina, "ON EVEN", oneven, y, config);
        }

        // Even
        List<StraatFotoOrganisatorPerPostcode.FotoInfo> even = straatkantData.get("EVEN");
        if (!even.isEmpty()) {
          voegFotoSectieToe(cs, document, pagina, "EVEN", even, y, config);
        }

      } finally {
        if (cs != null) {
          cs.close();
        }
      }

      postcodeNr++;
    }
  }

  private static float voegFotoSectieToe(PDPageContentStream cs, PDDocument document, PDPage pagina, String titel,
      List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst, float startY, PdfConfig config) throws IOException {

    if (fotoLijst.isEmpty())
      return startY;

    // Sectie titel
    cs.beginText();
    cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
    cs.newLineAtOffset(config.marge, startY);
    cs.showText(titel);
    cs.endText();

    float y = startY - 25;

    // Groepeer per huisnummer
    Map<Integer, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> perHuisnummer = groepeerPerHuisnummer(fotoLijst);

    for (Map.Entry<Integer, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> huisEntry : perHuisnummer.entrySet()) {

      int huisnummer = huisEntry.getKey();
      List<StraatFotoOrganisatorPerPostcode.FotoInfo> huisFoto = huisEntry.getValue();

      // Huisnummer label
      if (config.toonHuisnummer) {
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 11);
        cs.newLineAtOffset(config.marge, y);
        cs.showText("Huisnr " + huisnummer + ":");
        cs.endText();
        y -= 20;
      }

      // Voeg foto's toe voor dit huisnummer
      y = voegFotoRijToe(cs, document, huisFoto, config, y);

      y -= 10; // Ruimte tussen huisnummers
    }

    return y;
  }

  private static float voegFotoRijToe(PDPageContentStream cs, PDDocument document,
      List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst, PdfConfig config, float startY) throws IOException {

    float x = config.marge;
    float y = startY;
    int fotoIndex = 0;

    while (fotoIndex < fotoLijst.size()) {
      // Nieuwe rij?
      if (fotoIndex > 0 && fotoIndex % config.fotoPerRij == 0) {
        x = config.marge;
        y -= (config.fotoHoogte + 25);

        // Check pagina ruimte
        if (y < config.marge + 50) {
          // Nieuwe pagina nodig
          return y;
        }
      }

      StraatFotoOrganisatorPerPostcode.FotoInfo fotoInfo = fotoLijst.get(fotoIndex);
      float fotoX = x;
      float fotoY = y - config.fotoHoogte;

      // Probeer foto toe te voegen
      if (voegFotoToeVeilig(cs, document, fotoInfo, fotoX, fotoY, config.fotoBreedte, config.fotoHoogte)) {
        // Huisnummer onder foto
        if (config.toonHuisnummer) {
          cs.beginText();
          cs.setFont(PDType1Font.HELVETICA, 9);
          cs.newLineAtOffset(fotoX, fotoY - 12);
          cs.showText("" + fotoInfo.getHuisnummer());
          cs.endText();
        }
      }

      x += config.fotoBreedte + config.ruimteTussenFoto;
      fotoIndex++;
    }

    return y - (config.fotoHoogte + 25);
  }

  /**
   * VEILIGE methode om foto's toe te voegen (lost NullPointerException op)
   */
  private static boolean voegFotoToeVeilig(PDPageContentStream cs, PDDocument document,
      StraatFotoOrganisatorPerPostcode.FotoInfo fotoInfo, float x, float y, float breedte, float hoogte) {

    File fotoBestand = fotoInfo.getFotoBestand();

    // Methode 1: Probeer met FileInputStream (meest betrouwbaar)
    try (FileInputStream fis = new FileInputStream(fotoBestand)) {
      // Lees bytes eerst in memory
      byte[] imageBytes = IOUtils.toByteArray(fis);

      // Maak PDImageXObject van bytes
      PDImageXObject image = PDImageXObject.createFromByteArray(document, imageBytes, fotoBestand.getName());

      cs.drawImage(image, x, y, breedte, hoogte);
      return true;

    } catch (Exception e1) {
      System.err.println("Methode 1 mislukt voor: " + fotoBestand.getName());

      // Methode 2: Probeer met LosslessFactory
      try {
        PDImageXObject image = PDImageXObject.createFromFile(fotoBestand.getAbsolutePath(), document);
        cs.drawImage(image, x, y, breedte, hoogte);
        return true;

      } catch (Exception e2) {
        System.err.println("Methode 2 mislukt voor: " + fotoBestand.getName());

        // Methode 3: Plaatshouder tekenen
        try {
          cs.setStrokingColor(180, 180, 180);
          cs.setLineWidth(1);
          cs.addRect(x, y, breedte, hoogte);
          cs.stroke();

          cs.beginText();
          cs.setFont(PDType1Font.HELVETICA, 8);
          cs.newLineAtOffset(x + 5, y + hoogte / 2);
          cs.showText(fotoBestand.getName());
          cs.endText();

          return false;

        } catch (Exception e3) {
          System.err.println("Kon ook geen placeholder tekenen");
          return false;
        }
      }
    }
  }

  private static void genereerSimpel(PDDocument document,
      Map<String, Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>>> data, PdfConfig config)
      throws IOException {

    // Verzamel alle foto's
    List<StraatFotoOrganisatorPerPostcode.FotoInfo> alleOneven = new ArrayList<>();
    List<StraatFotoOrganisatorPerPostcode.FotoInfo> alleEven = new ArrayList<>();

    for (Map<String, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> postcodeData : data.values()) {
      alleOneven.addAll(postcodeData.get("ONEVEN"));
      alleEven.addAll(postcodeData.get("EVEN"));
    }

    // Sorteer
    alleOneven.sort(Comparator.comparingInt(StraatFotoOrganisatorPerPostcode.FotoInfo::getHuisnummer));
    alleEven.sort(Comparator.comparingInt(StraatFotoOrganisatorPerPostcode.FotoInfo::getHuisnummer));

    // Maak pagina
    PDPage pagina = new PDPage(PDRectangle.A4);
    document.addPage(pagina);

    PDPageContentStream cs = null;
    try {
      cs = new PDPageContentStream(document, pagina);

      float y = PDRectangle.A4.getHeight() - config.marge;

      // Titel
      cs.beginText();
      cs.setFont(PDType1Font.HELVETICA_BOLD, 20);
      cs.newLineAtOffset(config.marge, y - 30);
      cs.showText("STRAATFOTO OVERZICHT");
      cs.endText();

      y -= 80;

      // Voeg foto's toe
      if (!alleOneven.isEmpty()) {
        y = voegFotoGridToe(cs, document, "Oneven", alleOneven, y, config);
      }

      if (!alleEven.isEmpty()) {
        voegFotoGridToe(cs, document, "Even", alleEven, y, config);
      }

    } finally {
      if (cs != null) {
        cs.close();
      }
    }
  }

  private static float voegFotoGridToe(PDPageContentStream cs, PDDocument document, String titel,
      List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst, float startY, PdfConfig config) throws IOException {

    cs.beginText();
    cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
    cs.newLineAtOffset(config.marge, startY);
    cs.showText(titel);
    cs.endText();

    float y = startY - 25;
    float x = config.marge;
    int fotoIndex = 0;

    while (fotoIndex < fotoLijst.size()) {
      if (fotoIndex > 0 && fotoIndex % config.fotoPerRij == 0) {
        x = config.marge;
        y -= (config.fotoHoogte + 25);
      }

      if (y < config.marge + 50) {
        break; // Geen ruimte meer
      }

      StraatFotoOrganisatorPerPostcode.FotoInfo fotoInfo = fotoLijst.get(fotoIndex);
      float fotoX = x;
      float fotoY = y - config.fotoHoogte;

      voegFotoToeVeilig(cs, document, fotoInfo, fotoX, fotoY, config.fotoBreedte, config.fotoHoogte);

      x += config.fotoBreedte + config.ruimteTussenFoto;
      fotoIndex++;
    }

    return y - 30;
  }

  private static Map<Integer, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> groepeerPerHuisnummer(
      List<StraatFotoOrganisatorPerPostcode.FotoInfo> fotoLijst) {

    Map<Integer, List<StraatFotoOrganisatorPerPostcode.FotoInfo>> groepering = new TreeMap<>();

    for (StraatFotoOrganisatorPerPostcode.FotoInfo fotoInfo : fotoLijst) {
      groepering.computeIfAbsent(fotoInfo.getHuisnummer(), k -> new ArrayList<>()).add(fotoInfo);
    }

    return groepering;
  }
}
