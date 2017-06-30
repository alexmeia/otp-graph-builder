package it.phoops.mint.otp.service;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.phoops.mint.otp.model.CKANDataSet;

public class OpenDataServiceImpl implements OpenDataService {
	
	public CKANDataSet getCkanDataSet(String url) throws JsonParseException, JsonMappingException, IOException {
		
		Client client = ClientBuilder.newClient( new ClientConfig().register( LoggingFilter.class ) );
		WebTarget webTarget = client.target(url);
		 
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		if (response.getStatus() != 200) {
			throw new RuntimeException(String.format("The requested url %s returned a response code %d (%s)", url, response.getStatus(), response.readEntity(String.class)));
		}
		String dataSet = response.readEntity(String.class);
		
		ObjectMapper mapper = new ObjectMapper();
		CKANDataSet ckanDataSet = mapper.readValue(dataSet, CKANDataSet.class);
		return ckanDataSet;
		
	}

}
