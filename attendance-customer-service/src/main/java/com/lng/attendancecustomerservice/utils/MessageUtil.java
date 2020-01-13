package com.lng.attendancecustomerservice.utils;


import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.lng.attendancecustomerservice.entity.smsVendor.SMSVendorCust;
import com.lng.attendancecustomerservice.repositories.smsVendor.SMSVendorRepositoryCust;


public class MessageUtil {


	@Autowired
	SMSVendorRepositoryCust smsVendorRepository;
	
	//Send SMS
	public String sms(String to, String message)
	{
		String result = null;
		try {
			RestTemplate restTemplate = new RestTemplate();

			//  String messageBody="Your OTP for this registration is "+message;
			//message=messageBody;
			
			String mobileNumbers = to;

			String textMessage = message;
			
			// SMSVendorCust smsVendor = smsVendorRepository.getAllBySmsVndrIsActive();
			
			// SMSVendorCust smsVendor = smsVendorRepository.findAllBySmsVndrIsActive(true);

			// final  String uri = smsVendor.getSmsVndrURL();
			
		    final  String uri = "http://promotional.mysmsbasket.com/V2/http-api.php?apikey=YE5ssFpB9306XlDP&senderid=LNGATS&number=mobileNumbers&message=textMessage&format=json";

			StringBuilder sb = new StringBuilder(uri);
			sb.replace(sb.indexOf("mobileNumbers"), sb.indexOf("mobileNumbers") + "mobileNumbers".length(), mobileNumbers);
			sb.replace(sb.indexOf("textMessage"), sb.indexOf("textMessage") + "textMessage".length(), textMessage);
			
			result = restTemplate.getForObject(sb.toString(),String.class);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		return result;
	}

		//Send Only Email Without Attchments
		public  void sendOnlyEmail(String host, String port,
				final String userName, final String password, String toAddress,
				String subject, String message)
						throws AddressException, MessagingException {
			try {
				// sets SMTP server properties
				Properties properties = new Properties();
				properties.put("mail.smtp.host", host);
				properties.put("mail.smtp.port", port);
				properties.put("mail.smtp.auth", "true");
				properties.put("mail.smtp.starttls.enable", "true");
				properties.put("mail.user", userName);
				properties.put("mail.password", password);

				// creates a new session with an authenticator
				Authenticator auth = new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(userName, password);
					}
				};
				Session session = Session.getInstance(properties, auth);

				// creates a new e-mail message
				Message msg = new MimeMessage(session);

				msg.setFrom(new InternetAddress(userName, "LNGAdmin"));
				InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
				msg.setRecipients(Message.RecipientType.TO, toAddresses);
				msg.setSubject(subject);
				msg.setSentDate(new Date());

				// creates message part
				MimeBodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setContent(message, "text/html");
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);
				msg.setContent(multipart);

				// sends the e-mail
				Transport.send(msg);
			} catch(Exception ex) {
				ex.printStackTrace();
			}	

		}


	//Send Email with Attachments
	public  void sendEmailWithAttachments(String host, String port,
			final String userName, final String password, String toAddress,
			String subject, String message, String[] attachFiles)
					throws AddressException, MessagingException {
		// sets SMTP server properties
		Properties properties = new Properties();
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.user", userName);
		properties.put("mail.password", password);

		// creates a new session with an authenticator
		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		};
		Session session = Session.getInstance(properties, auth);

		// creates a new e-mail message
		Message msg = new MimeMessage(session);

		msg.setFrom(new InternetAddress(userName));
		InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
		msg.setRecipients(Message.RecipientType.TO, toAddresses);
		msg.setSubject(subject);
		msg.setSentDate(new Date());

		// creates message part
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(message, "text/html");

		// creates multi-part
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		// adds attachments
		if (attachFiles != null && attachFiles.length > 0) {
			for (String filePath : attachFiles) {
				MimeBodyPart attachPart = new MimeBodyPart();

				try {
					// FileSystemResource file = new FileSystemResource(filePath);
					attachPart.attachFile(filePath);
					attachPart.getFileName();
				} catch (IOException ex) {
					ex.printStackTrace();
				}

				multipart.addBodyPart(attachPart);
			}
		}

		// sets the multi-part as e-mail's content
		msg.setContent(multipart);

		// sends the e-mail
		Transport.send(msg);

	}
}