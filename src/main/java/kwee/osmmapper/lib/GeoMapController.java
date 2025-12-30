package kwee.osmmapper.lib;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import kwee.library.ApplicationMessages;
import kwee.logger.MyLogger;
import kwee.osmmapper.gui.OsmMapViewer;
import kwee.osmmapper.main.UserSetting;

public class GeoMapController {
  private static final Logger LOGGER = MyLogger.getLogger();
  private static GeoMapController uniqueInstance;
  private UserSetting m_params;
  private ApplicationMessages bundle = ApplicationMessages.getInstance();
  private JTabbedPane kaartTabPane;
  private Map<String, OsmMapViewer> kaarten;
  private List<TabInfo> tablist;
  private boolean m_duplicates = false;

  /**
   * Singleton
   * 
   * @return Instance
   */
  public static GeoMapController getInstance() {
    if (uniqueInstance == null) {
      uniqueInstance = new GeoMapController();
    }
    return uniqueInstance;
  }

  /**
   * Private constructor part of Singleton
   */
  private GeoMapController() {
    this.kaarten = new HashMap<>();
    this.m_params = UserSetting.getInstance();
    this.tablist = new ArrayList<TabInfo>();
  }

  /**
   * Construct panel
   * 
   * @param container  Container panel.
   * @param duplicates Duplicate tab names allowed or not.
   */
  public void InitPanel(JPanel container, boolean duplicates) {
    this.kaartTabPane = new JTabbedPane();
    this.m_duplicates = duplicates;
    LOGGER.log(Level.INFO, bundle.getMessage("DoubleTabsSet", Boolean.toString(this.m_duplicates)));

    container.setLayout(new BorderLayout());
    container.add(kaartTabPane, BorderLayout.CENTER);
    voegContextMenuToe();

    List<TabInfo> itemsToProcess = new ArrayList<>();
    if (duplicates) {
      this.tablist = m_params.get_TabState();
    } else {
      this.tablist = m_params.get_TabStateNoDup();
      m_params.set_TabState(tablist);
      m_params.save();
    }
    synchronized (m_params.get_TabState()) {
      itemsToProcess.addAll(tablist);

      // Verwerk de items
      for (TabInfo tab : itemsToProcess) {
        String inputFile = tab.getFilePath();
        String subtottitel = tab.getTitle();
        double lat = tab.getLatitude();
        double lon = tab.getLongtitude();
        int zoom = tab.getZoomfactor();
        voegKaartToe(inputFile, subtottitel, lat, lon, zoom, false);
      }
    }
  }

  /**
   * Add tab with map
   * 
   * @param fileNaam Ecel filename with marker information
   * @param naam     Name of tab
   * @param lat      Latitude center
   * @param lon      Longitude center
   * @param zoom     Zoom factor
   */
  public void voegKaartToe(String fileNaam, String naam, double lat, double lon, int zoom) {
    voegKaartToe(fileNaam, naam, lat, lon, zoom, true);
  }

