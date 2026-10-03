package kwee.osmmapper.lib;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;

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

    assertThat(result).hasSize(1);
    MemoContent memo = result.get(0);
    assertThat(memo.getPostcode()).isEqualTo("1234AB");
    assertThat(memo.getHousenumber()).isEqualTo("12");
    assertThat(memo.getStreet()).isEqualTo("Kerkstraat");
    assertThat(memo.getCity()).isEqualTo("Amsterdam");
  }

  @Test
  void getStreet4ZipCodeWerkt() throws Exception {
    Path file = maakTestExcel();
    OSMMapExcel excel = new OSMMapExcel(file.toString());
    excel.ReadExcel();

    assertThat(excel.getStreet4ZipCode("1234AB")).isEqualTo("Kerkstraat");
    assertThat(excel.getStreet4ZipCode("9999ZZ")).isNull();
  }

  @Test
  void getCitiesBevatAmsterdam() throws Exception {
    Path file = maakTestExcel();
    OSMMapExcel excel = new OSMMapExcel(file.toString());
    excel.ReadExcel();

    assertThat(excel.getCities()).contains("Amsterdam");
  }
}