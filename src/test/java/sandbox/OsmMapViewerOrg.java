package sandbox;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import kwee.library.Address;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A demo class to start a Swing application which shows a map and has some pre-defined options set.
 * 
 * Based on: http://svn.openstreetmap.org/applications/viewer/jmapviewer/src/org/openstreetmap/gui/jmapviewer/Demo.java
 * by Jan Peter Stotz
 */
public class OsmMapViewerOrg extends JFrame implements JMapViewerEventListener {

  private static final long serialVersionUID = 1L;

  private JMapViewerTree treeMap;
  private JLabel zoomLabel;
  private JLabel zoomValue;
  private JLabel mperpLabelName;
  private JLabel mperpLabelValue;

  /**
   * Setups the JFrame layout, sets some default options for the JMapViewerTree and displays a map in the window.
   */
  public OsmMapViewerOrg() {
    super("JMapViewer Demo");
    treeMap = new JMapViewerTree("Zones");
    setupJFrame();
    setupPanels();

    // Listen to the map viewer for user operations so components will
    // receive events and updates
    map().addJMVListener(this);

    // Set some options, e.g. tile source and that markers are visible
    map().setTileSource(new OsmTileSource.Mapnik());
    map().setTileLoader(new OsmTileLoader(map()));
    map().setMapMarkerVisible(true);
    map().setZoomContolsVisible(true);

    // VOEG HIER MARKERS TOE
    addSampleMarkers();

    // activate map in window
    treeMap.setTreeVisible(true);
    add(treeMap, BorderLayout.CENTER);
  }

  private void setupJFrame() {
    setSize(400, 400);
    setLayout(new BorderLayout());
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setExtendedState(JFrame.MAXIMIZED_BOTH);
  }

  private void setupPanels() {
    JPanel panel = new JPanel(new BorderLayout());
    JPanel panelTop = new JPanel();
    JPanel panelBottom = new JPanel();
    JPanel helpPanel = new JPanel();

    mperpLabelName = new JLabel("Meters/Pixels: ");
    mperpLabelValue = new JLabel(String.format("%s", map().getMeterPerPixel()));
    zoomLabel = new JLabel("Zoom: ");
    zoomValue = new JLabel(String.format("%s", map().getZoom()));

    add(panel, BorderLayout.NORTH);
    add(helpPanel, BorderLayout.SOUTH);
    panel.add(panelTop, BorderLayout.NORTH);
    panel.add(panelBottom, BorderLayout.SOUTH);
    JLabel helpLabel = new JLabel("Use right mouse button to move,\n " + "left double click or mouse wheel to zoom.");
    helpPanel.add(helpLabel);

    panelTop.add(zoomLabel);
    panelTop.add(zoomValue);
    panelTop.add(mperpLabelName);
    panelTop.add(mperpLabelValue);
  }

  private JMapViewer map() {
    return treeMap.getViewer();
  }

  /**
   * @param args Main program arguments
   */
  public static void main(String[] args) {
    new OsmMapViewerOrg().setVisible(true);
  }

  private void updateZoomParameters() {
    if (mperpLabelValue != null)
      mperpLabelValue.setText(String.format("%s", map().getMeterPerPixel()));
    if (zoomValue != null)
      zoomValue.setText(String.format("%s", map().getZoom()));
  }

  @Override
  public void processCommand(JMVCommandEvent command) {
    if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM)
        || command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)) {
      updateZoomParameters();
    }
  }

  /**
   * Voegt een marker toe met een specifieke kleur
   * 
   * @param lat   Latitude (breedtegraad)
   * @param lon   Longitude (lengtegraad)
   * @param name  Naam van de marker
   * @param color Kleur van de marker
   */
  public void addMarker(double lat, double lon, String name, Color color) {
    MapMarkerDot marker = new MapMarkerDot(null, name, lat, lon);
    marker.setColor(color);
    map().addMapMarker(marker);
  }

  /**
   * Voegt enkele voorbeeldmarkers toe
   * 
   * @throws FileNotFoundException
   */
  private void addSampleMarkers() {
    String inputFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-adressenlijst_met_coordinaten.xlsx";
    Workbook workbook;
    try {
      FileInputStream file = new FileInputStream(inputFile);
      workbook = WorkbookFactory.create(file);

      // 2. Kies het eerste werkblad
      Sheet sheet = workbook.getSheetAt(0);

      // 3. Loop door alle rijen
      int rowIndex = 0;
      for (Row row : sheet) {
        Address l_address = new Address();
        // if (rowIndex < 10) {
        if (rowIndex == 0) {

        } else {
          Cell longCell = row.getCell(7);
          Double longitude = longCell.getNumericCellValue();

          Cell latCell = row.getCell(8);
          Double latitude = latCell.getNumericCellValue();

          addMarker(latitude, longitude, l_address.getRoad() + " " + l_address.getHouseNumber(), Color.RED);
        }
        rowIndex++;
      }
    } catch (EncryptedDocumentException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}