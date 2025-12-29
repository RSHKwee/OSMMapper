package kwee.osmmapper.gui;

/**
 * GUI Main menu
 */
import javax.swing.*;
import java.awt.*;

import java.io.IOException;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import kwee.library.ApplicationMessages;
import kwee.library.swing.TextAreaHandler;
import kwee.logger.MyLogger;
import kwee.osmmapper.lib.CustomJULHandler;
import kwee.osmmapper.lib.GeoMapController;
import kwee.osmmapper.main.Main;
import kwee.osmmapper.main.UserSetting;

public class MainMenu {
  private static final long serialVersionUID = 3219345000874466690L;
  private static final Logger LOGGER = MyLogger.getLogger();

  private Font customFont = new Font("Arial", Font.PLAIN, 12);
  private ApplicationMessages bundle = ApplicationMessages.getInstance();

  // Preferences
  private UserSetting m_params;
  private String m_LogDir = "c:/";
  private boolean m_toDisk = false;
  private Level m_Level = Level.INFO;
  private String m_Language = "";

  // GUI Components etc.
  private JFrame mainFrame;
  private GeoMapController geoMapController;
  private boolean m_DuplicateTabs = false; // NO duplicate tabs.
  private JMenuBar menuBar = new JMenuBar();

  /**
   * 
   */
  public void start() {
    this.m_params = UserSetting.getInstance();
    this.m_Language = m_params.get_Language();
    this.m_Level = m_params.get_Level();
    this.m_toDisk = m_params.is_toDisk();
    this.m_LogDir = m_params.get_LogDir();
    this.m_DuplicateTabs = m_params.is_DuplicateTabs();
    String apptxt = bundle.getMessage("AppTitel", Main.m_creationtime, Main.c_CopyrightYear);

    mainFrame = new JFrame(apptxt);
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setSize(1000, 700);

    DefMenuBar dmenu = new DefMenuBar();
    menuBar = dmenu.defineMenuBar(mainFrame);
    mainFrame.setJMenuBar(menuBar);

    // 1. Upper panel with Buttons
    CreateUpperPanel upperpanel = new CreateUpperPanel();
    mainFrame.add(upperpanel.createBovenPaneel(mainFrame), BorderLayout.NORTH);

    // 2. Middle: GeoMap container
    JPanel geoMapContainer = new JPanel();
    geoMapController = GeoMapController.getInstance();
    mainFrame.add(geoMapContainer, BorderLayout.CENTER);

    // 3. Onderste paneel voor logging
    JPanel logpanel = createLogPaneel();
    mainFrame.add(logpanel, BorderLayout.SOUTH);

    mainFrame.setVisible(true);

    LOGGER.log(Level.INFO, "" + apptxt);
    geoMapController.InitPanel(geoMapContainer, m_DuplicateTabs);
  }

  /**
   * Create logpanel and "connect" to logger.
   * 
   * @return Log panel
   */
  private JPanel createLogPaneel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setPreferredSize(new Dimension(1000, 100));
    panel.setBorder(BorderFactory.createTitledBorder("Log"));
    JTextArea logArea = new JTextArea(4, 80);

    // Build output area.
    try {
      MyLogger.setup(m_Level, m_LogDir, m_toDisk);
    } catch (IOException es) {
      LOGGER.log(Level.SEVERE, Class.class.getName() + ": " + es.toString());
      es.printStackTrace();
    }

    // Register the handler
    Logger julLogger = Logger.getLogger("");
    julLogger.setLevel(m_Level);
    julLogger.addHandler(new CustomJULHandler());

    Logger rootLogger = Logger.getLogger("");
    for (Handler handler : rootLogger.getHandlers()) {
      if (handler instanceof TextAreaHandler) {
        TextAreaHandler textAreaHandler = (TextAreaHandler) handler;
        logArea = textAreaHandler.getTextArea();
        logArea.setFont(customFont);
      }
    }

    logArea.setEditable(false);
    panel.add(new JScrollPane(logArea), BorderLayout.CENTER);
    return panel;
  }
}
