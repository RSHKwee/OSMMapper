package sandbox;

import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import kwee.library.Address;
import kwee.library.NominatimAPI;

public class ReadLongLat {
  public static void main(String[] args) throws Exception {
    // 1. Open het bestand
    String inputFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-adressenlijst.xlsx";
    String outputFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-adressenlijst_met_coordinaten.xlsx";

    FileInputStream file = new FileInputStream(inputFile);
    Workbook workbook = WorkbookFactory.create(file);

    // 2. Kies het eerste werkblad
    Sheet sheet = workbook.getSheetAt(0);

    // 3. Loop door alle rijen
    int rowIndex = 0;
    for (Row row : sheet) {
      Address l_address = new Address();
      // if (rowIndex < 10) {
      if (rowIndex == 0) {
        // HEADER RIJ: maak kolommen aan als ze nog niet bestaan
        Cell longCell = row.getCell(7);
        if (longCell == null) {
          longCell = row.createCell(7);
        }
        longCell.setCellValue("Longitude");

        Cell latCell = row.getCell(8);
        if (latCell == null) {
          latCell = row.createCell(8);
        }
        latCell.setCellValue("Latitude");
      } else {
        // DATA RIJEN: lees bestaande gegevens
        for (Cell cell : row) {
          int cellIndex = cell.getColumnIndex();
          CellType type = cell.getCellType();

          // Controleer het type van elke cel
          if (type == CellType.STRING) {
            String value = cell.getStringCellValue();
            if (cellIndex == 2 && !value.isEmpty()) { // toevoeging
              String huisnr = l_address.getHouseNumber();
              huisnr = (huisnr != null ? huisnr + " " : "") + value;
              l_address.setHouseNumber(huisnr);
            } else if (cellIndex == 3) { // straat
              l_address.setRoad(value);
            } else if (cellIndex == 4) { // plaats
              l_address.setCity(value);
              l_address.setCountry("Netherlands");
            }
          } else if (type == CellType.NUMERIC) {
            if (cellIndex == 1) { // huisnummer
              String huisnr = Integer.toString((int) cell.getNumericCellValue());
              l_address.setHouseNumber(huisnr);
            } else if (cellIndex == 7) { // longitude (al aanwezig?)
              // Al aanwezige longitude overschrijven we niet
            } else if (cellIndex == 8) { // latitude (al aanwezig?)
              // Al aanwezige latitude overschrijven we niet
            }
          }
        }

        // Voer geocoding uit als adresgegevens aanwezig zijn
        if (l_address.getRoad() != null && l_address.getHouseNumber() != null) {
          NominatimAPI l_api = new NominatimAPI(10);
          l_address = l_api.geocode(l_address);

          // Maak of overschrijf de cellen voor coordinaten
          Cell longCell = row.getCell(7);
          if (longCell == null) {
            longCell = row.createCell(7);
          }
          longCell.setCellValue(l_address.getLongitude());

          Cell latCell = row.getCell(8);
          if (latCell == null) {
            latCell = row.createCell(8);
          }
          latCell.setCellValue(l_address.getLatitude());

          System.out.println("Row " + rowIndex + ": " + l_address.getRoad() + " " + l_address.getHouseNumber() + " -> "
              + l_address.getLongitude() + ", " + l_address.getLatitude());
        }

        // Rate limiting voor Nominatim
        /*
         * try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
         */
      }
      rowIndex++;
    }
    // }
    // 4. SLA OP NAAR EEN NIEUW BESTAND
    try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
      workbook.write(outputStream);
      System.out.println("Bestand opgeslagen als: " + outputFile);
    }

    // 5. Sluit de werkmap en input stream
    workbook.close();
    file.close();
  }
}