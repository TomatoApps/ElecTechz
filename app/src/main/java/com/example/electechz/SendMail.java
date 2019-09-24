package com.example.electechz;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
    class RetrieveFeedTask extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... urls) {
            final String username = "electechzhelp@gmail.com";
            final String password = "electechzsupport0!";

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("electechzhelp@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(urls[0]));
                message.setSubject(urls[1]);
                message.setText(urls[2]);
                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public SendMail(String email, String subject, String body) {
        new RetrieveFeedTask().execute(email, subject, body);
    }
}