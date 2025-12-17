package osmmapper;

import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import java.awt.BorderLayout;

import javax.swing.*;

public class MapApp extends JFrame {
  private JMapViewer mapViewer;

  public MapApp() {
    mapViewer = new JMapViewer();
    mapViewer.setTileSource(new OsmTileSource.Mapnik()); // Use OSM tiles
    mapViewer.setZoomControlsVisible(true);
    mapViewer.setDisplayPosition(new Coordinate(52.5200, 13.4050), 10); // Center on Berlin, zoom 10

    this.setLayout(new BorderLayout());
    this.add(mapViewer, BorderLayout.CENTER);
    this.setSize(800, 600);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new MapApp().setVisible(true));
  }
}
