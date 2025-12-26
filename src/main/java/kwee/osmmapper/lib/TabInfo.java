package kwee.osmmapper.lib;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class TabInfo {
  private final String id; // final maakt het immutable
  private String filePath;
  private String title;

  // Default constructor nodig voor Jackson
  public TabInfo() {
    this.id = UUID.randomUUID().toString();
  }

  public TabInfo(String filePath, String title) {
    this.id = UUID.randomUUID().toString();
    this.filePath = filePath;
    this.title = title;
  }

  // Getters en setters
  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
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
    return bstat;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id); // consistent met equals()
  }

  public static List<TabInfo> verwijderDuplicatenOpFile(List<TabInfo> tabList) {
    if (tabList == null || tabList.isEmpty()) {
      return new ArrayList<>();
    }

    Map<String, TabInfo> uniekeMap = new LinkedHashMap<>();
    for (TabInfo tab : tabList) {
      uniekeMap.putIfAbsent(tab.getFilePath(), tab); // of een ander uniek veld
    }
    return new ArrayList<>(uniekeMap.values());
  }

  @Override
  public String toString() {
    return "TabInfo{filePath='" + filePath + "', title='" + title + "'}";
  }
}
