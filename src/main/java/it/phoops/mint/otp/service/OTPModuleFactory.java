package it.phoops.mint.otp.service;

import java.util.List;

import org.opentripplanner.graph_builder.model.GtfsBundle;
import org.opentripplanner.graph_builder.module.GtfsModule;
import org.opentripplanner.graph_builder.module.PruneFloatingIslands;
import org.opentripplanner.graph_builder.module.osm.DefaultWayPropertySetSource;
import org.opentripplanner.graph_builder.module.osm.OpenStreetMapModule;
import org.opentripplanner.graph_builder.services.DefaultStreetEdgeFactory;
import org.opentripplanner.routing.impl.DefaultFareServiceFactory;

public class OTPModuleFactory {

	/**
	 * Based on default parameters defined in
	 * org.opentripplanner.graph_builder.GraphBuilder.java and
	 * org.opentripplanner.standalone.GraphBuilderParameters.java
	 */
	public static OpenStreetMapModule createDefaultOSMModule() {
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
	public static PruneFloatingIslands createDefaultPruneFloatingIslands() {
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
	public static GtfsBundle createDefaultGtfsBundle() {
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
	public static GtfsModule createDefaultGtfsModule(List<GtfsBundle> gtfsBundles) {
		GtfsModule gtfsModule = new GtfsModule(gtfsBundles);
		gtfsModule.setFareServiceFactory(new DefaultFareServiceFactory());
		
		return gtfsModule;
	}

}
