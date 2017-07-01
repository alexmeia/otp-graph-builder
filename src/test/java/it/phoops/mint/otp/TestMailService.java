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
        props.put("mail.smtp.password", "set_password");
	}
	
	@Test
	public void testSendSimpleMail() throws EmailException  {
		
		MailService ms = new MailServiceImpl(props);
		String messageId = ms.sendMail(String.format("Test mail from %s", this.getClass()), "Test message.", false);		
		assertTrue(messageId != null);
		
	}
	
	@Test
	public void testSendHtmlMail() throws EmailException  {
		
		StringBuilder sb = new StringBuilder("");
		sb.append("<html><body><p>Test html mail</p>")
		.append("<ul>")
		.append("<li>first li element</li>")
		.append("<li>second li element</li>")
		.append("<li>third li element</li>")
		.append("</ul></body></html>");
		
		MailService ms = new MailServiceImpl(props);
		String messageId = ms.sendMail(String.format("Test html mail from %s", this.getClass()), sb.toString(), true);		
		assertTrue(messageId != null);
		
	}

}
