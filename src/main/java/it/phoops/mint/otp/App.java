package it.phoops.mint.otp;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.opentripplanner.graph_builder.model.GtfsBundle;
import org.opentripplanner.graph_builder.module.GtfsModule;
import org.opentripplanner.graph_builder.module.PruneFloatingIslands;
import org.opentripplanner.graph_builder.module.StreetLinkerModule;
import org.opentripplanner.graph_builder.module.TransitToTaggedStopsModule;
import org.opentripplanner.graph_builder.module.osm.DefaultWayPropertySetSource;
import org.opentripplanner.graph_builder.module.osm.OpenStreetMapModule;
import org.opentripplanner.graph_builder.services.DefaultStreetEdgeFactory;
import org.opentripplanner.openstreetmap.impl.AnyFileBasedOpenStreetMapProviderImpl;
import org.opentripplanner.routing.graph.Graph;

import it.phoops.mint.otp.model.CKANDataSet;
import it.phoops.mint.otp.model.CKANResource;
import it.phoops.mint.otp.service.OpenDataService;

/**
 * Hello world!
 *
 */
public class App {
	
    public static void main( String[] args ) {
    	
        System.out.println( "OTP Graph Builder started." );
        
        HashMap<Class<?>, Object> extra = new HashMap<Class<?>, Object>();
        
        Graph graph = new Graph();
        
    	try {
        
	        // Shapefile Street Builder
	
	        // OSM builder
	        OpenStreetMapModule osmModule = new OpenStreetMapModule();
	        osmModule.setDefaultWayPropertySetSource(new DefaultWayPropertySetSource());
	        AnyFileBasedOpenStreetMapProviderImpl provider = new AnyFileBasedOpenStreetMapProviderImpl();
	        
	        //TODO: read url from properties file
	        URL osmUrl = new URL("http://geodati.fmach.it/gfoss_geodata/osm/output_osm_regioni/toscana.pbf");
	        File osmTuscany = new File(System.getProperty("java.io.tmpdir") + File.separator + "toscana.pbf");
	        osmTuscany.deleteOnExit();
			FileUtils.copyURLToFile(osmUrl, osmTuscany);
	        provider.setPath(osmTuscany);
	        osmModule.setProvider(provider);
	        
	        DefaultStreetEdgeFactory streetEdgeFactory = new DefaultStreetEdgeFactory();
            streetEdgeFactory.useElevationData = false;
            osmModule.edgeFactory = streetEdgeFactory;
            //osmModule.customNamer = 
            DefaultWayPropertySetSource defaultWayPropertySetSource = new DefaultWayPropertySetSource();
            osmModule.setDefaultWayPropertySetSource(defaultWayPropertySetSource);
            osmModule.skipVisibility = false;
            osmModule.staticBikeRental = true;
            osmModule.staticBikeParkAndRide = true;
            osmModule.staticParkAndRide = true;
	    
            osmModule.buildGraph(graph, extra);
	        
	        // GTFS builder
	        // 1. Get list of GTFS files form OpenData RT platform.
	        //TODO: read url from properties file
	        List<GtfsBundle> gtfsBundles = new ArrayList<GtfsBundle>();
	        OpenDataService openDataService = new OpenDataService();
	        String gftsDataSetUrl = "http://dati.toscana.it/api/rest/dataset/rt-oraritb";
	        
	        CKANDataSet gftsDataSet = openDataService.getCkanDataSet(gftsDataSetUrl);
			
	        for (CKANResource gtfsResource : gftsDataSet.getResources()) {
	        	GtfsBundle gtfsBundle = new GtfsBundle();
	 	        URL gtfsUrl = new URL(gtfsResource.getUrl()); 
	 	        File gtfsFile = new File(System.getProperty("java.io.tmpdir") + File.separator + gtfsResource.getName());
	 	        System.out.println("Saving file " + gtfsFile.getAbsolutePath());
	 	        gtfsFile.deleteOnExit();
	 	        FileUtils.copyURLToFile(gtfsUrl, gtfsFile);
	 	        gtfsBundle.setPath(gtfsFile);
	 	        gtfsBundle.parentStationTransfers = true;
	 	        gtfsBundle.linkStopsToParentStations = true;
	 	        gtfsBundle.setTransfersTxtDefinesStationPaths(false);
	 	        gtfsBundle.maxInterlineDistance = 200;
	 	        gtfsBundles.add(gtfsBundle);
	        }
	       
	        GtfsModule gtfsBuilder = new GtfsModule(gtfsBundles);
	        gtfsBuilder.buildGraph(graph, extra);
	        
	        // Street Linker
	        StreetLinkerModule streetLinker = new StreetLinkerModule();
	        streetLinker.buildGraph(graph, extra);
	        
	        // Transit to tagged stops
	        TransitToTaggedStopsModule transitToTaggedStops = new TransitToTaggedStopsModule();
	        transitToTaggedStops.buildGraph(graph, extra);
	        
	        // Prune floating islands
	        PruneFloatingIslands pruneFloatingIslands = new PruneFloatingIslands();
	        pruneFloatingIslands.setPruningThresholdIslandWithoutStops(20); //TODO: capire cosa significa
	        pruneFloatingIslands.setPruningThresholdIslandWithStops(20);
	        pruneFloatingIslands.buildGraph(graph, extra);
	        
	        File graphOutput = new File("/Users/ale/otp/graphs/toscana/" + "Graph.obj");
	
	        graph.save(graphOutput);
	        
    	} catch (MalformedURLException mue) {
    		mue.printStackTrace();
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
