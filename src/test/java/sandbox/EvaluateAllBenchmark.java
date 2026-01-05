package sandbox;

import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;

public class EvaluateAllBenchmark {
  public static void main(String[] args) throws Exception {
    String s_Filenaam = "D:\\Data\\Hoevelaken\\Hoevelaken-adressenlijst_met_geo.xlsx";
    FileInputStream file = new FileInputStream(s_Filenaam);
    Workbook workbook = WorkbookFactory.create(file);
    FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

    Sheet sheet = workbook.getSheetAt(0);
    int formulaCount = 0;

    // Methode 1: Zonder evaluateAll() (trager)
    long startTime1 = System.currentTimeMillis();
    for (Row row : sheet) {
      for (Cell cell : row) {
        if (cell.getCellType() == CellType.FORMULA) {
          CellValue value = evaluator.evaluate(cell); // Evalueert elke keer
          formulaCount++;
        }
      }
    }
    long endTime1 = System.currentTimeMillis();

    // Methode 2: Met evaluateAll() (sneller)
    long startTime2 = System.currentTimeMillis();
    evaluator.evaluateAll(); // Eén keer alles berekenen
    for (Row row : sheet) {
      for (Cell cell : row) {
        if (cell.getCellType() == CellType.FORMULA) {
          CellValue value = evaluator.evaluate(cell); // Uit cache lezen
        }
      }
    }
    long endTime2 = System.currentTimeMillis();

    System.out.println("Aantal formules: " + formulaCount);
    System.out.println("Zonder evaluateAll(): " + (endTime1 - startTime1) + "ms");
    System.out.println("Met evaluateAll(): " + (endTime2 - startTime2) + "ms");

    workbook.close();
  }
}
