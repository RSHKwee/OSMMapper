package kwee.osmmapper.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FotoIntegrationTest {

  @TempDir
  Path tempDir;

  @Test
  void legeConstructorGeeftLegeFotosVoorAdres() {
    FotoIntegration fi = new FotoIntegration();
    assertTrue((fi.getFotosVoorAdres("1234AB12")).isEmpty());
    assertNull(fi.getPictureRootDir());
  }

  @Test
  void constructorMetDirectoryKoppeltFotosAanAdres() throws IOException {
    // Adres in MemoContent: postcode 1234AB + huisnummer 12 -> index 1234AB12
    MemoContent memo = new MemoContent();
    memo.setPostcode("1234 AB");
    memo.setHousenumber("12");
    memo.setStreet("Kerkstraat");

    ArrayList<MemoContent> memoList = new ArrayList<>();
    memoList.add(memo);

    // Maak een subdirectory met de adres-index als naam
    Path adresDir = Files.createDirectory(tempDir.resolve("1234AB12"));
    Files.writeString(adresDir.resolve("foto1.jpg"), "x");

    FotoIntegration fi = new FotoIntegration(tempDir.toString(), memoList);

    assertEquals(tempDir.toString(), fi.getPictureRootDir());
    assertEquals(1, fi.getFotosVoorAdres("1234AB12").size());
  }

  @Test
  void onbekendAdresGeeftLegeLijst() {
    FotoIntegration fi = new FotoIntegration();
    assertTrue((fi.getFotosVoorAdres("bestaatniet")).isEmpty());
  }
}
