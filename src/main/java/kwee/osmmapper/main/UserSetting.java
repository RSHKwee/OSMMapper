package kwee.osmmapper.main;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import kwee.logger.MyLogger;

/**
 * User setting persistence.
 * 
 * @author rshkw
 *
 */
public class UserSetting {
  private static UserSetting uniqueInstance;
  private static UserSetting freezeInstance = null;

  private static final Logger LOGGER = MyLogger.getLogger();
  public static String NodePrefName = "kwee.osmmapper";

  private String c_Level = "Level";
  private String c_LevelValue = "INFO";

  private String c_OsmMapperExe = "OsmMapperExe";

  private String c_ConfirmOnExit = "ConfirmOnExit";
  private String c_toDisk = "ToDisk";

  private String c_OutputFolder = "OutputFolder";
  private String c_CsvFiles = "CsvFiles";
  private String c_LookAndFeel = "LookAndFeel";
  private String c_LookAndFeelVal = "Nimbus";

  private String c_LogDir = "LogDir";
  private String c_Language = "Language";

  private String m_Level = c_LevelValue;
  private String m_LookAndFeel;
  private String m_OutputFolder = "";
  private String m_LogDir = "";
  private String m_Language = "nl";

  private boolean m_ConfirmOnExit = false;
  private boolean m_toDisk = false;

  private Preferences pref;
  private Preferences userPrefs = Preferences.userRoot();

  /**
   * Get "access" to Singleton.
   * 
   * @return Instance
   */
  public static UserSetting getInstance() {
    if (uniqueInstance == null) {
      uniqueInstance = new UserSetting();
    }
    return uniqueInstance;
  }

  /**
   * Private constructor and initialization.
   */
  private UserSetting() {
    // Navigate to the preference node that stores the user setting
    pref = userPrefs.node(NodePrefName);

    m_toDisk = pref.getBoolean(c_toDisk, false);

    m_ConfirmOnExit = pref.getBoolean(c_ConfirmOnExit, false);

    m_LookAndFeel = pref.get(c_LookAndFeel, c_LookAndFeelVal);
    m_OutputFolder = pref.get(c_OutputFolder, "");

    m_Language = pref.get(c_Language, "nl");

    m_Level = pref.get(c_Level, c_LevelValue);
    m_LogDir = pref.get(c_LogDir, "");
  }

  // Getters for all parameters
  public String get_OutputFolder() {
    return m_OutputFolder;
  }

  public String get_Language() {
    return m_Language;
  }

  public Level get_Level() {
    return Level.parse(m_Level);
  }

  public String get_LogDir() {
    return m_LogDir;
  }

  public String get_LookAndFeel() {
    return m_LookAndFeel;
  }

  public boolean is_toDisk() {
    return m_toDisk;
  }

  public boolean is_ConfirmOnExit() {
    return m_ConfirmOnExit;
  }

  public void set_LogDir(String m_LogDir) {
    this.m_LogDir = m_LogDir;
  }

  public void set_OutputFolder(File a_OutputFolder) {
    pref.put(c_OutputFolder, a_OutputFolder.getAbsolutePath());
    this.m_OutputFolder = a_OutputFolder.getAbsolutePath();
  }

  public void set_OutputFolder(String a_OutputFolder) {
    pref.put(c_OutputFolder, a_OutputFolder);
    this.m_OutputFolder = a_OutputFolder;
  }

  public void set_Language(String m_Language) {
    this.m_Language = m_Language;
  }

  public void set_toDisk(boolean a_toDisk) {
    pref.putBoolean(c_toDisk, a_toDisk);
    this.m_toDisk = a_toDisk;
  }

  public void set_Level(Level a_Level) {
    pref.put(c_Level, a_Level.toString());
    this.m_Level = a_Level.toString();
  }

  public void set_LookAndFeel(String a_LookAndFeel) {
    pref.put(c_LookAndFeel, a_LookAndFeel);
    this.m_LookAndFeel = a_LookAndFeel;
  }

  public void set_ConfirmOnExit(boolean a_ConfirmOnExit) {
    pref.putBoolean(c_ConfirmOnExit, a_ConfirmOnExit);
    this.m_ConfirmOnExit = a_ConfirmOnExit;
  }

  /**
   * Save all settings
   */
  public void save() {
    try {
      pref.putBoolean(c_toDisk, m_toDisk);

      pref.putBoolean(c_ConfirmOnExit, m_ConfirmOnExit);

      pref.put(c_Language, m_Language);
      pref.put(c_LookAndFeel, m_LookAndFeel);
      pref.put(c_OutputFolder, m_OutputFolder);

      pref.put(c_Level, m_Level);
      pref.put(c_LogDir, m_LogDir);

      pref.flush();
    } catch (BackingStoreException e) {
      LOGGER.log(Level.INFO, e.getMessage());
    }
  }

  /**
   * Copy UserSetings
   * 
   * @return Copy of UserSetings
   */
  public void freeze() {
    if (freezeInstance == null) {
      freezeInstance = new UserSetting();
      freezeInstance.set_toDisk(m_toDisk);

      freezeInstance.set_ConfirmOnExit(m_ConfirmOnExit);
      freezeInstance.set_Language(m_Language);

      freezeInstance.set_LookAndFeel(m_LookAndFeel);
      freezeInstance.set_OutputFolder(m_OutputFolder);

      freezeInstance.set_Level(Level.parse(m_Level));
      freezeInstance.set_LogDir(m_LogDir);
    } else {
      LOGGER.log(Level.INFO, "Nothing to freeze....");
    }
  }

  public void unfreeze() {
    if (freezeInstance != null) {
      uniqueInstance.set_toDisk(m_toDisk);

      uniqueInstance.set_ConfirmOnExit(freezeInstance.is_ConfirmOnExit());
      uniqueInstance.set_Language(freezeInstance.get_Language());

      uniqueInstance.set_LookAndFeel(freezeInstance.get_LookAndFeel());
      uniqueInstance.set_OutputFolder(freezeInstance.get_OutputFolder());

      uniqueInstance.set_Level(freezeInstance.get_Level());
      uniqueInstance.set_LogDir(freezeInstance.get_LogDir());

      freezeInstance = null;
    } else {
      LOGGER.log(Level.INFO, "Nothing to unfreeze....");
    }
  }

  /**
   * Print settings, convert to a String
   * 
   * @return String with Setting info
   */
  public String print() {
    String l_line = "User setting \n";
    l_line = l_line + "Name: " + pref.name() + "\n";
    l_line = l_line + c_toDisk + ": " + m_toDisk + "\n";
    l_line = l_line + c_Language + ": " + m_Language + "\n";
    l_line = l_line + c_ConfirmOnExit + ": " + m_ConfirmOnExit + "\n";

    l_line = l_line + c_LookAndFeel + ": " + m_LookAndFeel + "\n";
    l_line = l_line + c_OutputFolder + ": " + m_OutputFolder + "\n";
    l_line = l_line + c_Level + ": " + m_Level + "\n";
    l_line = l_line + c_LogDir + ": " + m_LogDir + "\n";

    return l_line;
  }

}
