/*******************************************************************************
 * Copyright 2014 Ednovo d/b/a Gooru. All rights reserved.
 * http://www.goorulearning.org/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 *  "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package org.ednovo.gooru.mail.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
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
import org.springframework.stereotype.Component;

@Component
public class SMTPMailSerivceHandler implements MailHandler {

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
					try {
						connection = (HttpURLConnection) url.openConnection();
					} catch (IOException e1) {
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
			
			InternetAddress to = new InternetAddress(mail.getRecipient());
			message.setRecipient(Message.RecipientType.TO, to);
			
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
