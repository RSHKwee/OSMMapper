package sandbox;

import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.MapMarkerCircle;
import javax.swing.*;
import java.awt.*;

public class HoofdMenuMetKaart {
  private JFrame hoofdFrame;
  private JTabbedPane tabPane;
  private JTextArea logArea;
  private JMapViewer kaartViewer; // Bewaar referentie

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new HoofdMenuMetKaart().createGUI());
  }

  private void createGUI() {
    hoofdFrame = new JFrame("Hoofdmenu met Kaart");
    hoofdFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    hoofdFrame.setSize(1200, 800);

    tabPane = new JTabbedPane();
    tabPane.addTab("Dashboard", createDashboard());
    tabPane.addTab("Kaart", createKaartPaneel());
    tabPane.addTab("Configuratie", createConfiguratiePaneel());

    hoofdFrame.add(tabPane, BorderLayout.CENTER);
    hoofdFrame.add(createLogPaneel(), BorderLayout.SOUTH);

    hoofdFrame.setVisible(true);
    log("Applicatie gestart");
  }

  private JPanel createKaartPaneel() {
    JPanel kaartContainer = new JPanel(new BorderLayout());

    try {
      JMapViewerTree treeMap = new JMapViewerTree("Kaartlagen");
      kaartViewer = treeMap.getViewer(); // Bewaar referentie

      // Configureer kaart
      kaartViewer.setTileSource(new OsmTileSource.Mapnik());
      kaartViewer.setTileLoader(new OsmTileLoader(kaartViewer));
      kaartViewer.setZoomContolsVisible(false); // We maken eigen knoppen
      // kaartViewer.setDisplayPositionByLatLon(52.1326, 5.2913, 7); // CORRECTE METHODE

      kaartContainer.add(treeMap, BorderLayout.CENTER);

      // Eigen controlepaneel
      JPanel controlePanel = createKaartControles();
      kaartContainer.add(controlePanel, BorderLayout.NORTH);

      log("Kaart geladen in tabblad");

    } catch (Exception e) {
      log("FOUT bij laden kaart: " + e.getMessage());
      kaartContainer.add(new JLabel("Kaart kon niet geladen worden"), BorderLayout.CENTER);
    }

    return kaartContainer;
  }

  private JPanel createKaartControles() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    panel.setBorder(BorderFactory.createTitledBorder("Kaartcontroles"));

    // Zoom knoppen
    JButton zoomInBtn = new JButton("+ Zoom In");
    zoomInBtn.addActionListener(e -> {
      if (kaartViewer != null) {
        kaartViewer.setZoom(kaartViewer.getZoom() + 1);
        log("Zoom in naar niveau: " + kaartViewer.getZoom());
      }
    });

    JButton zoomOutBtn = new JButton("- Zoom Uit");
    zoomOutBtn.addActionListener(e -> {
      if (kaartViewer != null && kaartViewer.getZoom() > 0) {
        kaartViewer.setZoom(kaartViewer.getZoom() - 1);
        log("Zoom uit naar niveau: " + kaartViewer.getZoom());
      }
    });

    // Locatie knoppen
    JButton nederlandBtn = new JButton("Nederland");
    nederlandBtn.addActionListener(e -> centerOpLocatie(52.1326, 5.2913, 7, "Nederland"));

    JButton amsterdamBtn = new JButton("Amsterdam");
    amsterdamBtn.addActionListener(e -> centerOpLocatie(52.3676, 4.9041, 12, "Amsterdam"));

    JButton rotterdamBtn = new JButton("Rotterdam");
    rotterdamBtn.addActionListener(e -> centerOpLocatie(51.9244, 4.4777, 12, "Rotterdam"));

    // Marker knop
    JButton addMarkerBtn = new JButton("Voeg Marker Toe");
    addMarkerBtn.addActionListener(e -> voegMarkerToe());

    panel.add(zoomInBtn);
    panel.add(zoomOutBtn);
    panel.add(Box.createHorizontalStrut(20));
    panel.add(nederlandBtn);
    panel.add(amsterdamBtn);
    panel.add(rotterdamBtn);
    panel.add(Box.createHorizontalStrut(20));
    panel.add(addMarkerBtn);

    return panel;
  }

  private void centerOpLocatie(double lat, double lon, int zoom, String locatieNaam) {
    if (kaartViewer != null) {
      // CORRECTE METHODE om kaart te centreren
      // kaartViewer.setDisplayPositionByLatLon(lat, lon, zoom);
      log("Gecentreerd op: " + locatieNaam + " (" + lat + ", " + lon + ")");
    }
  }

  private void voegMarkerToe() {
    if (kaartViewer != null) {
      // Voeg een marker toe op huidige kaartcentrum
      ICoordinate coord = kaartViewer.getPosition();
      if (coord != null) {
        // MapMarker marker = new MapMarkerCircle("Marker", coord);
        // kaartViewer.addMapMarker(marker);
        log("Marker toegevoegd op: " + coord.getLat() + ", " + coord.getLon());
      }
    }
  }

  private JPanel createDashboard() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(245, 245, 250));

    JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
    buttonPanel.setBorder(BorderFactory.createTitledBorder("Snelle Acties"));

    JButton naarKaartKnop = new JButton("Ga naar Kaart Tabblad");
    naarKaartKnop.addActionListener(e -> {
      tabPane.setSelectedIndex(1);
      log("Naar kaart tabblad gegaan");
    });

    JButton huidigeLocatieKnop = new JButton("Toon Huidige Locatie");
    huidigeLocatieKnop.addActionListener(e -> {
      if (kaartViewer != null) {
        ICoordinate coord = kaartViewer.getPosition();
        if (coord != null) {
          String msg = String.format("Huidige locatie: Lat: %.4f, Lon: %.4f, Zoom: %d", coord.getLat(), coord.getLon(),
              kaartViewer.getZoom());
          log(msg);
          JOptionPane.showMessageDialog(hoofdFrame, msg, "Huidige Locatie", JOptionPane.INFORMATION_MESSAGE);
        }
      }
    });

    JButton clearMarkersKnop = new JButton("Verwijder Alle Markers");
    clearMarkersKnop.addActionListener(e -> {
      if (kaartViewer != null) {
        kaartViewer.removeAllMapMarkers();
        log("Alle markers verwijderd");
      }
    });

    buttonPanel.add(naarKaartKnop);
    buttonPanel.add(huidigeLocatieKnop);
    buttonPanel.add(clearMarkersKnop);

    panel.add(buttonPanel, BorderLayout.NORTH);

    // Info paneel
    JTextArea infoArea = new JTextArea(5, 40);
    infoArea.setText("Kaartapplicatie Dashboard\n\n" + "• Gebruik het Kaart tabblad om te navigeren\n"
        + "• Voeg markers toe met de knop op de kaart\n" + "• Bekijk locaties met de vooringestelde knoppen");
    infoArea.setEditable(false);
    infoArea.setFont(new Font("Arial", Font.PLAIN, 14));
    panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

    return panel;
  }

  private JPanel createConfiguratiePaneel() {
    JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
    panel.setBorder(BorderFactory.createTitledBorder("Configuratie"));

    panel.add(new JLabel("Tile Source:"));
    JComboBox<String> tileCombo = new JComboBox<>(new String[] { "Mapnik", "Cycle", "Transport" });
    tileCombo.addActionListener(e -> {
      if (kaartViewer != null) {
        String keuze = (String) tileCombo.getSelectedItem();
        switch (keuze) {
        // case "Cycle": kaartViewer.setTileSource(new OsmTileSource.CycleMap()); break;
        case "Transport":
          kaartViewer.setTileSource(new OsmTileSource.TransportMap());
          break;
        default:
          kaartViewer.setTileSource(new OsmTileSource.Mapnik());
        }
        log("Tile source gewijzigd naar: " + keuze);
      }
    });
    panel.add(tileCombo);

    panel.add(new JLabel("Kaartlagen:"));
    JCheckBox markersBox = new JCheckBox("Toon markers", true);
    markersBox.addActionListener(e -> log("Markers " + (markersBox.isSelected() ? "ingeschakeld" : "uitgeschakeld")));
    panel.add(markersBox);

    panel.add(new JLabel("Cache modus:"));
    JCheckBox cacheBox = new JCheckBox("Gebruik cache", true);
    panel.add(cacheBox);

    return panel;
  }

  private JPanel createLogPaneel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Logging"));
    panel.setPreferredSize(new Dimension(1200, 150));

    logArea = new JTextArea(6, 100);
    logArea.setEditable(false);
    logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

    JScrollPane scrollPane = new JScrollPane(logArea);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    // Log knoppen
    JPanel logButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

    JButton clearLogBtn = new JButton("Log Leegmaken");
    clearLogBtn.addActionListener(e -> {
      logArea.setText("");
      log("Log geleegd");
    });

    JButton saveLogBtn = new JButton("Log Opslaan");
    saveLogBtn.addActionListener(e -> log("Log opslaan (niet geïmplementeerd)"));

    JButton errorTestBtn = new JButton("Test Fout");
    errorTestBtn.addActionListener(e -> log("TEST: Dit is een test foutmelding", "ERROR"));

    logButtonPanel.add(clearLogBtn);
    logButtonPanel.add(saveLogBtn);
    logButtonPanel.add(errorTestBtn);

    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(logButtonPanel, BorderLayout.SOUTH);

    return panel;
  }

  private void log(String msg) {
    log(msg, "INFO");
  }

  private void log(String msg, String level) {
    SwingUtilities.invokeLater(() -> {
      String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
      String colorTag = "";

      switch (level) {
      case "ERROR":
        colorTag = "[ERROR] ";
        break;
      case "WARN":
        colorTag = "[WARN] ";
        break;
      default:
        colorTag = "[INFO] ";
      }

      logArea.append("[" + time + "]" + colorTag + msg + "\n");
      logArea.setCaretPosition(logArea.getDocument().getLength());
    });
  }
}