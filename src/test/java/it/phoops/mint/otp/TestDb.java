package it.phoops.mint.otp;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.Date;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import it.phoops.mint.otp.dao.GraphBuilderDao;
import it.phoops.mint.otp.model.GraphProperties;
import it.phoops.mint.otp.util.DbUtils;

public class TestDb {
	
	private Properties props;
	
	@Before
	public void init() {
		props = new Properties();
		props.setProperty("db.driver", "org.postgresql.Driver");
		props.setProperty("db.environment", "dev");
		props.setProperty("db.dev.url", "jdbc:postgresql://localhost:5432/otp_development");
		props.setProperty("db.dev.user", "otp");
		props.setProperty("db.dev.password", "otp");
		
	}
	
	@Test
	public void testDbConnection() throws Exception {
		
		Connection connection = DbUtils.createConnection(props);
		assertFalse(connection.getAutoCommit());
		
	}
	
	@Test
	public void testSaveGraphProperties() throws Exception {
		
		GraphProperties gp = new GraphProperties();
		gp.setCreationDate(new Date());
		gp.setEdges(878978);
		gp.setVertices(8798798);
		gp.setTransitModes("BUS, RAIL, FERRY");
		gp.setAgencies(20);
		gp.setHasDirectTransfers(true);
		gp.setHasTranist(true);
		gp.setHasStreets(true);
		
		Connection connection = DbUtils.createConnection(props);
		GraphBuilderDao gbd = new GraphBuilderDao(connection);
		
		gbd.saveGraphProperties(gp);
		//connection.commit();
		connection.close();
		
	}

}
