package me.izhong.jobs.agent.util;

import lombok.extern.slf4j.Slf4j;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Slf4j
public class MailUtil {

    public static void sendMail(String host, int port, String senderAccount, String senderPassword, String senderAddress, String senderName,
                                String recipientAddress, String title, String body) throws Exception {
        sendMail(host, port, senderAccount, senderPassword, senderAddress, senderName, recipientAddress, title, body, null, null);
    }

    public static void sendMail(String host, int port, String senderAccount, String senderPassword, String senderAddress, String senderName,
                                String recipientAddress, String title, String body, String fileFullName) throws Exception {
        sendMail(host, port, senderAccount, senderPassword, senderAddress, senderName, recipientAddress, title, body, new File(fileFullName), new File(fileFullName).getName());
    }

    public static void sendMail(String host, int port, String senderAccount, String senderPassword, String senderAddress, String senderName,
                                String recipientAddress, String title, String body, File file) throws Exception {
        sendMail(host, port, senderAccount, senderPassword, senderAddress, senderName, recipientAddress, title, body, file, file.getName());
    }

    public static void sendMail(String host, int port, String senderAccount, String senderPassword, String senderAddress, String senderName,
                                String recipientAddress, String title, String body, File file, String fileName) throws Exception {
        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", host);
        Session session = Session.getInstance(props);
        Message msg = getMimeMessage(session, senderAddress, recipientAddress, title, body, senderName, file, fileName);
        Transport transport = session.getTransport();
        transport.connect(host, port, senderAccount, senderPassword);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }

    private static Message getMimeMessage(Session session, String senderAddress, String recipientAddress, String title, String body, String senderName,
                                          File file, String fileName) throws UnsupportedEncodingException, MessagingException {
        MimeMessage msg = new MimeMessage(session);
        ;
        msg.setFrom(new InternetAddress(senderAddress, senderName));
        String[] res = recipientAddress.split(",");
        List<InternetAddress> addresses = new ArrayList<>();
        for (String s : res) {
            addresses.add(new InternetAddress(s));
        }
        Address[] ias = addresses.toArray(new InternetAddress[addresses.size()]);
        msg.addRecipients(MimeMessage.RecipientType.TO, ias);
        msg.setSubject(title, "UTF-8");

        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(body, "text/html;charset=utf-8");
        MimeMultipart mm = new MimeMultipart();
        mm.addBodyPart(messageBodyPart);

        if (file != null) {
            BodyPart attachment = new MimeBodyPart();
            DataHandler dHandler = new DataHandler(new FileDataSource(file));
            attachment.setDataHandler(dHandler);
            if (fileName == null)
                fileName = file.getName();
            attachment.setFileName(MimeUtility.encodeText(fileName));
            mm.addBodyPart(attachment);
        }

        msg.setContent(mm);
        msg.setSentDate(new Date());
        msg.saveChanges();
        return msg;
    }
}
