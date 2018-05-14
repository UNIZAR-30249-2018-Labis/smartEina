package src.infrastructure.repository;

import org.springframework.stereotype.Repository;
import src.application.domain.Email;
import src.application.domain.EmailRepository;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

@Repository
public class EmailRepositoryImplementation implements EmailRepository {

    public boolean sendEmail(Email email) {
        // Assuming you are sending email from localhost
        String host = "localhost";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(email.getFrom()));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getTo()));

            // Set Subject: header field
            message.setSubject(email.getSubject());

            // Now set the actual message
            message.setText(email.getContent());

            // Send message
            Transport.send(message);
            System.out.println("Email enviado..");
            return true;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            System.out.println("Fallo al enviar email..");
            return false;
        }
    }
}
