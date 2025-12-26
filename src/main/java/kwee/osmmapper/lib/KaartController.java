package kwee.osmmapper.lib;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import kwee.logger.MyLogger;
import kwee.osmmapper.gui.OsmMapViewer;
import kwee.osmmapper.main.UserSetting;

public class KaartController {
  private static final Logger LOGGER = MyLogger.getLogger();
  private static KaartController uniqueInstance;
  private UserSetting m_params;
  private JTabbedPane kaartTabPane;
  private Map<String, OsmMapViewer> kaarten;
  private List<TabInfo> tablist;

  public static KaartController getInstance() {
    if (uniqueInstance == null) {
      uniqueInstance = new KaartController();
    }
    return uniqueInstance;
  }

  private KaartController() {
    this.kaarten = new HashMap<>();
    this.m_params = UserSetting.getInstance();
    this.tablist = new ArrayList<TabInfo>();
  }

  public void InitPanel(JPanel container, boolean duplicates) {
    this.kaartTabPane = new JTabbedPane();

    container.setLayout(new BorderLayout());
    container.add(kaartTabPane, BorderLayout.CENTER);

    List<TabInfo> itemsToProcess = new ArrayList<>();
    if (duplicates) {
      synchronized (m_params.get_TabState()) {
        itemsToProcess.addAll(m_params.get_TabState());
      }
    } else {
      synchronized (m_params.get_TabStateNoDup()) {
        itemsToProcess.addAll(m_params.get_TabStateNoDup());
      }
    }
    // Verwerk de items
    for (TabInfo tab : itemsToProcess) {
      String inputFile = tab.getFilePath();
      String subtottitel = tab.getTitle();
      voegKaartToe(inputFile, subtottitel, 0.0, 0.0, 0);
    }
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
      synchronized (tablist) {
        TabInfo tab = new TabInfo();
        tab.setFilePath(fileNaam);
        tab.setTitle(naam);
        tablist.add(tab);
        m_params.set_TabState(tablist);
        m_params.save();
      }

      LOGGER.log(Level.INFO, "Kaart toegevoegd: " + naam);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Fout bij aanmaken kaart: " + e.getMessage());
    }
  }

  public void toonKaart(String naam) {
    for (int i = 0; i < kaartTabPane.getTabCount(); i++) {
      if (kaartTabPane.getTitleAt(i).equals(naam)) {
        kaartTabPane.setSelectedIndex(i);
        LOGGER.log(Level.INFO, "Kaart getoond: " + naam);
        return;
      }
    }
    LOGGER.log(Level.INFO, "Kaart niet gevonden: " + naam);
  }

  // 1. Verwijder specifieke kaart
  public boolean verwijderKaart(String kaartNaam) {
    if (!kaarten.containsKey(kaartNaam))
      return false;

    // Verwijder uit map
    OsmMapViewer kaart = kaarten.remove(kaartNaam);
    if (kaart != null) {
      kaart.dispose(); // Belangrijk voor JFrame cleanup
    }

    // Verwijder tabblad
    for (int i = 0; i < kaartTabPane.getTabCount(); i++) {
      if (kaartTabPane.getTitleAt(i).equals(kaartNaam)) {
        kaartTabPane.removeTabAt(i);
        System.out.println("Kaart verwijderd: " + kaartNaam);
        return true;
      }
    }
    return false;
  }

  // 2. Verwijder huidige kaart
  public boolean verwijderHuidigeKaart() {
    int index = kaartTabPane.getSelectedIndex();
    if (index == -1)
      return false;

    String naam = kaartTabPane.getTitleAt(index);
    return verwijderKaart(naam);
  }

  // 3. Verwijder alle kaarten
  public void verwijderAlleKaarten() {
    while (kaartTabPane.getTabCount() > 0) {
      String naam = kaartTabPane.getTitleAt(0);
      verwijderKaart(naam);
    }
  }

  public String[] getKaartNamen() {
    String[] namen = new String[kaartTabPane.getTabCount()];
    for (int i = 0; i < namen.length; i++) {
      namen[i] = kaartTabPane.getTitleAt(i);
    }
    return namen;
  }
}
