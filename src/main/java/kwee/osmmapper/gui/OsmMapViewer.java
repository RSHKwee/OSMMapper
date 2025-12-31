package kwee.osmmapper.gui;

/**
 * OSM Map GUI
 */

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import kwee.osmmapper.lib.Const;
import kwee.logger.MyLogger;
import kwee.osmmapper.lib.CustomMarker;
import kwee.osmmapper.lib.Mediaan;
import kwee.osmmapper.lib.MemoContent;
import kwee.osmmapper.lib.OSMMapExcel;
import kwee.osmmapper.lib.TabInfo;
import kwee.osmmapper.main.UserSetting;

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

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.List;
import java.util.logging.Level;

/**
 * Start a Swing application which shows a map and has some pre-defined options set.
 */
public class OsmMapViewer extends JFrame implements JMapViewerEventListener {
  private static final Logger LOGGER = MyLogger.getLogger();
  private static final long serialVersionUID = 1L;
  private UserSetting m_params = UserSetting.getInstance();
  private double distanctTreshold = 0.005;

  private JMapViewerTree treeMap;
  private JLabel zoomLabel;
  private JLabel zoomValue;
  private JLabel mperpLabelName;
  private JLabel mperpLabelValue;
  private JLabel statusLabel;

  private ArrayList<Double> longarr = new ArrayList<Double>();
  private ArrayList<Double> latarr = new ArrayList<Double>();

  private String inputFile = "";
  private String title = "";
  private String m_projects = "";
  private double lat = Const.c_LongLatUndefined;
  private double lon = Const.c_LongLatUndefined;
  private int zoom = Const.c_ZoomUndefined;
  private int rowIndex = 0;

  public OsmMapViewer(String inpFile, String subtitel, double a_lat, double a_lon, int a_zoom, String a_projects) {
    super("OSM Map Viewer " + subtitel);
    title = subtitel;
    inputFile = inpFile;
    lat = a_lat;
    lon = a_lon;
    zoom = a_zoom;
    m_projects = a_projects;
    OsmMapViewerInit();
  }

  public OsmMapViewer() {
    super("OSM Map Viewer");
    m_projects = "";
    OsmMapViewerInit();
  }

  /**
   * Add Custom marker
   * 
   * @param lat         Latitude
   * @param lon         Longitude
   * @param title       Title
   * @param description Description
   * @param extraInfo   Additional information
   * @param color       Marker color
   */
  public void addCustomMarker(double lat, double lon, String title, String description, String extraInfo, Color color) {
    if (!Const.compareDouble(lat, Const.c_LongLatUndefined) && !Const.compareDouble(lon, Const.c_LongLatUndefined)) {
      CustomMarker marker = new CustomMarker(lat, lon, title, description, extraInfo, color);
      map().addMapMarker(marker);
      longarr.add(lon);
      latarr.add(lat);
    }
  }

  /**
   * Center map in Window on coordinates lat, lon and zoom map.
   * 
   * @param lat  Latitude
   * @param lon  Longitude
   * @param zoom Zoom factor
   */
  public void centerOnLocation(double lat, double lon, int zoom) {
    map().setDisplayPosition(new Coordinate(lat, lon), zoom);
  }

  /**
   * Prevent JFrame from closing when using the Tab page. Override to prevent closing the JFrame
   */
  @Override
  public void setDefaultCloseOperation(int operation) {
    super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
  }

