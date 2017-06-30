package it.phoops.mint.otp.service;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.EmailException;
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

import com.fasterxml.jackson.databind.node.MissingNode;

import it.phoops.mint.otp.dao.GraphBuilderDao;
import it.phoops.mint.otp.model.CKANDataSet;
import it.phoops.mint.otp.model.CKANResource;
import it.phoops.mint.otp.model.GraphProperties;
import it.phoops.mint.otp.util.Constants;
import it.phoops.mint.otp.util.DbUtils;

public class OTPGraphBuilderImpl implements OTPGraphBuilder {
	
	private static Logger log = LoggerFactory.getLogger(OTPGraphBuilderImpl.class);
	
	private MailService mailService;
	private OpenDataService openDataService;
	private OTPModuleFactory otpModuleFactory;
	private Properties properties;
	
	public OTPGraphBuilderImpl(Properties properties) {
		this.properties = properties;
		this.mailService = new MailServiceImpl();
		this.openDataService = new OpenDataServiceImpl();
		this.otpModuleFactory = new OTPModuleFactoryImpl(); 
	}

	/**
	 * @param properties file
	 * @return batch exit code
	 * 
	 * Generates an Open Trip Planner graph object by creating modules and building
	 * the graph with each module. Modules order is not arbitrary. Is important that OSM
	 * module is executed first and DirectTransferGenerator is executed last (except for
	 * EmbedConfig module, whose utility needs to be investigated).
	 * 
	 */
	public int generateGraphObject() {
		
		log.info( "OTP Graph Builder service started." );
		
		HashMap<Class<?>, Object> extra = new HashMap<Class<?>, Object>();
        Graph graph = new Graph();
        
    	try {
        
	        // 1. OSM module
	        OpenStreetMapModule osmModule = otpModuleFactory.createDefaultOSMModule();
	     
	        AnyFileBasedOpenStreetMapProviderImpl provider = new AnyFileBasedOpenStreetMapProviderImpl();
	        URL osmUrl = new URL(properties.getProperty("osm.data.url"));
	        File osmTuscany = new File(properties.getProperty("tmp.dir") + "toscana.pbf");
	        osmTuscany.deleteOnExit();
	        
			FileUtils.copyURLToFile(osmUrl, osmTuscany);
			
	        provider.setPath(osmTuscany);
	        osmModule.setProvider(provider);
	        osmModule.checkInputs();
            osmModule.buildGraph(graph, extra);
            
            // 2. Prune floating islands module
	        PruneFloatingIslands pruneFloatingIslands = otpModuleFactory.createDefaultPruneFloatingIslands();
	        pruneFloatingIslands.buildGraph(graph, extra);
	        
	        // 3. GTFS module
	        List<GtfsBundle> gtfsBundles = new ArrayList<GtfsBundle>();
	        CKANDataSet gftsDataSet = openDataService.getCkanDataSet(properties.getProperty("gtfs.dataset.rest.url"));
			
	        for (CKANResource gtfsResource : gftsDataSet.getResources()) {
	        	
	        	GtfsBundle gtfsBundle = otpModuleFactory.createDefaultGtfsBundle();
	        	
	 	        URL gtfsUrl = new URL(gtfsResource.getUrl()); 
	 	        File gtfsFile = new File(properties.getProperty("tmp.dir") + gtfsResource.getName());
	 	        log.info(String.format("Saving file %s.", gtfsFile.getAbsolutePath()));
	 	        gtfsFile.deleteOnExit();
	 	        FileUtils.copyURLToFile(gtfsUrl, gtfsFile);
	 	        gtfsBundle.setPath(gtfsFile);
	 	        gtfsBundle.checkInputs();
	 	        gtfsBundles.add(gtfsBundle);
	        }
	       
	        GtfsModule gtfsModule = otpModuleFactory.createDefaultGtfsModule(gtfsBundles);
	        gtfsModule.checkInputs();
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
	        
	        // Check graph integrity before saving
	        
	        File graphOutput = new File(properties.getProperty("graph.output.dir") + "Graph.obj");
	        graph.save(graphOutput);
	        
	        mailService.sendMail(properties, Constants.MAIL_OK_SUBJECT, String.format(Constants.MAIL_OK_MESSAGE_HEADER, graphOutput.getAbsolutePath()));
	        saveGraphProperties(graph, properties);
	        
	        return 0;
	        
    	} catch (Exception e) {
    		log.error(e.getMessage(), e);
    		String mailSubject = String.format("%s: %s",Constants.MAIL_ERROR_SUBJECT, e.getMessage());
    		String mailMessage = String.format(Constants.MAIL_ERROR_MESSAGE_HEADER, e.toString());
    		try {
				mailService.sendMail(properties, mailSubject, mailMessage);
			} catch (EmailException ee) {
				log.error("Error in sending feedback mail message:", ee);
			}
    		return 8;
    	} 	
	}
	
	private void saveGraphProperties(Graph graph, Properties properties) throws Exception {
		
		GraphProperties gp = new GraphProperties(); //TODO: pass graph in constructor
		gp.setCreationDate(new Date());
		gp.setEdges(graph.countEdges());
		gp.setVertices(graph.countVertices());
		gp.setTransitModes(graph.getTransitModes().toArray().toString());
		gp.setAgencies(graph.getFeedIds().size());
		gp.setHasDirectTransfers(graph.hasDirectTransfers);
		gp.setHasTranist(graph.hasTransit);
		gp.setHasStreets(graph.hasStreets);
		
		Connection conn = DbUtils.createConnection(properties);
		
		GraphBuilderDao graphBuilderDao = new GraphBuilderDao(conn);
		graphBuilderDao.saveGraphProperties(gp);
		
		conn.close();
		
	}
	
}
