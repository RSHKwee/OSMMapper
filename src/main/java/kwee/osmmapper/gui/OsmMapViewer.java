package kwee.osmmapper.gui;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import kwee.osmmapper.lib.CustomMarker;
import kwee.osmmapper.lib.Mediaan;
import kwee.osmmapper.lib.MemoContent;
import kwee.osmmapper.lib.OSMMapExcel;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.JComponent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;

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

  private ArrayList<Double> longarr = new ArrayList<Double>();
  private ArrayList<Double> latarr = new ArrayList<Double>();

  // private String inputFile =
  // "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-adressenlijst_met_coordinaten.xlsx";
  private String inputFile = "F:\\dev\\Tools\\OSMMapper\\src\\test\\resources\\Hoevelaken-warmtescan_met_coordinaten_new.xlsx";

  public OsmMapViewer(String inpFile, String subtitel) {
    super("OSM Map Viewer " + subtitel);
    inputFile = inpFile;
    OsmMapViewerInit();
  }

  public OsmMapViewer() {
    super("OSM Map Viewer");
    OsmMapViewerInit();
  }

  private void OsmMapViewerInit() {
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
    addMarkers(inputFile);

    double lat = Mediaan.mediaanList(latarr);
    double lon = Mediaan.mediaanList(longarr);
    map().setDisplayPosition(new Coordinate(lat, lon), 15);
    updateZoomParameters();

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
    longarr.add(lon);
    latarr.add(lat);
  }

  /**
   * Voegt markers toe vanuit Excel bestand
   */
  int rowIndex = 0;

  private void addMarkers(String inputFile) {
    ArrayList<MemoContent> memocontarr = new ArrayList<MemoContent>();
    OSMMapExcel mExcel = new OSMMapExcel(inputFile);
    memocontarr = mExcel.ReadExcel();

    memocontarr.forEach(memoinh -> {
      Double longitude = memoinh.getLongitude();
      Double latitude = memoinh.getLatitude();
      String street = memoinh.getStreet();
      String houseNumber = memoinh.getHousenumber();
      String postcode = memoinh.getPostcode();
      String city = memoinh.getCity();

      if (!street.isBlank()) {
        String sNameDetail = memoinh.getSurname() + " " + memoinh.getFamilyname() + "\nTel: " + memoinh.getPhonenumber()
            + "\nMail: " + memoinh.getMailaddress();

        // Maak titel en extra informatie
        String title = houseNumber;
        // String description = "Adres in " + city;
        String description = String.format("Adres: %s %s\nPostcode: %s\nPlaats: %s", street, houseNumber, postcode,
            city);
        String extraInfo = String.format(" %s\nCoördinaten: %.6f, %.6f", sNameDetail, latitude, longitude);

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

        rowIndex++;
      }
    });
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

              if (distance < 0.005 && distance < minDistance) {
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

            if (distance < 0.0003) {
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

  public void centerOnLocation(double lat, double lon, int zoom) {
    map().setDisplayPosition(new Coordinate(lat, lon), zoom);
  }

  // OPTIONEEL: Maak een methode om alleen de kaart component te krijgen
  public JComponent getKaartComponent() {
    // Als je een JMapViewer instantie hebt:
    // return mapViewer;

    // Of retourneer het complete content pane:
    return (JComponent) getContentPane();
  }

  // Methode om te controleren of kaart geladen is
  public boolean isKaartGeladen() {
    return getContentPane().getComponentCount() > 0;
  }

  // Zorg dat de JFrame niet sluit bij gebruik in tabblad
  @Override
  public void setDefaultCloseOperation(int operation) {
    // Overschrijf om te voorkomen dat de JFrame sluit
    super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
  }
}
