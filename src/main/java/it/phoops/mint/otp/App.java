package it.phoops.mint.otp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.opentripplanner.graph_builder.model.GtfsBundle;
import org.opentripplanner.graph_builder.module.GtfsModule;
import org.opentripplanner.graph_builder.module.osm.DefaultWayPropertySetSource;
import org.opentripplanner.graph_builder.module.osm.OpenStreetMapModule;
import org.opentripplanner.openstreetmap.impl.AnyFileBasedOpenStreetMapProviderImpl;
import org.opentripplanner.routing.graph.Graph;

/**
 * Hello world!
 *
 */
public class App {
	
    public static void main( String[] args ) throws IOException {
    	
        System.out.println( "OTP Graph Builder started." );
        
        HashMap<Class<?>, Object> extra = new HashMap<Class<?>, Object>();
        
        Graph gg = new Graph();

        // OSM builder
        OpenStreetMapModule osmBuilder = new OpenStreetMapModule();
        osmBuilder.setDefaultWayPropertySetSource(new DefaultWayPropertySetSource());
        AnyFileBasedOpenStreetMapProviderImpl provider = new AnyFileBasedOpenStreetMapProviderImpl();
        URL osmUrl = new URL("http://geodati.fmach.it/gfoss_geodata/osm/output_osm_regioni/toscana.pbf"); 
        File osmTuscany = new File(System.getProperty("java.io.tmpdir") + File.separator + "toscana.pbf");
        osmTuscany.deleteOnExit();
        FileUtils.copyURLToFile(osmUrl, osmTuscany);
        
        provider.setPath(osmTuscany);
        osmBuilder.setProvider(provider);
        osmBuilder.buildGraph(gg, extra);
        
        // GTFS builder
        List<GtfsBundle> gtfsBundles = new ArrayList<GtfsBundle>();
        GtfsBundle gtfsBundle = new GtfsBundle();
        URL gftsUrl = new URL(" http://dati.toscana.it/dataset/8bb8f8fe-fe7d-41d0-90dc-49f2456180d1/resource/71303a3a-9859-415e-8478-9a354b726774/download/trenitalia.gtfs_31672987-5f43-45e1-b370-e5d20303f66a.zip"); 
        File gftsTest = new File(System.getProperty("java.io.tmpdir") + File.separator + "trenitalia.zip");
        gftsTest.deleteOnExit();
        FileUtils.copyURLToFile(gftsUrl, gftsTest);
        
        gtfsBundle.setPath(gftsTest);
        gtfsBundles.add(gtfsBundle);
        GtfsModule gtfsBuilder = new GtfsModule(gtfsBundles);
        
        gtfsBuilder.buildGraph(gg, extra);
        System.out.println(gg.countVertices());
        
        File graphOutput = new File(System.getProperty("java.io.tmpdir") + File.separator + "test.graph");
        gg.save(graphOutput);
    }
}
