package kwee.osmmapper.main;

import javax.swing.SwingUtilities;

import kwee.osmmapper.gui.HoofdMenu;

public class Main {
  // String inputFile =
  // "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-adressenlijst_met_coordinaten.xlsx";
  // String subtitel = " Koophuizen";

  String inputFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-warmtescan_met_coordinaten_new.xlsx";
  String subtitel = " Warmtescan";

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new HoofdMenu().start());
  }
}
