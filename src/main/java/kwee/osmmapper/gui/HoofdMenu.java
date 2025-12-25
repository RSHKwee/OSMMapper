package kwee.osmmapper.gui;

import javax.swing.*;
import java.awt.*;

import kwee.osmmapper.lib.KaartController;

public class HoofdMenu {
  private JFrame hoofdFrame;
  private KaartController kaartController;

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new HoofdMenu().start());
  }

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
