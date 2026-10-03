package kwee.osmmapper.lib;

import static org.assertj.core.api.Assertions.assertThat;

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
    assertThat(fi.getFotosVoorAdres("1234AB12")).isEmpty();
    assertThat(fi.getPictureRootDir()).isNull();
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

    assertThat(fi.getPictureRootDir()).isEqualTo(tempDir.toString());
    assertThat(fi.getFotosVoorAdres("1234AB12")).hasSize(1);
  }

  @Test
  void onbekendAdresGeeftLegeLijst() {
    FotoIntegration fi = new FotoIntegration();
    assertThat(fi.getFotosVoorAdres("bestaatniet")).isEmpty();
  }
}
