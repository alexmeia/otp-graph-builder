package it.phoops.mint.otp;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.apache.commons.mail.EmailException;
import org.junit.Before;
import org.junit.Test;

import it.phoops.mint.otp.service.MailService;
import it.phoops.mint.otp.service.MailServiceImpl;

public class TestMailService {
	
	private Properties props;
	
	@Before
	public void init() {
		props = new Properties();
        props.put("mail.from", "alessandro.meiattini@phoops.it");
        props.put("mail.to", "alessandro.meiattini@phoops.it");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.username", "alessandro.meiattini@phoops.it");
        props.put("mail.smtp.password", "0");
	}
	
	@Test
	public void testMailService() throws EmailException  {
		
		MailService ms = new MailServiceImpl();
		String messageId = ms.sendMail(props, String.format("Test mail from %s", this.getClass()), "Test message.");		
		assertTrue(messageId != null);
		
	}

}
