package sandbox;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import kwee.logger.MyLogger;
import kwee.osmmapper.gui.FotoViewerDialog;

public class TestFotoViewer {
  private static final Logger LOGGER = MyLogger.getLogger();

  public static void main(String[] args) {
    // 1. Maak het hoofdvenster (Frame)
    Frame mainFrame = new Frame("Test OSMMapper Foto Viewer");
    mainFrame.setSize(600, 400);
    mainFrame.setLayout(new BorderLayout());

    // 2. Voeg wat testinhoud toe
    Label titleLabel = new Label("Test Foto Viewer Dialog", Label.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
    mainFrame.add(titleLabel, BorderLayout.NORTH);

    TextArea infoArea = new TextArea();
    infoArea.setText("Klik op de knop hieronder om de FotoViewerDialog te testen.\n\n"
        + "De dialog zal 3 testfoto's tonen (of fallback afbeeldingen als bestanden niet bestaan).\n"
        + "Navigeer met de knoppen als er meerdere foto's zijn.");
    infoArea.setEditable(false);
    mainFrame.add(infoArea, BorderLayout.CENTER);

    // 3. Testknoppen panel
    Panel buttonPanel = new Panel(new FlowLayout());

    // Knop 1: Test met echte bestandspaden (aan te passen naar je eigen foto's)
    Button testWithPathsButton = new Button("Test met bestandspaden");
    testWithPathsButton.addActionListener(e -> testWithFilePaths(mainFrame));

    // Knop 2: Test met niet-bestaande bestanden (toont fallback)
    Button testWithMockButton = new Button("Test met mock data");
    testWithMockButton.addActionListener(e -> testWithMockData(mainFrame));

    // Knop 3: Test met lege lijst
    Button testEmptyButton = new Button("Test lege lijst");
    testEmptyButton.addActionListener(e -> testEmptyList(mainFrame));

    buttonPanel.add(testWithPathsButton);
    buttonPanel.add(testWithMockButton);
    buttonPanel.add(testEmptyButton);

    mainFrame.add(buttonPanel, BorderLayout.SOUTH);

    // 4. Sluitknop voor de hele applicatie
    mainFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        mainFrame.dispose();
        System.exit(0);
      }
    });

    // 5. Toon het hoofdvenster
    mainFrame.setLocationRelativeTo(null); // Centreer op scherm
    mainFrame.setVisible(true);
  }

  // Methode 1: Test met echte bestandspaden
  private static void testWithFilePaths(Frame parentFrame) {
    List<Path> fotoPaths = new ArrayList<>();

    // VUL HIER JE EIGEN FOTO PADEN IN
    // Voorbeeld - pas deze paden aan naar waar jouw foto's staan
    String[] mogelijkePaden = { "D:\\Users\\René\\iCloud-foto's-archief\\", "/home/gebruiker/afbeeldingen/test.jpg",
        "test_data/foto.jpg", "src/test/resources/sample.jpg", "iCloud-foto's" };

    for (String pad : mogelijkePaden) {
      File file = new File(pad);
      if (file.exists() && !file.isDirectory()) {
        fotoPaths.add(Paths.get(pad));
        System.out.println("Gevonden: " + pad);
      }
    }

    // Als er geen echte bestanden zijn, maak dan testbestanden aan
    if (fotoPaths.isEmpty()) {
      System.out.println("Geen echte bestanden gevonden, gebruik mock data...");
      testWithMockData(parentFrame);
      return;
    }

    openFotoViewer(parentFrame, fotoPaths, "Test met echte bestanden");
  }

  // Methode 2: Test met mock data (simuleert niet-bestaande bestanden)
  private static void testWithMockData(Frame parentFrame) {
    List<Path> fotoPaths = new ArrayList<>();

    // Voeg 3 testpaden toe (deze bestaan waarschijnlijk niet)
    fotoPaths.add(Paths.get("test_foto_001.jpg"));
    fotoPaths.add(Paths.get("test_foto_002.jpg"));
    fotoPaths.add(Paths.get("test_foto_003.jpg"));

    openFotoViewer(parentFrame, fotoPaths, "Test met mock data");
  }

  // Methode 3: Test met lege lijst
  private static void testEmptyList(Frame parentFrame) {
    openFotoViewer(parentFrame, new ArrayList<>(), "Test met lege lijst");
  }

  // Gemeenschappelijke methode om de dialog te openen
  private static void openFotoViewer(Frame parentFrame, List<Path> fotoPaths, String testType) {
    System.out.println("\n=== " + testType + " ===");
    System.out.println("Aantal foto's: " + fotoPaths.size());

    if (fotoPaths.isEmpty()) {
      // Toon een informatie dialog
      Dialog infoDialog = new Dialog(parentFrame, "Test Resultaat", true);
      infoDialog.setLayout(new BorderLayout());

      Label message = new Label("Geen foto's beschikbaar - dit is verwacht gedrag voor deze test", Label.CENTER);
      message.setForeground(Color.RED);

      Button okButton = new Button("OK");
      okButton.addActionListener(e -> infoDialog.dispose());

      infoDialog.add(message, BorderLayout.CENTER);
      infoDialog.add(okButton, BorderLayout.SOUTH);

      infoDialog.setSize(400, 150);
      infoDialog.setLocationRelativeTo(parentFrame);
      infoDialog.setVisible(true);
    } else {
      // Open de FotoViewerDialog
      FotoViewerDialog viewer = new FotoViewerDialog(parentFrame, fotoPaths);

      // Voeg een listener toe om te weten wanneer de dialog sluit
      viewer.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
          System.out.println("FotoViewerDialog gesloten");
        }
      });

      viewer.setVisible(true);
    }
  }
}

// Vereenvoudigde versie van ImagePanel voor de test
class ImagePanel extends Panel {
  private Image image;

  public void setImage(Image image) {
    this.image = image;
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);

    if (image != null) {
      // Eenvoudige weergave
      g.drawImage(image, 10, 10, getWidth() - 20, getHeight() - 20, this);
    } else {
      // Teken een fallback rechthoek
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(10, 10, getWidth() - 20, getHeight() - 20);

      g.setColor(Color.BLACK);
      g.drawRect(10, 10, getWidth() - 20, getHeight() - 20);

      g.setColor(Color.RED);
      g.drawString("Geen afbeelding geladen", getWidth() / 2 - 60, getHeight() / 2);
      g.drawString("(Dit is een test - gebruik echte foto paden)", getWidth() / 2 - 100, getHeight() / 2 + 20);
    }
  }
}
