package sandbox;

import javax.swing.*;

import kwee.osmmapper.report.image.FlexibeleWordGenerator;

import java.awt.*;
import java.util.Map;
import java.util.List;

public class ConfiguratieScherm extends JFrame {

  private JComboBox<Integer> fotoPerRijCombo;
  private JCheckBox paginaPerPostcodeCheck;
  private JCheckBox toonHuisnummerCheck;
  private JCheckBox toonBestandsnaamCheck;
  private JTextField documentTitelField;

  public ConfiguratieScherm() {
    setTitle("Document Configuratie");
    setSize(400, 300);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // Configuratie panel
    JPanel configPanel = new JPanel(new GridLayout(6, 2, 10, 10));
    configPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Foto's per rij
    configPanel.add(new JLabel("Foto's per rij:"));
    fotoPerRijCombo = new JComboBox<>(new Integer[] { 1, 2, 3, 4 });
    fotoPerRijCombo.setSelectedItem(2);
    configPanel.add(fotoPerRijCombo);

    // Pagina per postcode
    configPanel.add(new JLabel("Nieuwe pagina per postcode:"));
    paginaPerPostcodeCheck = new JCheckBox("", true);
    configPanel.add(paginaPerPostcodeCheck);

    // Toon huisnummer
    configPanel.add(new JLabel("Toon huisnummer:"));
    toonHuisnummerCheck = new JCheckBox("", true);
    configPanel.add(toonHuisnummerCheck);

    // Toon bestandsnaam
    configPanel.add(new JLabel("Toon bestandsnaam:"));
    toonBestandsnaamCheck = new JCheckBox("", true);
    configPanel.add(toonBestandsnaamCheck);

    // Document titel
    configPanel.add(new JLabel("Document titel:"));
    documentTitelField = new JTextField("Straatfoto Overzicht");
    configPanel.add(documentTitelField);

    add(configPanel, BorderLayout.CENTER);

    // Knoppen panel
    JPanel buttonPanel = new JPanel();
    JButton genereerButton = new JButton("Genereer Document");
    JButton annuleerButton = new JButton("Annuleren");

    genereerButton.addActionListener(e -> {
      FlexibeleWordGenerator.Config config = getConfig();
      dispose();
      startGeneratie(config);
    });

    annuleerButton.addActionListener(e -> System.exit(0));

    buttonPanel.add(genereerButton);
    buttonPanel.add(annuleerButton);
    add(buttonPanel, BorderLayout.SOUTH);

    setLocationRelativeTo(null);
    setVisible(true);
  }

  private FlexibeleWordGenerator.Config getConfig() {
    FlexibeleWordGenerator.Config config = new FlexibeleWordGenerator.Config();
    config.fotoPerRij = (int) fotoPerRijCombo.getSelectedItem();
    config.paginaPerPostcode = paginaPerPostcodeCheck.isSelected();
    config.toonHuisnummer = toonHuisnummerCheck.isSelected();
    config.toonBestandsnaam = toonBestandsnaamCheck.isSelected();
    config.documentTitel = documentTitelField.getText();
    return config;
  }

  private void startGeneratie(FlexibeleWordGenerator.Config config) {
    // Hier zou je de daadwerkelijke generatie starten
    System.out.println("Configuratie:");
    System.out.println("  Foto's per rij: " + config.fotoPerRij);
    System.out.println("  Pagina per postcode: " + config.paginaPerPostcode);
    System.out.println("  Toon huisnummer: " + config.toonHuisnummer);
    System.out.println("  Toon bestandsnaam: " + config.toonBestandsnaam);
    System.out.println("  Document titel: " + config.documentTitel);

    // Roep hier je hoofdprogramma aan
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(ConfiguratieScherm::new);
  }
}