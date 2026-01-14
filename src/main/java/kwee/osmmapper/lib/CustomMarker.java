package kwee.osmmapper.lib;

import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import java.awt.Color;

/**
 * Aangepaste marker met extra informatie
 */
public class CustomMarker extends MapMarkerDot {
  private String title;
  private String description;
  private String extraInfo;
  private String pictureIndex;

  public CustomMarker(double lat, double lon, String title, String description, String extraInfo, Color color,
      String pictureIndex) {
    super(null, title, lat, lon);
    this.setTitle(title);
    this.setDescription(description);
    this.setExtraInfo(extraInfo);
    this.setColor(color);
    this.setPictureIndex(pictureIndex);
  }

  @Override
  public String getName() {
    return title; // Toon de titel als naam
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setExtraInfo(String extraInfo) {
    this.extraInfo = extraInfo;
  }

  public void setPictureIndex(String pictureIndex) {
    this.pictureIndex = pictureIndex;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getExtraInfo() {
    return extraInfo;
  }

  public String getPictureIndex() {
    return pictureIndex;
  }
}