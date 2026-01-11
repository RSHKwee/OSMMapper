package kwee.osmmapper.gui;

import java.awt.Font;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import kwee.osmmapper.lib.CustomMarker;
import kwee.osmmapper.lib.FotoIntegration;

public class ShowMarkerDetail {

  private JFrame m_frame;
  private FotoIntegration m_fotoIntegration = new FotoIntegration();

  public ShowMarkerDetail(JFrame frame, FotoIntegration fotoIntegration) {
    m_frame = frame;
    m_fotoIntegration = fotoIntegration;
  }

  public void showMarkerDetails(MapMarker marker) {
    List<File> fotos = new ArrayList<File>();
    showMarkerDetails(marker, fotos);
  }

  /**
   * Toont gedetailleerde informatie over een marker
   */
  String markerAdres = ""; // We slaan het adres op voor foto lookup

  public void showMarkerDetails(MapMarker marker, List<File> fotos) {
    String title = marker.getName();
    String details;

    if (marker instanceof CustomMarker) {
      // Gebruik de extra informatie van CustomMarker
      CustomMarker customMarker = (CustomMarker) marker;
      String description = customMarker.getDescription();
      String extraInfo = customMarker.getExtraInfo();
      markerAdres = customMarker.getDescription();

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

    // Maak een JLabel met HTML formatting
    JLabel messageLabel = new JLabel(details);
    messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));

    // Maak een panel voor de inhoud
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.add(messageLabel, BorderLayout.CENTER);

    // Voeg een foto knop toe als er een adres is
    // if (markerAdres != null && !markerAdres.isEmpty()) {
    // Haal foto's op voor dit adres
    List<File> fotos1 = m_fotoIntegration.getFotosVoorAdres(markerAdres);
    int aantalFotos = fotos1.size();

    // Maak een knop met het aantal foto's
    JButton fotoKnop = new JButton(
        aantalFotos > 0 ? String.format("📸 Toon Foto's (%d)", aantalFotos) : "📷 Geen foto's beschikbaar");

    fotoKnop.setEnabled(aantalFotos > 0);

    fotoKnop.addActionListener(e -> {
      if (aantalFotos > 0) {
        // Open de foto viewer
        openFotoViewer(fotos1, markerAdres);
      }
    });

    // Voeg de knop toe onderaan het panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(fotoKnop);
    panel.add(buttonPanel, BorderLayout.SOUTH);

    // Pas panel grootte aan voor de knop
    panel.setPreferredSize(new Dimension(350, 200));
    // }

    // Toon het aangepaste JOptionPane
    JOptionPane.showMessageDialog(m_frame, panel, "Marker Details: " + title, JOptionPane.INFORMATION_MESSAGE);
  }

  int currentIndex = 0;

  /**
   * Opent een Swing JDialog voor het tonen van foto's
   */
  private void openFotoViewer(List<File> fotos, String adres) {
    // Maak een JDialog (Swing variant)
    JDialog fotoDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(m_frame), "Foto's voor: " + adres, true);
    fotoDialog.setLayout(new BorderLayout());

    currentIndex = 0;

    // 1. Maak een JLabel voor de afbeelding
    JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    updateFoto(imageLabel, fotos.get(currentIndex));

    // 2. Voeg de afbeelding toe aan een scrollpane (voor grote foto's)
    JScrollPane scrollPane = new JScrollPane(imageLabel);
    scrollPane.setPreferredSize(new Dimension(600, 400));
    fotoDialog.add(scrollPane, BorderLayout.CENTER);

    // 3. Navigatiepanel als er meerdere foto's zijn
    if (fotos.size() > 1) {
      JPanel navPanel = new JPanel(new FlowLayout());

      JButton prevButton = new JButton("← Vorige");
      JButton nextButton = new JButton("Volgende →");
      JLabel counterLabel = new JLabel(" " + (currentIndex + 1) + "/" + fotos.size() + " ");

      // Action listeners voor navigatie
      prevButton.addActionListener(e -> {
        currentIndex = (currentIndex - 1 + fotos.size()) % fotos.size();
        updateFoto(imageLabel, fotos.get(currentIndex));
        counterLabel.setText(" " + (currentIndex + 1) + "/" + fotos.size() + " ");
      });

      nextButton.addActionListener(e -> {
        currentIndex = (currentIndex + 1) % fotos.size();
        updateFoto(imageLabel, fotos.get(currentIndex));
        counterLabel.setText(" " + (currentIndex + 1) + "/" + fotos.size() + " ");
      });

      navPanel.add(prevButton);
      navPanel.add(counterLabel);
      navPanel.add(nextButton);

      fotoDialog.add(navPanel, BorderLayout.SOUTH);
    }

    // 4. Sluitknop
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton closeButton = new JButton("Sluiten");
    closeButton.addActionListener(e -> fotoDialog.dispose());
    topPanel.add(closeButton);
    fotoDialog.add(topPanel, BorderLayout.NORTH);

    // 5. Toon de dialog
    fotoDialog.pack();
    fotoDialog.setLocationRelativeTo(m_frame); // Centreer op hoofdvenster
    fotoDialog.setVisible(true);
  }

  /**
   * Update een JLabel met een nieuwe foto
   */
  private void updateFoto(JLabel label, File fotoPad) {
    try {
      // Gebruik ImageIcon voor Swing
      ImageIcon originalIcon = new ImageIcon(fotoPad.toString());
      Image image = originalIcon.getImage();

      // Schaalbare grootte (max 800x600)
      int maxWidth = 800;
      int maxHeight = 600;
      int imgWidth = originalIcon.getIconWidth();
      int imgHeight = originalIcon.getIconHeight();

      if (imgWidth > maxWidth || imgHeight > maxHeight) {
        // Bereken scaling
        double widthRatio = (double) maxWidth / imgWidth;
        double heightRatio = (double) maxHeight / imgHeight;
        double scale = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (imgWidth * scale);
        int newHeight = (int) (imgHeight * scale);

        Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaledImage));
      } else {
        label.setIcon(originalIcon);
      }

      label.setText(""); // Verwijder eventuele tekst
    } catch (Exception e) {
      label.setIcon(null);
      label.setText("<html><div style='text-align:center;color:red;'>" + "Foto kon niet geladen worden:<br/>"
          + fotoPad.getName() + "</div></html>");
    }
  }
}
