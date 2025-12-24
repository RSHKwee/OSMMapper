package sandbox;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;

public class WriteExcel {
  public static void main(String[] args) throws Exception {
    // 1. Maak een nieuwe werkmap
    Workbook workbook = new XSSFWorkbook();

    // 2. Maak een nieuw werkblad
    Sheet sheet = workbook.createSheet("Studenten");

    // 3. Maak een rij en cellen
    Row headerRow = sheet.createRow(0);
    headerRow.createCell(0).setCellValue("Naam");
    headerRow.createCell(1).setCellValue("Cijfer");

    Row dataRow = sheet.createRow(1);
    dataRow.createCell(0).setCellValue("Jan Jansen");
    dataRow.createCell(1).setCellValue(8.5);

    // 4. Sla het bestand op
    try (FileOutputStream output = new FileOutputStream("Studenten.xlsx")) {
      workbook.write(output);
    }
    // 5. Sluit de werkmap
    workbook.close();
    System.out.println("Bestand geschreven.");
  }
}
