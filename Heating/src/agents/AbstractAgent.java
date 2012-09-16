package agents;

import jade.core.Agent;

import java.util.HashMap;

import environment.Room;
import environment.TimeHandler;

import utils.XMLHandler;
import utils.Enums.Day;

public abstract class AbstractAgent extends Agent {
	private static final long serialVersionUID = 1L;

	Room room_in;
	int id;

	boolean auto_power_on, heating_on;

	int num_of_people_currently_in_room;
	float performance_measure = -1;

	// num of people in the room for each interval of time of each day of the
	// week
	HashMap<Day, HashMap<Integer, Integer[]>> people_in_room_stats;
	HashMap<Integer, Integer> curr_day_people_in_room_stats;
	Day curr_day_stats;

	Integer current_pow_consumption, max_pow_consumption;

	int current_time_interval, max_num_people_in_room;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the current_pow_consumption
	 */
	public Integer getCurrent_pow_consumption() {
		return current_pow_consumption;
	}

	/**
	 * @param current_pow_consumption
	 *            the current_pow_consumption to set
	 */
	public void setCurrentPowerConsumption(Integer current_pow_consumption) {
		this.current_pow_consumption = current_pow_consumption;
	}

	/**
	 * @return the max_pow_consumption
	 */
	public Integer getMaxPowConsumption() {
		return max_pow_consumption;
	}

	/**
	 * @param max_pow_consumption
	 *            the max_pow_consumption to set
	 */
	public void setMaxPowerConsumption(Integer max_pow_consumption) {
		this.max_pow_consumption = max_pow_consumption;
	}

	/**
	 * @return the powered_on
	 */
	public boolean isAutoPowerOn() {
		return auto_power_on;
	}

	/**
	 * @return the manual_heating
	 */
	public boolean isHeating() {
		return heating_on;
	}

	/**
	 * @param manual_heating
	 *            the manual_heating to set
	 */
	public void setHeating(boolean heating) {
		this.heating_on = heating;
	}

	/**
	 * Methods that resets the hashmap containing information about the room
	 * collected during the current (last) execution of the agent.
	 */
	protected void resetCurrPeopleInRoomStats(Day day) {
		curr_day_people_in_room_stats = new HashMap<Integer, Integer>();
		for (int i = 1; i <= 48; i++)
			curr_day_people_in_room_stats.put(i, -1);
		curr_day_stats = day;
	}

	/**
	 * People in room asked for more power. Accomplish the request.
	 */
	public void askedForMorePower() {

	}

	public void initStats() {
		people_in_room_stats = XMLHandler.readPeopleXMLFile(getId());
	}

	/**
	 * Handles the stats about people in the room
	 */
	public void peopleHandling(boolean force) {
		if (force) {
			curr_day_people_in_room_stats.put(current_time_interval,
					max_num_people_in_room);
			return;
		}

		this.num_of_people_currently_in_room = room_in.getNum_of_people();

		// if someone entered during the same interval of time
		if (current_time_interval == TimeHandler.getInstance()
				.getCurrentInterval()) {

			if (num_of_people_currently_in_room > max_num_people_in_room) {
				max_num_people_in_room = num_of_people_currently_in_room;

				curr_day_people_in_room_stats.put(current_time_interval,
						max_num_people_in_room);
				// System.out.println("Agent " + id
				// + ": set(time_inteval,value) to ("
				// + current_time_interval + ", " + max_num_people_in_room
				// + ")");
			}
		}
		// else set the max to the current value and update the stats.
		else {
			boolean first = (max_num_people_in_room == -1);

			// set the people at the end of the interval as the max minus the
			// 1/2 * difference between max and current num
			int n = max_num_people_in_room;

			if (max_num_people_in_room > num_of_people_currently_in_room
					&& (TimeHandler.getInstance().getCurrentInterval()
							- current_time_interval == 1 || (TimeHandler
							.getInstance().getCurrentInterval() == 1 && current_time_interval == 48))) {
				n -= (int) ((max_num_people_in_room - num_of_people_currently_in_room) / 2);
			}

			curr_day_people_in_room_stats.put(current_time_interval, n);

			// System.out.println("Agent " + id +
			// ": set(time_inteval,value) to ("
			// + current_time_interval + ", " + n + ")");

			if (!first) {
				max_num_people_in_room = num_of_people_currently_in_room;
				// System.out.println("updated max: " + max_num_people_in_room);
			}

			current_time_interval = TimeHandler.getInstance()
					.getCurrentInterval();
			peopleHandling(false);
		}
	}

