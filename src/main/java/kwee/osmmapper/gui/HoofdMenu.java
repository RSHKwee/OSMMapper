package kwee.osmmapper.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import kwee.library.swing.TextAreaHandler;
import kwee.logger.MyLogger;
import kwee.osmmapper.lib.CustomJULHandler;
import kwee.osmmapper.lib.KaartController;
import kwee.osmmapper.lib.MemoContent;
import kwee.osmmapper.lib.OSMMapExcel;

public class HoofdMenu {
  private static final Logger LOGGER = MyLogger.getLogger();
  private JFrame hoofdFrame;
  private KaartController kaartController;
  private Font customFont = new Font("Arial", Font.PLAIN, 12);

  private String m_LogDir = "c:/";
  private boolean m_toDisk = false;
  private Level m_Level = Level.INFO;
  // private Level m_Level = Level.ALL;

  public void start() {
    hoofdFrame = new JFrame("Kaart Applicatie");
    hoofdFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    hoofdFrame.setSize(1000, 700);

    // 1. Bovenste paneel met knoppen
    hoofdFrame.add(createBovenPaneel(), BorderLayout.NORTH);

    // 2. Midden: Kaarten container
    JPanel kaartenContainer = new JPanel();
    kaartController = new KaartController(kaartenContainer);
    hoofdFrame.add(kaartenContainer, BorderLayout.CENTER);

    // 3. Onderste paneel voor logging
    hoofdFrame.add(createLogPaneel(), BorderLayout.SOUTH);

    hoofdFrame.setVisible(true);
  }

  private JPanel createBovenPaneel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    panel.setBorder(BorderFactory.createTitledBorder("Acties"));

    // Knop 1: Nieuwe kaart
    JButton nieuweKnop = new JButton("➕ Nieuwe Kaart");
    nieuweKnop.addActionListener(e -> {
      String naam = JOptionPane.showInputDialog(hoofdFrame, "Naam voor nieuwe kaart:");
      if (naam != null && !naam.trim().isEmpty()) {
        kaartController.voegKaartToe("", naam, 52.1326, 5.2913, 7);
      }
    });

    // Knop 2: Switch tussen kaarten
    JButton switchKnop = new JButton("🔀 Wissel Kaart");
    switchKnop.addActionListener(e -> {
      String[] kaartNamen = kaartController.getKaartNamen();
      if (kaartNamen.length > 0) {
        String keuze = (String) JOptionPane.showInputDialog(hoofdFrame, "Kies een kaart:", "Kaart Selectie",
            JOptionPane.QUESTION_MESSAGE, null, kaartNamen, kaartNamen[0]);
        if (keuze != null) {
          kaartController.toonKaart(keuze);
        }
      }
    });

    // Knop 3: Voeg Geo info toe

    // ... in je knop action listener:

    JButton addLongLatKnop = new JButton("🔀 Voeg Geo info toe");
    addLongLatKnop.addActionListener(e -> {
      // Maak een JPanel voor de bestandsselectie
      JPanel filePanel = new JPanel(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(5, 5, 5, 5);

      // Input CSV bestand
      gbc.gridx = 0;
      gbc.gridy = 0;
      filePanel.add(new JLabel("Input CSV bestand:"), gbc);

      JTextField inputField = new JTextField(25);
      gbc.gridx = 1;
      gbc.gridy = 0;
      gbc.weightx = 1.0;
      filePanel.add(inputField, gbc);

      JButton inputBrowseButton = new JButton("📁 Selecteer");
      gbc.gridx = 2;
      gbc.gridy = 0;
      gbc.weightx = 0.0;
      filePanel.add(inputBrowseButton, gbc);

      // Output CSV bestand
      gbc.gridx = 0;
      gbc.gridy = 1;
      filePanel.add(new JLabel("Output CSV bestand:"), gbc);

      JTextField outputField = new JTextField(25);
      gbc.gridx = 1;
      gbc.gridy = 1;
      gbc.weightx = 1.0;
      filePanel.add(outputField, gbc);

      JButton outputBrowseButton = new JButton("💾 Opslaan als");
      gbc.gridx = 2;
      gbc.gridy = 1;
      gbc.weightx = 0.0;
      filePanel.add(outputBrowseButton, gbc);

      // Event handlers voor browse knoppen
      inputBrowseButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          JFileChooser fileChooser = new JFileChooser();
          fileChooser.setDialogTitle("Selecteer CSV invoerbestand");
          fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

          // Optioneel: CSV filter toevoegen
          fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
              return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
            }

            @Override
            public String getDescription() {
              return "CSV Bestanden (*.csv)";
            }
          });

          int result = fileChooser.showOpenDialog(filePanel);
          if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            inputField.setText(selectedFile.getAbsolutePath());

