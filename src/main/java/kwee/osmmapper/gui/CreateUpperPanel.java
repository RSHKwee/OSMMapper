package kwee.osmmapper.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import kwee.library.ApplicationMessages;
import kwee.osmmapper.lib.KaartController;
import kwee.osmmapper.lib.MemoContent;
import kwee.osmmapper.lib.OSMMapExcel;
import kwee.osmmapper.main.UserSetting;

public class CreateUpperPanel {
  private UserSetting m_params;

  private String m_InpDirectory = "";
  private KaartController kaartController;
  private JProgressBar m_ProgressBar = new JProgressBar();
  private JLabel m_ProgressLabel = new JLabel(" ");;

  public CreateUpperPanel() {
    kaartController = KaartController.getInstance();
    m_params = UserSetting.getInstance();
    m_InpDirectory = m_params.get_InpDirectory();
  }

  public JPanel createBovenPaneel(JFrame hoofdFrame) {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    panel.setBorder(BorderFactory.createTitledBorder("Acties"));

    // Knop 1: Nieuwe kaart
    JButton nieuweKnop = new JButton("➕ Nieuwe Kaart");
    nieuweKnop.addActionListener(e -> {
      // Maak een JPanel met GridBagLayout voor nette uitlijning
      JPanel panel2 = new JPanel(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(5, 5, 5, 5);

      // 1. Input file selector
      gbc.gridx = 0;
      gbc.gridy = 0;
      panel2.add(new JLabel("XLSX Bestand:"), gbc);

      JTextField fileField = new JTextField(25);
      gbc.gridx = 1;
      gbc.gridy = 0;
      gbc.weightx = 1.0;
      panel2.add(fileField, gbc);

      JButton browseButton = new JButton("📁 Selecteer");
      gbc.gridx = 2;
      gbc.gridy = 0;
      gbc.weightx = 0.0;
      panel2.add(browseButton, gbc);

      // 2. Titel string invoer
      gbc.gridx = 0;
      gbc.gridy = 1;
      panel2.add(new JLabel("Titel:"), gbc);

      JTextField titleField = new JTextField(25);
      gbc.gridx = 1;
      gbc.gridy = 1;
      gbc.gridwidth = 2; // Span over 2 kolommen
      gbc.weightx = 1.0;
      panel2.add(titleField, gbc);

      // Browse button functionaliteit
      browseButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
          JFileChooser fileChooser = new JFileChooser();
          fileChooser.setDialogTitle("Selecteer XLSX bestand");
          fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
          fileChooser.setSelectedFile(new File(m_InpDirectory));

          // xlsxfilter
          fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
              return f.isDirectory() || f.getName().toLowerCase().endsWith(".xlsx");
            }

            @Override
            public String getDescription() {
              return "xlsxBestanden (*.xlsx)";
            }
          });

          int result = fileChooser.showOpenDialog(panel2);
          if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            fileField.setText(selectedFile.getAbsolutePath());
            m_InpDirectory = selectedFile.getAbsolutePath();
            m_params.set_InpDirectory(m_InpDirectory);

            // Optioneel: Auto-vul titel op basis van bestandsnaam
            if (titleField.getText().isEmpty()) {
              String fileName = selectedFile.getName().replaceFirst("[.][^.]+$", "") // Verwijder extensie
                  .replace("_", " ") // Vervang underscores
                  .replace("-", " "); // Vervang koppeltekens
              titleField.setText(fileName);
            }
          }
        }
      });

      // Toon de dialoog
      int result = JOptionPane.showConfirmDialog(null, panel2, "Gegevens voor Geo-info", JOptionPane.OK_CANCEL_OPTION,
          JOptionPane.PLAIN_MESSAGE);

      // Verwerk het resultaat
      if (result == JOptionPane.OK_OPTION) {
        String filePath = fileField.getText().trim();
        String title = titleField.getText().trim();

        // Validatie
        if (filePath.isEmpty()) {
          JOptionPane.showMessageDialog(null, "Selecteer eerst een xlsxbestand!", "Fout", JOptionPane.ERROR_MESSAGE);
          return;
        }

        File inputFile = new File(filePath);
        if (!inputFile.exists()) {
          JOptionPane.showMessageDialog(null, "Bestand bestaat niet:\n" + filePath, "Fout", JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (title.isEmpty()) {
          title = "Adressen"; // Default waarde
        }

        // Bevestigingsdialoog
        int confirm = JOptionPane.showConfirmDialog(null,
            "Bevestig verwerking:\n\n" + "Bestand: " + inputFile.getName() + "\n" + "Titel: " + title, "Bevestiging",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
          kaartController.voegKaartToe(inputFile.getAbsolutePath(), title, 0.0, 0.0, 10);
          JOptionPane.showMessageDialog(null, "Geo-info toegevoegd met titel:\n" + title, "Succes",
              JOptionPane.INFORMATION_MESSAGE);
        }
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
      filePanel.add(new JLabel("Input XLSX bestand:"), gbc);

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
      filePanel.add(new JLabel("Output XLSX bestand:"), gbc);

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
          fileChooser.setDialogTitle("Selecteer XLSX invoerbestand");
          fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
          fileChooser.setSelectedFile(new File(m_InpDirectory));

          // Optioneel: CSV filter toevoegen
          fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
              return f.isDirectory() || f.getName().toLowerCase().endsWith(".xlsx");
            }

            @Override
            public String getDescription() {
              return "XLSX Bestanden (*.xlsx)";
            }
          });

          int result = fileChooser.showOpenDialog(filePanel);
          if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            inputField.setText(selectedFile.getAbsolutePath());

            // Auto-suggest output naam
            if (outputField.getText().isEmpty()) {
              String baseName = selectedFile.getName().replaceFirst("[.][^.]+$", "");
              String outputName = baseName + "_met_geo.xlsx";
              outputField.setText(new File(selectedFile.getParent(), outputName).getAbsolutePath());
            }
          }
        }
      });

      outputBrowseButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
          JFileChooser fileChooser = new JFileChooser();
          fileChooser.setDialogTitle("Opslaan XLSX met geo-info");
          fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

          // CSV filter
          fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
              return f.isDirectory() || f.getName().toLowerCase().endsWith(".xslx");
            }

            @Override
            public String getDescription() {
              return "XLSX Bestanden (*.xlsx)";
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
            if (!path.toLowerCase().endsWith(".xlsx")) {
              path += ".xlsx";
            }
            outputField.setText(path);
          }
        }
      });

      // Toon de dialoog
      int dialogResult = JOptionPane.showConfirmDialog(null, filePanel, "Selecteer XLSX bestanden voor geo-informatie",
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

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
          @Override
          protected Void doInBackground() throws Exception {
            m_params.set_InpExcelFile(inputFile.getAbsolutePath());
            m_params.set_OutpExcelFile(outputFile.getAbsolutePath());
            m_params.save();

            @SuppressWarnings("unused")
            ArrayList<MemoContent> memocontarr = new ArrayList<MemoContent>();
            OSMMapExcel mexcel = new OSMMapExcel(inputFile.getAbsolutePath());
            memocontarr = mexcel.ReadExcel();
            mexcel.WriteExcel(outputFile.getAbsolutePath(), m_ProgressBar, m_ProgressLabel);
            return null;
          }

          @Override
          protected void done() {
            m_ProgressLabel.setVisible(false);
            m_ProgressBar.setVisible(false);

            try {
              get(); // Check voor excepties
              JOptionPane.showMessageDialog(null, "Klaar!", "Succes", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
              JOptionPane.showMessageDialog(null, "Fout: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
          }
        };
        worker.execute();
      }
    });

    // Progress bars
    JPanel progrespanel = new JPanel();
    m_ProgressLabel = new JLabel("Progress ");

    m_ProgressLabel.setVisible(false);
    m_ProgressBar.setVisible(false);

    progrespanel.add(m_ProgressLabel, BorderLayout.SOUTH);
    progrespanel.add(m_ProgressBar, BorderLayout.SOUTH);

    panel.add(addLongLatKnop);
    panel.add(nieuweKnop);
    panel.add(switchKnop);
    panel.add(progrespanel, BorderLayout.SOUTH);

    return panel;
  }
}
