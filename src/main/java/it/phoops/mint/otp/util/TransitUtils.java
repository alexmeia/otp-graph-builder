package it.phoops.mint.otp.util;

import java.util.HashSet;
import java.util.Iterator;

import org.opentripplanner.routing.core.TraverseMode;

public class TransitUtils {
	
	public static String convertTranistModesToString(HashSet<TraverseMode> transitModes) {
		
		String transitModesStr = "";
		
		Iterator<TraverseMode> iterator = transitModes.iterator();
		while (iterator.hasNext()) {
			transitModesStr += iterator.next().name() + ", ";
		}
		
		return transitModesStr.substring(0, transitModesStr.lastIndexOf(","));
		
	}

}
