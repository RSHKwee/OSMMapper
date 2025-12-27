package kwee.osmmapper.gui;

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
import kwee.osmmapper.lib.KaartController;
import kwee.osmmapper.main.Main;
import kwee.osmmapper.main.UserSetting;

public class HoofdMenu {
  private static final long serialVersionUID = 3219345000874466690L;
  private static final Logger LOGGER = MyLogger.getLogger();
  private UserSetting m_params;
  private JFrame hoofdFrame;
  private KaartController kaartController;
  private Font customFont = new Font("Arial", Font.PLAIN, 12);
  private ApplicationMessages bundle = ApplicationMessages.getInstance();

  private String m_LogDir = "c:/";
  private boolean m_toDisk = false;
  private Level m_Level = Level.INFO;
  private String m_Language = "";

  private boolean m_DuplicateTabs = false; // NO duplicate tabs.

  JMenuBar menuBar = new JMenuBar();

  public void start() {
    this.m_params = UserSetting.getInstance();
    this.m_Language = m_params.get_Language();
    this.m_Level = m_params.get_Level();
    this.m_toDisk = m_params.is_toDisk();
    this.m_LogDir = m_params.get_LogDir();
    this.m_DuplicateTabs = m_params.is_DuplicateTabs();

    hoofdFrame = new JFrame("OSM Mapper (" + Main.c_CopyrightYear + " " + Main.m_creationtime + ")");
    hoofdFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    hoofdFrame.setSize(1000, 700);

    DefMenuBar dmenu = new DefMenuBar();
    menuBar = dmenu.defineMenuBar(hoofdFrame);
    hoofdFrame.setJMenuBar(menuBar);

    // 1. Bovenste paneel met knoppen
    CreateUpperPanel upperpanel = new CreateUpperPanel();
    hoofdFrame.add(upperpanel.createBovenPaneel(hoofdFrame), BorderLayout.NORTH);

    // 2. Midden: Kaarten container
    JPanel kaartenContainer = new JPanel();
    kaartController = KaartController.getInstance();
    kaartController.InitPanel(kaartenContainer, m_DuplicateTabs);
    hoofdFrame.add(kaartenContainer, BorderLayout.CENTER);

    // 3. Onderste paneel voor logging
    hoofdFrame.add(createLogPaneel(), BorderLayout.SOUTH);
    hoofdFrame.setVisible(true);

  }

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

    // Registreer de handler
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
