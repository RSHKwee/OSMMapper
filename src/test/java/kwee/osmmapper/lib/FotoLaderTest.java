package kwee.osmmapper.lib;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FotoLaderTest {

  @TempDir
  Path tempDir;

  @Test
  void haalBestandsnamenNietRecursiefFiltertOpExtensie() throws IOException {
    Files.writeString(tempDir.resolve("a.jpg"), "x");
    Files.writeString(tempDir.resolve("b.txt"), "x");
    Files.writeString(tempDir.resolve("c.PNG"), "x");

    List<String> result = FotoLader.haalBestandsnamen(tempDir.toString(), false, "jpg", "png");

    assertThat(result).containsExactlyInAnyOrder("a.jpg", "c.PNG");
  }

  @Test
  void haalBestandsnamenZonderExtensiesGeeftAlles() throws IOException {
    Files.writeString(tempDir.resolve("a.jpg"), "x");
    Files.writeString(tempDir.resolve("b.txt"), "x");

    List<String> result = FotoLader.haalBestandsnamen(tempDir.toString(), false);

    assertThat(result).containsExactlyInAnyOrder("a.jpg", "b.txt");
  }

  @Test
  void haalBestandsnamenRecursief() throws IOException {
    Path sub = Files.createDirectory(tempDir.resolve("sub"));
    Files.writeString(sub.resolve("d.png"), "x");

    List<String> result = FotoLader.haalBestandsnamen(tempDir.toString(), true, "png");

    assertThat(result).containsExactly("d.png");
  }

  @Test
  void haalFotoBestandsnamenGebruiktStandaardExtensies() throws IOException {
    Files.writeString(tempDir.resolve("a.jpg"), "x");
    Files.writeString(tempDir.resolve("b.jpeg"), "x");
    Files.writeString(tempDir.resolve("c.gif"), "x");
    Files.writeString(tempDir.resolve("d.txt"), "x");

    List<String> result = FotoLader.haalFotoBestandsnamen(tempDir.toString(), false);

    assertThat(result).containsExactlyInAnyOrder("a.jpg", "b.jpeg", "c.gif");
  }

  @Test
  void onbestaandeDirectoryGeeftLegeLijst() {
    List<String> result = FotoLader.haalBestandsnamen("/pad/bestaat/niet", false, "jpg");
    assertThat(result).isEmpty();
  }
}