package it.phoops.mint.otp.service;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
import it.phoops.mint.otp.util.TransitUtils;

public class OTPGraphBuilderImpl implements OTPGraphBuilder {
	
	private static Logger log = LoggerFactory.getLogger(OTPGraphBuilderImpl.class);
	
	private MailService mailService;
	private OpenDataService openDataService;
	private OTPModuleFactory otpModuleFactory;
	private Properties properties;
	private Connection connection;
	
	public OTPGraphBuilderImpl(Properties properties) {
		this.properties = properties;
		this.mailService = new MailServiceImpl(this.properties);
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
	@Override
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
	        
	        // 7. Embed configuration module (empty configuration files)
	        EmbedConfig embedConfig = new EmbedConfig(MissingNode.getInstance(), MissingNode.getInstance());
	        embedConfig.buildGraph(graph, extra);
	        
	        // Check graph integrity before saving
	        connection = DbUtils.createConnection(properties);
	        GraphBuilderDao graphBuilderDao = new GraphBuilderDao(connection);
	        
	        GraphProperties graphProperties = new GraphProperties(graph);
	        GraphProperties lastSavedProperties = graphBuilderDao.getLastSavedGraph();
	        
	        if (lastSavedProperties == null) {
	        	lastSavedProperties = graphProperties;
	        }
	        
	        log.info("Validating graph");
	        
	        if (isValidGraph(graphProperties, lastSavedProperties)) {
	        
		        File graphOutput = new File(properties.getProperty("graph.output.dir") + "Graph.obj");
		        graph.save(graphOutput);
		        
		        log.info("Graph saved in " + graphOutput.getAbsolutePath());
		        
		        graphBuilderDao.saveGraphProperties(graphProperties);
		        
		        String message = mailService.buildHtmlReport(
		        		String.format(Constants.MAIL_OK_MESSAGE_HEADER, graphOutput.getAbsolutePath()), 
		        		graphProperties, lastSavedProperties);
		        mailService.sendMail(Constants.MAIL_OK_SUBJECT, message, true);
		        log.info("Feedback mail sent.");
		        return 0;
		        
	        } else {
	        	log.error("Proprieties of generated graph didn't match the expected values. Graph not saved.");
	        	
	        	String message = mailService.buildHtmlReport(
		        		"The properties of the generated OTP graph didn't match the expected values. Graph was not saved.", 
		        		graphProperties, lastSavedProperties);
	        	mailService.sendMail("OTP graph was generated correctly but is not valid", message, true);
	        	
	        	log.info("Feedback mail sent.");
	        	return 8;
	        }
	        
    	} catch (Exception e) {
    		log.error(e.getMessage(), e);
    		String mailSubject = String.format("%s: %s",Constants.MAIL_ERROR_SUBJECT, e.getMessage());
    		String mailMessage = String.format(Constants.MAIL_ERROR_MESSAGE_HEADER, e.toString());
    		try {
				mailService.sendMail(mailSubject, mailMessage, false);
				log.info("Feedback mail sent.");
			} catch (EmailException ee) {
				log.error("Error in sending feedback mail message:", ee);
			}
    		return 8;
    		
    	} finally {
    		if (connection != null) {
    			try {
					connection.close();
				} catch (SQLException e) {
					log.error("Error in closing db connection:", e);
				}
    		}
    	}
	}
	
	// TODO: move to validator service class
	private boolean isValidGraph(GraphProperties actual, GraphProperties last) {
		
		int verticesMaxDelta = Integer.parseInt(properties.getProperty("vertices.max.delta", "10000"));
		int edgesMaxDelta = Integer.parseInt(properties.getProperty("edges.max.delta", "10000"));
		
		if (actual.getAgencies() < last.getAgencies()) {
			log.warn("The angencies size of the actual graph are lower than the agencies size of the last saved graph.");
			return false;
		}
		
		if (Math.abs(actual.getVertices() - last.getVertices()) > verticesMaxDelta) {
			log.warn("The vertces difference between the actual graph and the last saved graph is greater than maximum delta.");
			return false;
		}
		
		if (Math.abs(actual.getEdges() - last.getEdges()) > edgesMaxDelta) {
			log.warn("The edges difference between the actual graph and the last saved graph is greater than maximum delta.");
			return false;
		}
		
		if (!TransitUtils.areTransitModesEqual(actual.getTransitModes(), last.getTransitModes())) {
			log.warn("Actual graph transit modes and last saved graph tranisit modes don't match.");
			return false;
		}
		
		log.info("Graph is valid.");
		
		return true;
		
	}
	
}
