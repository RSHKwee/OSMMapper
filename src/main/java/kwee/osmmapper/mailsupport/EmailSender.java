package kwee.osmmapper.mailsupport;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.*;
import java.util.*;
import java.io.File;

public class EmailSender {

  private String smtpHost;
  private int smtpPort;
  private String username;
  private String password;
  private boolean useSSL;
  private boolean useTLS;

  public EmailSender(String smtpHost, int smtpPort, String username, String password, boolean useSSL, boolean useTLS) {
    this.smtpHost = smtpHost;
    this.smtpPort = smtpPort;
    this.username = username;
    this.password = password;
    this.useSSL = useSSL;
    this.useTLS = useTLS;
  }

  public void sendBulkEmails(List<EmailData> emailList, String standardText) {
    int successCount = 0;
    int failCount = 0;

    for (int i = 0; i < emailList.size(); i++) {
      EmailData emailData = emailList.get(i);
      System.out.printf("\n[%d/%d] Verzenden naar: %s... ", i + 1, emailList.size(), emailData.getEmail());

      try {
        sendSingleEmail(emailData, standardText);
        System.out.println("SUCCESS");
        successCount++;

        // Kleine pauze tussen e-mails
        if (i < emailList.size() - 1) {
          Thread.sleep(1000);
        }

      } catch (Exception e) {
        System.out.println("FAILED: " + e.getMessage());
        failCount++;
        e.printStackTrace();
      }
    }

    System.out.printf("\n\nResultaat: %d succesvol, %d gefaald\n", successCount, failCount);
  }

  private void sendSingleEmail(EmailData emailData, String standardText)
      throws MessagingException, jakarta.mail.internet.AddressException {

    // 1. Configureer properties
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", String.valueOf(useTLS));
    props.put("mail.smtp.ssl.enable", String.valueOf(useSSL));
    props.put("mail.smtp.host", smtpHost);
    props.put("mail.smtp.port", String.valueOf(smtpPort));

    // Timeout instellingen
    props.put("mail.smtp.connectiontimeout", "15000");
    props.put("mail.smtp.timeout", "15000");
    props.put("mail.smtp.writetimeout", "15000");

    // Debug (zet op true voor problemen oplossen)
    props.put("mail.debug", "false");

    // 2. Maak sessie - LET OP: jakarta.mail.Authenticator
    Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });

    session.setDebug(false);

    // 3. Maak bericht
    MimeMessage message = new MimeMessage(session);

    // Afzender
    message.setFrom(new InternetAddress(username));

    // Ontvanger(s)
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailData.getEmail()));

    // Onderwerp
    message.setSubject("Uw bestanden - " + emailData.getName(), "UTF-8");

    // 4. Personaliseer tekst
    String personalizedText = standardText.replace("{naam}", emailData.getName());

    // 5. Maak multipart (tekst + attachments)
    MimeMultipart multipart = new MimeMultipart();

    // Tekst deel
    MimeBodyPart textPart = new MimeBodyPart();
    textPart.setText(personalizedText, "UTF-8");
    multipart.addBodyPart(textPart);

    // 6. Voeg attachments toe
    List<File> attachments = emailData.getFiles();
    for (File file : attachments) {
      if (file.exists() && file.canRead()) {
        addAttachment(multipart, file);
      } else {
        System.err.println("Waarschuwing: Bestand niet gevonden of niet leesbaar: " + file.getAbsolutePath());
      }
    }

    // 7. Stel content in
    message.setContent(multipart);

    // 8. Verstuur
    Transport.send(message);
  }

  private void addAttachment(MimeMultipart multipart, File file) throws MessagingException {

    MimeBodyPart attachmentPart = new MimeBodyPart();

    // Gebruik FileDataSource
    FileDataSource source = new FileDataSource(file);
    attachmentPart.setDataHandler(new DataHandler(source));
    attachmentPart.setFileName(file.getName());

    // Set content type
    String contentType = getContentType(file.getName());
    attachmentPart.setHeader("Content-Type", contentType);
    attachmentPart.setHeader("Content-Transfer-Encoding", "base64");

    multipart.addBodyPart(attachmentPart);
  }

  private String getContentType(String filename) {
    if (filename == null) {
      return "application/octet-stream";
    }

    String extension = "";
    int dotIndex = filename.lastIndexOf('.');
    if (dotIndex > 0) {
      extension = filename.substring(dotIndex + 1).toLowerCase();
    }

    // Content type mapping
    Map<String, String> contentTypes = new HashMap<>();
    contentTypes.put("pdf", "application/pdf");
    contentTypes.put("doc", "application/msword");
    contentTypes.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    contentTypes.put("xls", "application/vnd.ms-excel");
    contentTypes.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    contentTypes.put("ppt", "application/vnd.ms-powerpoint");
    contentTypes.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
    contentTypes.put("jpg", "image/jpeg");
    contentTypes.put("jpeg", "image/jpeg");
    contentTypes.put("png", "image/png");
    contentTypes.put("gif", "image/gif");
    contentTypes.put("txt", "text/plain");
    contentTypes.put("zip", "application/zip");
    contentTypes.put("rar", "application/x-rar-compressed");

    return contentTypes.getOrDefault(extension, "application/octet-stream");
  }
}