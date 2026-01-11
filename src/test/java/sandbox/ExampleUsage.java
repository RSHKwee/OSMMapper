package sandbox;

import java.io.File;
import java.util.List;

import kwee.osmmapper.lib.FilePathAnalyzer;

public class ExampleUsage {
  public static void main(String[] args) {
    File root = new File("C:/Projects/MyApp");

    // Verschillende bestanden
    File[] files = { new File("D:/Data/Hoevelaken/Hoevelaken-adressenlijst.xlsx"),
        new File("D:/Data/Hoevelaken/Fotos/019.jpg"),
        new File("D:/Data/Hoevelaken/_Archief/hoevelaken-contacten_20260106.xlsx"),
        new File("D:/Data/Hoevelaken/_Archief/hoevelaken-contacten_202601070937.xlsx") };

    for (File file : files) {
      System.out.println("\nBestand: " + file.getName());
      System.out.println("Directe subdirectory: " + FilePathAnalyzer.getImmediateSubdirectory(file, root));

      List<String> allSubdirs = FilePathAnalyzer.getAllSubdirectories(file, root);
      System.out.println("Alle subdirectories: " + allSubdirs);

      System.out.println("Volledig subdirectory pad: " + FilePathAnalyzer.getFullSubdirectoryPath(file, root));

      System.out.println("Directory diepte: " + FilePathAnalyzer.getDirectoryDepth(file, root));
    }
  }
}
