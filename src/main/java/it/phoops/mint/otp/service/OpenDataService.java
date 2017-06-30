package it.phoops.mint.otp.service;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import it.phoops.mint.otp.model.CKANDataSet;

public interface OpenDataService {
	
	public CKANDataSet getCkanDataSet(String url) throws JsonParseException, JsonMappingException, IOException;
	
}
