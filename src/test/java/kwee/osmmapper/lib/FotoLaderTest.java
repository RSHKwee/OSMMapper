package kwee.osmmapper.lib;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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
    List<String> expected = Arrays.asList("a.jpg", "c.PNG");
    assertTrue(result.containsAll(expected));
  }

  @Test
  void haalBestandsnamenZonderExtensiesGeeftAlles() throws IOException {
    Files.writeString(tempDir.resolve("a.jpg"), "x");
    Files.writeString(tempDir.resolve("b.txt"), "x");

    List<String> result = FotoLader.haalBestandsnamen(tempDir.toString(), false);
    List<String> expected = Arrays.asList("a.jpg", "b.txt");
    assertTrue(result.containsAll(expected));
  }

  @Test
  void haalBestandsnamenRecursief() throws IOException {
    Path sub = Files.createDirectory(tempDir.resolve("sub"));
    Files.writeString(sub.resolve("d.png"), "x");

    List<String> result = FotoLader.haalBestandsnamen(tempDir.toString(), true, "png");
    List<String> expected = Arrays.asList("d.png");
    assertTrue(result.containsAll(expected));
  }

  @Test
  void haalFotoBestandsnamenGebruiktStandaardExtensies() throws IOException {
    Files.writeString(tempDir.resolve("a.jpg"), "x");
    Files.writeString(tempDir.resolve("b.jpeg"), "x");
    Files.writeString(tempDir.resolve("c.gif"), "x");
    Files.writeString(tempDir.resolve("d.txt"), "x");

    List<String> result = FotoLader.haalFotoBestandsnamen(tempDir.toString(), false);
    List<String> expected = Arrays.asList("a.jpg", "b.jpeg", "c.gif");
    assertTrue(result.containsAll(expected));
  }

  @Test
  void onbestaandeDirectoryGeeftLegeLijst() {
    List<String> result = FotoLader.haalBestandsnamen("/pad/bestaat/niet", false, "jpg");
    assertTrue(result.isEmpty());
  }
}