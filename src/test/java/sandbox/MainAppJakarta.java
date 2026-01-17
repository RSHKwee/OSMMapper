package sandbox;

import java.io.File;
import java.util.*;

import kwee.osmmapper.mailsupport.EmailData;

import java.io.InputStream;
import java.nio.file.*;

public class MainAppJakarta {

  public static void main(String[] args) {
    try {
      // Laad configuratie (optioneel uit properties bestand)
      Properties config = new Properties();
      try (InputStream input = MainAppJakarta.class.getClassLoader().getResourceAsStream("emailconfig.properties")) {
        if (input != null) {
          config.load(input);
        }
      }

      // Configuratie (met defaults)
      String smtpHost = config.getProperty("smtp.host", "smtp.gmail.com");
      int smtpPort = Integer.parseInt(config.getProperty("smtp.port", "587"));
      String username = config.getProperty("smtp.username", "jouw.email@gmail.com");
      String password = config.getProperty("smtp.password", "");
      boolean useSSL = Boolean.parseBoolean(config.getProperty("smtp.ssl", "false"));
      boolean useTLS = Boolean.parseBoolean(config.getProperty("smtp.tls", "true"));

      // Initialiseer sender
      EmailSender sender = new EmailSender(smtpHost, smtpPort, username, password, useSSL, useTLS);

      // Lees template uit bestand
      String standaardTekst = """
          Beste {naam},

          Hierbij ontvangt u de aangevraagde bestanden.

          Deze e-mail is automatisch verzonden. Mocht u vragen hebben,
          neem dan contact met ons op.

          Met vriendelijke groet,

          Hoevelaken Duurzaam Buurkracht
          """;

      // Dynamisch bestandenlijst maken
      List<EmailData> emailLijst = new ArrayList<>();

      // Voorbeeld: lees uit CSV of database
      // Hier een hardcoded voorbeeld:

      // Ontvanger 1
      List<File> files1 = Arrays.asList(Paths.get("bestanden", "rapport.pdf").toFile(),
          Paths.get("bestanden", "offerte.docx").toFile());
      emailLijst.add(new EmailData("Jan Jansen", "jan@voorbeeld.nl", files1));

      // Ontvanger 2
      List<File> files2 = Arrays.asList(Paths.get("bestanden", "presentatie.pptx").toFile());
      emailLijst.add(new EmailData("Marie Peters", "marie@voorbeeld.nl", files2));

      // Verstuur e-mails
      System.out.println("Start verzenden van " + emailLijst.size() + " e-mails...");
      sender.sendBulkEmails(emailLijst, standaardTekst);
      System.out.println("Klaar met verzenden!");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
