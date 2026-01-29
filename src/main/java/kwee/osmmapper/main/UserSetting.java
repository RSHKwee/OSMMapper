package kwee.osmmapper.main;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.fasterxml.jackson.databind.ObjectMapper;

import kwee.logger.MyLogger;
import kwee.osmmapper.lib.TabInfo;

/**
 * User setting persistence.
 * 
 * @author rshkw
 *
 */
public class UserSetting {
  private static final Logger LOGGER = MyLogger.getLogger();

  private static UserSetting uniqueInstance;
  private static UserSetting freezeInstance = null;

  public static String NodePrefName = "kwee.osmmapper";

  private String c_Level = "Level";
  private String c_LevelValue = "INFO";

  private String c_ConfirmOnExit = "ConfirmOnExit";
  private String c_toDisk = "ToDisk";
  private String c_LookAndFeel = "LookAndFeel";
  private String c_LookAndFeelVal = "Nimbus";

  private String c_LogDir = "LogDir";
  private String c_Language = "Language";

  private String c_KeyTabData = "Tab_data";
  private String c_InpDirectory = "InputDirectory";
  private String c_InpExcelFile = "InputExcelFile";
  private String c_outpExcelFile = "OutputExcelFile";
  private String c_DuplicateTabs = "DuplicateTabs";
  private String c_Country = "Country";
  private String c_PictureDirectory = "PictureDirectory";
  private String c_ReportDirectory = "ReportDirectory";

  private String m_Level = c_LevelValue;
  private String m_LookAndFeel;
  private String m_OutputFolder = "";
  private String m_LogDir = "";
  private String m_Language = "nl";

  private boolean m_ConfirmOnExit = false;
  private boolean m_toDisk = false;

  private String m_KeyTabData = "[]";
  private String m_InpDirectory = "";
  private String m_InpExcelFile = "";
  private String m_outpExcelFile = "";
  private boolean m_DuplicateTabs = false; // NO duplicate tabs.
  private String m_Country = "Netherlands";
  private String m_PictureDirectory = "";
  private String m_ReportDirectory = "";

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
    m_Language = pref.get(c_Language, "nl");

    m_Level = pref.get(c_Level, c_LevelValue);
    m_LogDir = pref.get(c_LogDir, "");

    m_KeyTabData = pref.get(c_KeyTabData, "[]");
    m_InpDirectory = pref.get(c_InpDirectory, "");
    m_InpExcelFile = pref.get(c_InpExcelFile, "");
    m_outpExcelFile = pref.get(c_outpExcelFile, "");
    m_DuplicateTabs = pref.getBoolean(c_DuplicateTabs, false);
    m_Country = pref.get(c_Country, "");
    m_PictureDirectory = pref.get(c_PictureDirectory, "");
    m_ReportDirectory = pref.get(c_ReportDirectory, "");
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

  public boolean is_DuplicateTabs() {
    return m_DuplicateTabs;
  }

