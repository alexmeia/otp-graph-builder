package it.phoops.mint.otp.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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

	public static boolean areTransitModesEqual(String actual, String last) {
		
		List<String> actualList = Arrays.asList(actual.split("[,\\s]+"));
		actualList.sort(String::compareToIgnoreCase);
		
		List<String> lastList = Arrays.asList(actual.split("[,\\s]+"));
		lastList.sort(String::compareToIgnoreCase);
		
		return actualList.equals(lastList);
	}

}
