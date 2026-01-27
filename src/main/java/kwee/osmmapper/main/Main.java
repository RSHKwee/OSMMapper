package kwee.osmmapper.main;

import javax.swing.SwingUtilities;

import kwee.library.JarInfo;
import kwee.osmmapper.gui.MainMenu;

public class Main {
  static public String m_creationtime;
  static public String c_CopyrightYear;

  public static void main(String[] args) {
    m_creationtime = JarInfo.getProjectVersion(MainMenu.class);
    c_CopyrightYear = JarInfo.getYear(MainMenu.class);

    SwingUtilities.invokeLater(() -> new MainMenu().start());
  }
}
