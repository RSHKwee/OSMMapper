package kwee.osmmapper.lib;

import static org.junit.Assert.assertEquals;

import java.awt.Color;

import org.junit.jupiter.api.Test;

class CustomMarkerTest {

  @Test
  void constructorEnGettersWerken() {
    CustomMarker marker = new CustomMarker(52.0, 5.0, "Titel", "Beschrijving", "Extra", Color.RED, "1234AB12");

    assertEquals(52.0, marker.getLat(), 1e-9);
    assertEquals(5.0, marker.getLon(), 1e-9);
    assertEquals("Titel", marker.getTitle());
    assertEquals("Beschrijving", marker.getDescription());
    assertEquals("Extra", marker.getExtraInfo());
    assertEquals("1234AB12", marker.getPictureIndex());
    assertEquals("Titel", marker.getName());
  }

  @Test
  void settersOverschrijvenVelden() {
    CustomMarker marker = new CustomMarker(0, 0, "A", "B", "C", Color.BLACK, "X");
    marker.setTitle("Nieuw");
    marker.setDescription("Desc");
    marker.setExtraInfo("Info");
    marker.setPictureIndex("idx");

    assertEquals("Nieuw", marker.getTitle());
    assertEquals("Desc", marker.getDescription());
    assertEquals("Info", marker.getExtraInfo());
    assertEquals("idx", marker.getPictureIndex());
  }
}