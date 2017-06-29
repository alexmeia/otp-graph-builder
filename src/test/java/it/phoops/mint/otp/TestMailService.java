package it.phoops.mint.otp;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.phoops.mint.otp.model.CKANDataSet;

public class TestMailService {
	
	private Properties props;
	
	@Before
	public void init() {
		props = new Properties();
        props.put("mail.from", "alessandro.meiattini@phoops.it");
        props.put("mail.to", "alessandro.meiattini@phoops.it");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
	}
	
	@Test
	public void testMailService() throws EmailException  {
		
		Email email = new SimpleEmail();
    	email.setHostName(props.getProperty("mail.smtp.host"));
    	email.setSmtpPort(Integer.valueOf(props.getProperty("mail.smtp.port")));
    	email.setAuthenticator(new DefaultAuthenticator("alessandro.meiattini@phoops.it", "vonnegut74"));
    	email.setSSLOnConnect(true);
    	email.setFrom(props.getProperty("mail.from"));
    	email.setSubject("Test mail");
    	email.setMsg("Test message.");
    	email.addTo(props.getProperty("mail.to"));
    	String messageId = email.send();
		
		assertTrue(messageId != null);
		
	}

}