  // Laad de opgeslagen lijst met tab-info
  public List<TabInfo> get_TabState() {
    try {
      m_KeyTabData = pref.get(c_KeyTabData, "[]"); // Lege array als default
      ObjectMapper mapper = new ObjectMapper();
      List<TabInfo> localList = new ArrayList<TabInfo>();
      localList = mapper.readValue(m_KeyTabData,
          mapper.getTypeFactory().constructCollectionType(List.class, TabInfo.class));
      if (localList.size() > 20) {
        localList = localList.subList(0, 20);
      }
      return localList;
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage());
      return new ArrayList<>(); // Geef lege lijst terug bij fout
    }
  }

  public List<TabInfo> get_TabStateNoDup() {
    List<TabInfo> localList = new ArrayList<TabInfo>();
    localList = get_TabState();
    // Gebruik LinkedHashSet om volgorde te behouden
    Set<TabInfo> uniekeSet = new LinkedHashSet<>(TabInfo.verwijderDuplicatenOpFile(localList));
    return new ArrayList<>(uniekeSet);
  }

  public String get_InpDirectory() {
    return m_InpDirectory;
  }

  public String get_InpExcelFile() {
    return m_InpExcelFile;
  }

  public String get_OutpExcelFile() {
    return m_outpExcelFile;
  }

  public String get_Country() {
    return m_Country;
  }

  public String get_PictureDirectory() {
    return m_PictureDirectory;
  }

  public String get_ReportDirectory() {
    return m_ReportDirectory;
  }

  // == Setters ========
  public void set_LogDir(String m_LogDir) {
    this.m_LogDir = m_LogDir;
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

  public void set_TabState(List<TabInfo> tabList) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      this.m_KeyTabData = mapper.writeValueAsString(tabList);
      pref.put(c_KeyTabData, m_KeyTabData);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage());
    }
  }

  private void set_TabState(String tabList) {
    this.m_KeyTabData = tabList;
    pref.put(c_KeyTabData, m_KeyTabData);
  }

  public void set_InpDirectory(String InpDirectory) {
    pref.put(c_InpDirectory, InpDirectory);
    this.m_InpDirectory = InpDirectory;
  }

  public void set_InpExcelFile(String InpDirectory) {
    pref.put(c_InpExcelFile, InpDirectory);
    this.m_InpExcelFile = InpDirectory;
  }

  public void set_OutpExcelFile(String OutpDirectory) {
    pref.put(c_outpExcelFile, OutpDirectory);
    this.m_outpExcelFile = OutpDirectory;
  }

  public void set_DuplicateTabs(boolean DuplicateTabs) {
    pref.putBoolean(c_DuplicateTabs, DuplicateTabs);
    this.m_DuplicateTabs = DuplicateTabs;
  }

  public void set_Country(String Country) {
    pref.put(c_Country, Country);
    this.m_Country = Country;
  }

  public void set_PictureDirectory(String a_PictureDirectory) {
    pref.put(c_PictureDirectory, a_PictureDirectory);
    this.m_PictureDirectory = a_PictureDirectory;
  }

  public void set_ReportDirectory(String a_ReportDirectory) {
    pref.put(c_ReportDirectory, a_ReportDirectory);
    this.m_ReportDirectory = a_ReportDirectory;
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
      pref.put(c_Level, m_Level);
      pref.put(c_LogDir, m_LogDir);

      pref.put(c_KeyTabData, m_KeyTabData);
      pref.put(c_InpDirectory, m_InpDirectory);
      pref.put(c_InpExcelFile, m_InpExcelFile);
      pref.put(c_outpExcelFile, m_outpExcelFile);
      pref.putBoolean(c_DuplicateTabs, m_DuplicateTabs);
      pref.put(c_Country, m_Country);
      pref.put(c_PictureDirectory, m_PictureDirectory);
      pref.put(c_ReportDirectory, m_ReportDirectory);

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
      freezeInstance.set_Level(Level.parse(m_Level));
      freezeInstance.set_LogDir(m_LogDir);

      freezeInstance.set_TabState(m_KeyTabData);
      freezeInstance.set_InpDirectory(m_InpDirectory);
      freezeInstance.set_InpExcelFile(m_InpExcelFile);
      freezeInstance.set_OutpExcelFile(m_outpExcelFile);
      freezeInstance.set_DuplicateTabs(m_DuplicateTabs);
      freezeInstance.set_Country(m_Country);
      freezeInstance.set_PictureDirectory(m_PictureDirectory);
      freezeInstance.set_ReportDirectory(m_ReportDirectory);
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
      uniqueInstance.set_Level(freezeInstance.get_Level());
      uniqueInstance.set_LogDir(freezeInstance.get_LogDir());

      uniqueInstance.set_TabState(freezeInstance.get_TabState());
      uniqueInstance.set_InpDirectory(freezeInstance.get_InpDirectory());
      uniqueInstance.set_InpExcelFile(freezeInstance.get_InpExcelFile());
      uniqueInstance.set_OutpExcelFile(freezeInstance.get_OutpExcelFile());
      uniqueInstance.set_DuplicateTabs(freezeInstance.is_DuplicateTabs());
      uniqueInstance.set_Country(freezeInstance.get_Country());
      uniqueInstance.set_PictureDirectory(freezeInstance.get_PictureDirectory());
      uniqueInstance.set_ReportDirectory(freezeInstance.get_ReportDirectory());

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
    l_line = l_line + c_Level + ": " + m_Level + "\n";
    l_line = l_line + c_LogDir + ": " + m_LogDir + "\n";

    l_line = l_line + c_KeyTabData + ": " + m_KeyTabData + "\n";
    l_line = l_line + c_InpDirectory + ": " + m_InpDirectory + "\n";
    l_line = l_line + c_InpExcelFile + ": " + m_InpExcelFile + "\n";
    l_line = l_line + c_outpExcelFile + ": " + m_outpExcelFile + "\n";
    l_line = l_line + c_DuplicateTabs + ": " + m_DuplicateTabs + "\n";
    l_line = l_line + c_Country + ": " + m_Country + "\n";
    l_line = l_line + c_PictureDirectory + ": " + m_PictureDirectory + "\n";
    l_line = l_line + c_ReportDirectory + ": " + m_ReportDirectory + "\n";

    return l_line;
  }

}
