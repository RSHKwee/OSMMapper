package kwee.osmmapper.mailsupport;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.nio.file.Files;

public class EmailSenderGUI extends JFrame {
  /**
   * 
   */
  private static final long serialVersionUID = 36074059731630598L;
  // GUI Componenten
  private JTextField smtpField, portField, usernameField, saveDirField, subjectField;
  private JPasswordField passwordField;
  private JTextArea emailsArea, messageArea, logArea;
  private JList<File> attachmentsList;
  private DefaultListModel<File> attachmentsModel;
  private JCheckBox saveEmlCheckbox;

  // Statistische labels
  private JLabel countLabel, validLabel;

  public EmailSenderGUI() {
    setTitle("E-mail Verzender met EML Opslag");
    setSize(1100, 800);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    initComponents();
  }

  private void initComponents() {
    // Hoofd tabbed pane
    JTabbedPane tabbedPane = new JTabbedPane();

    // Tabs toevoegen
    tabbedPane.addTab("📧 Configuratie", createConfigPanel());
    tabbedPane.addTab("👥 Ontvangers", createRecipientsPanel());
    tabbedPane.addTab("✉️ Bericht", createMessagePanel());
    tabbedPane.addTab("📎 Bijlagen", createAttachmentsPanel());
    tabbedPane.addTab("💾 EML Opslag", createEmlStoragePanel());
    tabbedPane.addTab("📋 Log", createLogPanel());

    // Button panel onderaan
    JPanel buttonPanel = createButtonPanel();

    // Layout
    setLayout(new BorderLayout());
    add(tabbedPane, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  // ============================================
  // PANEL CREATIE METHODEN
  // ============================================

  private JPanel createConfigPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createTitledBorder("SMTP Configuratie"));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    // Provider selector
    String[] providers = { "Kies provider...", "Gmail", "Outlook/Hotmail", "Office365", "Yahoo", "Custom" };
    JComboBox<String> providerCombo = new JComboBox<>(providers);
    providerCombo.addActionListener(e -> setProviderConfig((String) providerCombo.getSelectedItem()));

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    panel.add(new JLabel("E-mail provider:"), gbc);
    gbc.gridy = 1;
    panel.add(providerCombo, gbc);

    // SMTP config velden
    gbc.gridwidth = 1;
    gbc.gridy = 2;
    gbc.gridx = 0;
    panel.add(new JLabel("SMTP Server:"), gbc);
    gbc.gridx = 1;
    smtpField = new JTextField(25);
    panel.add(smtpField, gbc);

    gbc.gridy = 3;
    gbc.gridx = 0;
    panel.add(new JLabel("Port:"), gbc);
    gbc.gridx = 1;
    portField = new JTextField(10);
    panel.add(portField, gbc);

    gbc.gridy = 4;
    gbc.gridx = 0;
    panel.add(new JLabel("Gebruikersnaam:"), gbc);
    gbc.gridx = 1;
    usernameField = new JTextField(25);
    panel.add(usernameField, gbc);

    gbc.gridy = 5;
    gbc.gridx = 0;
    panel.add(new JLabel("Wachtwoord:"), gbc);
    gbc.gridx = 1;
    passwordField = new JPasswordField(25);
    panel.add(passwordField, gbc);

    return panel;
  }

