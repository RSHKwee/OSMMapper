package kwee.osmmapper.main;

import kwee.osmmapper.gui.OsmMapViewer;

public class Main {
  public static void main(String[] args) {
    // String inputFile =
    // "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-adressenlijst_met_coordinaten.xlsx";
    // String subtitel = " Koophuizen";

    String inputFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-warmtescan_met_coordinaten_new.xlsx";
    String subtitel = " Warmtescan";
    new OsmMapViewer(inputFile, subtitel).setVisible(true);
  }
}
