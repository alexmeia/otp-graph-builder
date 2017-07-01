package it.phoops.mint.otp.service;

import org.apache.commons.mail.EmailException;

import it.phoops.mint.otp.model.GraphProperties;

public interface MailService {

	public String sendMail(String subject, String message, boolean html) throws EmailException;
	
	public String buildHtmlReport(String header, GraphProperties actual, GraphProperties last);
	
}
