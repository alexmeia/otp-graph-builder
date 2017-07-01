package it.phoops.mint.otp.model;

import java.util.Date;

import org.opentripplanner.routing.graph.Graph;

import it.phoops.mint.otp.util.TransitUtils;

public class GraphProperties {

	private int vertices;
	private int edges;
	private boolean hasStreets;
	private boolean hasTranist;
	private boolean hasDirectTransfers;
	private Date creationDate;
	private String transitModes;
	private int agencies;
	
	public GraphProperties() {
		
	}
	
	public GraphProperties(Graph graph) {
		this.creationDate = new Date();
		this.edges = graph.countEdges();
		this.vertices = graph.countVertices();
		this.transitModes = TransitUtils.convertTranistModesToString(graph.getTransitModes());
		this.agencies = graph.getFeedIds().size();
		this.hasDirectTransfers = graph.hasDirectTransfers;
		this.hasTranist = graph.hasTransit;
		this.hasStreets = graph.hasStreets;
	}
	
	public int getVertices() {
		return vertices;
	}
	public void setVertices(int vertices) {
		this.vertices = vertices;
	}
	public int getEdges() {
		return edges;
	}
	public void setEdges(int edges) {
		this.edges = edges;
	}
	public boolean isHasStreets() {
		return hasStreets;
	}
	public void setHasStreets(boolean hasStreets) {
		this.hasStreets = hasStreets;
	}
	public boolean isHasTranist() {
		return hasTranist;
	}
	public void setHasTranist(boolean hasTranist) {
		this.hasTranist = hasTranist;
	}
	public boolean isHasDirectTransfers() {
		return hasDirectTransfers;
	}
	public void setHasDirectTransfers(boolean hasDirectTransfers) {
		this.hasDirectTransfers = hasDirectTransfers;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getTransitModes() {
		return transitModes;
	}
	public void setTransitModes(String transitModes) {
		this.transitModes = transitModes;
	}
	public int getAgencies() {
		return agencies;
	}
	public void setAgencies(int agencies) {
		this.agencies = agencies;
	}
}
