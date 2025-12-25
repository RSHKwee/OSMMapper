package sandbox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileInputOutputDialog {
  public static void main(String[] args) {

    // Maak een JPanel met GridLayout
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    // Input file veld
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(new JLabel("Invoerbestand:"), gbc);

    JTextField inputField = new JTextField(25);
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    panel.add(inputField, gbc);

    JButton inputBrowseButton = new JButton("Bladeren...");
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weightx = 0.0;
    panel.add(inputBrowseButton, gbc);

    // Output file veld
    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(new JLabel("Uitvoerbestand:"), gbc);

    JTextField outputField = new JTextField(25);
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    panel.add(outputField, gbc);

    JButton outputBrowseButton = new JButton("Bladeren...");
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.weightx = 0.0;
    panel.add(outputBrowseButton, gbc);

    // Event handlers voor de browse knoppen
    inputBrowseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecteer invoerbestand");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(panel);
        if (result == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          inputField.setText(selectedFile.getAbsolutePath());

          // Optioneel: automatisch een output naam suggereren
          if (outputField.getText().isEmpty()) {
            String inputName = selectedFile.getName();
            String outputName = inputName.replaceFirst("[.][^.]+$", "") + "_metCoordinaten.xlsx";
            outputField.setText(new File(selectedFile.getParent(), outputName).getAbsolutePath());
          }
        }
      }
    });

    outputBrowseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecteer uitvoerbestand");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Stel een standaard bestandsnaam voor
        if (!outputField.getText().isEmpty()) {
          fileChooser.setSelectedFile(new File(outputField.getText()));
        }

        int result = fileChooser.showSaveDialog(panel);
        if (result == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          outputField.setText(selectedFile.getAbsolutePath());
        }
      }
    });

    // Toon de JOptionPane
    int result = JOptionPane.showConfirmDialog(null, panel, "Bestandsconfiguratie", JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE);

    // Verwerk het resultaat
    if (result == JOptionPane.OK_OPTION) {
      String inputFile = inputField.getText().trim();
      String outputFile = outputField.getText().trim();

      // Validatie
      if (inputFile.isEmpty() || outputFile.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Beide bestanden moeten worden opgegeven!", "Fout",
            JOptionPane.ERROR_MESSAGE);
      } else if (!new File(inputFile).exists()) {
        JOptionPane.showMessageDialog(null, "Invoerbestand bestaat niet!", "Fout", JOptionPane.ERROR_MESSAGE);
      } else if (inputFile.equals(outputFile)) {
        JOptionPane.showMessageDialog(null, "Invoer- en uitvoerbestand mogen niet hetzelfde zijn!", "Fout",
            JOptionPane.ERROR_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(null, "Invoerbestand: " + inputFile + "\n" + "Uitvoerbestand: " + outputFile,
            "Bevestiging", JOptionPane.INFORMATION_MESSAGE);

        // Hier kun je je bestandsbewerkingen uitvoeren
        // processFiles(inputFile, outputFile);
      }
    }
  }
}