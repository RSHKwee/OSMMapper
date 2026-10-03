package kwee.osmmapper.lib;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class TabInfoTest {

  @Test
  void defaultConstructorGenereertUniekId() {
    TabInfo a = new TabInfo();
    TabInfo b = new TabInfo();
    assertThat(a.getId()).isNotBlank();
    assertThat(b.getId()).isNotBlank();
    assertThat(a.getId()).isNotEqualTo(b.getId());
  }

  @Test
  void constructorMetArgumentenVultVelden() {
    TabInfo tab = new TabInfo("/tmp/x.xlsx", "Titel", "/fotos");
    assertThat(tab.getFilePath()).isEqualTo("/tmp/x.xlsx");
    assertThat(tab.getTitle()).isEqualTo("Titel");
    assertThat(tab.getFotodirectory()).isEqualTo("/fotos");
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

    assertThat(tab.getFilePath()).isEqualTo("f");
    assertThat(tab.getTitle()).isEqualTo("t");
    assertThat(tab.getLatitude()).isEqualTo(52.1);
    assertThat(tab.getLongtitude()).isEqualTo(5.2);
    assertThat(tab.getZoomfactor()).isEqualTo(12);
    assertThat(tab.getProjects()).isEqualTo("p");
    assertThat(tab.getFotodirectory()).isEqualTo("d");
  }

  @Test
  void verwijderDuplicatenOpFileOpNullOfLeeg() {
    assertThat(TabInfo.verwijderDuplicatenOpFile(null)).isEmpty();
    assertThat(TabInfo.verwijderDuplicatenOpFile(new ArrayList<>())).isEmpty();
  }

  @Test
  void verwijderDuplicatenOpFileBehoudtEerstePerTitel() {
    TabInfo a1 = new TabInfo("f1", "A", "");
    TabInfo a2 = new TabInfo("f2", "A", ""); // duplicaat titel
    TabInfo b = new TabInfo("f3", "B", "");

    List<TabInfo> result = TabInfo.verwijderDuplicatenOpFile(Arrays.asList(a1, a2, b));

    assertThat(result).hasSize(2);
    assertThat(result).extracting(TabInfo::getTitle).containsExactly("A", "B");
    assertThat(result.get(0).getFilePath()).isEqualTo("f1"); // eerste behouden
  }

  @Test
  void hashCodeIsConsistentMetId() {
    TabInfo tab = new TabInfo();
    assertThat(tab.hashCode()).isEqualTo(tab.hashCode());
  }
}