package com.lng.attendancecompanyservice.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.lng.attendancecompanyservice.entity.smsVendor.SMSVendor;
import com.lng.attendancecompanyservice.repositories.smsvendor.SMSVendorRepository;

public class MessageUtil {

	@Value("#('${sms.url}')")
	private String smsurl;

	@Autowired
	SMSVendorRepository smsVendorRepository;
	
	//Send SMS
	public String sms(String to, String message)
	{
		RestTemplate restTemplate = new RestTemplate();
		
		

		//  String messageBody="Your OTP for this registration is "+message;
		//message=messageBody;

		//SMSVendor smsVendor = smsVendorRepository.getAllBySmsVndrIsActive();
		
		String mobileNumbers = to;

		String textMessage = message;

		//final  String uri = smsVendor.getSmsVndrURL();
		
		// final  String uri = "http://promotional.mysmsbasket.com/V2/http-api.php?apikey=YE5ssFpB9306XlDP&senderid=LNGATS&number=mobileNumbers&message=textMessage&format=json";

		final  String uri = "http://onex-ultimo.in/api/pushsms?user=SerenityHostels&authkey=92TMhDdRCtDe6&sender=SERENT&mobile=mobileNumbers&text=textMessage&output=json";
		
		StringBuilder sb = new StringBuilder(uri);
		sb.replace(sb.indexOf("mobileNumbers"), sb.indexOf("mobileNumbers") + "mobileNumbers".length(), mobileNumbers);
		sb.replace(sb.indexOf("textMessage"), sb.indexOf("textMessage") + "textMessage".length(), textMessage);

		String result = restTemplate.getForObject(sb.toString(),String.class);

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

			msg.setFrom(new InternetAddress(userName, "Facetek Admin"));
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

		} catch(javax.mail.SendFailedException  mx) {
			StringBuilder errorSB = null;

			if(mx.getInvalidAddresses() != null) {
				errorSB = new StringBuilder();
				for(Address email: mx.getInvalidAddresses()) {
					errorSB.append(email.toString());
					errorSB.append(", ");
				}
				System.out.println("Invalid Address Found: "+ errorSB);
			}

			if(mx.getValidSentAddresses() != null) {
				errorSB = new StringBuilder();
				for(Address email: mx.getValidSentAddresses()) {
					errorSB.append(email.toString());
					errorSB.append(", ");
				}
				System.out.println("Email sent to valid address: "+ errorSB);
			}

			if(mx.getValidUnsentAddresses() != null) {
				errorSB = new StringBuilder();
				for(Address email: mx.getValidUnsentAddresses()) {
					errorSB.append(email.toString());
					errorSB.append(", ");
				}
				System.out.println("Email not sent to valid address: "+ errorSB);
			}

		} catch(javax.mail.MessagingException mx) {

			System.out.println(mx.getMessage());

		} catch (Exception ex) {

			System.out.println(ex.getMessage());

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
	
	/* public void sendMsg(String to, String message) {
		try {   
			Date mydate = new Date(System.currentTimeMillis());   
			String data = "";   data += "method=sendMessage";   
			data += "&userid=2000191058"; // your loginId 
			data += "&password=" +   
					URLEncoder.encode("W3lcome2@LNG", "UTF-8"); // your password   
			data += "&msg=" + URLEncoder.encode(message + mydate.toString(), "UTF-8");   
			data += "&send_to=" +   URLEncoder.encode(to, "UTF-8"); // a valid 10 digit phone no.   
			data += "&v=1.1" ;   data += "&msg_type=TEXT"; // Can by "FLASH" or   "UNICODE_TEXT" or “BINARY”   
			data += "&auth_scheme=PLAIN";   
			URL url = new URL("http://enterprise.smsgupshup.com/GatewayAPI/rest?" + data);   
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();   
			conn.setRequestMethod("GET");   
			conn.setDoOutput(true);   
			conn.setDoInput(true);   
			conn.setUseCaches(false);   
			conn.connect();   
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));   
			String line;   
			StringBuffer buffer = new StringBuffer(); 
			while ((line = rd.readLine()) != null)
			{   
				buffer.append(line).append("\n");   
			}   
			System.out.println(buffer.toString());   
			rd.close();   
			conn.disconnect();   
		}   
		catch(Exception e)
		{ 
			e.printStackTrace();   
		} 
	}*/
}