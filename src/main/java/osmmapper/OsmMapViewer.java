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
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A demo class to start a Swing application which shows a map and has some pre-defined options set.
 */
public class OsmMapViewer extends JFrame implements JMapViewerEventListener {

  private static final long serialVersionUID = 1L;

  private JMapViewerTree treeMap;
  private JLabel zoomLabel;
  private JLabel zoomValue;
  private JLabel mperpLabelName;
  private JLabel mperpLabelValue;
  private JLabel statusLabel;

  public OsmMapViewer() {
    super("OSM Map Viewer");
    treeMap = new JMapViewerTree("Locaties");
    setupJFrame();
    setupPanels();

    map().addJMVListener(this);

    map().setTileSource(new OsmTileSource.Mapnik());
    map().setTileLoader(new OsmTileLoader(map()));
    map().setMapMarkerVisible(true);
    map().setZoomControlsVisible(true);

    setupMarkerInteraction();
    enableMarkerTooltips();
    addSampleMarkers();

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
    JPanel helpPanel = new JPanel();

    mperpLabelName = new JLabel("Meters/Pixels: ");
    mperpLabelValue = new JLabel(String.format("%s", map().getMeterPerPixel()));
    zoomLabel = new JLabel("Zoom: ");
    zoomValue = new JLabel(String.format("%s", map().getZoom()));

    add(panel, BorderLayout.NORTH);
    add(helpPanel, BorderLayout.SOUTH);
    panel.add(panelTop, BorderLayout.NORTH);

    JLabel helpLabel = new JLabel("Beweeg over een marker voor titel, klik voor details");
    helpPanel.add(helpLabel);

    panelTop.add(zoomLabel);
    panelTop.add(zoomValue);
    panelTop.add(mperpLabelName);
    panelTop.add(mperpLabelValue);

    // Statusbalk
    JPanel statusPanel = new JPanel(new BorderLayout());
    statusLabel = new JLabel("Klik op een marker voor gedetailleerde informatie");
    statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    statusPanel.add(statusLabel, BorderLayout.WEST);
    add(statusPanel, BorderLayout.SOUTH);
  }

  private JMapViewer map() {
    return treeMap.getViewer();
  }

