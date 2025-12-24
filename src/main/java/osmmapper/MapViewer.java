package osmmapper;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A demo class to start a Swing application which shows a map and has some pre-defined options set.
 * 
 * Based on: http://svn.openstreetmap.org/applications/viewer/jmapviewer/src/org/openstreetmap/gui/jmapviewer/Demo.java
 * by Jan Peter Stotz
 */
public class MapViewer extends JFrame implements JMapViewerEventListener {

  private static final long serialVersionUID = 1L;

  private JMapViewerTree treeMap;
  private JLabel zoomLabel;
  private JLabel zoomValue;
  private JLabel mperpLabelName;
  private JLabel mperpLabelValue;
  private JLabel statusLabel; // Nieuwe statusbalk label

  /**
   * Setups the JFrame layout, sets some default options for the JMapViewerTree and displays a map in the window.
   */
  public MapViewer() {
    super("JMapViewer");
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

    // MARKER INTERACTIE INSTELLEN
    setupMarkerInteraction();

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
    JLabel helpLabel = new JLabel("Use right mouse buttons to move,\n " + "left double click or mouse wheel to zoom.");
    helpPanel.add(helpLabel);

    panelTop.add(zoomLabel);
    panelTop.add(zoomValue);
    panelTop.add(mperpLabelName);
    panelTop.add(mperpLabelValue);

    // VOEG STATUS BALK TOE
    JPanel statusPanel = new JPanel(new BorderLayout());
    statusLabel = new JLabel("Klik op een marker voor informatie");
    statusPanel.add(statusLabel, BorderLayout.WEST);
    add(statusPanel, BorderLayout.SOUTH);
  }

  private JMapViewer map() {
    return treeMap.getViewer();
  }

  /**
   * @param args Main program arguments
   */
  public static void main(String[] args) {
    new MapViewer().setVisible(true);
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
    // String inputFile =
    // "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-adressenlijst_met_coordinaten.xlsx";
    String inputFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-warmtescan_met_coordinaten.xlsx";
    Workbook workbook;
    try {
      FileInputStream file = new FileInputStream(inputFile);
      workbook = WorkbookFactory.create(file);

      // 2. Kies het eerste werkblad
      Sheet sheet = workbook.getSheetAt(0);

      // 3. Loop door alle rijen
      int rowIndex = 0;
      for (Row row : sheet) {
        if (rowIndex == 0) {
          // Skip header rij
        } else {
          Cell longCell = row.getCell(7);
          Double longitude = longCell.getNumericCellValue();

          Cell latCell = row.getCell(8);
          Double latitude = latCell.getNumericCellValue();

          // Haal adresgegevens uit Excel als die beschikbaar zijn
          String sHuisnr = "";
          try {
            Double huisnr = row.getCell(1).getNumericCellValue();
            int iHuisnr = huisnr.intValue();
            sHuisnr = Integer.toString(iHuisnr);
          } catch (Exception e) {
            sHuisnr = row.getCell(1).getStringCellValue();
          }
          String sHuisNrAdd = "";
          try {
            sHuisNrAdd = row.getCell(2).getStringCellValue();
          } catch (Exception e) {
            sHuisNrAdd = "";
          }
          String sNameDetail = "";
          Cell nameDetailCell = row.getCell(13);
          CellType type = nameDetailCell.getCellType();
          if (type == CellType.STRING) {
            if (nameDetailCell != null) {
              sNameDetail = sNameDetail + " " + nameDetailCell.getStringCellValue();
            }
          }

          nameDetailCell = row.getCell(14);
          type = nameDetailCell.getCellType();
          if (type == CellType.STRING) {
            if (nameDetailCell != null) {
              sNameDetail = sNameDetail + "\n" + nameDetailCell.getStringCellValue();
            }
          }
          nameDetailCell = row.getCell(15);
          type = nameDetailCell.getCellType();
          if (type == CellType.STRING) {
            if (nameDetailCell != null) {
              sNameDetail = sNameDetail + " " + nameDetailCell.getStringCellValue();
            }
          }
          String addressName = sHuisnr + sHuisNrAdd + sNameDetail;
          if (addressName.trim().isEmpty()) {
            addressName = "Adres " + rowIndex;
          }

          addMarker(latitude, longitude, addressName, Color.RED);
        }
        rowIndex++;
      }
    } catch (EncryptedDocumentException | IOException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this, "Fout bij laden Excel bestand: " + e.getMessage(), "Fout",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Stelt de marker interactie in
   */
  Double minDistance = 0.00005;

  private void setupMarkerInteraction() {
    // Voeg mouse listeners toe voor hover effect
    map().addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        // Converteer schermcoördinaten naar kaartcoördinaten
        ICoordinate coord = map().getPosition(e.getPoint());
        if (coord != null) {
          double lat = coord.getLat();
          double lon = coord.getLon();

          // Controleer alle markers
          for (org.openstreetmap.gui.jmapviewer.interfaces.MapMarker marker : map().getMapMarkerList()) {
            double markerLat = marker.getLat();
            double markerLon = marker.getLon();

            // Bereken afstand (vereenvoudigd - in graden)
            double distance = Math.sqrt(Math.pow(lat - markerLat, 2) + Math.pow(lon - markerLon, 2));

            // Als de cursor dichtbij een marker is (binnen 0.005 graden)
            if (distance < minDistance) {
              String markerName = marker.getName();
              if (markerName != null && !markerName.trim().isEmpty()) {
                statusLabel.setText("Marker: " + markerName);
              } else {
                statusLabel.setText("Marker op: " + String.format("%.6f, %.6f", markerLat, markerLon));
              }
              return;
            }
          }
          // Geen marker in de buurt
          statusLabel.setText("Beweeg over een marker voor informatie");
        }
      }
    });

    // Voeg mouse listener toe voor klikken
    map().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        // Linkermuisklik
        if (e.getButton() == MouseEvent.BUTTON1) {
          // Converteer schermcoördinaten naar kaartcoördinaten
          ICoordinate coord = map().getPosition(e.getPoint());
          if (coord != null) {
            double lat = coord.getLat();
            double lon = coord.getLon();

            // Controleer alle markers
            org.openstreetmap.gui.jmapviewer.interfaces.MapMarker clickedMarker = null;
            for (org.openstreetmap.gui.jmapviewer.interfaces.MapMarker marker : map().getMapMarkerList()) {
              double markerLat = marker.getLat();
              double markerLon = marker.getLon();

              // Bereken afstand
              double distance = Math.sqrt(Math.pow(lat - markerLat, 2) + Math.pow(lon - markerLon, 2));

              // Als er op een marker is geklikt (binnen 0.01 graden)
              if (distance < minDistance) {
                clickedMarker = marker;
                break;
              }
            }

            if (clickedMarker != null) {
              // Toon informatievenster
              String markerName = clickedMarker.getName();
              String message;
              if (markerName != null && !markerName.trim().isEmpty()) {
                message = "Marker: " + markerName + "\nLocatie: "
                    + String.format("%.6f, %.6f", clickedMarker.getLat(), clickedMarker.getLon());
              } else {
                message = "Marker op: " + String.format("%.6f, %.6f", clickedMarker.getLat(), clickedMarker.getLon());
              }

              JOptionPane.showMessageDialog(MapViewer.this, message, "Marker Informatie",
                  JOptionPane.INFORMATION_MESSAGE);
            } else {
              // Klikte niet op een marker
              statusLabel.setText("Klik op een marker voor gedetailleerde informatie");
            }
          }
        }
      }
    });
  }
}
