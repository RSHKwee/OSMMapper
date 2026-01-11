package kwee.osmmapper.lib;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileUtils {

  // Basis methode om alle File objecten te krijgen
  public static List<File> getAllFiles(String directoryPath) throws IOException {
    return getAllFiles(directoryPath, Integer.MAX_VALUE, EnumSet.noneOf(FileVisitOption.class));
  }

  // Met maximale diepte
  public static List<File> getAllFiles(String directoryPath, int maxDepth) throws IOException {
    return getAllFiles(directoryPath, maxDepth, EnumSet.noneOf(FileVisitOption.class));
  }

  // Volledige controle
  public static List<File> getAllFiles(String directoryPath, int maxDepth, Set<FileVisitOption> options)
      throws IOException {
    try (var walk = Files.walk(Paths.get(directoryPath), maxDepth, options.toArray(new FileVisitOption[0]))) {
      return walk.filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
    }
  }

  // File objecten gesorteerd op naam
  public static List<File> getAllFilesSortedByName(String directoryPath) throws IOException {
    try (var walk = Files.walk(Paths.get(directoryPath))) {
      return walk.filter(Files::isRegularFile).map(Path::toFile).sorted(Comparator.comparing(File::getName))
          .collect(Collectors.toList());
    }
  }

  // File objecten gesorteerd op grootte
  public static List<File> getAllFilesSortedBySize(String directoryPath) throws IOException {
    try (var walk = Files.walk(Paths.get(directoryPath))) {
      return walk.filter(Files::isRegularFile).map(Path::toFile).sorted(Comparator.comparingLong(File::length))
          .collect(Collectors.toList());
    }
  }

  // Alleen leesbare bestanden
  public static List<File> getReadableFiles(String directoryPath) throws IOException {
    try (var walk = Files.walk(Paths.get(directoryPath))) {
      return walk.filter(Files::isRegularFile).map(Path::toFile).filter(File::canRead).collect(Collectors.toList());
    }
  }
}
