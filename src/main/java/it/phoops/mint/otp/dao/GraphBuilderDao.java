package it.phoops.mint.otp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import it.phoops.mint.otp.model.GraphProperties;

public class GraphBuilderDao {
	
	private Connection connection;
	
	public GraphBuilderDao(Connection connection) {
		this.connection = connection;
	}
	
	public void saveGraphProperties(GraphProperties gp) throws Exception {
		
		String sql = " insert into otp_graph_builder.saved_graph "
				   + " (vertices, edges, has_streets, has_transit, creation_date, transit_modes, has_direct_transfers, agencies_size)"
				   + " values (?, ?, ?, ?, ?, ?, ?, ?)";
		
		PreparedStatement ps = null;
		
		int i = 0;
	
		ps = connection.prepareStatement(sql);
		ps.setInt(i++, gp.getVertices());
		ps.setInt(i++, gp.getEdges());
		ps.setBoolean(i++, gp.isHasStreets());
		ps.setBoolean(i++, gp.isHasTranist());
		ps.setTimestamp(i++, new Timestamp(gp.getCreationDate().getTime()));
		ps.setString(i++, gp.getTransitModes());
		ps.setBoolean(i++, gp.isHasDirectTransfers());
		ps.setInt(i++, gp.getAgencies());
		
		ps.executeUpdate();
		ps.close();
	}

}