  public static void main(String[] args) {
    new OsmMapViewer().setVisible(true);
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
   * Voegt een aangepaste marker toe
   */
  public void addCustomMarker(double lat, double lon, String title, String description, String extraInfo, Color color) {
    CustomMarker marker = new CustomMarker(lat, lon, title, description, extraInfo, color);
    map().addMapMarker(marker);
  }

  /**
   * Voegt markers toe vanuit Excel bestand
   */
  private void addSampleMarkers() {
    String inputFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-adressenlijst_met_coordinaten.xlsx";
    // String inputFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-warmtescan_met_coordinaten.xlsx";

    try (FileInputStream file = new FileInputStream(inputFile); Workbook workbook = WorkbookFactory.create(file)) {

      Sheet sheet = workbook.getSheetAt(0);
      int rowIndex = 0;

      for (Row row : sheet) {
        if (rowIndex == 0) {
          // Skip header rij
          rowIndex++;
          continue;
        }

        try {
          // Lees coördinaten (pas kolomnummers aan indien nodig)
          Cell longCell = row.getCell(7);
          Cell latCell = row.getCell(8);

          if (longCell == null || latCell == null) {
            continue;
          }

          Double longitude = longCell.getNumericCellValue();
          Double latitude = latCell.getNumericCellValue();

          // Lees extra gegevens (voorbeeld - pas aan naar jouw Excel structuur)
          String street = row.getCell(3) != null ? row.getCell(3).getStringCellValue() : "";
          String houseNumber = "";
          try {
            houseNumber = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "";
          } catch (Exception e) {
            Double huisnr = row.getCell(1).getNumericCellValue();
            int iHuisnr = huisnr.intValue();
            houseNumber = Integer.toString(iHuisnr);
          }
          houseNumber = houseNumber + row.getCell(2).getStringCellValue();

          String postcode = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : "";
          String city = row.getCell(4) != null ? row.getCell(4).getStringCellValue() : "";

          String sNameDetail = "";
          try {
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
          } catch (Exception e) {

          }
          // Maak titel en extra informatie
          String title = houseNumber;
          String description = "Adres in " + city;
          String extraInfo = String.format(
              "Straat: %s\nHuisnummer: %s\nPostcode: %s\nPlaats: %s\n %s\nCoördinaten: %.6f, %.6f", street, houseNumber,
              postcode, city, sNameDetail, latitude, longitude);

          // Bepaal kleur op basis van rij index (voor variatie)
          Color color;
          switch (rowIndex % 5) {
          case 0:
            color = Color.RED;
            break;
          case 1:
            color = Color.BLUE;
            break;
          case 2:
            color = Color.GREEN;
            break;
          case 3:
            color = Color.ORANGE;
            break;
          default:
            color = Color.MAGENTA;
            break;
          }

          addCustomMarker(latitude, longitude, title, description, extraInfo, color);

        } catch (Exception e) {
          System.err.println("Fout in rij " + rowIndex + ": " + e.getMessage());
        }

        rowIndex++;
      }

      System.out.println("Aantal markers toegevoegd: " + (rowIndex - 1));

    } catch (EncryptedDocumentException | IOException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this, "Fout bij laden Excel bestand: " + e.getMessage(), "Fout",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Stelt de marker interactie in
   */
  private void setupMarkerInteraction() {
    // Mouse beweging voor hover effect
    map().addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        ICoordinate coord = map().getPosition(e.getPoint());
        if (coord != null) {
          double lat = coord.getLat();
          double lon = coord.getLon();

          MapMarker hoveredMarker = null;
          double minDistance = Double.MAX_VALUE;

          // Zoek de dichtstbijzijnde marker
          for (MapMarker marker : map().getMapMarkerList()) {
            double markerLat = marker.getLat();
            double markerLon = marker.getLon();

            double distance = Math.sqrt(Math.pow(lat - markerLat, 2) + Math.pow(lon - markerLon, 2));

            if (distance < 0.005 && distance < minDistance) {
              minDistance = distance;
              hoveredMarker = marker;
            }
          }

          if (hoveredMarker != null) {
            // Toon de titel in de statusbalk
            statusLabel.setText("Marker: " + hoveredMarker.getName());
          } else {
            statusLabel.setText("Beweeg over een marker voor titel, klik voor details");
          }
        }
      }
    });

    // Mouse klik voor details
    map().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          ICoordinate coord = map().getPosition(e.getPoint());
          if (coord != null) {
            double lat = coord.getLat();
            double lon = coord.getLon();

            MapMarker clickedMarker = null;
            double minDistance = Double.MAX_VALUE;

            // Zoek de dichtstbijzijnde marker
            for (MapMarker marker : map().getMapMarkerList()) {
              double markerLat = marker.getLat();
              double markerLon = marker.getLon();

              double distance = Math.sqrt(Math.pow(lat - markerLat, 2) + Math.pow(lon - markerLon, 2));

              if (distance < 0.01 && distance < minDistance) {
                minDistance = distance;
                clickedMarker = marker;
              }
            }

            if (clickedMarker != null) {
              // Toon gedetailleerde informatie
              showMarkerDetails(clickedMarker);
            } else {
              statusLabel.setText("Klik op een marker voor gedetailleerde informatie");
            }
          }
        }
      }
    });
  }

  /**
   * Toont gedetailleerde informatie over een marker
   */
  private void showMarkerDetails(MapMarker marker) {
    String title = marker.getName();
    String details;

    if (marker instanceof CustomMarker) {
      // Gebruik de extra informatie van CustomMarker
      CustomMarker customMarker = (CustomMarker) marker;
      String description = customMarker.getDescription();
      String extraInfo = customMarker.getExtraInfo();

      details = String.format(
          "<html><div style='width: 300px;'>" + "<h3>%s</h3>" + "<b>Beschrijving:</b><br/>%s<br/><br/>"
              + "<b>Details:</b><br/>%s" + "</div></html>",
          title != null ? title : "Onbekende marker",
          description != null ? description.replace("\n", "<br/>") : "Geen beschrijving",
          extraInfo != null ? extraInfo.replace("\n", "<br/>") : "Geen extra informatie");
    } else {
      // Standaard marker
      details = String.format(
          "<html><div style='width: 300px;'>" + "<h3>%s</h3>" + "<b>Locatie:</b><br/>%.6f, %.6f<br/><br/>"
              + "<i>Geen extra informatie beschikbaar</i>" + "</div></html>",
          title != null ? title : "Marker", marker.getLat(), marker.getLon());
    }

    // Maak een aangepaste JOptionPane met HTML formatting
    JLabel messageLabel = new JLabel(details);
    messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));

    JOptionPane.showMessageDialog(this, messageLabel, "Marker Details", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Voegt tooltip ondersteuning toe aan markers
   */
  private void enableMarkerTooltips() {
    // Zorg dat de JMapViewer tooltips ondersteunt
    map().setToolTipText("");

    map().addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        ICoordinate coord = map().getPosition(e.getPoint());
        if (coord != null) {
          double lat = coord.getLat();
          double lon = coord.getLon();

          for (MapMarker marker : map().getMapMarkerList()) {
            double markerLat = marker.getLat();
            double markerLon = marker.getLon();

            double distance = Math.sqrt(Math.pow(lat - markerLat, 2) + Math.pow(lon - markerLon, 2));

            if (distance < 0.003) {
              // Set tooltip voor de kaart
              if (marker instanceof CustomMarker) {
                CustomMarker customMarker = (CustomMarker) marker;
                map().setToolTipText(
                    "<html><b>" + customMarker.getTitle() + "</b><br/>" + customMarker.getDescription() + "</html>");
              } else {
                map().setToolTipText(marker.getName());
              }
              return;
            }
          }
          map().setToolTipText(null);
        }
      }
    });
  }
}
