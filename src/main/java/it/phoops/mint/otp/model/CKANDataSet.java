package it.phoops.mint.otp.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CKANDataSet {
	
	private String name;
	private String title;
	private String id;
	private List<CKANResource> resources;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<CKANResource> getResources() {
		return resources;
	}
	public void setResources(List<CKANResource> resources) {
		this.resources = resources;
	}

}
