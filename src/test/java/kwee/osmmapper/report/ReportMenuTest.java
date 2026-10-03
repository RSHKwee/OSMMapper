package kwee.osmmapper.report;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ReportMenuTest {

  @TempDir
  Path tempDir;

  private static final String TAB = "TestTab";

  @BeforeEach
  void setUp() throws Exception {
    tempDir = Files.createTempDirectory("reportmenu-test-");
  }

  @AfterEach
  void tearDown() {
    // Best-effort cleanup; faalt niet als een bestand nog gelocked is
    try (var walk = Files.walk(tempDir)) {
      walk.sorted(Comparator.reverseOrder()).forEach(p -> {
        try {
          Files.deleteIfExists(p);
        } catch (Exception ignored) {
        }
      });
    } catch (Exception ignored) {
    }
  }

  @Test
  void nullTypesDoetNiets() {
    assertDoesNotThrow(() -> ReportMenu.generateReport(TAB, tempDir.toFile(), "x.xlsx", tempDir.toString(), null));
  }

  @Test
  void legeTypesDoetNiets() {
    assertDoesNotThrow(
        () -> ReportMenu.generateReport(TAB, tempDir.toFile(), "x.xlsx", tempDir.toString(), Collections.emptySet()));

    // geen bestanden aangemaakt
    File[] files = tempDir.toFile().listFiles();
    assertTrue(files == null || files.length == 0, "Er mogen geen bestanden gemaakt zijn bij lege selectie");
  }

  @Test
  void onbestaandExcelBestandWordtOpgevangen() {
    // ReportMenu vangt exceptions op en logt; mag niet crashen
    Set<ReportType> types = EnumSet.of(ReportType.MEMO_OVERZICHT);

    assertDoesNotThrow(() -> ReportMenu.generateReport(TAB, tempDir.toFile(),
        tempDir.resolve("bestaat_niet.xlsx").toString(), tempDir.toString(), types));
  }

  @Test
  void memoOverzichtMetGeldigExcelMaaktPdf() throws Exception {
    // Deze test werkt alleen als je een minimale geldige Excel kunt aanmaken
    // via OSMMapExcel. Hieronder een voorbeeld met een bestaand testbestand.
    File excel = new File("src/test/resources/test-memo.xlsx");
    org.junit.jupiter.api.Assumptions.assumeTrue(excel.exists(), "Test-Excelbestand niet aanwezig, test overgeslagen");

    Set<ReportType> types = EnumSet.of(ReportType.MEMO_OVERZICHT);

    ReportMenu.generateReport(TAB, tempDir.toFile(), excel.getAbsolutePath(), tempDir.toString(), types);

    Path expected = tempDir
        .resolve(TAB + "_Memo_overzicht_" + java.time.LocalDate.now().toString().replace("-", "") + ".pdf");

    assertTrue(Files.exists(expected), "Verwacht PDF-bestand niet gevonden: " + expected);
    assertTrue(Files.size(expected) > 0);
  }

  @Test
  void postcodeRapportenWordenOvergeslagenBijLegeFotoMap() {
    Set<ReportType> types = EnumSet.of(ReportType.POSTCODE_PER_STRAATKANT, ReportType.POSTCODE_EENVOUDIG);

    File legeFotoMap = tempDir.resolve("leeg").toFile();
    assertTrue(legeFotoMap.mkdirs());

    assertDoesNotThrow(() -> ReportMenu.generateReport("TestTab", legeFotoMap, tempDir.resolve("geen.xlsx").toString(),
        tempDir.toString(), types));
  }
}