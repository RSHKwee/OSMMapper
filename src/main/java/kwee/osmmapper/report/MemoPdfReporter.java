package kwee.osmmapper.report;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import kwee.osmmapper.lib.Const;
import kwee.osmmapper.lib.MemoContent;

public class MemoPdfReporter {

  private static final String[] HEADERS = { "#", "Naam", "Adres", "Postcode", "Plaats", "Land", "Telefoon", "E-mail",
      "Projecten", "Agenda", "Lat", "Lon" };

  private static final float[] WIDTHS = { 0.8f, 2.5f, 3f, 1.4f, 2f, 1.5f, 1.8f, 3f, 2.5f, 2.5f, 1.3f, 1.3f };

  private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private static final Color HEADER_BG = new Color(60, 90, 140);
  private static final Color ROW_ALT_BG = new Color(240, 240, 245);

  public static void generateReport(List<MemoContent> entries, String outputPath, String title)
      throws IOException, DocumentException {

    if (HEADERS.length != WIDTHS.length) {
      throw new IllegalStateException("HEADERS en WIDTHS hebben verschillend aantal kolommen");
    }

    Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 36);
    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputPath));
    document.open();

    try {
      // ---- Titel ----
      Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
      Paragraph titlePara = new Paragraph(title, titleFont);
      titlePara.setAlignment(Element.ALIGN_CENTER);
      document.add(titlePara);

      // ---- Metadata ----
      long aantal = entries.stream().filter(MemoPdfReporter::hasContent).count();
      Font metaFont = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY);
      Paragraph meta = new Paragraph("Aantal entries: " + aantal + " van " + entries.size() + "   |   Gegenereerd: "
          + LocalDateTime.now().format(DT_FMT), metaFont);
      meta.setAlignment(Element.ALIGN_CENTER);
      meta.setSpacingAfter(12f);
      document.add(meta);

      // ---- Tabel ----
      PdfPTable table = new PdfPTable(HEADERS.length);
      table.setWidthPercentage(100);
      table.setWidths(WIDTHS);
      table.setHeaderRows(1); // header herhaalt op elke pagina

      Font headerFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
      for (String h : HEADERS) {
        PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
        cell.setBackgroundColor(HEADER_BG);
        cell.setPadding(4f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
      }

      Font cellFont = new Font(Font.HELVETICA, 8);
      boolean alternate = false;
      int rowNr = 0;

      for (MemoContent m : entries) {
        if (!hasContent(m)) {
          continue;
        }

        rowNr++;
        Color bg = alternate ? ROW_ALT_BG : Color.WHITE;
        alternate = !alternate;

        addCell(table, String.valueOf(rowNr), cellFont, bg);
        addCell(table, fullName(m), cellFont, bg);
        addCell(table, addressLine(m), cellFont, bg);
        addCell(table, nz(m.getPostcode()), cellFont, bg);
        addCell(table, nz(m.getCity()), cellFont, bg);
        addCell(table, nz(m.getCountry()), cellFont, bg);
        addCell(table, nz(m.getPhonenumber()), cellFont, bg);
        addCell(table, nz(m.getMailaddress()), cellFont, bg);
        addCell(table, nz(m.getProjects()), cellFont, bg);
        addCell(table, nz(m.getAgenda()), cellFont, bg);
        addCell(table, coord(m.getLatitude()), cellFont, bg);
        addCell(table, coord(m.getLongitude()), cellFont, bg);
      }

      document.add(table);
    } finally {
      document.close(); // sluit ook de PdfWriter
    }
  }

  // ---------- helpers ----------

  /**
   * Ruimere "heeft inhoud"-check dan MemoContent.isEmpty(), omdat die country en
   * pictureIdx negeert.
   */
  private static boolean hasContent(MemoContent m) {
    if (m == null) {
      return false;
    }
    if (!m.isEmpty()) {
      return true;
    }
    return !nz(m.getCountry()).isBlank();
  }

  private static void addCell(PdfPTable table, String text, Font font, Color bg) {
    PdfPCell cell = new PdfPCell(new Phrase(text, font));
    cell.setBackgroundColor(bg);
    cell.setPadding(3f);
    cell.setVerticalAlignment(Element.ALIGN_TOP);
    table.addCell(cell);
  }

  private static String fullName(MemoContent m) {
    return (nz(m.getSurname()) + " " + nz(m.getFamilyname())).trim();
  }

  private static String addressLine(MemoContent m) {
    return (nz(m.getStreet()) + " " + nz(m.getHousenumber())).trim();
  }

  private static String nz(String s) {
    return s == null ? "" : s;
  }

  private static String coord(double d) {
    if (d == Const.c_LongLatUndefined) {
      return "";
    }
    return String.format("%.5f", d);
  }
}