package kwee.osmmapper.lib;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import kwee.osmmapper.gui.OsmMapViewer;

public class KaartController {
  private JTabbedPane kaartTabPane;
  private Map<String, OsmMapViewer> kaarten = new HashMap<>();

  public KaartController(JPanel container) {
    this.kaartTabPane = new JTabbedPane();
    container.setLayout(new BorderLayout());
    container.add(kaartTabPane, BorderLayout.CENTER);

    // Voeg 1 standaard kaart toe
    voegKaartToe("Standaard Kaart", 52.1326, 5.2913, 7);
  }

  public void voegKaartToe(String naam, double lat, double lon, int zoom) {
    OsmMapViewer kaart = new OsmMapViewer();
    kaart.setVisible(false); // BELANGRIJK!

    JPanel kaartContainer = new JPanel(new BorderLayout());
    kaartContainer.add(kaart.getContentPane(), BorderLayout.CENTER);

    kaartTabPane.addTab(naam, kaartContainer);
    kaarten.put(naam, kaart);

    System.out.println("Kaart toegevoegd: " + naam);
  }

  public void toonKaart(String naam) {
    for (int i = 0; i < kaartTabPane.getTabCount(); i++) {
      if (kaartTabPane.getTitleAt(i).equals(naam)) {
        kaartTabPane.setSelectedIndex(i);
        break;
      }
    }
  }
}
