package kwee.osmmapper.gui;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumSet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import kwee.osmmapper.report.ReportType;

class ReportSelectionDialogTest {

  @BeforeAll
  static void headless() {
    System.setProperty("java.awt.headless", "true");
  }

  @Test
  void beginSelectieKomtOvereenMetModel() throws Exception {
    ReportType eerste = ReportType.values()[0];

    // JDialog aanmaken op de EDT
    ReportSelectionDialog[] holder = new ReportSelectionDialog[1];
    javax.swing.SwingUtilities.invokeAndWait(() -> {
      holder[0] = new ReportSelectionDialog(null, EnumSet.of(eerste));
    });

    // Checkbox-state controleren via reflectie of via een getter die je toevoegt
    // Beter: voeg een package-private methode toe zoals isChecked(ReportType)
  }
}