  @Override
  public void processCommand(JMVCommandEvent command) {
    if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM)
        || command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)) {
      updateZoomParameters();
    }
  }

  // ======== Private functions ========
  /**
   * Initialization OsmMapViewr, setup frame, markers, tooltips, etc.
   */
  private void OsmMapViewerInit() {
    treeMap = new JMapViewerTree("Locaties");
    longarr.clear();
    latarr.clear();

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

    if (Const.compareDouble(lat, Const.c_LongLatUndefined) || Const.compareDouble(lon, Const.c_LongLatUndefined)
        || (zoom == Const.c_ZoomUndefined)) {
      if (!((latarr.isEmpty() || longarr.isEmpty()))) {
        lat = Mediaan.mediaanList(latarr);
        lon = Mediaan.mediaanList(longarr);
        zoom = 15;
      } else {
        // centre NL Ut
        lat = 52.1326;
        lon = 5.2913;
        zoom = 7;
        LOGGER.log(Level.INFO, "Kaart NL, centrum Ut, wordt aangemaakt.");
      }
    }
    if (!inputFile.isEmpty() && (latarr.isEmpty() || longarr.isEmpty())) {
      LOGGER.log(Level.INFO, "Geen geo info in XLSX: " + inputFile);
    }

    map().setDisplayPosition(new Coordinate(lat, lon), zoom);
    updateZoomParameters();

    treeMap.setTreeVisible(false);
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
    updatePreference();
  }

  /**
   * Add markers
   * 
   * @param inputFile Excel file with marker info.
   */
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
      String country = memoinh.getCountry();
      String projects = memoinh.getProjects();
      if (projects.toLowerCase().contains(m_projects) || m_projects.isBlank()) {
        if (!street.isBlank()) {
          String sNameDetail = "";
          if (!memoinh.getSurname().isBlank()) {
            sNameDetail = sNameDetail + memoinh.getSurname();
          }
          if (!memoinh.getFamilyname().isBlank()) {
            sNameDetail = sNameDetail + " " + memoinh.getFamilyname();
          }
          if (!memoinh.getPhonenumber().isBlank()) {
            sNameDetail = sNameDetail + "\nTel: " + memoinh.getPhonenumber();
          }
          if (!memoinh.getMailaddress().isBlank()) {
            sNameDetail = sNameDetail + "\nMail: " + memoinh.getMailaddress();
          }
          if (!memoinh.getProjects().isBlank()) {
            sNameDetail = sNameDetail + "\nProjecten: " + memoinh.getProjects();
          }

          // Maak titel en extra informatie
          String title = houseNumber;
          String description = String.format("Adres: %s %s \nPostcode: %s \nPlaats: %s \nLand: %s", street, houseNumber,
              postcode, city, country);
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
        } // Projects
        rowIndex++;
      }
    });
    LOGGER.log(Level.INFO, "Aantal markers: " + rowIndex);
  }

  /**
   * Setup Marker interaction
   */
  private void setupMarkerInteraction() {
    // Mouse beweging voor hover effect
    map().addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        ICoordinate coord = map().getPosition(e.getPoint());
        if (coord != null) {
          MapMarker hoveredMarker = getMousePosition4Marker(coord);

          if (hoveredMarker != null) {
            // Toon de titel in de statusbalk
            CustomMarker customMarker = (CustomMarker) hoveredMarker;
            statusLabel.setText("Marker: " + customMarker.getName() + " " + customMarker.getDescription());
          } else {
            statusLabel.setText("Beweeg over een marker voor titel, klik voor details");
          }
        }
      }
    });

    // Mouse click for details
    map().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          ICoordinate coord = map().getPosition(e.getPoint());
          if (coord != null) {
            MapMarker clickedMarker = getMousePosition4Marker(coord);

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

    // Make a special JOptionPane with HTML formatting
    JLabel messageLabel = new JLabel(details);
    messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));

    JOptionPane.showMessageDialog(this, messageLabel, "Marker Details", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Add tooltip support for markers
   */
  private void enableMarkerTooltips() {
    map().setToolTipText("");

    map().addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        ICoordinate coord = map().getPosition(e.getPoint());
        if (coord != null) {
          MapMarker hoveredMarker = getMousePosition4Marker(coord);

          // Set tooltip for the map
          if (hoveredMarker != null) {
            CustomMarker customMarker = (CustomMarker) hoveredMarker;
            map().setToolTipText(
                "<html><b>" + customMarker.getTitle() + "</b><br/>" + customMarker.getDescription() + "</html>");
          } else {
            map().setToolTipText("");
          }
        }
      }
    });
  }

  /**
   * Get Marker position according to Mouse position.
   * 
   * @param coord Mouse coordinates
   * @return Found marker or null (if not found)
   */
  private MapMarker getMousePosition4Marker(ICoordinate coord) {
    double lat = coord.getLat();
    double lon = coord.getLon();

    MapMarker hoveredMarker = null;
    double minDistance = Double.MAX_VALUE;

    // Zoek de dichtstbijzijnde marker
    for (MapMarker marker : map().getMapMarkerList()) {
      double markerLat = marker.getLat();
      double markerLon = marker.getLon();
      double distance = Math.sqrt(Math.pow(lat - markerLat, 2) + Math.pow(lon - markerLon, 2));

      if (distance < distanctTreshold && distance < minDistance) {
        minDistance = distance;
        hoveredMarker = marker;
      }
    }
    return hoveredMarker;
  }

  /**
   * Update Preference
   */
  private void updatePreference() {
    List<TabInfo> tablist = new ArrayList<>();
    tablist = m_params.get_TabState();
    synchronized (tablist) {
      List<TabInfo> l_tablist = new ArrayList<TabInfo>(tablist);
      for (int i = 0; i < tablist.size(); i++) {
        TabInfo tab = new TabInfo();
        tab = l_tablist.get(i);
        if (tab.getTitle().equalsIgnoreCase(title)) {
          tab.setZoomfactor(map().getZoom());
          // Via de huidige viewport (zichtbaar gebied)
          JMapViewer l_map = new JMapViewer();
          l_map = map();
          Coordinate center = (Coordinate) l_map.getPosition(l_map.getWidth() / 2, l_map.getHeight() / 2);

          double latitude = center.getLat();
          double longitude = center.getLon();
          tab.setLatitude(latitude);
          tab.setLongtitude(longitude);

          m_params.set_TabState(l_tablist);
          m_params.save();
        }
      }
    }
  }
}