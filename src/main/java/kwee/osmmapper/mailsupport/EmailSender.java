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

  public void sendBulkEmails(List<EmailData> emailList, String standardText) throws Exception {
    for (EmailData emailData : emailList) {
      try {
        sendEmailWithAttachment(emailData, standardText);
        System.out.println("✓ E-mail verzonden naar: " + emailData.getEmail());
      } catch (Exception e) {
        System.err.println("✗ Fout bij verzenden naar " + emailData.getEmail() + ": " + e.getMessage());
        // Optioneel: log de fout en ga door met volgende
      }
    }
  }

  private void sendEmailWithAttachment(EmailData emailData, String standardText) throws Exception {
    // Configureer properties
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", String.valueOf(useTLS));
    props.put("mail.smtp.ssl.enable", String.valueOf(useSSL));
    props.put("mail.smtp.host", smtpHost);
    props.put("mail.smtp.port", String.valueOf(smtpPort));

    // Extra properties voor betrouwbaarheid
    props.put("mail.smtp.connectiontimeout", "5000");
    props.put("mail.smtp.timeout", "5000");
    props.put("mail.smtp.writetimeout", "5000");

    // Maak sessie
    Session session = Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });

    // Debug modus (optioneel)
    session.setDebug(false);

    // Maak bericht
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(username, "Uw Organisatie")); // Met weergavenaam

    // Voeg ontvanger toe
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailData.getEmail()));

    message.setSubject("Uw bestanden - " + emailData.getName());
    message.setSentDate(new Date());

    // Personaliseer tekst
    String personalizedText = standardText.replace("{naam}", emailData.getName());

    // Maak multipart
    Multipart multipart = new MimeMultipart();

    // Tekst deel
    MimeBodyPart textPart = new MimeBodyPart();
    textPart.setText(personalizedText, "UTF-8");
    multipart.addBodyPart(textPart);

    // Voeg attachments toe
    for (File file : emailData.getFiles()) {
      if (file.exists() && file.canRead()) {
        MimeBodyPart attachmentPart = new MimeBodyPart();

        // Gebruik FileDataSource voor betere ondersteuning
        FileDataSource source = new FileDataSource(file);
        attachmentPart.setDataHandler(new DataHandler(source));
        attachmentPart.setFileName(file.getName());

        // Voeg content type header toe voor betere compatibiliteit
        String contentType = getContentType(file.getName());
        if (contentType != null) {
          attachmentPart.setHeader("Content-Type", contentType);
        }

        multipart.addBodyPart(attachmentPart);
      } else {
        System.err.println("Bestand niet gevonden of niet leesbaar: " + file.getPath());
      }
    }

    // Stel content in
    message.setContent(multipart);

    // Verstuur e-mail
    Transport.send(message);
  }

  private String getContentType(String filename) {
    String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

    Map<String, String> contentTypes = Map.of("pdf", "application/pdf", "doc", "application/msword", "docx",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "xls", "application/vnd.ms-excel",
        "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "jpg", "image/jpeg", "jpeg",
        "image/jpeg", "png", "image/png", "txt", "text/plain");

    return contentTypes.getOrDefault(extension, "application/octet-stream");
  }
}