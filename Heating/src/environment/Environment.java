package environment;

import graphics.GraphicEnvironment;

import java.util.HashMap;

import utils.Enums.Day;
import utils.Enums.Month;
import utils.Settings;
import utils.XMLHandler;

import agents.Agent;

/**
 * Class that represents the entire Environment providing all the methods for
 * setting and update it
 * 
 * @author falkor
 * 
 */
public class Environment {

	public static HashMap<String, Thing> things;
	public static HashMap<String, Room> rooms;
	public static HashMap<Integer, Floor> floors;

	public static HashMap<Integer, Agent> agents;

	public static TimeHandler time_handler;

	private static Integer other_consumption;

	private static Float external_temp;
	public static HashMap<Month, Integer> external_medium_temp;
	private static boolean auto_set_external_temp;

	public Environment() {
		things = new HashMap<String, Thing>();
		for (Thing curr : XMLHandler.readThingsXMLFile()) {
			things.put(curr.getName(), curr);
			// System.out.println(curr.toString());
		}

		rooms = new HashMap<String, Room>();
		for (Room curr : XMLHandler.readRoomsXMLFile()) {
			rooms.put(curr.getName(), curr);
			// System.out.println(curr.toString());
		}

		floors = new HashMap<Integer, Floor>();
		for (Floor curr : XMLHandler.readFloorsXMLFile()) {
			floors.put(curr.getNum(), curr);
			// System.out.println(curr.toString());
		}

		time_handler = TimeHandler.getInstance();
		external_temp = 25F;
		auto_set_external_temp = true;
		external_medium_temp = XMLHandler.readExternalTempFile();
		other_consumption = 180;
	}

	/**
	 * Update the values of each room of the environment Pay attention to the
	 * order of each function call in this method.
	 */
	public static void update() {
		time_handler.goAway();

		Day day = time_handler.isAnotherDay();
		if (day != null) {
			System.out.println("Altro giorno. Quello appena passato era "
					+ day.toString());
			for (Room curr_room : rooms.values())
				curr_room.getAgent().updateStats(day);
		} else {

		}

		// if nothing changes in the environment update each tot of time
		// otherwise, update everything when something in the environment
		// changed.
		// update_rooms(seconds);
		update_rooms();

		setExternal_temperature();

		// foreach room updateTemp()
		// simulate presence of people in rooms
		// agentsNextAction()
		GraphicEnvironment.repaintAll();

	}

	/**
	 * Update the temperature value of each room
	 */
	private static void update_rooms() {
		for (Room curr_room : rooms.values()) {
			curr_room.updateTemp(external_temp);
			if (curr_room.getAgent().isAutoPowerOn())
				curr_room.getAgent().peopleHandling(false);
			curr_room.getAgent().updatePerformanceMeasure();
		}
	}

	public static Integer getCurrentSolarPower() {
		return SolarPowHandler.getCurrentSolarEnergy();
	}

	/**
	 * @return the external_temperature
	 */
	public static Float getExternal_temperature() {
		return external_temp;
	}

	/**
	 * Sets the external temperature automatically, basing on the hour of the
	 * day and the month. The temperature follows a sinusoidal trend, that is
	 * good for the simulation but not so much pertinent to a real case.
	 * 
	 * @param external_temperature
	 *            the external_temperature to set
	 */
	public static void setExternal_temperature() {
		if (auto_set_external_temp) {
			int interval = time_handler.getCurrentInterval();
			double x = ((float) (((float) interval - 19F) / 24F)) * Math.PI;
			if (x < 0)
				x += 2 * Math.PI;
			external_temp = external_medium_temp.get(Month
					.getMonthByNumber(time_handler.getNumericMonth()))
					+ (float) Math.sin(x) * 4;
		}
	}

	/**
	 * Sets the external temperature to the passed value
	 * 
	 * @param external_temperature
	 *            the external_temperature to set
	 */
	public static void setExternal_temperature(Float f) {
		external_temp = f;

		int interval = time_handler.getCurrentInterval();
		double x = ((float) (((float) interval - 19F) / 24F)) * Math.PI;
		if (x < 0)
			x += 2 * Math.PI;

		float new_external_temp = f - (float) Math.sin(x) * 4;
		external_medium_temp.put(
				Month.getMonthByNumber(time_handler.getNumericMonth()),
				(int) new_external_temp);
	}

	/**
	 * @return the auto_set_external_temp
	 */
	public static boolean isAuto_set_external_temp() {
		return auto_set_external_temp;
	}

	/**
	 * @param auto_set_external_temp
	 *            the auto_set_external_temp to set
	 */
	public static void setAuto_set_external_temp(boolean auto_set_external_temp) {
		Environment.auto_set_external_temp = auto_set_external_temp;
	}

	/**
	 * Returns the energy absorbed by other objects in the house
	 * 
	 * @return the other_consumption
	 */
	public static Integer getOther_consumption() {
		return other_consumption;
	}

	/**
	 * Sets the energy absorbed by other objects in the house to the passed
	 * value
	 * 
	 * @param other_consumption
	 *            the other_consumption to set
	 */
	public static void setOther_consumption(Integer other_consumption) {
		Environment.other_consumption = other_consumption;
	}

	/**
	 * Returns the total energy absorbed by the objects in the house
	 * 
	 * @return the total actual energy consumption
	 */
	public static Integer getTotalEnergyConsumption() {
		int energy = 0;

		energy += getOther_consumption();
		for (Room curr : rooms.values()) {
			energy += curr.getEnergyAbsorbedByObjects();
			if (curr.getAgent().isHeating())
				energy += curr.getAgent().getCurrent_pow_consumption();
		}
		return energy;
	}

	/**
	 * Returns the available energy, that is the energy provided by Enel plus
	 * the current energy produces by the solar panels minus the total energy
	 * absorbed in the house
	 * 
	 * @return
	 */
	public static Integer getAvailableEnergy() {
		int energy = 0;

		energy += Settings.getEnel_energy();
		energy += getCurrentSolarPower();

		energy -= getTotalEnergyConsumption();

		return energy;
	}

	/**
	 * Returns the overall performance measure, that is the sum of the pm. of
	 * each turned on agent, divided by the their number.
	 * 
	 * @return overall performance measure
	 */
	public static float getOverallPM() {
		float overall_pm = -1;
		int count = 0;
		Agent a;
		for (Room r : rooms.values()) {
			a = r.getAgent();
			if (a.isAutoPowerOn()) {
				if (overall_pm == -1)
					overall_pm = 0;
				if (a.getPerformance_measure() != -1) {
					overall_pm += a.getPerformance_measure();
					count++;
				}
			}
		}
		
		if (count > 0)
			overall_pm /= count;
		
		return overall_pm;
	}

}