            // Auto-suggest output naam
            if (outputField.getText().isEmpty()) {
              String baseName = selectedFile.getName().replaceFirst("[.][^.]+$", "");
              String outputName = baseName + "_met_geo.csv";
              outputField.setText(new File(selectedFile.getParent(), outputName).getAbsolutePath());
            }
          }
        }
      });

      outputBrowseButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
          JFileChooser fileChooser = new JFileChooser();
          fileChooser.setDialogTitle("Opslaan CSV met geo-info");
          fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

          // CSV filter
          fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
              return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
            }

            @Override
            public String getDescription() {
              return "CSV Bestanden (*.csv)";
            }
          });

          // Suggestie voor output naam
          if (!inputField.getText().isEmpty() && outputField.getText().isEmpty()) {
            File inputFile = new File(inputField.getText());
            String outputName = inputFile.getName().replaceFirst("[.][^.]+$", "") + "_met_geo.csv";
            fileChooser.setSelectedFile(new File(inputFile.getParent(), outputName));
          } else if (!outputField.getText().isEmpty()) {
            fileChooser.setSelectedFile(new File(outputField.getText()));
          }

          int result = fileChooser.showSaveDialog(filePanel);
          if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Zorg dat het .csv extensie heeft
            String path = selectedFile.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".csv")) {
              path += ".csv";
            }
            outputField.setText(path);
          }
        }
      });

      // Toon de dialoog
      int dialogResult = JOptionPane.showConfirmDialog(null, filePanel, "Selecteer CSV bestanden voor geo-informatie",
          JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

      // Verwerk de selectie
      if (dialogResult == JOptionPane.OK_OPTION) {
        String inputPath = inputField.getText().trim();
        String outputPath = outputField.getText().trim();

        // Validatie
        if (inputPath.isEmpty() || outputPath.isEmpty()) {
          JOptionPane.showMessageDialog(null, "Beide bestanden moeten worden opgegeven!", "Fout",
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
          JOptionPane.showMessageDialog(null, "Invoerbestand bestaat niet:\n" + inputPath, "Fout",
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (inputPath.equals(outputPath)) {
          int overwrite = JOptionPane.showConfirmDialog(null,
              "Invoer- en uitvoerbestand zijn hetzelfde.\n" + "Wil je het originele bestand overschrijven?",
              "Waarschuwing", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

          if (overwrite != JOptionPane.YES_OPTION) {
            return;
          }
        }

        // Bestaat het output bestand al?
        File outputFile = new File(outputPath);
        if (outputFile.exists() && !inputPath.equals(outputPath)) {
          int overwrite = JOptionPane.showConfirmDialog(null,
              "Uitvoerbestand bestaat al:\n" + outputPath + "\n" + "Overschrijven?", "Bevestiging",
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

          if (overwrite != JOptionPane.YES_OPTION) {
            return;
          }
        }

        // Hier roep je je eigen methode aan om geo-info toe te voegen
        try {
          ArrayList<MemoContent> memocontarr = new ArrayList<MemoContent>();
          OSMMapExcel mexcel = new OSMMapExcel(inputFile.getAbsolutePath());
          memocontarr = mexcel.ReadExcel();
          mexcel.WriteExcel(outputFile.getAbsolutePath());

          JOptionPane.showMessageDialog(null,
              "Geo-informatie succesvol toegevoegd!\n\n" + "Invoer: " + inputPath + "\n" + "Uitvoer: " + outputPath,
              "Succes", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(null, "Fout bij toevoegen geo-info:\n" + ex.getMessage(), "Fout",
              JOptionPane.ERROR_MESSAGE);
          ex.printStackTrace();
        }
      }
    });

    panel.add(addLongLatKnop);
    panel.add(nieuweKnop);
    panel.add(switchKnop);
    return panel;
  }

  private JPanel createLogPaneel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setPreferredSize(new Dimension(1000, 100));
    panel.setBorder(BorderFactory.createTitledBorder("Log"));
    JTextArea logArea = new JTextArea(4, 80);
    // Build output area.
    try {
      MyLogger.setup(m_Level, m_LogDir, m_toDisk);
    } catch (IOException es) {
      LOGGER.log(Level.SEVERE, Class.class.getName() + ": " + es.toString());
      es.printStackTrace();
    }

    // Registreer de handler
    Logger julLogger = Logger.getLogger("");
    julLogger.setLevel(m_Level);
    julLogger.addHandler(new CustomJULHandler());

    Logger rootLogger = Logger.getLogger("");
    for (Handler handler : rootLogger.getHandlers()) {
      if (handler instanceof TextAreaHandler) {
        TextAreaHandler textAreaHandler = (TextAreaHandler) handler;
        logArea = textAreaHandler.getTextArea();
        logArea.setFont(customFont);
      }
    }

    logArea.setEditable(false);
    panel.add(new JScrollPane(logArea), BorderLayout.CENTER);

    return panel;
  }
}
