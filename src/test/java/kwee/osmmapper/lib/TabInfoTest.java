package kwee.osmmapper.lib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class TabInfoTest {

  @Test
  void defaultConstructorGenereertUniekId() {
    TabInfo a = new TabInfo();
    TabInfo b = new TabInfo();
    assertFalse(a.getId().isBlank());
    assertFalse(b.getId().isBlank());
    assertNotEquals(a.getId(), b.getId());
  }

  @Test
  void constructorMetArgumentenVultVelden() {
    TabInfo tab = new TabInfo("/tmp/x.xlsx", "Titel", "/fotos");
    assertEquals("/tmp/x.xlsx", tab.getFilePath());
    assertEquals("Titel", tab.getTitle());
    assertEquals("/fotos", tab.getFotodirectory());
  }

  @Test
  void settersEnGettersWerken() {
    TabInfo tab = new TabInfo();
    tab.setFilePath("f");
    tab.setTitle("t");
    tab.setLatitude(52.1);
    tab.setLongtitude(5.2);
    tab.setZoomfactor(12);
    tab.setProjects("p");
    tab.setFotodirectory("d");

    assertEquals("f", tab.getFilePath());
    assertEquals("t", tab.getTitle());
    assertEquals(52.1, tab.getLatitude());
    assertEquals(5.2, tab.getLongtitude());
    assertEquals(12, tab.getZoomfactor());
    assertEquals("p", tab.getProjects());
    assertEquals("d", tab.getFotodirectory());
  }

  @Test
  void verwijderDuplicatenOpFileOpNullOfLeeg() {
    assertTrue(TabInfo.verwijderDuplicatenOpFile(null).isEmpty());
    assertTrue(TabInfo.verwijderDuplicatenOpFile(new ArrayList<>()).isEmpty());
  }

  @Test
  void verwijderDuplicatenOpFileBehoudtEerstePerTitel() {
    TabInfo a1 = new TabInfo("f1", "A", "");
    TabInfo a2 = new TabInfo("f2", "A", ""); // duplicaat titel
    TabInfo b = new TabInfo("f3", "B", "");

    List<TabInfo> result = TabInfo.verwijderDuplicatenOpFile(Arrays.asList(a1, a2, b));

    assertEquals(2, result.size());
    assertEquals(Arrays.asList("A", "B"), result.stream().map(TabInfo::getTitle).toList());
    assertEquals("f1", result.get(0).getFilePath()); // eerste behouden
  }

  @Test
  void hashCodeIsConsistentMetId() {
    TabInfo tab = new TabInfo();
    assertEquals(tab.hashCode(), tab.hashCode());
  }
}