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
    
    public CustomMarker(double lat, double lon, String title, String description, String extraInfo, Color color) {
        super(null, title, lat, lon);
        this.title = title;
        this.description = description;
        this.extraInfo = extraInfo;
        this.setColor(color);
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
    
    @Override
    public String getName() {
        return title; // Toon de titel als naam
    }
}