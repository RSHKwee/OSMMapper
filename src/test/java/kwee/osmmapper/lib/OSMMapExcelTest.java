package kwee.osmmapper.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class OSMMapExcelTest {

  @TempDir
  Path tempDir;

  private Path maakTestExcel() throws Exception {
    Path file = tempDir.resolve("test.xlsx");
    try (XSSFWorkbook wb = new XSSFWorkbook()) {
      Sheet sheet = wb.createSheet("Blad1");
      Row header = sheet.createRow(0);
      header.createCell(0).setCellValue("postcode");
      header.createCell(1).setCellValue("huisnummer");
      header.createCell(2).setCellValue("straat");
      header.createCell(3).setCellValue("plaats");

      Row row = sheet.createRow(1);
      row.createCell(0).setCellValue("1234AB");
      row.createCell(1).setCellValue(12);
      row.createCell(2).setCellValue("Kerkstraat");
      row.createCell(3).setCellValue("Amsterdam");

      try (FileOutputStream out = new FileOutputStream(file.toFile())) {
        wb.write(out);
      }
    }
    return file;
  }

  @Test
  void readExcelLeestRijIn() throws Exception {
    Path file = maakTestExcel();
    OSMMapExcel excel = new OSMMapExcel(file.toString());

    ArrayList<MemoContent> result = excel.ReadExcel();

    assertEquals(1, result.size());
    MemoContent memo = result.get(0);

    assertEquals("1234AB", memo.getPostcode());
    assertEquals("12", memo.getHousenumber());
    assertEquals("Kerkstraat", memo.getStreet());
    assertEquals("Amsterdam", memo.getCity());
  }

  @Test
  void getStreet4ZipCodeWerkt() throws Exception {
    Path file = maakTestExcel();
    OSMMapExcel excel = new OSMMapExcel(file.toString());
    excel.ReadExcel();

    assertEquals("Kerkstraat", excel.getStreet4ZipCode("1234AB"));
    assertNull(excel.getStreet4ZipCode("9999ZZ"));
  }

  @Test
  void getCitiesBevatAmsterdam() throws Exception {
    Path file = maakTestExcel();
    OSMMapExcel excel = new OSMMapExcel(file.toString());
    excel.ReadExcel();
    List<String> l_cities = new ArrayList<String>(excel.getCities());
    assertTrue(l_cities.contains("Amsterdam"));
  }
}