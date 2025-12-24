package kwee.osmmapper.lib;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ReadOSMMapExcel {

  public static ArrayList<MemoContent> ReadExcel(String inputFile) {
    ArrayList<MemoContent> memocontarr = new ArrayList<MemoContent>();
    // 1. Open het bestand
    try (FileInputStream file = new FileInputStream(inputFile); Workbook workbook = WorkbookFactory.create(file)) {
      Sheet sheet = workbook.getSheetAt(0);

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
              }
            } else {
              System.out.println();
            }
            cellindex++;
          }
        } else {
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
          if (!memocont.isEmpty()) {
            memocontarr.add(memocont);
          }
        }
        rowIndex++;
      }
      workbook.close();
      file.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return memocontarr;
  }

  private static String getCelValue(Cell a_cell) {
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
