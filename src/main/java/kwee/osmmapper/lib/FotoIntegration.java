package kwee.osmmapper.lib;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import kwee.logger.MyLogger;

public class FotoIntegration {
  private static final Logger LOGGER = MyLogger.getLogger();

  // Bewaar de koppeling tussen adres en foto's
  private Map<String, List<File>> adresFotoMap = new HashMap<>();
  List<String> adressen;
  List<File> fotoBestanden;

  public FotoIntegration() {
    // TODO Nothing ???
  }

  public FotoIntegration(String pictureDirectory) {
    LOGGER.log(Level.INFO, "");
//TODO Initialiseren map
  }

  public void laadFotoKoppelingen(ArrayList<MemoContent> memocontarr) {
    // TODO

    // Eenvoudige koppeling op volgnummer
    for (int i = 0; i < Math.min(adressen.size(), fotoBestanden.size()); i++) {
      String adres = adressen.get(i);
      File foto = fotoBestanden.get(i);
      adresFotoMap.computeIfAbsent(adres, k -> new ArrayList<>()).add(foto);
    }
  }

  public List<File> getFotosVoorAdres(String adres) {
    File lpatsh = new File("D:\\Data\\Hoevelaken\\Fotos\\034.jpg");
    List<File> larr = new ArrayList<File>();
    larr.add(lpatsh);
    return larr;
    // return adresFotoMap.getOrDefault(adres, new ArrayList<>());
  }
}
