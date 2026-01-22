package kwee.osmmapper.gui;

import gui.EmailSenderGUI;
import kwee.logger.MyLogger;
import kwee.osmmapper.lib.FotoIntegration;
import kwee.osmmapper.lib.MemoContent;

import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MailHandlingGui {
  private static final Logger LOGGER = MyLogger.getLogger();

  public static void startMailSender(OsmMapViewer osmViewer, String a_title) {
    ArrayList<MemoContent> memocontarr = osmViewer.getMemoContArr();
    FotoIntegration fotoIntegration = osmViewer.getFotoIntegration();

    SwingUtilities.invokeLater(() -> {

      // 1. Bereid ontvangers voor
      List<EmailSenderGUI.RecipientData> recipients = new ArrayList<>();
      memocontarr.forEach(memo -> {
        String pictureIdx = memo.getPostcode().replace(" ", "").toUpperCase()
            + memo.getHousenumber().replace(" ", "").toUpperCase();
        List<File> attFiles = fotoIntegration.getFotosVoorAdres(pictureIdx);

        String l_address = memo.getStreet() + " " + memo.getHousenumber();
        EmailSenderGUI.RecipientData recepdata = new EmailSenderGUI.RecipientData(memo.getMailaddress(),
            memo.getSurname(), memo.getFamilyname(), attFiles, l_address, memo.getPostcode(), memo.getCity());

        recipients.add(recepdata);
        if (attFiles.size() > 0) {
          LOGGER.log(Level.INFO,
              " Voor " + memo.getSurname() + " " + memo.getFamilyname() + " attachments: " + attFiles.size());
        }
      });

      // 2. SMTP configuratie
      EmailSenderGUI.SMTPConfig smtpConfig = new EmailSenderGUI.SMTPConfig("smtp-mail.outlook.com", 587,
          "hoevelaken.duurzaam@outlook.com", "jouw-wachtwoord");

      // 3. Bericht configuratie
      String berichtTemplate = "";

      berichtTemplate = "Beste {voornaam} {achternaam},\n\n" + "Hierbij ontvangt u uw warmtescan foto.\n\n"
          + " Adres : \n" + "{straat_nr} \n{postcode} {plaats}\n\n" + "Met vriendelijke groet,\n"
          + "Het Buurkracht team Hoevelaken Duurzaam";

      EmailSenderGUI.MessageConfig messageConfig = new EmailSenderGUI.MessageConfig(
          "Uw persoonlijke documenten - {datum}", berichtTemplate);

      // 4. Algemene bijlagen (voor alle ontvangers)
      List<File> commonAttachments = Arrays.asList(new File("C:/documenten/algemeen/voorwaarden.pdf"),
          new File("C:/documenten/algemeen/privacybeleid.docx"));

      // 5. Start de GUI met alle parameters
      EmailSenderGUI gui = new EmailSenderGUI(a_title, recipients, smtpConfig, messageConfig, commonAttachments);

      gui.setVisible(true);
      gui.setLocationRelativeTo(null); // Centreer op scherm

      // Optioneel: Toon aantal geladen items
      LOGGER.log(Level.INFO, "GUI gestart met " + recipients.size() + " ontvangers");
    });
  }
}
