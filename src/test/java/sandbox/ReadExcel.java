package sandbox;

import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;

public class ReadExcel {
  public static void main(String[] args) throws Exception {
    // 1. Open het bestand
    FileInputStream file = new FileInputStream(
        "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-adressenlijst.xlsx");
    Workbook workbook = WorkbookFactory.create(file);

    // 2. Kies het eerste werkblad
    Sheet sheet = workbook.getSheetAt(0);
    // Sheet sheet = workbook.getSheetAt(1);
    int totalRows = sheet.getLastRowNum() + 1;
    System.out.println("Totaal aantal rijen in Excel: " + totalRows);

    // 3. Loop door alle rijen en cellen
    for (Row row : sheet) {
      for (Cell cell : row) {
        // 4. Controleer het type van elke cel
        CellType type = cell.getCellType();
        if (type == CellType.STRING) {
          System.out.print(cell.getStringCellValue() + "\t");
        } else if (type == CellType.NUMERIC) {
          System.out.print(cell.getNumericCellValue() + "\t");
        } else if (type == CellType.BOOLEAN) {
          System.out.print(cell.getBooleanCellValue() + "\t");
        } else if (type == CellType.FORMULA) {
          System.out.print(cell.getCellFormula() + "\t");
        }
        // Voeg hier meer celtypen toe (BOOLEAN, FORMULA, etc.)
      }
      System.out.println(); // Nieuwe regel per rij
    }
    // 5. Sluit de werkmap
    workbook.close();
    file.close();
  }
}
