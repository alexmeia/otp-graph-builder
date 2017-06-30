package it.phoops.mint.otp;

import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import it.phoops.mint.otp.model.CKANDataSet;
import it.phoops.mint.otp.service.OpenDataService;
import it.phoops.mint.otp.service.OpenDataServiceImpl;

public class TestOpenDataService {
	
	private OpenDataService openDataService;
	
	@Before
	public void init() {
		openDataService = new OpenDataServiceImpl();
	}
	
	@Test
	public void testGetCkanDataSet() throws JsonParseException, JsonMappingException, IOException {
		
		String url ="http://dati.toscana.it/api/rest/dataset/rt-oraritb";
		CKANDataSet ckanDataSet = openDataService.getCkanDataSet(url);
		
		assertFalse(ckanDataSet.getResources().isEmpty());
	}

}
