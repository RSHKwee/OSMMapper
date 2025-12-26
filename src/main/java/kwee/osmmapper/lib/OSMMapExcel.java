package kwee.osmmapper.lib;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import kwee.library.Address;
import kwee.library.NominatimAPI;
import kwee.logger.MyLogger;

public class OSMMapExcel {
  private static final Logger LOGGER = MyLogger.getLogger();

  int postcodeidx = -1;
  int huisnummeridx = -1;
  int toevoegingidx = -1;
  int straatidx = -1;
  int plaatsidx = -1;
  int voornaamidx = -1;
  int achternaamidx = -1;
  int telefoonidx = -1;
  int mailadresidx = -1;
  int projectidx = -1;
  int longIndex = -1;
  int latIndex = -1;
  int maxCellCount = -1;

  String m_ExcelFile = "";

  public OSMMapExcel(String inputFile) {
    m_ExcelFile = inputFile;
  }

  public ArrayList<MemoContent> ReadExcel() {
    ArrayList<MemoContent> memocontarr = new ArrayList<MemoContent>();
    // 1. Open het bestand
    try (FileInputStream file = new FileInputStream(m_ExcelFile); Workbook workbook = WorkbookFactory.create(file)) {
      Sheet sheet = workbook.getSheetAt(0);
      int rowIndex = 0;
      for (Row row : sheet) {
        if (rowIndex == 0) {
          // Header
          int cellindex = 0;
          for (Cell cell : row) {
            CellType type = cell.getCellType();
            if (type == CellType.STRING) {
              String str = cell.getStringCellValue();
              if (str.equalsIgnoreCase("postcode")) {
                postcodeidx = cellindex;
              } else if (str.toLowerCase().contains("huisnummer")) {
                huisnummeridx = cellindex;
              } else if (str.toLowerCase().contains("toevoeg")) {
                toevoegingidx = cellindex;
              } else if (str.toLowerCase().contains("straat")) {
                straatidx = cellindex;
              } else if (str.toLowerCase().contains("plaats")) {
                plaatsidx = cellindex;
              } else if (str.toLowerCase().contains("voornaam")) {
                voornaamidx = cellindex;
              } else if (str.toLowerCase().contains("achternaam")) {
                achternaamidx = cellindex;
              } else if (str.toLowerCase().contains("telefoon")) {
                telefoonidx = cellindex;
              } else if (str.toLowerCase().contains("e-mail")) {
                mailadresidx = cellindex;
              } else if (str.toLowerCase().contains("project")) {
                projectidx = cellindex;
              } else if (str.toLowerCase().contains("long")) {
                longIndex = cellindex;
              } else if (str.toLowerCase().contains("lat")) {
                latIndex = cellindex;
              } else {
                LOGGER.log(Level.FINE, "cellindex: " + cellindex);
              }
            } else {
              LOGGER.log(Level.FINE, "Ander celltype: " + type.toString());
            }
            maxCellCount = cellindex;
            cellindex++;
          }
          LOGGER.log(Level.FINE, "maxCellCount: " + maxCellCount);
        } else {
          MemoContent memocont = new MemoContent();
          memocont = getMemoContFromRow(row);
          if (!memocont.isEmpty()) {
            memocontarr.add(memocont);
          }
        }
        rowIndex++;
      }
      workbook.close();
      file.close();
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, e.getMessage());
    }
    return memocontarr;
  }

  /**
   * Add Longitude and Latitude to Address.
   * 
   * @param outputFile Excel with Longtitude and Latitude
   */
  public void WriteExcel(String outputFile) {
    try (FileInputStream file = new FileInputStream(m_ExcelFile); Workbook workbook = WorkbookFactory.create(file)) {
      Sheet sheet = workbook.getSheetAt(0);
      int totalRows = sheet.getLastRowNum() + 1;
      LOGGER.log(Level.INFO, "Totaal aantal rijen te verwerken: ~" + totalRows);

      int rowIndex = 0;
      for (Row row : sheet) {
        if (rowIndex == 0) {
          if ((longIndex == -1) || (latIndex == -1)) {
            maxCellCount++;
            longIndex = maxCellCount;
            Cell longCell = row.getCell(longIndex);
            if (longCell == null) {
              longCell = row.createCell(longIndex);
            }
            longCell.setCellValue("Longitude");

            maxCellCount++;
            latIndex = maxCellCount;
            Cell latCell = row.getCell(latIndex);
            if (latCell == null) {
              latCell = row.createCell(latIndex);
            }
            latCell.setCellValue("Latitude");
          }
        } else {
          MemoContent memocont = new MemoContent();
          memocont = getMemoContFromRow(row);

          Address l_address = new Address();
          l_address.setRoad(memocont.getStreet());
          l_address.setHouseNumber(memocont.getHousenumber());
          l_address.setCity(memocont.getCity());
          l_address.setCountry(memocont.getCountry());

          // Voer geocoding uit als adresgegevens aanwezig zijn
          if (l_address.getRoad() != null) {
            if (!l_address.getRoad().isBlank() && l_address.getHouseNumber() != null) {
              NominatimAPI l_api = new NominatimAPI(10);
              try {
                l_address = l_api.geocode(l_address);

                // Maak of overschrijf de cellen voor coordinaten
                Cell longCell = row.getCell(longIndex);
                if (longCell == null) {
                  longCell = row.createCell(longIndex);
                }
                longCell.setCellValue(l_address.getLongitude());

                Cell latCell = row.getCell(latIndex);
                if (latCell == null) {
                  latCell = row.createCell(latIndex);
                }
                latCell.setCellValue(l_address.getLatitude());
              } catch (Exception e) {
                // do nothing
              }
              LOGGER.log(Level.INFO, "Row " + rowIndex + ": " + l_address.getRoad() + " " + l_address.getHouseNumber()
                  + " " + l_address.getCity() + " -> " + l_address.getLongitude() + ", " + l_address.getLatitude());
            }
          }
        }
        rowIndex++;
      }

      // 4. SLA OP NAAR EEN NIEUW BESTAND
      try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
        workbook.write(outputStream);
        LOGGER.log(Level.INFO, "Bestand opgeslagen als: " + outputFile);
      }

      // 5. Sluit de werkmap en input stream
      workbook.close();
      file.close();
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, e.getMessage());
    }
  }

  private MemoContent getMemoContFromRow(Row row) {
    MemoContent memocont = new MemoContent();
    try {
      if (straatidx != -1) {
        Cell l_cell = row.getCell(straatidx);
        if (l_cell != null) {
          memocont.setStreet(getCelValue(l_cell));
        }
      }
      if (huisnummeridx != -1) {
        Cell l_cell = row.getCell(huisnummeridx);
        if (l_cell != null) {
          memocont.setHousenumber(getCelValue(l_cell));
          if (toevoegingidx != -1) {
            l_cell = row.getCell(toevoegingidx);
            if (l_cell != null) {
              String housenr = memocont.getHousenumber() + getCelValue(l_cell);
              memocont.setHousenumber(housenr);
            }
          }
        }
      }
      if (postcodeidx != -1) {
        Cell l_cell = row.getCell(postcodeidx);
        if (l_cell != null) {
          memocont.setPostcode(getCelValue(l_cell));
        }
      }
      if (plaatsidx != -1) {
        Cell l_cell = row.getCell(plaatsidx);
        if (l_cell != null) {
          memocont.setCity(getCelValue(l_cell));
        }
      }
      if (voornaamidx != -1) {
        Cell l_cell = row.getCell(voornaamidx);
        if (l_cell != null) {
          memocont.setSurname(getCelValue(l_cell));
        }
      }
      if (achternaamidx != -1) {
        Cell l_cell = row.getCell(achternaamidx);
        if (l_cell != null) {
          memocont.setFamilyname(getCelValue(l_cell));
        }
      }
      if (telefoonidx != -1) {
        Cell l_cell = row.getCell(telefoonidx);
        if (l_cell != null) {
          memocont.setPhonenumber(getCelValue(l_cell));
        }
      }
      if (mailadresidx != -1) {
        Cell l_cell = row.getCell(mailadresidx);
        if (l_cell != null) {
          memocont.setMailaddress(getCelValue(l_cell));
        }
      }
      if (projectidx != -1) {
        Cell l_cell = row.getCell(projectidx);
        if (l_cell != null) {
          memocont.setProjects(getCelValue(l_cell));
        }
      }
      if (longIndex != -1) {
        Cell l_cell = row.getCell(longIndex);
        if (l_cell != null) {
          memocont.setLongitude(l_cell.getNumericCellValue());
        }
      }
      if (latIndex != -1) {
        Cell l_cell = row.getCell(latIndex);
        if (l_cell != null) {
          memocont.setLatitude(l_cell.getNumericCellValue());
        }
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    memocont.setCountry("Netherlands");
    return memocont;
  }

  private String getCelValue(Cell a_cell) {
    String str = "";
    CellType type = a_cell.getCellType();
    if (type == CellType.STRING) {
      str = a_cell.getStringCellValue();
    } else if (type == CellType.NUMERIC) {
      Double huisnr = a_cell.getNumericCellValue();
      int iHuisnr = huisnr.intValue();
      str = Integer.toString(iHuisnr);
    } else if (type == CellType.BOOLEAN) {
      boolean bstat = a_cell.getBooleanCellValue();
      str = Boolean.toString(bstat);
    }
    return str;
  }
}
