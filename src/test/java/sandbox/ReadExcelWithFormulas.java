package sandbox;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;

public class ReadExcelWithFormulas {
    public static void main(String[] args) throws Exception {
        // 1. Open het bestand
        FileInputStream file = new FileInputStream("D:\\Data\\Hoevelaken\\Hoevelaken-flyerlijst_met_geo.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        
        // 2. Maak een formule-evaluator aan
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        
        // 3. Lees het werkblad
        Sheet sheet = workbook.getSheetAt(0);
        
        for (Row row : sheet) {
            for (Cell cell : row) {
                // 4. Controleer of het een formulecel is
                if (cell.getCellType() == CellType.FORMULA) {
                    System.out.print("Formule: " + cell.getCellFormula());
                    
                    // 5. Evalueer de formule
                    CellValue cellValue = evaluator.evaluate(cell);
                    
                    // 6. Haal de waarde op op basis van het resultaattype
                    switch (cellValue.getCellType()) {
                        case NUMERIC:
                            System.out.println(" → Resultaat: " + cellValue.getNumberValue());
                            break;
                        case STRING:
                            System.out.println(" → Resultaat: " + cellValue.getStringValue());
                            break;
                        case BOOLEAN:
                            System.out.println(" → Resultaat: " + cellValue.getBooleanValue());
                            break;
                        case ERROR:
                            System.out.println(" → Fout: " + cellValue.getErrorValue());
                            break;
                        default:
                            System.out.println(" → Leeg");
                    }
                } else {
                    // Geen formule: behandel als normale cel
                    CellType type = cell.getCellType();
                    if (type == CellType.STRING) {
                        System.out.print(cell.getStringCellValue() + "\t");
                    } else if (type == CellType.NUMERIC) {
                        System.out.print(cell.getNumericCellValue() + "\t");
                    }
                }
            }
            System.out.println();
        }
        
        workbook.close();
        file.close();
    }
}
