package sandbox;

import java.util.*;

public class TestApp {
    public static void main(String[] args) {
        System.out.println("=== Test EmailSender ===");
        
        try {
            // 1. Test sessie aanmaken
            testSessionCreation();
            
            // 2. Test e-mail versturen (zonder echt te verzenden)
            testEmailComposition();
            
            System.out.println("\n✅ Alle tests geslaagd!");
            
        } catch (Exception e) {
            System.err.println("\n❌ Test gefaald: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testSessionCreation() throws Exception {
        System.out.println("\n1. Test Session Creation...");
        
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        
        // BELANGRIJK: gebruik jakarta.mail.Authenticator
        jakarta.mail.Session session = jakarta.mail.Session.getInstance(
            props, 
            new jakarta.mail.Authenticator() {
                @Override
                protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new jakarta.mail.PasswordAuthentication("test", "test");
                }
            }
        );
        
        System.out.println("   Session type: " + session.getClass().getName());
        System.out.println("   ✅ Session succesvol aangemaakt");
    }
    
    private static void testEmailComposition() throws Exception {
        System.out.println("\n2. Test Email Composition...");
        
        // Maak een mock session
        Properties props = new Properties();
        jakarta.mail.Session session = jakarta.mail.Session.getInstance(props);
        
        // Maak een test bericht
        jakarta.mail.internet.MimeMessage message = new jakarta.mail.internet.MimeMessage(session);
        message.setFrom(new jakarta.mail.internet.InternetAddress("test@example.com"));
        message.setRecipients(
            jakarta.mail.Message.RecipientType.TO,
            jakarta.mail.internet.InternetAddress.parse("recipient@example.com")
        );
        message.setSubject("Test Subject");
        message.setText("Test body");
        
        System.out.println("   Bericht type: " + message.getClass().getName());
        System.out.println("   ✅ Email succesvol samengesteld");
    }
}
