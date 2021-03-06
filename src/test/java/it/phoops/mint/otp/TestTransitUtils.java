package it.phoops.mint.otp;

import static org.junit.Assert.*;

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
	
	@Test
	public void testAreTransitModesEqual() {
		
		String actual = "TRAM, BUS, RAIL";
		String last = "BUS, RAIL, TRAM";
		assertTrue(TransitUtils.areTransitModesEqual(actual, last));
		
		actual = "";
		last = "";
		assertTrue(TransitUtils.areTransitModesEqual(actual, last));
		
		actual = "test, one, two";
		last = null;
		assertFalse(TransitUtils.areTransitModesEqual(actual, last));
		
		actual = "rail, bus, ferry";
		last = "bus, ferry, tram";
		assertFalse(TransitUtils.areTransitModesEqual(actual, last));
		
		actual = null;
		last = null;
		assertTrue(TransitUtils.areTransitModesEqual(actual, last));
		
	}

}
