package kwee.osmmapper.main;

import javax.swing.SwingUtilities;

import kwee.library.JarInfo;
import kwee.osmmapper.gui.HoofdMenu;

public class Main {
  static public String m_creationtime;
  static public String c_CopyrightYear;

  public static void main(String[] args) {
    m_creationtime = JarInfo.getProjectVersion(Main.class);
    c_CopyrightYear = JarInfo.getYear(Main.class);

    SwingUtilities.invokeLater(() -> new HoofdMenu().start());
  }
}
