package environment;

import java.util.HashMap;

import utils.Enums.Month;
import utils.XMLHandler;

public class SolarPowHandler {

	private static HashMap<Month, HashMap<Integer, Integer>> solar_pow;
	private static TimeHandler time_handler;
	
	public static void init() {
		if (solar_pow == null) {
			solar_pow = XMLHandler.readSolarPowXMLFile();
			 time_handler = TimeHandler.getInstance();

//			System.out.println(solar_pow.toString());
		}
	}
	
	public static Integer getCurrentSolarEnergy() {
		Month month = Month.getMonthByNumber(time_handler.getNumericMonth());
		
		int interval = time_handler.getCurrentInterval();
		
		if (solar_pow.get(month).get(interval) == null) {
			System.err.println("ERROR: Solar energy value not available for that date.");
			return 0;
		}
		
		return solar_pow.get(month).get(interval);
	}
	
}