  public void voegKaartToe(String fileNaam, String naam, double lat, double lon, int zoom, boolean administrate) {
    try {
      // Maak nieuwe kaart
      OsmMapViewer kaart = new OsmMapViewer(fileNaam, naam, lat, lon, zoom);
      kaart.setVisible(false);

      // Maak container panel
      JPanel kaartContainer = new JPanel(new BorderLayout());
      kaartContainer.add(kaart.getContentPane(), BorderLayout.CENTER);

      // Voeg toe aan tabblad
      kaartTabPane.addTab(naam, kaartContainer);
      kaarten.put(naam, kaart);
      if (administrate) {
        synchronized (tablist) {
          TabInfo tab = new TabInfo();
          tab.setFilePath(fileNaam);
          tab.setTitle(naam);
          tablist.add(tab);
          m_params.set_TabState(tablist);
          m_params.save();
        }
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

  // Verwijder specifieke kaart
  int i = 0;

  private boolean verwijderKaart(String kaartNaam) {
    boolean bstat = false;
    if (kaarten.containsKey(kaartNaam)) {
      // Verwijder uit map
      OsmMapViewer kaart = kaarten.remove(kaartNaam);
      if (kaart != null) {
        kaart.dispose(); // Belangrijk voor JFrame cleanup
      }

      // Verwijder tabblad
      for (int i = 0; i < kaartTabPane.getTabCount(); i++) {
        if (kaartTabPane.getTitleAt(i).equals(kaartNaam)) {
          kaartTabPane.removeTabAt(i);
          LOGGER.log(Level.FINE, "Kaart verwijderd: " + kaartNaam);
          bstat = true;
        }
      }

      // Verwijder uit preference
      if (bstat) {
        i = -1;
        synchronized (tablist) {
          List<TabInfo> l_tablist = new ArrayList<TabInfo>(tablist);
          for (int i = 0; i < tablist.size(); i++) {
            TabInfo tab = new TabInfo();
            tab = l_tablist.get(i);
            if (tab.getTitle().equalsIgnoreCase(kaartNaam)) {
              l_tablist.remove(i);
              m_params.set_TabState(l_tablist);
              m_params.save();
            }
          }
          tablist = l_tablist;
        }
      }
    }
    return bstat;
  }

  public String[] getKaartNamen() {
    String[] namen = new String[kaartTabPane.getTabCount()];
    for (int i = 0; i < namen.length; i++) {
      namen[i] = kaartTabPane.getTitleAt(i);
    }
    return namen;
  }

  // Voeg context menu toe aan tabblad pane
  private void voegContextMenuToe() {
    JPopupMenu contextMenu = new JPopupMenu();

    JMenuItem verwijderItem = new JMenuItem("Verwijder kaart");
    verwijderItem.addActionListener(e -> {
      int tabIndex = kaartTabPane.getSelectedIndex();
      if (tabIndex != -1) {
        String naam = kaartTabPane.getTitleAt(tabIndex);
        if (verwijderKaartMetBevestiging(naam, kaartTabPane)) {
          LOGGER.log(Level.INFO, "Kaart " + naam + " verwijderd.");
        }
      }
    });

    JMenuItem hernoemItem = new JMenuItem("Hernoem kaart");
    hernoemItem.addActionListener(e -> {
      int tabIndex = kaartTabPane.getSelectedIndex();
      if (tabIndex != -1) {
        String oudeNaam = kaartTabPane.getTitleAt(tabIndex);
        String nieuweNaam = JOptionPane.showInputDialog(kaartTabPane, "Nieuwe naam voor kaart:", oudeNaam);

        if (nieuweNaam != null && !nieuweNaam.trim().isEmpty()) {
          hernoemKaart(oudeNaam, nieuweNaam);
          LOGGER.log(Level.FINE, "Kaart hernoemd: " + oudeNaam + " -> " + nieuweNaam);
          synchronized (tablist) {
            List<TabInfo> l_tablist = new ArrayList<TabInfo>(tablist);
            for (int i = 0; i < tablist.size(); i++) {
              TabInfo tab = new TabInfo();
              tab = l_tablist.get(i);
              if (tab.getTitle().equalsIgnoreCase(oudeNaam)) {
                l_tablist.remove(i);
                tab.setTitle(nieuweNaam);
                l_tablist.add(tab);
                m_params.set_TabState(l_tablist);
                m_params.save();
              }
            }
            tablist = l_tablist;
          }
        }
      }
    });

    contextMenu.add(verwijderItem);
    contextMenu.add(hernoemItem);

    // Voeg mouse listener toe voor context menu
    kaartTabPane.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
          showContextMenu(e);
        }
      }

      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          showContextMenu(e);
        }
      }

      private void showContextMenu(MouseEvent e) {
        int tabIndex = kaartTabPane.getUI().tabForCoordinate(kaartTabPane, e.getX(), e.getY());
        if (tabIndex != -1) {
          kaartTabPane.setSelectedIndex(tabIndex);
          contextMenu.show(kaartTabPane, e.getX(), e.getY());
        }
      }
    });
  }

  // Verwijder met bevestigingsdialoog
  public boolean verwijderKaartMetBevestiging(String kaartNaam, Component parent) {
    if (!kaarten.containsKey(kaartNaam)) {
      JOptionPane.showMessageDialog(parent, "Kaart '" + kaartNaam + "' niet gevonden.", "Fout",
          JOptionPane.ERROR_MESSAGE);
      return false;
    }

    // Toon bevestigingsdialoog
    int bevestiging = JOptionPane.showConfirmDialog(parent,
        "Weet je zeker dat je kaart '" + kaartNaam + "' wilt verwijderen?", "Kaart verwijderen",
        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

    if (bevestiging == JOptionPane.YES_OPTION) {
      boolean succes = verwijderKaart(kaartNaam);
      return succes;
    }
    return false;
  }

  private void hernoemKaart(String oudeNaam, String nieuweNaam) {
    if (kaarten.containsKey(oudeNaam)) {
      // Verplaats kaart naar nieuwe naam
      OsmMapViewer kaart = kaarten.remove(oudeNaam);
      kaarten.put(nieuweNaam, kaart);

      // Update tab titel
      for (int i = 0; i < kaartTabPane.getTabCount(); i++) {
        if (kaartTabPane.getTitleAt(i).equals(oudeNaam)) {
          kaartTabPane.setTitleAt(i, nieuweNaam);

          // Update tab component label als die bestaat
          Component tabComp = kaartTabPane.getTabComponentAt(i);
          if (tabComp instanceof JPanel) {
            for (Component c : ((JPanel) tabComp).getComponents()) {
              if (c instanceof JLabel) {
                ((JLabel) c).setText(nieuweNaam);
              }
            }
          }
        }
      }
      LOGGER.log(Level.INFO, "Kaart hernoemd: " + oudeNaam + " -> " + nieuweNaam);
    }
  }
}
