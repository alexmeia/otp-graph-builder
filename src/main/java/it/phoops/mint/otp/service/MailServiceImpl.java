package it.phoops.mint.otp.service;

import java.util.Properties;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

import it.phoops.mint.otp.model.GraphProperties;

public class MailServiceImpl implements MailService {
	
	private Properties properties;
	
	public MailServiceImpl(Properties properties) {
		this.properties = properties;
	}

	public String sendMail(String subject, String message, boolean html) throws EmailException {
		
		Email email = html ? new HtmlEmail() : new SimpleEmail();
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
	
	public String buildHtmlReport(String header, GraphProperties actual, GraphProperties last) {
		
		StringBuilder sb = new StringBuilder("");
		sb.append("<html><body><p>")
		.append(header)
		.append("</p>")
		.append("<table style=\"border: solid 1px #CCC; border-collapse: collapse; font-family: monospace;\">")
		.append("<tr><th></th><th>Actual graph</th><th>Last saved graph<th></tr>")
		.append("<tr><th>Agencies</th>")
		.append(String.format("<td>%s</td>", actual.getAgencies()))
		.append(String.format("<td>%s</td>", last.getAgencies()))
		.append("</tr>")
		.append("<tr><th>Edges</th>")
		.append(String.format("<td>%s</td>", actual.getEdges()))
		.append(String.format("<td>%s</td>", last.getEdges()))
		.append("</tr>")
		.append("<tr><th>Vertices</th>")
		.append(String.format("<td>%s</td>", actual.getVertices()))
		.append(String.format("<td>%s</td>", last.getVertices()))
		.append("</tr>")
		.append("<tr><th>Transit modes</th>")
		.append(String.format("<td>%s</td>", actual.getTransitModes()))
		.append(String.format("<td>%s</td>", last.getTransitModes()))
		.append("</tr>")
		.append("</table></body></html>");
		
		return sb.toString();
	}

}
