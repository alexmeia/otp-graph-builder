package it.phoops.mint.otp.service;

import java.util.Properties;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class MailService {
	
	final String username = "alessandro.meiattini@phoops.it";
    final String password = "vonnegut74";
    
    public void sendEmail(Properties properties, String subject, String message) throws EmailException {
    	
    	Email email = new SimpleEmail();
    	email.setHostName(properties.getProperty("mail.smtp.host"));
    	email.setSmtpPort(Integer.valueOf(properties.getProperty("mail.smtp.port")));
    	email.setAuthenticator(new DefaultAuthenticator(username, password));
    	email.setSSLOnConnect(true);
    	email.setFrom(properties.getProperty("mail.from"));
    	email.setSubject(subject);
    	email.setMsg(message);
    	email.addTo(properties.getProperty("mail.to"));
    	email.send();
    	
    }

}
