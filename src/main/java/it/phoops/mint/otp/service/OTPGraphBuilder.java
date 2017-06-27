package it.phoops.mint.otp.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.opentripplanner.graph_builder.model.GtfsBundle;
import org.opentripplanner.graph_builder.module.DirectTransferGenerator;
import org.opentripplanner.graph_builder.module.EmbedConfig;
import org.opentripplanner.graph_builder.module.GtfsModule;
import org.opentripplanner.graph_builder.module.PruneFloatingIslands;
import org.opentripplanner.graph_builder.module.StreetLinkerModule;
import org.opentripplanner.graph_builder.module.TransitToTaggedStopsModule;
import org.opentripplanner.graph_builder.module.osm.OpenStreetMapModule;
import org.opentripplanner.openstreetmap.impl.AnyFileBasedOpenStreetMapProviderImpl;
import org.opentripplanner.routing.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.node.MissingNode;

import it.phoops.mint.otp.model.CKANDataSet;
import it.phoops.mint.otp.model.CKANResource;

public class OTPGraphBuilder {
	
	private static Logger log = LoggerFactory.getLogger(OTPGraphBuilder.class);

	/**
	 * @param properties file
	 * @return batch exit code
	 * 
	 * Generates an Open Trip Planner graph object by creating modules and building
	 * the graph with each module. Modules order is not arbitrary. Is important that OSM
	 * module is executed first and DirectTransferGenerator is executed last (except for
	 * EmbedConfig module, whose utility needs to be investigated.
	 * 
	 */
	public int generateGraphObject(Properties properties) {
		
		log.info( "OTP Graph Builder service started." );
		
		HashMap<Class<?>, Object> extra = new HashMap<Class<?>, Object>();
        Graph graph = new Graph();
        
    	try {
        
	        // 1. OSM module
	        OpenStreetMapModule osmModule = OTPModuleFactory.createDefaultOSMModule();
	     
	        AnyFileBasedOpenStreetMapProviderImpl provider = new AnyFileBasedOpenStreetMapProviderImpl();
	        URL osmUrl = new URL(properties.getProperty("osm.data.url"));
	        File osmTuscany = new File(properties.getProperty("tmp.dir") + "toscana.pbf");
	        osmTuscany.deleteOnExit();
	        
			FileUtils.copyURLToFile(osmUrl, osmTuscany);
			
	        provider.setPath(osmTuscany);
	        osmModule.setProvider(provider);
	    
            osmModule.buildGraph(graph, extra);
            
            // 2. Prune floating islands module
	        PruneFloatingIslands pruneFloatingIslands = OTPModuleFactory.createDefaultPruneFloatingIslands();
	        pruneFloatingIslands.buildGraph(graph, extra);
	        
	        // 3. GTFS module
	        List<GtfsBundle> gtfsBundles = new ArrayList<GtfsBundle>();
	        OpenDataService openDataService = new OpenDataService();
	        
	        CKANDataSet gftsDataSet = openDataService.getCkanDataSet(properties.getProperty("gtfs.dataset.rest.url"));
			
	        for (CKANResource gtfsResource : gftsDataSet.getResources()) {
	        	
	        	GtfsBundle gtfsBundle = OTPModuleFactory.createDefaultGtfsBundle();
	        	
	 	        URL gtfsUrl = new URL(gtfsResource.getUrl()); 
	 	        File gtfsFile = new File(properties.getProperty("tmp.dir") + gtfsResource.getName());
	 	        log.info(String.format("Saving file %s.", gtfsFile.getAbsolutePath()));
	 	        gtfsFile.deleteOnExit();
	 	        FileUtils.copyURLToFile(gtfsUrl, gtfsFile);
	 	        gtfsBundle.setPath(gtfsFile);
	 	        
	 	        gtfsBundles.add(gtfsBundle);
	        }
	       
	        GtfsModule gtfsModule = OTPModuleFactory.createDefaultGtfsModule(gtfsBundles);
	        gtfsModule.buildGraph(graph, extra);
	        
	        // 4. Transit to tagged stops module
	        TransitToTaggedStopsModule transitToTaggedStops = new TransitToTaggedStopsModule();
	        transitToTaggedStops.buildGraph(graph, extra);
	        
	        // 5. Street Linker module
	        StreetLinkerModule streetLinker = new StreetLinkerModule();
	        streetLinker.buildGraph(graph, extra);
	        
	        // 6. Direct transfer generator module
	        DirectTransferGenerator directTransferGenerator = new DirectTransferGenerator();
	        directTransferGenerator.buildGraph(graph, extra);
	        
	        // 7. Embed config module (empty configuration files)
	        EmbedConfig embedConfig = new EmbedConfig(MissingNode.getInstance(), MissingNode.getInstance());
	        embedConfig.buildGraph(graph, extra);
	        
	        File graphOutput = new File(properties.getProperty("graph.output.dir") + "Graph.obj");
	        graph.save(graphOutput);
	        
	        return 0;
	        
    	} catch (MalformedURLException mue) {
    		log.error("Error in retrieving data from URL:", mue);
    		return 8;
    	} catch (JsonParseException jpe) {
    		log.error("Error in parsing json from OpenData RT:", jpe);
    		return 8;
    	} catch (JsonMappingException jme) {
    		log.error("Error in mapping json from OpenData RT:", jme);
    		return 8;
    	} catch (IOException e) {
    		log.error("Error in reding file:", e);
			return 8;
		}
	
	}
	
}