  private JPanel createRecipientsPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createTitledBorder("Ontvangers"));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Top panel met knoppen
    JPanel topPanel = new JPanel(new BorderLayout(5, 5));
    topPanel.add(new JLabel("E-mailadressen (één per regel, max 100):"), BorderLayout.NORTH);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton loadFromFileBtn = new JButton("📂 Laad uit bestand");
    loadFromFileBtn.addActionListener(e -> loadEmailsFromFile());

    JButton clearBtn = new JButton("🗑️ Wissen");
    clearBtn.addActionListener(e -> {
      emailsArea.setText("");
      log("E-mail lijst gewist");
    });

    JButton pasteBtn = new JButton("📋 Plakken");
    pasteBtn.addActionListener(e -> emailsArea.paste());

    JButton deduplicateBtn = new JButton("🔍 Duplicaten verwijderen");
    deduplicateBtn.addActionListener(e -> removeDuplicates());

    JButton validateBtn = new JButton("✅ Valideer");
    validateBtn.addActionListener(e -> validateAllEmails());

    buttonPanel.add(loadFromFileBtn);
    buttonPanel.add(clearBtn);
    buttonPanel.add(pasteBtn);
    buttonPanel.add(deduplicateBtn);
    buttonPanel.add(validateBtn);

    topPanel.add(buttonPanel, BorderLayout.SOUTH);
    panel.add(topPanel, BorderLayout.NORTH);

    // E-mail tekstgebied
    emailsArea = new JTextArea(20, 40);
    emailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    emailsArea.setLineWrap(true);
    emailsArea.setWrapStyleWord(true);

    // Voorbeeld inhoud
    emailsArea.setText("voorbeeld@email.com\nnaam.achternaam@bedrijf.nl\ninfo@domein.com");

    // Document listener voor real-time updates
    emailsArea.getDocument().addDocumentListener(createDocumentListener(this::updateStats));

    JScrollPane scrollPane = new JScrollPane(emailsArea);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Bottom panel met statistieken
    JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));

    JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    countLabel = new JLabel("Aantal adressen: 0");
    validLabel = new JLabel("Geldige adressen: 0");

    statsPanel.add(countLabel);
    statsPanel.add(Box.createHorizontalStrut(20));
    statsPanel.add(validLabel);

    bottomPanel.add(statsPanel, BorderLayout.WEST);

    // Voorbeeld en export knoppen
    JPanel examplePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton exampleBtn = new JButton("Voorbeeld adressen laden");
    exampleBtn.addActionListener(e -> loadExampleEmails());

    JButton exportBtn = new JButton("📤 Exporteren");
    exportBtn.addActionListener(e -> exportEmailsToFile());

    examplePanel.add(exampleBtn);
    examplePanel.add(exportBtn);
    bottomPanel.add(examplePanel, BorderLayout.EAST);

    panel.add(bottomPanel, BorderLayout.SOUTH);

    // Initialiseer statistieken
    updateStats();

    return panel;
  }

  private JPanel createMessagePanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createTitledBorder("Bericht"));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Onderwerp
    JPanel subjectPanel = new JPanel(new BorderLayout(5, 5));
    subjectPanel.add(new JLabel("Onderwerp:"), BorderLayout.WEST);

    subjectField = new JTextField(40);
    subjectField.setText("Belangrijke update - " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

    subjectPanel.add(subjectField, BorderLayout.CENTER);
    panel.add(subjectPanel, BorderLayout.NORTH);

    // Bericht tekstgebied
    JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
    messagePanel.add(new JLabel("Bericht tekst:"), BorderLayout.NORTH);

    messageArea = new JTextArea(20, 50);
    messageArea.setFont(new Font("Arial", Font.PLAIN, 12));
    messageArea.setLineWrap(true);
    messageArea.setWrapStyleWord(true);

    // Voorbeeld bericht
    String exampleMessage = "Beste {naam},\n\n" + "Hierbij ontvangt u onze wekelijkse update.\n\n"
        + "Met vriendelijke groet,\n" + "Het Team\n\n" + "Datum: {datum}\n" + "Tijd: {tijd}";

    messageArea.setText(exampleMessage);

    JScrollPane scrollPane = new JScrollPane(messageArea);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    messagePanel.add(scrollPane, BorderLayout.CENTER);

    panel.add(messagePanel, BorderLayout.CENTER);

    // Bottom panel met personalisatie
    JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));

    // Personalisatie knoppen
    JPanel personalizationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    personalizationPanel.add(new JLabel("Personalisatie variabelen:"));

    String[] tags = { "{naam}", "{email}", "{datum}", "{tijd}", "{bedrijf}", "{voornaam}", "{achternaam}" };

    for (String tag : tags) {
      JButton tagBtn = new JButton(tag);
      tagBtn.setFont(new Font("Arial", Font.PLAIN, 10));
      tagBtn.setMargin(new Insets(2, 5, 2, 5));
      tagBtn.setToolTipText("Klik om in te voegen");
      tagBtn.addActionListener(e -> {
        messageArea.insert(tag, messageArea.getCaretPosition());
        messageArea.requestFocus();
      });
      personalizationPanel.add(tagBtn);
    }

    bottomPanel.add(personalizationPanel, BorderLayout.CENTER);

    // Karakter telling
    JLabel charCountLabel = new JLabel("Tekens: 0");
    charCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

    messageArea.getDocument().addDocumentListener(createDocumentListener(() -> {
      int length = messageArea.getText().length();
      charCountLabel.setText("Tekens: " + length);

      if (length > 10000) {
        charCountLabel.setForeground(Color.RED);
      } else if (length > 5000) {
        charCountLabel.setForeground(Color.ORANGE);
      } else {
        charCountLabel.setForeground(Color.BLACK);
      }
    }));

    bottomPanel.add(charCountLabel, BorderLayout.EAST);
    panel.add(bottomPanel, BorderLayout.SOUTH);

    return panel;
  }

  private JPanel createAttachmentsPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createTitledBorder("Bijlagen"));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Lijst met bijlagen
    attachmentsModel = new DefaultListModel<>();
    attachmentsList = new JList<>(attachmentsModel);
    attachmentsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    attachmentsList.setCellRenderer(new AttachmentListCellRenderer());

    JScrollPane scrollPane = new JScrollPane(attachmentsList);
    scrollPane.setPreferredSize(new Dimension(500, 300));
    panel.add(scrollPane, BorderLayout.CENTER);

    // Button panel
    JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 5, 5));

    JButton addFileBtn = createIconButton("📎 Bestand toevoegen", "Voeg bestanden toe");
    addFileBtn.addActionListener(e -> addAttachment());

    JButton addFolderBtn = createIconButton("📁 Map toevoegen", "Voeg hele map toe");
    addFolderBtn.addActionListener(e -> addFolder());

    JButton removeBtn = createIconButton("🗑️ Verwijderen", "Verwijder geselecteerde");
    removeBtn.addActionListener(e -> removeAttachment());

    JButton clearBtn = createIconButton("🧹 Alles wissen", "Verwijder alle bijlagen");
    clearBtn.addActionListener(e -> clearAttachments());

    JButton previewBtn = createIconButton("👁️ Voorbeeld", "Toon voorbeeld");
    previewBtn.addActionListener(e -> previewAttachment());

    buttonPanel.add(addFileBtn);
    buttonPanel.add(addFolderBtn);
    buttonPanel.add(removeBtn);
    buttonPanel.add(clearBtn);
    buttonPanel.add(previewBtn);

    panel.add(buttonPanel, BorderLayout.SOUTH);

    // Info panel
    JPanel infoPanel = new JPanel(new BorderLayout(5, 5));
    JLabel totalSizeLabel = new JLabel("Totale grootte: 0 KB");
    JLabel fileCountLabel = new JLabel("Aantal bestanden: 0");

    infoPanel.add(totalSizeLabel, BorderLayout.WEST);
    infoPanel.add(fileCountLabel, BorderLayout.EAST);

    panel.add(infoPanel, BorderLayout.NORTH);

    // Update info wanneer bijlagen veranderen
    attachmentsModel.addListDataListener(new javax.swing.event.ListDataListener() {
      public void intervalAdded(javax.swing.event.ListDataEvent e) {
        updateAttachmentInfo();
      }

      public void intervalRemoved(javax.swing.event.ListDataEvent e) {
        updateAttachmentInfo();
      }

      public void contentsChanged(javax.swing.event.ListDataEvent e) {
        updateAttachmentInfo();
      }

      private void updateAttachmentInfo() {
        SwingUtilities.invokeLater(() -> {
          int count = attachmentsModel.size();
          long totalSize = 0;

          for (int i = 0; i < count; i++) {
            File file = attachmentsModel.getElementAt(i);
            totalSize += file.length();
          }

          fileCountLabel.setText("Aantal bestanden: " + count);

          if (totalSize > 1024 * 1024) {
            totalSizeLabel.setText(String.format("Totale grootte: %.2f MB", totalSize / (1024.0 * 1024.0)));
          } else if (totalSize > 1024) {
            totalSizeLabel.setText(String.format("Totale grootte: %.1f KB", totalSize / 1024.0));
          } else {
            totalSizeLabel.setText("Totale grootte: " + totalSize + " bytes");
          }

          // Waarschuwing bij grote bestanden
          if (totalSize > 25 * 1024 * 1024) {
            totalSizeLabel.setForeground(Color.RED);
            totalSizeLabel.setToolTipText("Waarschuwing: Sommige e-mailproviders hebben limieten!");
          } else if (totalSize > 10 * 1024 * 1024) {
            totalSizeLabel.setForeground(Color.ORANGE);
          } else {
            totalSizeLabel.setForeground(Color.BLACK);
          }
        });
      }
    });

    return panel;
  }

  private JPanel createEmlStoragePanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createTitledBorder("EML Bestandsopslag"));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Opslag directory
    JPanel dirPanel = new JPanel(new BorderLayout(5, 5));
    dirPanel.add(new JLabel("Opslag directory voor EML bestanden:"), BorderLayout.NORTH);

    JPanel dirFieldPanel = new JPanel(new BorderLayout(5, 5));
    saveDirField = new JTextField(System.getProperty("user.home") + File.separator + "eml_emails");
    JButton browseDirBtn = new JButton("Bladeren");
    browseDirBtn.addActionListener(e -> chooseSaveDirectory());

    dirFieldPanel.add(saveDirField, BorderLayout.CENTER);
    dirFieldPanel.add(browseDirBtn, BorderLayout.EAST);
    dirPanel.add(dirFieldPanel, BorderLayout.CENTER);

    panel.add(dirPanel, BorderLayout.NORTH);

    // Opslag opties
    JPanel optionsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
    saveEmlCheckbox = new JCheckBox("EML bestanden opslaan (ook bij succesvol verzenden)");
    saveEmlCheckbox.setSelected(true);

    JCheckBox saveOnFailOnlyCheckbox = new JCheckBox("Alleen opslaan bij mislukte verzending");
    JCheckBox includeAttachmentsCheckbox = new JCheckBox("Inclusief bijlagen in EML");
    includeAttachmentsCheckbox.setSelected(true);

    optionsPanel.add(saveEmlCheckbox);
    optionsPanel.add(saveOnFailOnlyCheckbox);
    optionsPanel.add(includeAttachmentsCheckbox);

    panel.add(optionsPanel, BorderLayout.CENTER);

    // Bestanden lijst
    JPanel filesPanel = new JPanel(new BorderLayout(5, 5));
    filesPanel.add(new JLabel("Opgeslagen EML bestanden:"), BorderLayout.NORTH);

    DefaultListModel<String> emlListModel = new DefaultListModel<>();
    JList<String> emlList = new JList<>(emlListModel);

    JScrollPane scrollPane = new JScrollPane(emlList);
    filesPanel.add(scrollPane, BorderLayout.CENTER);

    // Bestand acties
    JPanel fileActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton refreshBtn = new JButton("Vernieuwen");
    refreshBtn.addActionListener(e -> refreshEmlList(emlListModel));

    JButton openBtn = new JButton("Open in standaard app");
    openBtn.addActionListener(e -> openEmlFile(emlList.getSelectedValue()));

    JButton importBtn = new JButton("Importeer EML");
    importBtn.addActionListener(e -> importEmlFile());

    fileActionsPanel.add(refreshBtn);
    fileActionsPanel.add(openBtn);
    fileActionsPanel.add(importBtn);

    filesPanel.add(fileActionsPanel, BorderLayout.SOUTH);
    panel.add(filesPanel, BorderLayout.SOUTH);

    // Initialiseer lijst
    refreshEmlList(emlListModel);

    return panel;
  }

  private JPanel createLogPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Verzend Log"));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    logArea = new JTextArea(15, 60);
    logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    logArea.setEditable(false);

    JScrollPane scrollPane = new JScrollPane(logArea);
    panel.add(scrollPane, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton clearLogBtn = new JButton("Log wissen");
    clearLogBtn.addActionListener(e -> logArea.setText(""));

    JButton saveLogBtn = new JButton("Log opslaan");
    saveLogBtn.addActionListener(e -> saveLogToFile());

    buttonPanel.add(clearLogBtn);
    buttonPanel.add(saveLogBtn);
    panel.add(buttonPanel, BorderLayout.SOUTH);

    return panel;
  }

  private JPanel createButtonPanel() {
    JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JButton sendBtn = new JButton("📤 Verzend E-mails");
    sendBtn.setBackground(new Color(76, 175, 80));
    sendBtn.setForeground(Color.WHITE);
    sendBtn.setFont(new Font("Arial", Font.BOLD, 14));
    sendBtn.addActionListener(e -> sendEmails());

    JButton saveEmlsBtn = new JButton("💾 Bewaar als EML");
    saveEmlsBtn.setBackground(new Color(33, 150, 243));
    saveEmlsBtn.setForeground(Color.WHITE);
    saveEmlsBtn.addActionListener(e -> saveAllAsEml());

    JButton testBtn = new JButton("🔌 Test Verbinding");
    testBtn.setBackground(new Color(255, 152, 0));
    testBtn.setForeground(Color.WHITE);
    testBtn.addActionListener(e -> testConnection());

    JButton quitBtn = new JButton("🚪 Afsluiten");
    quitBtn.setBackground(new Color(244, 67, 54));
    quitBtn.setForeground(Color.WHITE);
    quitBtn.addActionListener(e -> System.exit(0));

    panel.add(sendBtn);
    panel.add(saveEmlsBtn);
    panel.add(testBtn);
    panel.add(quitBtn);

    return panel;
  }

  // ============================================
  // HELPER METHODEN
  // ============================================

  private JButton createIconButton(String text, String tooltip) {
    JButton button = new JButton(text);
    button.setToolTipText(tooltip);
    button.setHorizontalAlignment(SwingConstants.LEFT);
    return button;
  }

  private DocumentListener createDocumentListener(Runnable action) {
    return new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        action.run();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        action.run();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        action.run();
      }
    };
  }

  private void log(String message) {
    SwingUtilities.invokeLater(() -> {
      String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
      logArea.append("[" + timestamp + "] " + message + "\n");
      logArea.setCaretPosition(logArea.getDocument().getLength());
    });
  }

  // ============================================
  // EMAIL MANAGEMENT METHODEN
  // ============================================

  private void updateStats() {
    SwingUtilities.invokeLater(() -> {
      String text = emailsArea.getText().trim();
      String[] lines = text.isEmpty() ? new String[0] : text.split("\n");

      int total = lines.length;
      int valid = 0;

      for (String line : lines) {
        String email = line.trim();
        if (!email.isEmpty() && isValidEmail(email)) {
          valid++;
        }
      }

      if (countLabel != null) {
        countLabel.setText("Aantal adressen: " + total);

        if (total > 100) {
          countLabel.setForeground(Color.RED);
          countLabel.setText("Aantal adressen: " + total + " (MAX 100!)");
        } else if (total > 50) {
          countLabel.setForeground(Color.ORANGE);
        } else {
          countLabel.setForeground(Color.BLACK);
        }
      }

      if (validLabel != null) {
        validLabel.setText("Geldige adressen: " + valid);
        validLabel.setForeground(valid == total ? new Color(0, 150, 0) : Color.BLACK);
      }
    });
  }

  private boolean isValidEmail(String email) {
    if (email == null || email.trim().isEmpty())
      return false;

    String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    if (!email.matches(emailRegex))
      return false;

    // Controleer op veelvoorkomende fouten
    if (email.contains("..") || email.startsWith(".") || email.endsWith(".") || email.contains("@.")
        || email.contains(".@")) {
      return false;
    }

    return true;
  }

  private void loadEmailsFromFile() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Selecteer tekstbestand met e-mailadressen");
    fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
      public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt")
            || f.getName().toLowerCase().endsWith(".csv");
      }

      public String getDescription() {
        return "Tekstbestanden (*.txt, *.csv)";
      }
    });

    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      try {
        File file = fileChooser.getSelectedFile();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder emails = new StringBuilder();
        String line;
        int count = 0;

        while ((line = reader.readLine()) != null && count < 1000) {
          String trimmed = line.trim();
          if (!trimmed.isEmpty() && trimmed.contains("@")) {
            emails.append(trimmed).append("\n");
            count++;
          }
        }
        reader.close();

        emailsArea.setText(emails.toString());
        log(count + " e-mailadressen geladen uit: " + file.getName());
        updateStats();

      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Fout bij lezen bestand: " + ex.getMessage(), "Fout",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void removeDuplicates() {
    String text = emailsArea.getText();
    if (text.trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "Geen e-mailadressen om te verwerken", "Info",
          JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    String[] lines = text.split("\n");
    Set<String> uniqueEmails = new LinkedHashSet<>();
    List<String> duplicates = new ArrayList<>();

    for (String line : lines) {
      String trimmed = line.trim();
      if (!trimmed.isEmpty()) {
        String emailLower = trimmed.toLowerCase();
        if (!uniqueEmails.add(emailLower)) {
          duplicates.add(trimmed);
        }
      }
    }

    StringBuilder result = new StringBuilder();
    for (String email : uniqueEmails) {
      result.append(email).append("\n");
    }

    int removed = lines.length - uniqueEmails.size();
    emailsArea.setText(result.toString());

    if (removed > 0) {
      log(removed + " duplicaten verwijderd");
      JOptionPane.showMessageDialog(this, removed + " duplicaten verwijderd", "Succes",
          JOptionPane.INFORMATION_MESSAGE);
    }

    updateStats();
  }

  private void validateAllEmails() {
    String text = emailsArea.getText().trim();
    if (text.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Geen e-mailadressen om te valideren", "Info",
          JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    String[] lines = text.split("\n");
    List<String> validEmails = new ArrayList<>();
    List<String> invalidEmails = new ArrayList<>();

    for (String line : lines) {
      String email = line.trim();
      if (email.isEmpty())
        continue;

      if (isValidEmail(email)) {
        validEmails.add(email);
      } else {
        invalidEmails.add(email);
      }
    }

    StringBuilder result = new StringBuilder();
    result.append("Validatie resultaat:\n\n");
    result.append("Geldige adressen: ").append(validEmails.size()).append("\n");
    result.append("Ongeldige adressen: ").append(invalidEmails.size()).append("\n\n");

    if (!invalidEmails.isEmpty()) {
      result.append("Ongeldige adressen:\n");
      for (String invalid : invalidEmails) {
        result.append("  • ").append(invalid).append("\n");
      }
    }

    JTextArea resultArea = new JTextArea(result.toString(), 15, 40);
    resultArea.setEditable(false);
    resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

    JScrollPane scrollPane = new JScrollPane(resultArea);
    JOptionPane.showMessageDialog(this, scrollPane, "Validatie Resultaat", JOptionPane.INFORMATION_MESSAGE);

    log("E-mail validatie uitgevoerd: " + validEmails.size() + " geldig, " + invalidEmails.size() + " ongeldig");
  }

  private void exportEmailsToFile() {
    String text = emailsArea.getText().trim();
    if (text.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Geen e-mailadressen om te exporteren", "Info",
          JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Exporteer e-mailadressen");
    fileChooser
        .setSelectedFile(new File("email_list_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt"));

    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      try (PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
        writer.write(text);

        int count = text.split("\n").length;
        log(count + " e-mailadressen geëxporteerd");

        JOptionPane.showMessageDialog(this, count + " adressen succesvol geëxporteerd!", "Succes",
            JOptionPane.INFORMATION_MESSAGE);

      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Export mislukt: " + e.getMessage(), "Fout", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void loadExampleEmails() {
    String[] examples = { "test@voorbeeld.nl", "info@bedrijf.com", "contact@organisatie.org", "support@dienst.net",
        "sales@product.eu" };

    StringBuilder sb = new StringBuilder();
    for (String email : examples) {
      sb.append(email).append("\n");
    }

    emailsArea.setText(sb.toString());
    log("Voorbeeld e-mailadressen geladen");
    updateStats();
  }

  // ============================================
  // ATTACHMENT MANAGEMENT METHODEN
  // ============================================

  private void addAttachment() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setMultiSelectionEnabled(true);
    fileChooser.setDialogTitle("Selecteer bestanden om toe te voegen");

    fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
      public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".pdf")
            || f.getName().toLowerCase().endsWith(".doc") || f.getName().toLowerCase().endsWith(".docx")
            || f.getName().toLowerCase().endsWith(".xls") || f.getName().toLowerCase().endsWith(".xlsx")
            || f.getName().toLowerCase().endsWith(".jpg") || f.getName().toLowerCase().endsWith(".jpeg")
            || f.getName().toLowerCase().endsWith(".png") || f.getName().toLowerCase().endsWith(".zip")
            || f.getName().toLowerCase().endsWith(".txt");
      }

      public String getDescription() {
        return "Documenten, afbeeldingen, PDFs, etc.";
      }
    });

    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      for (File file : fileChooser.getSelectedFiles()) {
        if (!attachmentsModel.contains(file)) {
          attachmentsModel.addElement(file);
          log("Bijlage toegevoegd: " + file.getName());
        }
      }
    }
  }

  private void addFolder() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fileChooser.setDialogTitle("Selecteer een map");

    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      File folder = fileChooser.getSelectedFile();
      File[] files = folder.listFiles();

      if (files != null) {
        int added = 0;
        for (File file : files) {
          if (file.isFile() && !attachmentsModel.contains(file)) {
            attachmentsModel.addElement(file);
            added++;
          }
        }
        log(added + " bestanden toegevoegd uit map: " + folder.getName());
      }
    }
  }

  private void removeAttachment() {
    int[] selectedIndices = attachmentsList.getSelectedIndices();
    if (selectedIndices.length > 0) {
      int confirm = JOptionPane.showConfirmDialog(this,
          "Verwijder " + selectedIndices.length + " geselecteerde bestand(en)?", "Bevestiging",
          JOptionPane.YES_NO_OPTION);

      if (confirm == JOptionPane.YES_OPTION) {
        for (int i = selectedIndices.length - 1; i >= 0; i--) {
          File removed = attachmentsModel.getElementAt(selectedIndices[i]);
          attachmentsModel.remove(selectedIndices[i]);
          log("Bijlage verwijderd: " + removed.getName());
        }
      }
    } else {
      JOptionPane.showMessageDialog(this, "Selecteer eerst bestanden om te verwijderen", "Geen selectie",
          JOptionPane.WARNING_MESSAGE);
    }
  }

  private void clearAttachments() {
    if (attachmentsModel.size() > 0) {
      int confirm = JOptionPane.showConfirmDialog(this, "Alle " + attachmentsModel.size() + " bijlagen verwijderen?",
          "Bevestiging", JOptionPane.YES_NO_OPTION);

      if (confirm == JOptionPane.YES_OPTION) {
        attachmentsModel.clear();
        log("Alle bijlagen verwijderd");
      }
    }
  }

  private void previewAttachment() {
    int selectedIndex = attachmentsList.getSelectedIndex();
    if (selectedIndex >= 0) {
      File file = attachmentsModel.getElementAt(selectedIndex);

      JDialog previewDialog = new JDialog(this, "Voorbeeld: " + file.getName(), true);
      previewDialog.setSize(600, 400);
      previewDialog.setLocationRelativeTo(this);

      JTextArea previewArea = new JTextArea();
      previewArea.setEditable(false);
      previewArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

      try {
        if (file.length() > 1024 * 1024) {
          previewArea.setText("Bestand is te groot voor voorbeeld (" + (file.length() / (1024 * 1024)) + " MB)\n"
              + "Type: " + Files.probeContentType(file.toPath()) + "\n" + "Grootte: " + file.length() + " bytes\n"
              + "Pad: " + file.getAbsolutePath());
        } else if (file.getName().toLowerCase().endsWith(".txt") || file.getName().toLowerCase().endsWith(".csv")
            || file.getName().toLowerCase().endsWith(".html") || file.getName().toLowerCase().endsWith(".xml")) {
          BufferedReader reader = new BufferedReader(new FileReader(file));
          StringBuilder content = new StringBuilder();
          String line;
          int lineCount = 0;

          while ((line = reader.readLine()) != null && lineCount < 100) {
            content.append(line).append("\n");
            lineCount++;
          }
          reader.close();

          if (lineCount == 100) {
            content.append("\n... (bestand is langer, eerste 100 regels getoond)");
          }

          previewArea.setText(content.toString());
        } else {
          previewArea.setText("Binair bestand - geen tekstuele voorbeeld beschikbaar\n\n" + "Naam: " + file.getName()
              + "\n" + "Type: " + Files.probeContentType(file.toPath()) + "\n" + "Grootte: " + file.length()
              + " bytes (" + (file.length() / 1024) + " KB)\n" + "Pad: " + file.getAbsolutePath() + "\n"
              + "Laatst gewijzigd: " + new Date(file.lastModified()));
        }
      } catch (IOException e) {
        previewArea.setText("Fout bij lezen bestand: " + e.getMessage());
      }

      JScrollPane scrollPane = new JScrollPane(previewArea);
      previewDialog.add(scrollPane);
      previewDialog.setVisible(true);
    } else {
      JOptionPane.showMessageDialog(this, "Selecteer eerst een bestand om te bekijken", "Geen selectie",
          JOptionPane.WARNING_MESSAGE);
    }
  }

  private void updateAttachmentInfo() {
    SwingUtilities.invokeLater(() -> {
      int count = attachmentsModel.size();
      long totalSize = 0;

      for (int i = 0; i < count; i++) {
        File file = attachmentsModel.getElementAt(i);
        totalSize += file.length();
      }

      // Update labels in createAttachmentsPanel()
    });
  }

  // ============================================
  // EML STORAGE METHODEN
  // ============================================

  private void setProviderConfig(String provider) {
    switch (provider) {
    case "Gmail":
      smtpField.setText("smtp.gmail.com");
      portField.setText("587");
      break;
    case "Outlook/Hotmail":
      smtpField.setText("smtp-mail.outlook.com");
      portField.setText("587");
      break;
    case "Office365":
      smtpField.setText("smtp.office365.com");
      portField.setText("587");
      break;
    case "Yahoo":
      smtpField.setText("smtp.mail.yahoo.com");
      portField.setText("587");
      break;
    }
  }

  private void chooseSaveDirectory() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      saveDirField.setText(chooser.getSelectedFile().getAbsolutePath());
    }
  }

  private void refreshEmlList(DefaultListModel<String> model) {
    model.clear();
    File dir = new File(saveDirField.getText());
    if (dir.exists() && dir.isDirectory()) {
      File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".eml"));
      if (files != null) {
        Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
        for (File file : files) {
          model.addElement(file.getName());
        }
      }
    }
  }

  private void openEmlFile(String filename) {
    if (filename == null)
      return;

    File file = new File(saveDirField.getText(), filename);
    if (file.exists()) {
      try {
        Desktop.getDesktop().open(file);
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Kan bestand niet openen: " + e.getMessage(), "Fout",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void importEmlFile() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
      public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".eml");
      }

      public String getDescription() {
        return "EML Bestanden (*.eml)";
      }
    });

    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File source = chooser.getSelectedFile();
      File dest = new File(saveDirField.getText(), source.getName());

      try {
        Files.copy(source.toPath(), dest.toPath());
        log("EML bestand geïmporteerd: " + dest.getName());
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Import mislukt: " + e.getMessage(), "Fout", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void saveLogToFile() {
    JFileChooser chooser = new JFileChooser();
    chooser
        .setSelectedFile(new File("email_log_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt"));

    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      try (PrintWriter writer = new PrintWriter(chooser.getSelectedFile())) {
        writer.write(logArea.getText());
        JOptionPane.showMessageDialog(this, "Log opgeslagen!");
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Opslag mislukt: " + e.getMessage(), "Fout", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  // ============================================
  // EMAIL VERZEND METHODEN
  // ============================================

  private void sendEmails() {
    String[] emailArray = emailsArea.getText().split("\n");
    List<String> validEmails = new ArrayList<>();

    for (String email : emailArray) {
      String trimmed = email.trim();
      if (!trimmed.isEmpty() && trimmed.contains("@")) {
        validEmails.add(trimmed);
      }
    }

    if (validEmails.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Voer e-mailadressen in!", "Fout", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (usernameField.getText().isEmpty() || passwordField.getPassword().length == 0) {
      JOptionPane.showMessageDialog(this, "Vul SMTP inloggegevens in", "Fout", JOptionPane.ERROR_MESSAGE);
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(this, "Wilt u " + validEmails.size() + " e-mails verzenden?",
        "Bevestiging", JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION)
      return;

    SwingWorker<Void, String> worker = new SwingWorker<>() {
      private int successCount = 0;
      private int failCount = 0;

      @Override
      protected Void doInBackground() {
        log("=== Verzending gestart ===");
        log("Aantal ontvangers: " + validEmails.size());

        for (int i = 0; i < validEmails.size(); i++) {
          String email = validEmails.get(i);

          try {
            sendSingleEmail(email);
            successCount++;
            publish("✓ Verzonden naar: " + email);

            if (saveEmlCheckbox.isSelected()) {
              saveAsEml(email, true);
            }

          } catch (Exception e) {
            failCount++;
            publish("✗ Fout bij " + email + ": " + e.getMessage());
            saveAsEml(email, false);
          }

          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
          }
        }

        return null;
      }

      @Override
      protected void process(List<String> chunks) {
        for (String msg : chunks)
          log(msg);
      }

      @Override
      protected void done() {
        log("=== Verzending voltooid ===");
        log("Succesvol: " + successCount + ", Mislukt: " + failCount);

        JOptionPane.showMessageDialog(EmailSenderGUI.this,
            String.format("Verzending voltooid!\n\nSucces: %d\nMislukt: %d", successCount, failCount), "Resultaat",
            JOptionPane.INFORMATION_MESSAGE);
      }
    };

    worker.execute();
  }

  private void sendSingleEmail(String recipient) throws Exception {
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", smtpField.getText());
    props.put("mail.smtp.port", portField.getText());

    Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(usernameField.getText(), new String(passwordField.getPassword()));
      }
    });

    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(usernameField.getText()));
      message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
      message.setSubject(subjectField.getText());
      message.setSentDate(new Date());

      String personalizedMessage = personalizeMessage(messageArea.getText(), recipient);

      if (attachmentsModel.size() > 0) {
        MimeMultipart multipart = new MimeMultipart();

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(personalizedMessage);
        multipart.addBodyPart(messageBodyPart);

        for (int i = 0; i < attachmentsModel.size(); i++) {
          File file = attachmentsModel.getElementAt(i);
          MimeBodyPart attachmentPart = new MimeBodyPart();
          attachmentPart.attachFile(file);
          multipart.addBodyPart(attachmentPart);
        }

        message.setContent(multipart);
      } else {
        message.setText(personalizedMessage);
      }

      Transport.send(message);

    } catch (MessagingException e) {
      throw new Exception("Verzending mislukt: " + e.getMessage());
    }
  }

  private void saveAllAsEml() {
    String[] emailArray = emailsArea.getText().split("\n");
    int count = 0;

    for (String email : emailArray) {
      String trimmed = email.trim();
      if (!trimmed.isEmpty() && trimmed.contains("@")) {
        try {
          saveAsEml(trimmed, true);
          count++;
        } catch (Exception e) {
          log("Fout bij opslaan EML voor " + trimmed + ": " + e.getMessage());
        }
      }
    }

    JOptionPane.showMessageDialog(this, count + " EML bestanden opgeslagen", "Opslag voltooid",
        JOptionPane.INFORMATION_MESSAGE);
  }

  private void saveAsEml(String recipient, boolean success) {
    try {
      File saveDir = new File(saveDirField.getText());
      if (!saveDir.exists())
        saveDir.mkdirs();

      String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      String status = success ? "success" : "failed";
      String filename = String.format("email_%s_%s_%s.eml", recipient.replace("@", "_at_"), timestamp, status);

      File emlFile = new File(saveDir, filename);

      Session session = Session.getInstance(new Properties());
      MimeMessage message = new MimeMessage(session);

      message.setFrom(new InternetAddress(usernameField.getText()));
      message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
      message.setSubject(subjectField.getText());
      message.setSentDate(new Date());

      MimeMultipart multipart = new MimeMultipart();

      MimeBodyPart textPart = new MimeBodyPart();
      String personalizedText = personalizeMessage(messageArea.getText(), recipient);
      textPart.setText(personalizedText);
      multipart.addBodyPart(textPart);

      if (attachmentsModel.size() > 0) {
        for (int i = 0; i < attachmentsModel.size(); i++) {
          File attachment = attachmentsModel.getElementAt(i);
          MimeBodyPart attachmentPart = new MimeBodyPart();
          attachmentPart.attachFile(attachment);
          multipart.addBodyPart(attachmentPart);
        }
      }

      message.setContent(multipart);

      try (FileOutputStream fos = new FileOutputStream(emlFile)) {
        message.writeTo(fos);
        log("EML opgeslagen: " + emlFile.getName());
      }

    } catch (Exception e) {
      throw new RuntimeException("EML opslag mislukt: " + e.getMessage());
    }
  }

  private String personalizeMessage(String template, String recipient) {
    String personalized = template;
    personalized = personalized.replace("{email}", recipient);
    personalized = personalized.replace("{datum}", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
    personalized = personalized.replace("{tijd}", new SimpleDateFormat("HH:mm").format(new Date()));

    String name = recipient.split("@")[0];
    name = name.replace(".", " ").replace("_", " ");
    name = name.substring(0, 1).toUpperCase() + name.substring(1);
    personalized = personalized.replace("{naam}", name);

    return personalized;
  }

  private void testConnection() {
    SwingWorker<Boolean, String> worker = new SwingWorker<>() {
      @Override
      protected Boolean doInBackground() {
        log("Testen verbinding met " + smtpField.getText() + ":" + portField.getText());

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpField.getText());
        props.put("mail.smtp.port", portField.getText());
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");

        try {
          Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
              return new PasswordAuthentication(usernameField.getText(), new String(passwordField.getPassword()));
            }
          });

          Transport transport = session.getTransport("smtp");
          transport.connect();
          transport.close();

          log("✓ Verbinding succesvol!");
          return true;

        } catch (Exception e) {
          log("✗ Verbinding mislukt: " + e.getMessage());
          return false;
        }
      }

      @Override
      protected void done() {
        try {
          if (get()) {
            JOptionPane.showMessageDialog(EmailSenderGUI.this, "Verbinding succesvol getest!", "Succes",
                JOptionPane.INFORMATION_MESSAGE);
          } else {
            JOptionPane.showMessageDialog(EmailSenderGUI.this, "Verbinding mislukt. Controleer instellingen.", "Fout",
                JOptionPane.ERROR_MESSAGE);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };

    worker.execute();
  }

  // ============================================
  // CUSTOM RENDERER
  // ============================================

  private class AttachmentListCellRenderer extends DefaultListCellRenderer {
    /**
     * 
     */
    private static final long serialVersionUID = -2800123506781962365L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

      if (value instanceof File) {
        File file = (File) value;
        String icon = getFileIcon(file.getName());

        long fileSize = file.length();
        String size;
        if (fileSize > 1024 * 1024) {
          size = String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        } else if (fileSize > 1024) {
          size = String.format("%.0f KB", fileSize / 1024.0);
        } else {
          size = fileSize + " bytes";
        }

        setText(icon + " " + file.getName() + " (" + size + ")");
        setToolTipText(file.getAbsolutePath());
      }

      return this;
    }

    private String getFileIcon(String filename) {
      String lower = filename.toLowerCase();
      if (lower.endsWith(".pdf"))
        return "📕";
      if (lower.endsWith(".doc") || lower.endsWith(".docx"))
        return "📘";
      if (lower.endsWith(".xls") || lower.endsWith(".xlsx"))
        return "📗";
      if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".gif"))
        return "🖼️";
      if (lower.endsWith(".zip") || lower.endsWith(".rar"))
        return "📦";
      if (lower.endsWith(".txt"))
        return "📝";
      return "📎";
    }
  }

  // ============================================
  // MAIN METHOD
  // ============================================

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
        e.printStackTrace();
      }
      new EmailSenderGUI().setVisible(true);
    });
  }
}
