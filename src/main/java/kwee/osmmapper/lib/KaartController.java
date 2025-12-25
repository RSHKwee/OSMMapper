package kwee.osmmapper.lib;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import kwee.osmmapper.gui.OsmMapViewer;

//KAARTCONTROLLER.java - Vereenvoudigd
public class KaartController {
  private JTabbedPane kaartTabPane;
  private Map<String, OsmMapViewer> kaarten;

  public KaartController(JPanel container) {
    this.kaartTabPane = new JTabbedPane();
    this.kaarten = new HashMap<>();

    container.setLayout(new BorderLayout());
    container.add(kaartTabPane, BorderLayout.CENTER);

    String inputFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-adressenlijst_met_coordinaten.xlsx";
    String subtottitel = " Koophuizen";

    String warmteFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-warmtescan_met_coordinaten_new.xlsx";
    String subtitel = " Warmtescan";

    // Voeg standaard kaart toe
    voegKaartToe(inputFile, subtottitel, 52.1326, 5.2913, 7);
    voegKaartToe(warmteFile, subtitel, 52.1326, 5.2913, 7);
  }

  public void voegKaartToe(String fileNaam, String naam, double lat, double lon, int zoom) {
    try {
      // Maak nieuwe kaart
      OsmMapViewer kaart = new OsmMapViewer(fileNaam, naam);
      kaart.setVisible(false); // CRITIEK!

      // Maak container panel
      JPanel kaartContainer = new JPanel(new BorderLayout());
      kaartContainer.add(kaart.getContentPane(), BorderLayout.CENTER);

      // Voeg toe aan tabblad
      kaartTabPane.addTab(naam, kaartContainer);
      kaarten.put(naam, kaart);

      System.out.println("Kaart toegevoegd: " + naam);

    } catch (Exception e) {
      System.err.println("Fout bij aanmaken kaart: " + e.getMessage());
    }
  }

  public void toonKaart(String naam) {
    for (int i = 0; i < kaartTabPane.getTabCount(); i++) {
      if (kaartTabPane.getTitleAt(i).equals(naam)) {
        kaartTabPane.setSelectedIndex(i);
        System.out.println("Kaart getoond: " + naam);
        return;
      }
    }
    System.out.println("Kaart niet gevonden: " + naam);
  }

  public String[] getKaartNamen() {
    String[] namen = new String[kaartTabPane.getTabCount()];
    for (int i = 0; i < namen.length; i++) {
      namen[i] = kaartTabPane.getTitleAt(i);
    }
    return namen;
  }
}
