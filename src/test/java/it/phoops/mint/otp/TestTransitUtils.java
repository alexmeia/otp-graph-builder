package it.phoops.mint.otp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.opentripplanner.routing.core.TraverseMode;

import it.phoops.mint.otp.util.TransitUtils;

public class TestTransitUtils {
	
	private HashSet<TraverseMode> transitModes;
	
	@Before
	public void init() {
		transitModes = new HashSet<TraverseMode>();
		transitModes.add(TraverseMode.BUS);
		transitModes.add(TraverseMode.TRAM);
		transitModes.add(TraverseMode.RAIL);
	}
	
	@Test
	public void testTransitModesToString() {
		String transitModeStr = TransitUtils.convertTranistModesToString(transitModes);
		assertEquals("BUS, TRAM, RAIL".length(), transitModeStr.length());
		assertTrue(transitModeStr.contains("BUS"));
		assertTrue(transitModeStr.contains("TRAM"));
		assertTrue(transitModeStr.contains("RAIL"));
	}

}
