package utils;

public class Settings {

	private static Settings instance;

	// time interval in minutes
	private static Integer interval_length;
	private static Integer max_power_consumption;
	private static Integer min_temperature, max_temperature;
	private static Integer enel_energy;
	private static Integer max_solar_energy;
	private static Integer stats_interval; // in days
	private static Integer rooms_height; // in meters
	private static Integer min_energy;
	private static boolean log;
	private static boolean people_simulation;

	private Settings() {
		XMLHandler.readSettingsFile();
		// printSettings();

		// Initialize values, only if the settings.xml file doesn't exist.
		// interval_length = 30;
		// max_power_consumption = 2000;
		// min_temperature = 16;
		// max_temperature = 30;
		// enel_energy = 3000;
		// max_solar_energy = 5000;
		// stats_interval = 30;
		// rooms_height = 3;
		// xml_handler.createSettingsXMLFile();
//		min_energy = 200;
//		log = true;
//		people_simulation = true;
	}

	public static void init() {
		if (instance == null)
			instance = new Settings();
	}

	/**
	 * @return the interval_length
	 */
	public static Integer getInterval_length() {
		return interval_length;
	}

	/**
	 * @param interval_length
	 *            the interval_length to set
	 */
	public static void setInterval_length(Integer interval_length) {
		Settings.interval_length = interval_length;
	}

	/**
	 * @return the max_power_consumption
	 */
	public static Integer getMax_power_consumption() {
		return max_power_consumption;
	}

	/**
	 * @param max_power_consumption
	 *            the max_power_consumption to set
	 */
	public static void setMax_power_consumption(Integer max_power_consumption) {
		Settings.max_power_consumption = max_power_consumption;
	}

	/**
	 * @return the min_temperature
	 */
	public static Integer getMin_temperature() {
		return min_temperature;
	}

	/**
	 * @param min_temperature
	 *            the min_temperature to set
	 */
	public static void setMin_temperature(Integer min_temperature) {
		Settings.min_temperature = min_temperature;
	}

	/**
	 * @return the max_temperature
	 */
	public static Integer getMax_temperature() {
		return max_temperature;
	}

	/**
	 * @param max_temperature
	 *            the max_temperature to set
	 */
	public static void setMax_temperature(Integer max_temperature) {
		Settings.max_temperature = max_temperature;
	}

	/**
	 * @return the enel_energy
	 */
	public static Integer getEnel_energy() {
		return enel_energy;
	}

	/**
	 * @param enel_energy
	 *            the enel_energy to set
	 */
	public static void setEnel_energy(Integer enel_energy) {
		Settings.enel_energy = enel_energy;
	}

	/**
	 * @return the max_solar_energy
	 */
	public static Integer getMax_solar_energy() {
		return max_solar_energy;
	}

	/**
	 * @param max_solar_energy
	 *            the max_solar_energy to set
	 */
	public static void setMax_solar_energy(Integer max_solar_energy) {
		Settings.max_solar_energy = max_solar_energy;
	}

	/**
	 * @return the stats_interval
	 */
	public static Integer getStats_interval() {
		return stats_interval;
	}

	/**
	 * @param stats_interval
	 *            the stats_interval to set
	 */
	public static void setStats_interval(Integer stats_interval) {
		Settings.stats_interval = stats_interval;
	}

	/**
	 * @return the rooms_height
	 */
	public static Integer getRooms_height() {
		return rooms_height;
	}

	/**
	 * @param rooms_height
	 *            the rooms_height to set
	 */
	public static void setRooms_height(Integer rooms_height) {
		Settings.rooms_height = rooms_height;
	}

	/**
	 * @return the min_energy
	 */
	public static Integer getMin_energy() {
		return min_energy;
	}

	/**
	 * @return the log
	 */
	public static boolean isLog() {
		return log;
	}
	
	/**
	 * @param log the log to set
	 */
	public static void setLog(boolean log) {
		Settings.log = log;
	}	

	/**
	 * @return the people_simulation
	 */
	public static boolean isPeople_simulation() {
		return people_simulation;
	}

	/**
	 * @param people_simulation the people_simulation to set
	 */
	public static void setPeople_simulation(boolean people_simulation) {
		Settings.people_simulation = people_simulation;
	}

	/**
	 * @param min_energy the min_energy to set
	 */
	public static void setMin_energy(Integer min_energy) {
		Settings.min_energy = min_energy;
	}


	public static void printSettings() {
		System.out.println("Settings: interval length: " + interval_length
				+ ", max power consumption: " + max_power_consumption
				+ ", min temperature: " + min_temperature
				+ ", max temperature: " + max_temperature
				+ ", energy provided by enel: " + enel_energy
				+ ", max energy provided by solar panels: " + max_solar_energy);
	}

}
