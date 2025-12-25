package sandbox;

import javax.swing.*;

import kwee.osmmapper.gui.OsmMapViewer;

import java.awt.*;

public class HoofdMenuMetOsmMapViewer {
  private JFrame hoofdFrame;
  private JTabbedPane tabPane;
  private JTextArea logArea;
  private OsmMapViewer osmMapViewer;

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new HoofdMenuMetOsmMapViewer().createGUI());
  }

  private void createGUI() {
    hoofdFrame = new JFrame("Hoofdmenu met OSM Kaart");
    hoofdFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    hoofdFrame.setSize(1300, 850);

    tabPane = new JTabbedPane();

    // Tab 1: Dashboard
    tabPane.addTab("🏠 Dashboard", createDashboard());

    // Tab 2: Kaart (gebruik wrapper)
    tabPane.addTab("🗺️ Kaart", createKaartTab());

    // Tab 3: Instellingen
    tabPane.addTab("⚙️ Instellingen", createInstellingenTab());

    hoofdFrame.add(tabPane, BorderLayout.CENTER);
    hoofdFrame.add(createLogPaneel(), BorderLayout.SOUTH);

    hoofdFrame.setVisible(true);
    log("Applicatie gestart - OsmMapViewer geïntegreerd");
  }

  private JPanel createKaartTab() {
    JPanel kaartTabPanel = new JPanel(new BorderLayout());

    try {
      // Maak je bestaande OsmMapViewer (JFrame) aan
      osmMapViewer = new OsmMapViewer();

      // BELANGRIJK: Zorg dat de JFrame niet zichtbaar is als standalone
      osmMapViewer.setVisible(false);

      // Verwijder de JFrame-specifics en haal content op
      osmMapViewer.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

      // Methode 1: Neem alle componenten over naar een JPanel
      JPanel kaartWrapper = new JPanel(new BorderLayout());
      kaartWrapper.add(osmMapViewer.getContentPane(), BorderLayout.CENTER);
//====
      // Maak je bestaande OsmMapViewer (JFrame) aan
      osmMapViewer = new OsmMapViewer();

      // BELANGRIJK: Zorg dat de JFrame niet zichtbaar is als standalone
      osmMapViewer.setVisible(false);

      // Verwijder de JFrame-specifics en haal content op
      osmMapViewer.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      kaartWrapper.add(osmMapViewer.getContentPane(), BorderLayout.CENTER);

      // ===
      // Voeg wrapper toe aan tabblad
      kaartTabPanel.add(kaartWrapper, BorderLayout.CENTER);

      // Voeg extra controles toe
      kaartTabPanel.add(createKaartControlePanel(), BorderLayout.NORTH);
      kaartTabPanel.add(createKaartToolbar(), BorderLayout.WEST);

      log("OsmMapViewer succesvol geladen in tabblad");

    } catch (Exception e) {
      log("FOUT bij laden kaart: " + e.getMessage());
      kaartTabPanel.add(new JLabel("<html><h3>Kaart kon niet laden</h3><p>" + e.getMessage() + "</p></html>"),
          BorderLayout.CENTER);
    }

    return kaartTabPanel;
  }

  private JPanel createKaartControlePanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    panel.setBorder(BorderFactory.createTitledBorder("Kaart Besturing"));
    panel.setBackground(new Color(240, 245, 250));

    // Knoppen die methoden van OsmMapViewer aanroepen
    JButton centerNederlandBtn = new JButton("Centreren op Nederland");
    centerNederlandBtn.addActionListener(e -> {
      if (osmMapViewer != null) {
        osmMapViewer.centerOnLocation(52.1326, 5.2913, 7);
        log("Gecentreerd op Nederland (via OsmMapViewer)");
      }
    });

    JButton toggleLagenBtn = new JButton("Lagen Weergeven");
    toggleLagenBtn.addActionListener(e -> {
      log("Kaartlagen getoond");
      // osmMapViewer.toggleLayersPanel();
    });

    JButton exportKaartBtn = new JButton("Exporteer Kaart");
    exportKaartBtn.addActionListener(e -> {
      log("Kaart export functionaliteit");
      // osmMapViewer.exportToImage();
    });

    // Zoom controls
    JLabel zoomLabel = new JLabel("Zoom:");
    JSlider zoomSlider = new JSlider(1, 18, 10);
    zoomSlider.addChangeListener(e -> {
      if (!zoomSlider.getValueIsAdjusting() && osmMapViewer != null) {
        // osmMapViewer.setZoom(zoomSlider.getValue());
        log("Zoom aangepast: " + zoomSlider.getValue());
      }
    });

    panel.add(centerNederlandBtn);
    panel.add(toggleLagenBtn);
    panel.add(exportKaartBtn);
    panel.add(Box.createHorizontalStrut(20));
    panel.add(zoomLabel);
    panel.add(zoomSlider);

    return panel;
  }

  private JPanel createKaartToolbar() {
    JPanel toolbar = new JPanel();
    toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
    toolbar.setBorder(BorderFactory.createTitledBorder("Tools"));
    toolbar.setPreferredSize(new Dimension(120, 400));

    String[] tools = { "Selecteer", "Pan", "Marker", "Meet", "Route" };
    for (String tool : tools) {
      JButton toolBtn = new JButton(tool);
      toolBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
      toolBtn.setMaximumSize(new Dimension(100, 30));
      toolBtn.addActionListener(e -> log("Tool geselecteerd: " + tool));
      toolbar.add(toolBtn);
      toolbar.add(Box.createVerticalStrut(5));
    }

    return toolbar;
  }

  private JPanel createDashboard() {
    JPanel dashboard = new JPanel(new BorderLayout());
    dashboard.setBackground(new Color(250, 250, 255));

    // Header
    JLabel header = new JLabel("OSM Kaart Dashboard", SwingConstants.CENTER);
    header.setFont(new Font("Arial", Font.BOLD, 24));
    header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    dashboard.add(header, BorderLayout.NORTH);

    // Snelle acties panel
    JPanel actiesPanel = new JPanel(new GridLayout(2, 2, 15, 15));
    actiesPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

    String[] acties = { "Open Kaart", "Laatste Locatie", "Favorieten", "Historie" };

    for (String actie : acties) {
      JButton btn = new JButton(actie);
      btn.setFont(new Font("Arial", Font.PLAIN, 16));
      btn.addActionListener(e -> {
        if (actie.equals("Open Kaart")) {
          tabPane.setSelectedIndex(1); // Ga naar kaart tabblad
        } else if (actie.equals("Favorieten")) {
          // tabPane.setSelectedIndex(2); // Ga naar kaart tabblad
        }
        log("Dashboard actie: " + actie);
      });
      actiesPanel.add(btn);
    }

    dashboard.add(actiesPanel, BorderLayout.CENTER);

    // Status panel
    JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));

    JLabel statusLabel = new JLabel("Kaart gereed voor gebruik");
    JCheckBox autoRefresh = new JCheckBox("Auto-ververs", true);

    statusPanel.add(statusLabel);
    statusPanel.add(Box.createHorizontalStrut(20));
    statusPanel.add(autoRefresh);

    dashboard.add(statusPanel, BorderLayout.SOUTH);

    return dashboard;
  }

  private JPanel createInstellingenTab() {
    JPanel instellingen = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Kaart instellingen
    gbc.gridx = 0;
    gbc.gridy = 0;
    instellingen.add(new JLabel("Kaart Stijl:"), gbc);

    gbc.gridx = 1;
    JComboBox<String> styleCombo = new JComboBox<>(new String[] { "Standaard", "Donker", "Satelliet", "Topografisch" });
    instellingen.add(styleCombo, gbc);

    // Cache instellingen
    gbc.gridx = 0;
    gbc.gridy = 1;
    instellingen.add(new JLabel("Cache Grootte (MB):"), gbc);

    gbc.gridx = 1;
    JSlider cacheSlider = new JSlider(10, 500, 100);
    instellingen.add(cacheSlider, gbc);

    // Weergave opties
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    JPanel weergavePanel = new JPanel(new GridLayout(0, 1));
    weergavePanel.setBorder(BorderFactory.createTitledBorder("Weergave"));

    weergavePanel.add(new JCheckBox("Toon coördinaten", true));
    weergavePanel.add(new JCheckBox("Toon schaal", true));
    weergavePanel.add(new JCheckBox("Animaties", false));

    instellingen.add(weergavePanel, gbc);

    // Opslaan knop
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    JButton saveBtn = new JButton("Instellingen Opslaan");
    saveBtn.addActionListener(e -> log("Instellingen opgeslagen"));
    instellingen.add(saveBtn, gbc);

    return instellingen;
  }

  private JPanel createLogPaneel() {
    JPanel logPanel = new JPanel(new BorderLayout());
    logPanel.setBorder(BorderFactory.createTitledBorder("Applicatie Log"));
    logPanel.setPreferredSize(new Dimension(1300, 180));

    logArea = new JTextArea(8, 100);
    logArea.setEditable(false);
    logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
    logArea.setBackground(new Color(248, 248, 248));

    JScrollPane scrollPane = new JScrollPane(logArea);

    // Log acties
    JPanel logActies = new JPanel(new FlowLayout(FlowLayout.LEFT));

    JButton clearLog = new JButton("Log Wissen");
    clearLog.addActionListener(e -> {
      logArea.setText("");
      log("Log gewist");
    });

    JButton exportLog = new JButton("Exporteer Log");
    exportLog.addActionListener(e -> {
      JFileChooser chooser = new JFileChooser();
      if (chooser.showSaveDialog(hoofdFrame) == JFileChooser.APPROVE_OPTION) {
        log("Log geëxporteerd naar: " + chooser.getSelectedFile().getName());
      }
    });

    logActies.add(clearLog);
    logActies.add(exportLog);

    logPanel.add(scrollPane, BorderLayout.CENTER);
    logPanel.add(logActies, BorderLayout.SOUTH);

    return logPanel;
  }

  private void log(String message) {
    SwingUtilities.invokeLater(() -> {
      String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
      logArea.append(String.format("[%s] %s\n", timestamp, message));
      logArea.setCaretPosition(logArea.getDocument().getLength());
    });
  }

  // Sluit de applicatie netjes af
  private void shutdown() {
    if (osmMapViewer != null) {
      osmMapViewer.dispose();
    }
    log("Applicatie wordt afgesloten");
    System.exit(0);
  }
}
