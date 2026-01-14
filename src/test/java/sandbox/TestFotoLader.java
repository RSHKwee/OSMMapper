package sandbox;

import java.io.File;
import java.io.IOException;
import java.util.List;

import kwee.library.FileUtils;
import kwee.osmmapper.lib.FotoLader;

public class TestFotoLader {
  public static void main(String[] args) {
    String fotoMap = "D:\\Data\\Hoevelaken\\Fotos\\3871TD_15\\"; // Pas dit aan
    String rootdir = "D:\\Data\\Hoevelaken\\Fotos\\";

    // Test Methode 1: NIO
    System.out.println("=== Methode 1: Java NIO ===");
    List<String> fotosNIO = FotoLader.haalFotoBestandsnamen(fotoMap, false);
    fotosNIO.forEach(System.out::println);

    try {
      List<File> fotolist = FileUtils.getAllFiles(rootdir);
      fotolist.forEach(f -> {
        System.out.print(FileUtils.getSubdirectory(f, new File(rootdir)) + " ");
        System.out.println(f.getName());
      });
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // Test Methode 2: Traditioneel
    // System.out.println("\n=== Methode 2: Traditioneel ===");
    // List<String> fotosTraditioneel =
    // TraditioneleFotoLader.haalBestandsnamen(fotoMap);
    // fotosTraditioneel.forEach(System.out::println);

    // Test Methode 3: Met volgnummer sortering
    // System.out.println("\n=== Methode 3: Gesorteerd op volgnummer ===");
    // List<String> fotosGesorteerd =
    // TraditioneleFotoLader.haalBestandsnamenMetFilter(fotoMap);
    // fotosGesorteerd.forEach(System.out::println);
  }
}
