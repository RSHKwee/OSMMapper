package kwee.osmmapper.gui;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import kwee.logger.MyLogger;

public class FotoViewerDialog extends Dialog {
  private static final Logger LOGGER = MyLogger.getLogger();

  private static final long serialVersionUID = -3300548362604042199L;
  private List<Path> fotos;
  private int currentIndex = 0;
  private ImagePanel imagePanel;
  private Label counterLabel;

  // Constructor met vereiste parent Frame
  public FotoViewerDialog(Frame parent, List<Path> fotos) {
    super(parent, "Foto's voor adres - " + fotos.size() + " beschikbaar", true); // true = modal
    this.fotos = fotos;
    initUI();
  }

  private void initUI() {
    setLayout(new BorderLayout());

    // Hoofdweergave van de huidige foto
    imagePanel = new ImagePanel();
    updateImage(fotos.get(currentIndex));

    // Noord: Titel en sluitknop
    Panel northPanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
    Button closeButton = new Button("Sluiten");
    closeButton.addActionListener(e -> dispose());
    northPanel.add(closeButton);
    add(northPanel, BorderLayout.NORTH);

    add(imagePanel, BorderLayout.CENTER);

    // Zuiden: Navigatieknoppen als er meerdere foto's zijn
    if (fotos.size() > 1) {
      Panel southPanel = new Panel(new FlowLayout());
      Button prevButton = new Button("← Vorige");
      Button nextButton = new Button("Volgende →");

      counterLabel = new Label(" " + (currentIndex + 1) + "/" + fotos.size() + " ");

      prevButton.addActionListener(e -> {
        currentIndex = (currentIndex - 1 + fotos.size()) % fotos.size();
        updateImage(fotos.get(currentIndex));
        updateCounter();
      });

      nextButton.addActionListener(e -> {
        currentIndex = (currentIndex + 1) % fotos.size();
        updateImage(fotos.get(currentIndex));
        updateCounter();
      });

      southPanel.add(prevButton);
      southPanel.add(counterLabel);
      southPanel.add(nextButton);

      add(southPanel, BorderLayout.SOUTH);
    }

    setSize(800, 600);

    // Centreren op het scherm
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (screenSize.width - getWidth()) / 2;
    int y = (screenSize.height - getHeight()) / 2;
    setLocation(x, y);
  }

  private void updateImage(Path fotoPad) {
    try {
      Image image = ImageIO.read(fotoPad.toFile());
      imagePanel.setImage(image);
      imagePanel.repaint();
    } catch (IOException e) {
      imagePanel.setImage(null);
      imagePanel.repaint();
      LOGGER.log(Level.WARNING, "Fout bij laden foto: " + fotoPad + " - " + e.getMessage());
      // System.err.println("Fout bij laden foto: " + fotoPad + " - " +
      // e.getMessage());
    }
  }

  private void updateCounter() {
    if (counterLabel != null) {
      counterLabel.setText(" " + (currentIndex + 1) + "/" + fotos.size() + " ");
    }
  }
}

// Aangepaste ImagePanel met betere error handling
class ImagePanel extends Panel {
  private Image image;

  public void setImage(Image image) {
    this.image = image;
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);

    if (image != null) {
      // Bereken scaling om binnen het panel te passen
      int panelWidth = getWidth();
      int panelHeight = getHeight();
      int imgWidth = image.getWidth(this);
      int imgHeight = image.getHeight(this);

      if (imgWidth <= 0 || imgHeight <= 0) {
        g.drawString("Ongeldige afbeelding", 20, 20);
        return;
      }

      // Bereken schaalfactor
      double widthRatio = (double) panelWidth / imgWidth;
      double heightRatio = (double) panelHeight / imgHeight;
      double scale = Math.min(widthRatio, heightRatio);

      int scaledWidth = (int) (imgWidth * scale);
      int scaledHeight = (int) (imgHeight * scale);
      int x = (panelWidth - scaledWidth) / 2;
      int y = (panelHeight - scaledHeight) / 2;

      g.drawImage(image, x, y, scaledWidth, scaledHeight, this);
    } else {
      g.drawString("Geen foto beschikbaar", getWidth() / 2 - 40, getHeight() / 2);
    }
  }
}