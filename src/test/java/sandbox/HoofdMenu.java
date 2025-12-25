package sandbox;

import javax.swing.*;

import kwee.osmmapper.gui.OsmMapViewer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HoofdMenu {
  private JTextArea logArea;

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      new HoofdMenu().createAndShowGUI();
    });
  }

  private void createAndShowGUI() {
    // Hoofdframe instellen
    JFrame hoofdFrame = new JFrame("Hoofdmenu - Kaart Applicatie");
    hoofdFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    hoofdFrame.setSize(900, 700);
    hoofdFrame.setLayout(new BorderLayout());

    // Log een startbericht
    log("Applicatie gestart");

    // Bovenpaneel maken met knoppen (noord)
    JPanel bovenPaneel = createBovenPaneel();
    hoofdFrame.add(bovenPaneel, BorderLayout.NORTH);

    // Middenpaneel maken (optioneel voor toekomstige uitbreidingen)
    JPanel middenPaneel = new JPanel();
    middenPaneel.setBackground(new Color(240, 240, 240));
    hoofdFrame.add(middenPaneel, BorderLayout.CENTER);

    // Onderpaneel maken voor logging (zuid)
    JPanel onderPaneel = createOnderPaneel();
    hoofdFrame.add(onderPaneel, BorderLayout.SOUTH);

    hoofdFrame.setVisible(true);
  }

  private JPanel createBovenPaneel() {
    JPanel panel = new JPanel();
    panel.setBorder(BorderFactory.createTitledBorder("Besturing"));
    panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
    panel.setBackground(new Color(220, 230, 240));

    // Kaart knoppen
    JButton openKaartKnop = new JButton("Open Kaart");
    openKaartKnop.setToolTipText("Open een nieuw kaartvenster");
    openKaartKnop.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          log("Open kaartvenster...");
          OsmMapViewer kaart = new OsmMapViewer();
          kaart.setVisible(true);
          log("Kaartvenster geopend");
        } catch (Exception ex) {
          log("FOUT bij openen kaart: " + ex.getMessage());
        }
      }
    });

    JButton sluitKaartenKnop = new JButton("Sluit Alle Kaarten");
    sluitKaartenKnop.setToolTipText("Sluit alle openstaande kaartvensters");
    sluitKaartenKnop.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        log("Alle kaartvensters sluiten...");
        // Hier zou je logica kunnen toevoegen om alle kaartvensters bij te houden
        log("Kaartvensters gesloten");
      }
    });

    // Configuratie knop
    JButton configKnop = new JButton("Configuratie");
    configKnop.addActionListener(e -> log("Configuratie menu geopend"));

    // Info knop
    JButton infoKnop = new JButton("Info");
    infoKnop.addActionListener(e -> {
      log("Info: Kaartapplicatie v1.0");
      JOptionPane.showMessageDialog(null, "Kaartapplicatie met JMapViewer\nVersie 1.0", "Info",
          JOptionPane.INFORMATION_MESSAGE);
    });

    // Knoppen toevoegen
    panel.add(openKaartKnop);
    panel.add(sluitKaartenKnop);
    panel.add(Box.createHorizontalStrut(20)); // Spacer
    panel.add(configKnop);
    panel.add(infoKnop);

    return panel;
  }

  private JPanel createOnderPaneel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Logging & Uitvoer"));
    panel.setPreferredSize(new Dimension(900, 200));

    // TextArea voor logging
    logArea = new JTextArea(8, 80);
    logArea.setEditable(false);
    logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    logArea.setBackground(new Color(250, 250, 245));

    // Scrollpane toevoegen
    JScrollPane scrollPane = new JScrollPane(logArea);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    // Knoppen voor logbeheer
    JPanel logKnoppenPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    JButton clearLogKnop = new JButton("Log Leegmaken");
    clearLogKnop.addActionListener(e -> {
      logArea.setText("");
      log("Log geleegd");
    });

    JButton kopieerLogKnop = new JButton("Kopieer Log");
    kopieerLogKnop.addActionListener(e -> {
      logArea.selectAll();
      logArea.copy();
      log("Log naar klembord gekopieerd");
    });

    JButton logLevelKnop = new JButton("Log Level: INFO");
    logLevelKnop.addActionListener(e -> {
      String[] levels = { "DEBUG", "INFO", "WAARSCHUWING", "FOUT" };
      String keuze = (String) JOptionPane.showInputDialog(null, "Kies log niveau:", "Log Level",
          JOptionPane.QUESTION_MESSAGE, null, levels, levels[1]);
      if (keuze != null) {
        log("Log level gewijzigd naar: " + keuze);
        logLevelKnop.setText("Log Level: " + keuze);
      }
    });

    logKnoppenPanel.add(clearLogKnop);
    logKnoppenPanel.add(kopieerLogKnop);
    logKnoppenPanel.add(logLevelKnop);

    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(logKnoppenPanel, BorderLayout.SOUTH);

    return panel;
  }

  private void log(String bericht) {
    String tijdstip = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
    String logRegel = "[" + tijdstip + "] " + bericht + "\n";

    SwingUtilities.invokeLater(() -> {
      logArea.append(logRegel);
      // Auto-scroll naar beneden
      logArea.setCaretPosition(logArea.getDocument().getLength());
    });
  }
}
