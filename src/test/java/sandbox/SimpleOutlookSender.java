package sandbox;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.*;

public class SimpleOutlookSender {

  // Gebruik deze voor basis SMTP auth (minder veilig)
  public static void sendWithPassword(String username, String password, String toEmail, String subject, String body)
      throws MessagingException {

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.office365.com");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });

    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(username));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
    message.setSubject(subject);
    message.setText(body);

    Transport.send(message);
  }

  public static void main(String[] args) {
    String username = "hoevelaken.duurzaam@outlook.com";
    String password = "X5m7r8KB1pTD2nJiqqKw";
    String toMail = "rsh.kwee@gmail.com";
    String subject = "Test mail vai java";
    String body = "test";
    try {
      sendWithPassword(username, password, toMail, subject, body);
    } catch (MessagingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}