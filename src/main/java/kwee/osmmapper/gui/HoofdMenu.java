package kwee.osmmapper.gui;

//HOOFDMENU.java - ALLES wat je nodig hebt
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HoofdMenu {
  private JFrame hoofdFrame;
  private KaartController kaartController;

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new HoofdMenu().start());
  }

  private void start() {
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
    panel.setBorder(BorderFactory.createTitledBorder("Kaart Acties"));

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

    panel.add(nieuweKnop);
    panel.add(switchKnop);

    return panel;
  }

  private JPanel createLogPaneel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setPreferredSize(new Dimension(1000, 100));
    panel.setBorder(BorderFactory.createTitledBorder("Log"));

    JTextArea logArea = new JTextArea(4, 80);
    logArea.setEditable(false);
    panel.add(new JScrollPane(logArea), BorderLayout.CENTER);

    return panel;
  }
}

//KAARTCONTROLLER.java - Vereenvoudigd
class KaartController {
  private JTabbedPane kaartTabPane;
  private Map<String, OsmMapViewer> kaarten;

  public KaartController(JPanel container) {
    this.kaartTabPane = new JTabbedPane();
    this.kaarten = new HashMap<>();

    container.setLayout(new BorderLayout());
    container.add(kaartTabPane, BorderLayout.CENTER);

    String inputFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-adressenlijst_met_coordinaten.xlsx";
    String subtottitel = " Koophuizen";

    String warmteFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-warmtescan_met_coordinaten_new.xlsx";
    String subtitel = " Warmtescan";

    // Voeg standaard kaart toe
    voegKaartToe(inputFile, subtottitel, 52.1326, 5.2913, 7);
    voegKaartToe(warmteFile, subtitel, 52.1326, 5.2913, 7);
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

      System.out.println("Kaart toegevoegd: " + naam);

    } catch (Exception e) {
      System.err.println("Fout bij aanmaken kaart: " + e.getMessage());
    }
  }

  public void toonKaart(String naam) {
    for (int i = 0; i < kaartTabPane.getTabCount(); i++) {
      if (kaartTabPane.getTitleAt(i).equals(naam)) {
        kaartTabPane.setSelectedIndex(i);
        System.out.println("Kaart getoond: " + naam);
        return;
      }
    }
    System.out.println("Kaart niet gevonden: " + naam);
  }

  public String[] getKaartNamen() {
    String[] namen = new String[kaartTabPane.getTabCount()];
    for (int i = 0; i < namen.length; i++) {
      namen[i] = kaartTabPane.getTitleAt(i);
    }
    return namen;
  }
}
