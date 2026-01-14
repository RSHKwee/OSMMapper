package sandbox;

import java.io.File;
import java.util.List;

import kwee.library.FileUtils;

//import kwee.osmmapper.lib.FilePathAnalyzer;

public class ExampleUsage {
  public static void main(String[] args) {
    File root = new File("D:/Data/Hoevelaken");

    // Verschillende bestanden
    File[] files = { new File("D:/Data/Hoevelaken/Hoevelaken-adressenlijst.xlsx"),
        new File("D:/Data/Hoevelaken/Fotos/019.jpg"),
        new File("D:/Data/Hoevelaken/_Archief/hoevelaken-contacten_20260106.xlsx"),
        new File("D:/Data/Hoevelaken/_Archief/hoevelaken-contacten_202601070937.xlsx"),
        new File("D:/Data/Hoevelaken/Fotos/3871TD_13/3871TD_13_1.jpg") };

    for (File file : files) {
      System.out.println("\nBestand: " + file.getName());
      System.out.println("Directe subdirectory: " + FileUtils.getImmediateSubdirectory(file, root));

      List<String> allSubdirs = FileUtils.getAllSubdirectories(file, root);
      System.out.println("Alle subdirectories: " + allSubdirs);
      String dirSubdir = FileUtils.getSubdirectory(file, root);
      System.out.println("Subdirectorie: " + dirSubdir);

      System.out.println("Volledig subdirectory pad: " + FileUtils.getFullSubdirectoryPath(file, root));

      System.out.println("Directory diepte: " + FileUtils.getDirectoryDepth(file, root));
    }
  }
}
