package kwee.osmmapper.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import kwee.osmmapper.report.ReportType;

public class ReportSelectionDialog extends JDialog {

  /**
   * 
   */
  private static final long serialVersionUID = 3409132813065651059L;
  private final Map<ReportType, JCheckBox> boxes = new EnumMap<>(ReportType.class);
  private Set<ReportType> result = null; // null = geannuleerd

  public ReportSelectionDialog(JFrame owner, Set<ReportType> voorselectie) {
    super(owner, "Selecteer rapporten", true);
    setLayout(new BorderLayout(10, 10));

    JLabel uitleg = new JLabel("Vink de rapporten aan die je wilt genereren:");
    uitleg.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
    add(uitleg, BorderLayout.NORTH);

    JPanel center = new JPanel(new GridLayout(0, 1, 4, 4));
    center.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

    for (ReportType type : ReportType.values()) {
      JCheckBox cb = new JCheckBox(type.getLabel());
      cb.setSelected(voorselectie == null || voorselectie.contains(type));
      boxes.put(type, cb);
      center.add(cb);
    }
    add(center, BorderLayout.CENTER);

    // Knoppen
    JPanel buttons = new JPanel();
    JButton ok = new JButton("OK");
    JButton cancel = new JButton("Annuleren");
    ok.addActionListener(e -> {
      result = EnumSet.noneOf(ReportType.class);
      boxes.forEach((type, cb) -> {
        if (cb.isSelected()) {
          result.add(type);
        }
      });
      dispose();
    });
    cancel.addActionListener(e -> {
      result = null;
      dispose();
    });
    buttons.add(ok);
    buttons.add(cancel);
    add(buttons, BorderLayout.SOUTH);

    pack();
    setLocationRelativeTo(owner);
  }

  /** @return geselecteerde types, of null als geannuleerd */
  public Set<ReportType> getSelection() {
    return result;
  }
}