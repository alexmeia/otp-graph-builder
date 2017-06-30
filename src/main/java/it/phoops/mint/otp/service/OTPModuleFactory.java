package it.phoops.mint.otp.service;

import java.util.List;
import java.util.Properties;

import org.opentripplanner.graph_builder.model.GtfsBundle;
import org.opentripplanner.graph_builder.module.GtfsModule;
import org.opentripplanner.graph_builder.module.PruneFloatingIslands;
import org.opentripplanner.graph_builder.module.osm.OpenStreetMapModule;

public interface OTPModuleFactory {
	
	public GtfsBundle createDefaultGtfsBundle();
	public GtfsBundle createCustomGtfsBundle(Properties props);
	
	public GtfsModule createDefaultGtfsModule(List<GtfsBundle> gtfsBundles);
	
	public OpenStreetMapModule createDefaultOSMModule();
	public OpenStreetMapModule createCustomOSMModule(Properties properties);
	
	public PruneFloatingIslands createDefaultPruneFloatingIslands();
	public PruneFloatingIslands createCustomPruneFloatingIslands(Properties properties);

}
