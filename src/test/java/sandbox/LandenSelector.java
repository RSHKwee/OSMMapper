package sandbox;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class LandenSelector extends JFrame {
  /**
  * 
  */
  private static final long serialVersionUID = 1208819387679568944L;
  private JComboBox<String> landenComboBox;
  private JLabel resultaatLabel;

  public LandenSelector() {
    setTitle("Landen Selector");
    setLayout(new BorderLayout());

    // Landen ophalen en sorteren
    ArrayList<String> landenLijst = new ArrayList<>();
    for (String code : Locale.getISOCountries()) {
      Locale locale = new Locale("", code);
      String landNaam = locale.getDisplayCountry(new Locale("nl", "NL"));
      if (!landNaam.isEmpty()) {
        landenLijst.add(landNaam);
      }
    }
    Collections.sort(landenLijst);

    // Voeg "Selecteer een land" toe als eerste optie
    landenLijst.add(0, "-- Selecteer een land --");

    // Maak combobox
    landenComboBox = new JComboBox<>(landenLijst.toArray(new String[0]));
    landenComboBox.setSelectedIndex(0);

    resultaatLabel = new JLabel(" ");
    resultaatLabel.setHorizontalAlignment(SwingConstants.CENTER);

    // Voeg action listener toe
    landenComboBox.addActionListener(e -> {
      String gekozen = (String) landenComboBox.getSelectedItem();
      if (!gekozen.equals("-- Selecteer een land --")) {
        resultaatLabel.setText("Gekozen land: " + gekozen);
      } else {
        resultaatLabel.setText(" ");
      }
    });

    // Layout
    JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    panel.add(new JLabel("Kies een land:"));
    panel.add(landenComboBox);
    panel.add(resultaatLabel);

    add(panel, BorderLayout.CENTER);

    setSize(400, 200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      new LandenSelector().setVisible(true);
    });
  }
}