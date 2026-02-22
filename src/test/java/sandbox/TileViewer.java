package sandbox;

import javax.swing.*;

import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;

import java.awt.*;
import java.awt.event.*;

public class TileViewer extends JFrame implements JMapViewerEventListener {
  private static final long serialVersionUID = 891528822217410315L;
  private JMapViewer mapViewer;
  private JTextArea textArea;
  private Timer updateTimer;

  public TileViewer() {
    setTitle("JMapViewer - Tile Informatie");
    setSize(1200, 800);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Maak de kaart
    mapViewer = new JMapViewer();
    mapViewer.addJMVListener(this);

    // Voeg muis listener toe voor interactie
    mapViewer.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
        updateTileInfo();
      }
    });

    mapViewer.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        // Gebruik timer om niet te vaak te updaten tijdens slepen
        if (updateTimer != null && updateTimer.isRunning()) {
          updateTimer.restart();
        } else {
          updateTimer = new Timer(500, evt -> {
            updateTileInfo();
            updateTimer.stop();
          });
          updateTimer.setRepeats(false);
          updateTimer.start();
        }
      }
    });

    // Maak split pane
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setLeftComponent(new JScrollPane(mapViewer));

    // Info paneel
    textArea = new JTextArea();
    textArea.setEditable(false);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(400, 600));
    splitPane.setRightComponent(scrollPane);

    add(splitPane, BorderLayout.CENTER);

    // Knoppen paneel
    JPanel buttonPanel = new JPanel();

    JButton updateButton = new JButton("Toon tile informatie");
    updateButton.addActionListener(e -> updateTileInfo());
    buttonPanel.add(updateButton);

    JButton centerButton = new JButton("Centrum Amsterdam");
    centerButton.addActionListener(e -> {
      mapViewer.setDisplayPosition(new Coordinate(52.370216, 4.895168), 12);
      updateTileInfo();
    });
    buttonPanel.add(centerButton);

    add(buttonPanel, BorderLayout.SOUTH);
  }

  private void updateTileInfo() {
    textArea.setText("");
    appendInfo("=== TILE INFORMATIE ===\n");

    int zoom = mapViewer.getZoom();
    ICoordinate center = mapViewer.getPosition();
    // TileSource tileSource = mapViewer.getTileController().getTileSource();
    int tileSize = 256;

    appendInfo("Zoom niveau: " + zoom);
    appendInfo("Centrum: (" + formatCoord(center.getLat()) + ", " + formatCoord(center.getLon()) + ")");
    appendInfo("Kaart afmeting: " + mapViewer.getWidth() + " x " + mapViewer.getHeight() + " pixels");

    // Bereken tile voor centrum
    int centerTileX = lonToTileX(center.getLon(), zoom);
    int centerTileY = latToTileY(center.getLat(), zoom);
    appendInfo("\nCentrum tile: X=" + centerTileX + ", Y=" + centerTileY);

    // Bereken hoeveel tiles er in het scherm passen
    int tilesWide = (int) Math.ceil((double) mapViewer.getWidth() / tileSize) + 2; // +2 voor marge
    int tilesHigh = (int) Math.ceil((double) mapViewer.getHeight() / tileSize) + 2;

    // Bereken de tile ranges
    int startX = centerTileX - tilesWide / 2;
    int endX = centerTileX + tilesWide / 2;
    int startY = centerTileY - tilesHigh / 2;
    int endY = centerTileY + tilesHigh / 2;

    appendInfo("\nTile bereik:");
    appendInfo("  X: " + startX + " tot " + endX);
    appendInfo("  Y: " + startY + " tot " + endY);

    int maxTiles = (1 << zoom) - 1;
    int validTiles = 0;

    appendInfo("\nZichtbare tiles:");
    for (int x = startX; x <= endX; x++) {
      for (int y = startY; y <= endY; y++) {
        // Controleer of tile binnen geldige grenzen valt
        if (x >= 0 && x <= maxTiles && y >= 0 && y <= maxTiles) {
          validTiles++;
          appendInfo(String.format("  Tile: X=%-8d Y=%-8d", x, y));
        }
      }
    }

    appendInfo("\nTotaal zichtbare tiles: " + validTiles);
  }

  // Hulpmethoden voor tile berekening
  private int lonToTileX(double lon, int zoom) {
    return (int) Math.floor((lon + 180) / 360 * (1 << zoom));
  }

  private int latToTileY(double lat, int zoom) {
    return (int) Math.floor(
        (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
  }

  private String formatCoord(double coord) {
    return String.format("%.6f", coord);
  }

  private void appendInfo(String text) {
    textArea.append(text + "\n");
  }

  @Override
  public void processCommand(JMVCommandEvent command) {
    if (command.getCommand() == JMVCommandEvent.COMMAND.ZOOM) {
      // Update bij zoomen
      SwingUtilities.invokeLater(this::updateTileInfo);
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
        e.printStackTrace();
      }
      new TileViewer().setVisible(true);
    });
  }
}