	/**
	 * This function takes the current_day stats and makes a shift in it's old
	 * stats, removing the oldest and adding new newest.
	 * 
	 * @param day
	 *            the day the stats are referred to
	 * 
	 */
	protected void updateStats() {

		peopleHandling(true);

		HashMap<Integer, Integer[]> values_for_that_day = people_in_room_stats
				.get(curr_day_stats);

		for (Integer interval : values_for_that_day.keySet()) {
			// if the agent was powered on during the current interval of time,
			// update the data.
			if (curr_day_people_in_room_stats.get(interval) != -1) {
				Integer[] old = values_for_that_day.get(interval);
				Integer[] news = new Integer[old.length];

				for (int i = 1; i < old.length; i++)
					news[i - 1] = old[i];

				news[old.length - 1] = curr_day_people_in_room_stats
						.get(interval);

				// System.out.print("Agent " + id
				// + " updating stats for interval " + interval
				// + ". values: [");
				// for (Integer integer : news) {
				// System.out.print(integer + ", ");
				// }

				// System.out.println();
				values_for_that_day.put(interval, news);
			} else {
				// System.out.print("Agent " + id
				// + " NOT updating stats for interval " + interval);
			}
		}
		people_in_room_stats.put(curr_day_stats, values_for_that_day);
		// resetCurrPeopleInRoomStats(TimeHandler.getInstance().getDay());
	}

	/**
	 * @return the curr_day_stats
	 */
	public Day getCurr_day_stats() {
		return curr_day_stats;
	}

	/**
	 * @param curr_day_stats
	 *            the curr_day_stats to set
	 */
	public void setCurr_day_stats(Day curr_day_stats) {
		this.curr_day_stats = curr_day_stats;
	}

	/**
	 * @return the people_in_room_stats
	 */
	public HashMap<Day, HashMap<Integer, Integer[]>> getPeople_in_room_stats() {
		return people_in_room_stats;
	}

	/**
	 * @return the room_in
	 */
	public Room getRoom_in() {
		return room_in;
	}

	/**
	 * Updates the performance measure. It is calculates as a percentage of
	 * comfort based on the difference between the expected (desired)
	 * temperature and the actual temperature, when the agent is in
	 * auto-power-on mode, and there's someone in the room.
	 */
	int tot_seconds = 0, curr_seconds;
	float curr_pm, diff;
	public void updatePerformanceMeasure() {
		getInstantPM();
		if (this.isAutoPowerOn()) {
			if (this.room_in.getNum_of_people() > 0) {
				// first calculation on pm for that agent
				if (performance_measure == -1)
					performance_measure = 0;

				curr_seconds = TimeHandler.getInstance()
						.getSecondsSinceLastUpdate();
				performance_measure = (performance_measure * tot_seconds + curr_pm
						* curr_seconds)
						/ (tot_seconds + curr_seconds);

				tot_seconds += curr_seconds;
				return;
			}
		}
	}

	/**
	 * Returns the instant comfort, based on the difference between the desired
	 * and actual temps
	 * 
	 * @return
	 */
	public float getInstantPM() {
		diff = this.room_in.getDesired_temp()
		- this.room_in.getCurrent_temperature();

		if (this.room_in.getNum_of_people() > 0) {
			if (diff <= 1)
				curr_pm = 1F;
			else if (diff <= 2)
				curr_pm = 0.9F;
			else if (diff <= 3)
				curr_pm = 0.82F;
			else if (diff <= 4)
				curr_pm = 0.7F;
			else if (diff <= 5)
				curr_pm = 0.6F;
			else if (diff <= 6)
				curr_pm = 0.5F;
			else if (diff <= 7)
				curr_pm = 0.25F;
			else if (diff <= 8)
				curr_pm = 0.1F;
			else
				curr_pm = 0F;
		} else
			curr_pm = -1;

		return curr_pm;
	}

	/**
	 * @return the performance_measure
	 */
	public float getPerformance_measure() {
		return performance_measure;
	}

}
