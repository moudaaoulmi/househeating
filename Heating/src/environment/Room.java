package environment;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.awt.Color;
import java.util.ArrayList;

import utils.Settings;

import agents.Agent;

/**
 * A room is a zone of the environment delimited by walls and independent from
 * other zones (rooms). It has a name, a dimension (in cubic meters) and a set
 * of things present in it. Each room has a thermal absorption that depends on
 * its exposition (north, south) and on the presence of things capable of
 * absorbing or releasing thermal energy, like a balcony or a fireplace.
 * 
 * @author falkor
 * 
 */
public class Room {

	private Integer id;
	private String name;
	private Integer squared_meters, cube_meters;
	// x and y coordinates
	private Integer[] x_coord, y_coord;
	private Integer initial_x_pos, initial_y_pos;
	private Color color;
	private ArrayList<String> things;

	// percentage of isolation of the room (if 50%, in an hour the temperature
	// varies of ah half the difference between the internal and external
	// temperature)
	private Integer isolation_value;
	private Integer num_of_people, desired_temp, min_temp;

	private Float current_temperature;
	private Agent agent;

	public Room() {
		this.things = new ArrayList<String>();

		// this.current_temperature = 30;
		// this.energy_absorbed_by_room = 100;
		// this.num_of_people = 0;
		// this.desired_temp = 20;
		// this.min_temp = 10;
	}

