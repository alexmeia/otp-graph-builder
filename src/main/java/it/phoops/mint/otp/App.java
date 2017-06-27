package it.phoops.mint.otp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.phoops.mint.otp.service.OTPGraphBuilder;


public class App {
	
	private static Logger log = LoggerFactory.getLogger(App.class);
	
    public static void main( String[] args ) {
    	      
        if (args.length != 1) {
			log.error("Wrong numbe of arguments:\nUsage: OtpGraphBuilder propertyFile");
			System.exit(8);
		}
		
		String propertiesPath = args[0];
		Properties properties = getPropertiesFile(propertiesPath);
				
		if (properties == null) {
			log.error("Invalid properties file.");
			System.exit(8);
		}
		
		OTPGraphBuilder otpGraphBuilder = new OTPGraphBuilder();
		System.exit(otpGraphBuilder.generateGraphObject(properties));
    }
    
    private static Properties getPropertiesFile(String filePath) {
		try {
			InputStream is = new FileInputStream(new File(filePath));
			Properties prop = null;
			prop = new Properties();
			prop.load(is);
			return prop;
		} catch (IOException e) {
			log.error("Error reading properties file: ", e);
			return null;
		}
	}
}
