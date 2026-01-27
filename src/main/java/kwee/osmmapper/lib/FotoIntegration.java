package kwee.osmmapper.lib;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import kwee.library.FileUtils;
import kwee.logger.MyLogger;

public class FotoIntegration {
  private static final Logger LOGGER = MyLogger.getLogger();

  // Bewaar de koppeling tussen adres en foto's
  private Map<String, List<File>> adresFotoMap = new HashMap<>();
  private Set<String> adressen = new HashSet<String>();
  private List<File> fotoBestanden = new ArrayList<File>();

  public FotoIntegration() {
    // Do Nothing
  }

  public FotoIntegration(String pictureSource, ArrayList<MemoContent> memocontarr) {
    LOGGER.log(Level.INFO, "Picture source: " + pictureSource);
    laadFotoKoppelingen(memocontarr);

    File testFile = new File(pictureSource);
    if (testFile.isDirectory()) {
      // Directory
      try {
        List<File> fotolist = FileUtils.getAllFiles(pictureSource);
        fotolist.forEach(f -> {
          String subdir = FileUtils.getSubdirectory(f, new File(pictureSource));
          if (!subdir.isBlank()) {
            fotoBestanden = adresFotoMap.get(subdir);
            if (fotoBestanden == null) {
              fotoBestanden = new ArrayList<File>();
            }
            fotoBestanden.add(f);
            adresFotoMap.put(subdir, fotoBestanden);
          } else {
            String fname = f.getName();
            String[] fidx = fname.split("_");
            if (fidx.length > 0) {
              subdir = fidx[0];
              if (adressen.contains(subdir)) {
                fotoBestanden = adresFotoMap.get(subdir);
                if (fotoBestanden == null) {
                  fotoBestanden = new ArrayList<File>();
                }
                fotoBestanden.add(f);
                adresFotoMap.put(subdir, fotoBestanden);
              } else {
                LOGGER.log(Level.INFO, "Voor foto " + fname + " geen adres gevonden.");
              }
            }
          }
        });
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, e.getMessage());
      }

    } else if (testFile.isFile()) {
      // File
    }
  }

  private void laadFotoKoppelingen(ArrayList<MemoContent> memocontarr) {
    adressen.clear();
    memocontarr.forEach(memoc -> {
      Address laddress = memoc.getAddress();
      String postcode = laddress.getPostalcode();
      String houseNumber = laddress.getHousenumber();
      String pictureIdx = String.format("%s%s", postcode.strip().replace(" ", "").toUpperCase(),
          houseNumber.strip().replace(" ", "").toUpperCase());
      adressen.add(pictureIdx);
    });
  }

  public List<File> getFotosVoorAdres(String adres) {
    List<File> larr = adresFotoMap.get(adres);
    if (larr == null) {
      larr = new ArrayList<File>();
    }
    LOGGER.log(Level.INFO, "Foto's opgehaald voor adres: " + adres);
    return larr;
  }
}
