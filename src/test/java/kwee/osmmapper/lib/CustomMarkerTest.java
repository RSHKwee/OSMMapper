package kwee.osmmapper.lib;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;

import org.junit.jupiter.api.Test;

class CustomMarkerTest {

  @Test
  void constructorEnGettersWerken() {
    CustomMarker marker = new CustomMarker(52.0, 5.0, "Titel", "Beschrijving", "Extra", Color.RED, "1234AB12");

    assertThat(marker.getLat()).isEqualTo(52.0);
    assertThat(marker.getLon()).isEqualTo(5.0);
    assertThat(marker.getTitle()).isEqualTo("Titel");
    assertThat(marker.getDescription()).isEqualTo("Beschrijving");
    assertThat(marker.getExtraInfo()).isEqualTo("Extra");
    assertThat(marker.getPictureIndex()).isEqualTo("1234AB12");
    assertThat(marker.getName()).isEqualTo("Titel");
  }

  @Test
  void settersOverschrijvenVelden() {
    CustomMarker marker = new CustomMarker(0, 0, "A", "B", "C", Color.BLACK, "X");
    marker.setTitle("Nieuw");
    marker.setDescription("Desc");
    marker.setExtraInfo("Info");
    marker.setPictureIndex("idx");

    assertThat(marker.getTitle()).isEqualTo("Nieuw");
    assertThat(marker.getDescription()).isEqualTo("Desc");
    assertThat(marker.getExtraInfo()).isEqualTo("Info");
    assertThat(marker.getPictureIndex()).isEqualTo("idx");
  }
}