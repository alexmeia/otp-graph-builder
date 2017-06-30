package it.phoops.mint.otp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbUtils {
	
	/**
	 * @param properties file
	 * @return Connection object
	 * @throws Exception
	 */
	public static Connection createConnection(Properties properties) throws Exception {
		Connection connection = null;
		try {			
			String driverClass = properties.getProperty("db.driver");
			String env = properties.getProperty("db.environment");
			String url = properties.getProperty("db." + env + ".url");
			String usr = properties.getProperty("db." + env + ".user");
			String pwd = properties.getProperty("db." + env + ".pwd");

			Class.forName(driverClass);
			connection = DriverManager.getConnection(url, usr, pwd);
			connection.setAutoCommit(false);
		} catch (ClassNotFoundException e) {			
			throw e;
		} catch (SQLException e) {
			throw e;
		}
		return connection;
	}

}
