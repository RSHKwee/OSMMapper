package kwee.osmmapper.lib;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FilePathAnalyzer {
    
    /**
     * Bepaal directe subdirectory van een bestand ten opzichte van root
     */
    public static String getImmediateSubdirectory(File file, File root) {
        Path relative = getRelativePath(file, root);
        
        if (relative != null && relative.getNameCount() > 1) {
            return relative.getName(0).toString();
        }
        
        return null;
    }
    
    /**
     * Bepaal alle parent directories tussen root en bestand
     */
    public static List<String> getAllSubdirectories(File file, File root) {
        List<String> directories = new ArrayList<>();
        Path relative = getRelativePath(file, root);
        
        if (relative != null && relative.getNameCount() > 1) {
            for (int i = 0; i < relative.getNameCount() - 1; i++) {
                directories.add(relative.getName(i).toString());
            }
        }
        
        return directories;
    }
    
    /**
     * Bepaal het volledige subdirectory pad (bv. "subdir1/subdir2/subdir3")
     */
    public static String getFullSubdirectoryPath(File file, File root) {
        Path relative = getRelativePath(file, root);
        
        if (relative != null && relative.getNameCount() > 1) {
            // Verwijder de bestandsnaam (laatste component)
            Path parentPath = relative.getParent();
            return parentPath != null ? parentPath.toString() : "";
        }
        
        return "";
    }
    
    /**
     * Bepaal hoe diep een bestand in de directory structuur zit
     */
    public static int getDirectoryDepth(File file, File root) {
        Path relative = getRelativePath(file, root);
        
        if (relative != null) {
            return Math.max(0, relative.getNameCount() - 1);
        }
        
        return 0;
    }
    
    /**
     * Helper: Bepaal relatief pad tussen bestand en root
     */
    private static Path getRelativePath(File file, File root) {
        try {
            Path filePath = file.toPath().toAbsolutePath().normalize();
            Path rootPath = root.toPath().toAbsolutePath().normalize();
            
            if (filePath.startsWith(rootPath)) {
                return rootPath.relativize(filePath);
            }
        } catch (Exception e) {
            // Log error indien nodig
        }
        
        return null;
    }
}
