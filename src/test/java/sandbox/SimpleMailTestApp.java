package sandbox;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.*;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientSecret;
import com.microsoft.aad.msal4j.SilentParameters;

public class SimpleMailTestApp {

  public static void main(String[] args) {
    // if (args.length < 5) {
    // System.out.println("Usage: java SimpleTestApp <host> <port> <username>
    // <password> <toEmail>");
    // System.out.println("Example: java SimpleTestApp smtp.gmail.com 587
    // user@gmail.com pass recipient@email.com");
    // return;
    // }
    ConfidentialClientApplication app;
    try {
      app = ConfidentialClientApplication
          .builder("your-application-id", ClientCredentialFactory.createFromSecret("your-client-secret"))
          .authority("https://login.microsoftonline.com/your-tenant-id").build();

      IClientSecret credential = ClientCredentialFactory.createFromSecret("your-client-secret");
      SilentParameters silentParameters = SilentParameters.builder(Collections.singleton("Mail.Send")).build();
      IAuthenticationResult result = app.acquireTokenSilently(silentParameters).join();
      String accessToken = result.accessToken();

      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", "smtp.office365.com");
      props.put("mail.smtp.port", "587");
      props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
      props.put("mail.smtp.auth.login.disable", "true");
      props.put("mail.smtp.auth.plain.disable", "true");
      Session session = Session.getInstance(props, new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(null, accessToken);
        }
      });
      MimeMessage message = new MimeMessage(session);
      message.setFrom(new InternetAddress("your-email@domain.com"));
      message.addRecipient(Message.RecipientType.TO, new InternetAddress("recipient-email@domain.com"));
      message.setSubject("Your Subject Here");
      message.setText("Email body content here.");
      Transport.send(message);

    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (AddressException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (MessagingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // Laad configuratie (optioneel uit properties bestand)
    Properties config = new Properties();
    try (InputStream input = MainAppJakarta.class.getClassLoader().getResourceAsStream("emailconfig.properties")) {
      if (input != null) {
        config.load(input);
      }
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    // Configuratie (met defaults)
    String smtpHost = config.getProperty("smtp.host", "smtp.gmail.com");
    int smtpPort = Integer.parseInt(config.getProperty("smtp.port", "587"));
    String username = config.getProperty("smtp.username", "jouw.email@gmail.com");
    String password = config.getProperty("smtp.password", "");
    boolean useSSL = Boolean.parseBoolean(config.getProperty("smtp.ssl", "false"));
    boolean useTLS = Boolean.parseBoolean(config.getProperty("smtp.tls", "true"));

    String host = smtpHost;
    int port = smtpPort;

    String toEmail = "rsh.kwee@gmail.com";

    try {
      System.out.println("Testing email send to: " + toEmail);

      // 1. Setup properties
      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", useTLS);
      props.put("mail.smtp.host", host);
      props.put("mail.smtp.port", port);

      // 2. Create session
      Session session = Session.getInstance(props, new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(username, password);
        }
      });

      // 3. Create message
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(username));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
      message.setSubject("Test Email from Java");
      message.setText("Hello!\n\nThis is a test email sent from Java application.\n\nIt works!");

      // 4. Send
      System.out.println("Sending...");
      Transport.send(message);

      System.out.println("SUCCESS: Email sent to " + toEmail);

    } catch (Exception e) {
      System.err.println("ERROR: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
