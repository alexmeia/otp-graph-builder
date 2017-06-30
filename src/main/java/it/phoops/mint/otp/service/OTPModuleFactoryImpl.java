package it.phoops.mint.otp.service;

import java.util.List;
import java.util.Properties;

import org.opentripplanner.graph_builder.model.GtfsBundle;
import org.opentripplanner.graph_builder.module.GtfsModule;
import org.opentripplanner.graph_builder.module.PruneFloatingIslands;
import org.opentripplanner.graph_builder.module.osm.DefaultWayPropertySetSource;
import org.opentripplanner.graph_builder.module.osm.OpenStreetMapModule;
import org.opentripplanner.graph_builder.services.DefaultStreetEdgeFactory;
import org.opentripplanner.routing.impl.DefaultFareServiceFactory;

public class OTPModuleFactoryImpl implements OTPModuleFactory {

	/**
	 * Based on default parameters defined in
	 * org.opentripplanner.graph_builder.GraphBuilder.java and
	 * org.opentripplanner.standalone.GraphBuilderParameters.java
	 */
	public OpenStreetMapModule createDefaultOSMModule() {
		OpenStreetMapModule osmModule = new OpenStreetMapModule();
		osmModule.setDefaultWayPropertySetSource(new DefaultWayPropertySetSource());

		DefaultStreetEdgeFactory streetEdgeFactory = new DefaultStreetEdgeFactory();
		streetEdgeFactory.useElevationData = false;
		osmModule.edgeFactory = streetEdgeFactory;

		osmModule.customNamer = null;

		DefaultWayPropertySetSource defaultWayPropertySetSource = new DefaultWayPropertySetSource();
		osmModule.setDefaultWayPropertySetSource(defaultWayPropertySetSource);

		osmModule.skipVisibility = true;
		osmModule.staticBikeRental = false;
		osmModule.staticBikeParkAndRide = false;
		osmModule.staticParkAndRide = true;

		return osmModule;
	}

	/**
	 * Based on default parameters defined in
	 * org.opentripplanner.graph_builder.GraphBuilder.java and
	 * org.opentripplanner.standalone.GraphBuilderParameters.java
	 */
	public PruneFloatingIslands createDefaultPruneFloatingIslands() {
		PruneFloatingIslands pruneFloatingIslands = new PruneFloatingIslands();
		pruneFloatingIslands.setPruningThresholdIslandWithoutStops(40); // TODO: what does it means? 
		pruneFloatingIslands.setPruningThresholdIslandWithStops(40);
		
		return pruneFloatingIslands;
	}
	
	/**
	 * Based on default parameters defined in
	 * org.opentripplanner.graph_builder.GraphBuilder.java and
	 * org.opentripplanner.standalone.GraphBuilderParameters.java
	 */
	public GtfsBundle createDefaultGtfsBundle() {
		GtfsBundle gtfsBundle = new GtfsBundle();
		gtfsBundle.parentStationTransfers = true;
        gtfsBundle.maxInterlineDistance = 200;
        gtfsBundle.linkStopsToParentStations = true;
        gtfsBundle.setTransfersTxtDefinesStationPaths(true);
        
        return gtfsBundle;
	}
	
	/**
	 * Based on default parameters defined in
	 * org.opentripplanner.graph_builder.GraphBuilder.java and
	 * org.opentripplanner.standalone.GraphBuilderParameters.java
	 */
	public GtfsModule createDefaultGtfsModule(List<GtfsBundle> gtfsBundles) {
		GtfsModule gtfsModule = new GtfsModule(gtfsBundles);
		gtfsModule.setFareServiceFactory(new DefaultFareServiceFactory());
		
		return gtfsModule;
	}

	public GtfsBundle createCustomGtfsBundle(Properties props) {
		// TODO Auto-generated method stub
		// Define in properties file values to be read and build
		// GtfsBundle based on those values.
		return null;
	}

	public OpenStreetMapModule createCustomOSMModule(Properties properties) {
		// TODO Auto-generated method stub
		// Define in properties file values to be read and build
		// OpenStreetMapModule based on those values.
		return null;
	}

	public PruneFloatingIslands createCustomPruneFloatingIslands(Properties properties) {
		// TODO Auto-generated method stub
		// Define in properties file values to be read and build
		// PruneFloatingIslands based on those values.
		return null;
	}

}
