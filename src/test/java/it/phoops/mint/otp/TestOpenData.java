package it.phoops.mint.otp;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.phoops.mint.otp.model.CKANDataSet;

public class TestOpenData {
	
	@Test
	public void testGetCkanDataSet() throws JsonParseException, JsonMappingException, IOException {
		
		String url ="http://dati.toscana.it/api/rest/dataset/rt-oraritb";
		Client client = ClientBuilder.newClient( new ClientConfig().register( LoggingFilter.class ) );
		WebTarget webTarget = client.target(url);
		
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		
		String dataSet = response.readEntity(String.class);
		ObjectMapper mapper = new ObjectMapper();
		CKANDataSet ckanDataSet = mapper.readValue(dataSet, CKANDataSet.class);
		
		assertFalse(ckanDataSet.getResources().isEmpty());
		
	}

}
