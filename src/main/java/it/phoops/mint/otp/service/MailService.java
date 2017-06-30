package it.phoops.mint.otp.service;

import java.util.Properties;

import org.apache.commons.mail.EmailException;

public interface MailService {

	public String sendMail(Properties props, String subject, String message) throws EmailException;
	
}
