package kwee.osmmapper.lib;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class TabInfo {
  private final String id; // final maakt het immutable
  private String filePath = "";
  private String title = "";
  private double latitude = Const.c_LongLatUndefined;
  private double longtitude = Const.c_LongLatUndefined;
  private int zoomfactor = Const.c_ZoomUndefined;
  private String projects = "";
  private String fotodirectory = "";

  // Default constructor nodig voor Jackson
  public TabInfo() {
    this.id = UUID.randomUUID().toString();
  }

  public TabInfo(String filePath, String title, String fotodirectory) {
    this.id = UUID.randomUUID().toString();
    this.filePath = filePath;
    this.title = title;
    this.fotodirectory = fotodirectory;
  }

  // Getters en setters
  public String getFilePath() {
    return filePath;
  }

  public String getTitle() {
    return title;
  }

  public String getId() {
    return id;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongtitude() {
    return longtitude;
  }

  public int getZoomfactor() {
    return zoomfactor;
  }

  public String getProjects() {
    return projects;
  }

  public String getFotodirectory() {
    return fotodirectory;
  }

  // Setters
  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public void setLongtitude(double longtitude) {
    this.longtitude = longtitude;
  }

  public void setZoomfactor(int zoomfactor) {
    this.zoomfactor = zoomfactor;
  }

  public void setProjects(String projects) {
    this.projects = projects;
  }

  public void setFotodirectory(String fotodirectory) {
    this.fotodirectory = fotodirectory;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    TabInfo tabInfo = (TabInfo) o;
    boolean bstat = false;
    bstat = tabInfo.getFilePath() == this.getFilePath();
    bstat = bstat && tabInfo.getTitle() == this.getTitle();
    bstat = bstat && tabInfo.getProjects() == this.getProjects();
    return bstat;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id); // consistent met equals()
  }

  /**
   * 
   * @param tabList
   * @return
   */
  public static List<TabInfo> verwijderDuplicatenOpFile(List<TabInfo> tabList) {
    if (tabList == null || tabList.isEmpty()) {
      return new ArrayList<>();
    }

    Map<String, TabInfo> uniekeMap = new LinkedHashMap<>();
    for (TabInfo tab : tabList) {
      uniekeMap.putIfAbsent(tab.title, tab); // of een ander uniek veld
    }
    return new ArrayList<>(uniekeMap.values());
  }

  @Override
  public String toString() {
    return "TabInfo{filePath='" + filePath + "', title='" + title + "', latitude='" + latitude + "', longtitude='"
        + longtitude + "', zoomfactor='" + zoomfactor + "', projects='" + projects + "' , fotodirectory='"
        + fotodirectory + "'}";
  }
}
