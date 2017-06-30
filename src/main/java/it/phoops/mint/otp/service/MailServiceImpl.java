package it.phoops.mint.otp.service;

import java.util.Properties;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class MailServiceImpl implements MailService {

	public String sendMail(Properties properties, String subject, String message) throws EmailException {

		Email email = new SimpleEmail();
		email.setHostName(properties.getProperty("mail.smtp.host"));
		email.setSmtpPort(Integer.valueOf(properties.getProperty("mail.smtp.port")));
		email.setAuthenticator(new DefaultAuthenticator(properties.getProperty("mail.smtp.username"),
				properties.getProperty("mail.smtp.password")));
		email.setSSLOnConnect(true);
		email.setFrom(properties.getProperty("mail.from"));
		email.setSubject(subject);
		email.setMsg(message);
		email.addTo(properties.getProperty("mail.to"));
		
		return email.send();

	}

}
