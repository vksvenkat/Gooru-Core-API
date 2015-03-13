package org.ednovo.gooru.mail.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.ednovo.gooru.mail.domain.Attachment;
import org.ednovo.gooru.mail.domain.MailDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SMTPMailSerivceHandler implements MailHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(SMTPMailSerivceHandler.class);
	
	public void sendSingleRecipient(MailDO mail) throws MessagingException, UnsupportedEncodingException {
		String[] to = mail.getRecipient().split(",");
		for (int i = 0; i < to.length; i++) {
			Address singleRecipient =  new InternetAddress(to[i]);
			Address[] address = new Address [] {singleRecipient};
			mail.setAddress(address);
			sendMail(mail, (System.currentTimeMillis() + 1000 * 60 * 5));
		}
	}
	
	@Override
	public void sendRecipient(MailDO mail) throws MessagingException,
			UnsupportedEncodingException {
			String[] to = mail.getRecipient().split(",");
			Address[] address = new Address[to.length];
			for (int i = 0; i < to.length; i++) {
				address[i] = new InternetAddress(to[i]);
			}
			mail.setAddress(address);
			sendMail(mail, (System.currentTimeMillis() + 1000 * 60 * 5));
	}

	public Object sendMail(final MailDO mail, Long expires) throws MessagingException, UnsupportedEncodingException {
		Long start = System.currentTimeMillis();
		if (start <= expires) {
			Security.addProvider(new BouncyCastleProvider());
			Properties props = new Properties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.host", mail.getHost());
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");
			Session mailSession = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(mail.getUsername(), mail.getPassword());
				}
			});
			Transport transport = mailSession.getTransport();
			MimeMessage message = new MimeMessage(mailSession);
			Multipart multipart = new MimeMultipart();
			BodyPart bodyPartForHtml = new MimeBodyPart();
			bodyPartForHtml.setContent(mail.getHtmlContent(), "text/html");
			multipart.addBodyPart(bodyPartForHtml);
			if (mail.getAttachFiles() != null) {
				BodyPart messageBodyPart = new MimeBodyPart();
				HttpURLConnection connection = null;
				URL url = null;
				for (Attachment attachment : mail.getAttachFiles()) {
					try {
						url = new URL(attachment.getUrl());
					} catch (MalformedURLException e) {
					}
					if (url != null) {
						try {
							connection = (HttpURLConnection) url.openConnection();
						} catch (Exception e) {
							logger.error("Connection Error:{}", e);
						}
					}
					ByteArrayDataSource bds = null;
					messageBodyPart = new MimeBodyPart();
					try {
						bds = new ByteArrayDataSource(url.openStream(), connection.getContentType());
					} catch (IOException e) {
					}
					messageBodyPart.setDataHandler(new DataHandler(bds));
					messageBodyPart.setFileName(attachment.getFileName());
					multipart.addBodyPart(messageBodyPart);
				}
			}
			message.setContent(multipart);
			message.setFrom(new InternetAddress(mail.getFrom(), mail.getFromName()));
			message.setSubject(mail.getSubject());
			
			message.setRecipients(Message.RecipientType.TO, mail.getAddress());
			
			if (mail.getCc() != null) {
				message.setRecipients(Message.RecipientType.CC, mail.getCc());
			}
			if (mail.getBcc() != null) {
				message.setRecipients(Message.RecipientType.BCC, mail.getBcc());
			}
			transport.connect();
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			return message;
		} else {
			throw new RuntimeException("error:tims was expired.");
		}
	}

}