	/**
	 * Initialize the agent of this room, turning it ON or OFF basing on its
	 * last stored status.
	 * 
	 * @param max_power
	 */
	public void initAgent(boolean status, int max_power, boolean check_only_min_temp) {
		// agent = new Agent(this, status, max_power);

		Object[] args_array = new Object[4];

		args_array[0] = this;
		args_array[1] = status;
		args_array[2] = max_power;
		args_array[3] = check_only_min_temp;

		AgentController a;
		try {
			a = Heating.cc.createNewAgent(Agent.getAgentName(this.id),
					Agent.class.getName(), args_array);
			a.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Update the current temperature based on the agent state and the room
	 * parameters.
	 * 
	 * @param seconds
	 * @param external_temp
	 *            the temperature of outside the house, that can affect the
	 *            inside temperature if the difference between these two
	 *            temperatures is relevant.
	 */
	float increment, air_energy = 3004.6F, isolation_value_modifier = 0.7F;
	int seconds;

	public void updateTemp(Float external_temp) {
		seconds = TimeHandler.getInstance().getSecondsSinceLastUpdate();
		increment = 0F;

		if (agent.isHeating()) {
			// System.out.println("agent heating in room " + this.name);
			int joules = agent.getCurrent_pow_consumption() * seconds;
			increment = (float) joules / (1.3F * cube_meters * air_energy);
		}

		float diff, external_temp_influence;

		diff = external_temp - current_temperature;
		external_temp_influence = (diff * isolation_value
				* isolation_value_modifier / 100)
				* seconds / 3600;

		this.current_temperature += increment + external_temp_influence;
		// if (this.current_temperature < 0F || this.current_temperature > 50F)
		// this.current_temperature =
		// (float)Environment.getExternal_temperature();
	}

	/**
	 * Returns the sum of the thermal power provided by the objects in the room
	 * 
	 * @return
	 */
	public Integer getThermalPowerProvidedByObjects() {
		int absortion = 0;
		for (String thing : this.things) {
			absortion += Environment.things.get(thing).getThermal_power();
		}
		return absortion;
	}

	/**
	 * Returns the sum of the energy absorbed by the objects in the room
	 * 
	 * @return
	 */
	public Integer getEnergyAbsorbedByObjects() {
		int energy = 0;
		for (String thing : this.things) {
			energy += Environment.things.get(thing).getEnergy();
		}
		return energy;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSquared_meters() {
		return squared_meters;
	}

	public void setSquared_meters(Integer squared_meters) {
		this.squared_meters = squared_meters;
		this.cube_meters = squared_meters * Settings.getRooms_height();
	}

	public int[] getX_coord() {
		int[] points = new int[x_coord.length];
		for (int i = 0; i < x_coord.length; i++) {
			points[i] = x_coord[i];
		}
		return points;
	}

	public void setX_coord(Integer[] x_coord) {
		this.x_coord = x_coord;
	}

	public int[] getY_coord() {
		int[] points = new int[y_coord.length];
		for (int i = 0; i < y_coord.length; i++) {
			points[i] = y_coord[i];
		}
		return points;
	}

	public void setY_coord(Integer[] y_coord) {
		this.y_coord = y_coord;
	}

	public Integer getInitial_x_pos() {
		return initial_x_pos;
	}

	public void setInitial_x_pos(Integer initial_x_pos) {
		this.initial_x_pos = initial_x_pos;
	}

	public Integer getInitial_y_pos() {
		return initial_y_pos;
	}

	public void setInitial_y_pos(Integer initial_y_pos) {
		this.initial_y_pos = initial_y_pos;
	}

	public Color getColor() {
		return color;
	}

	/**
	 * @return the temperature
	 */
	public Float getCurrent_temperature() {
		return current_temperature;
	}

	/**
	 * @param temperature
	 *            the temperature to set
	 */
	public void setCurrent_temperature(Float current_temperature) {
		this.current_temperature = current_temperature;
	}

	/**
	 * Returns the number of people present in the room
	 * 
	 * @return the num_of_people
	 */
	public Integer getNum_of_people() {
		return num_of_people;
	}

	/**
	 * @param num_of_people
	 *            the num_of_people to set
	 */
	public void setNum_of_people(Integer num_of_people) {
		this.num_of_people = num_of_people;
	}

	/**
	 * Increments the number of people in the room
	 */
	public void increaseNumOfPeople() {
		this.num_of_people++;
		if (this.agent.isAutoPowerOn())
			this.agent.peopleHandling(false);
	}

	/**
	 * Increments the number of people in the room
	 */
	public void decreaseNumOfPeople() {
		this.num_of_people--;
		if (this.agent.isAutoPowerOn())
			this.agent.peopleHandling(false);
	}

	/**
	 * Get the total energy absorbed (or released if negative) by the room
	 * itself and by all the things present in it.
	 * 
	 * @return the energy_absorbed_by_room
	 */
	public Integer getIsolationValue() {
		return this.isolation_value;
	}

	/**
	 * @param energy_absorbed_by_room
	 *            the energy_absorbed_by_room to set
	 */
	public void setIsolationValue(Integer isolation_value) {
		this.isolation_value = isolation_value;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public ArrayList<String> getThings() {
		return things;
	}

	public void setThings(ArrayList<String> things) {
		this.things = things;
	}

	/**
	 * @return the agent
	 */
	public Agent getAgent() {
		return agent;
	}

	/**
	 * @param agent
	 *            the agent to set
	 */
	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	/**
	 * Turn agent on
	 * 
	 * @param agent
	 *            the agent to set
	 */
	public void powerOnAutoHeating() {
		if (this.agent == null) {
			try {
				throw new Exception("Agent State Changing Exception");
			} catch (Exception e) {
				System.err.println("Agent null for room " + this.id);
			}
		}

		this.agent.setPowered_on(true);
	}

	/**
	 * Turn agent off
	 * 
	 * @param agent
	 *            the agent to set
	 */
	public void powerOffAutoHeating() {
		if (this.agent == null) {
			try {
				throw new Exception("Agent State Changing Exception");
			} catch (Exception e) {
				System.err.println("Agent null for room " + this.id);
			}
		}
		this.agent.setPowered_on(false);
	}

	/**
	 * @return the desired_temp
	 */
	public Integer getDesired_temp() {
		return desired_temp;
	}

	/**
	 * @param desired_temp
	 *            the desired_temp to set
	 */
	public void setDesired_temp(Integer desired_temp) {
		this.desired_temp = desired_temp;
	}

	/**
	 * @return the min_temp
	 */
	public Integer getMin_temp() {
		return min_temp;
	}

	/**
	 * @param min_temp
	 *            the min_temp to set
	 */
	public void setMin_temp(Integer min_temp) {
		this.min_temp = min_temp;
	}

	/**
	 * Returns true if the room contains at least one thing. False otherwise.
	 * 
	 * @return
	 */
	public boolean containsThings() {
		if (this.things.size() == 0)
			return false;
		return true;
	}

	@Override
	public String toString() {
		String str = "Room " + this.name + "\nof " + this.squared_meters
				+ " squared meters, located in (" + this.initial_x_pos + ","
				+ this.initial_y_pos + "). It contains the following things: ";
		for (String curr : this.things) {
			str += curr + " ";
		}
		str += "\n";
		return str;
	}

}
