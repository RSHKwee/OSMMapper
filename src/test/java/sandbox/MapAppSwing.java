package sandbox;

import java.awt.BorderLayout;

import javax.swing.*;
import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

public class MapAppSwing {

  public static void main(String[] args) {
    // 1. Keep Swing UI in Swing thread
    SwingUtilities.invokeLater(() -> {
      // 2. Create main frame
      JFrame frame = new JFrame("CSV Address Mapper");

      // 3. Create and configure JMapViewer
      JMapViewer mapViewer = new JMapViewer();
      mapViewer.setTileSource(new OsmTileSource.Mapnik());
      mapViewer.setZoomControlsVisible(true);
      mapViewer.setDisplayPosition(new Coordinate(52.172160, 5.392729), 10);
// wpt lat="52.172160" lon="5.392729"
      // 4. Create UI layout with control panel
      JPanel controlPanel = new JPanel();
      JButton loadCsvButton = new JButton("Load CSV");
      controlPanel.add(loadCsvButton);

      // Example: Add a red dot marker for a specific coordinate
      Coordinate amsterdamCoord = new Coordinate(52.172160, 5.392729);
      MapMarkerDot marker = new MapMarkerDot(amsterdamCoord);
      mapViewer.addMapMarker(marker);
      frame.setLayout(new BorderLayout());
      frame.add(controlPanel, BorderLayout.NORTH);
      frame.add(mapViewer, BorderLayout.CENTER);

      // 5. Handle button click with clean separation
      loadCsvButton.addActionListener(e -> {
        // This is where your CSV logic goes
        processCsvAndAddMarkers(mapViewer);
      });

      frame.setSize(1000, 700);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);
    });
  }

  private static void processCsvAndAddMarkers(JMapViewer mapViewer) {
    // This is where you integrate your CSV parsing and geocoding
    // Keep this logic separate from the UI code

    // Example: Add a marker after processing
    MapMarkerDot marker = new MapMarkerDot(new Coordinate(52.3676, 4.9041));
    mapViewer.addMapMarker(marker);
    mapViewer.repaint();

    // In reality, you would:
    // 1. Open CSV file using OpenCSV
    // 2. Send addresses to geocoding API
    // 3. Add markers for each coordinate
  }
}
