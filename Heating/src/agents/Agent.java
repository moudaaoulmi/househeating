package agents;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.GregorianCalendar;

import agents.behaviours.DoNothingBehaviour;
import agents.behaviours.HeaterBehaviour;
import agents.behaviours.OnlyMinTempHeaterBehaviour;

import utils.Enums.Day;
import utils.XMLHandler;
import environment.Room;
import environment.TimeHandler;

public class Agent extends AbstractAgent {
	private static final long serialVersionUID = 1L;

	private long date;
	private Day day;
	private int calls;
	private boolean to_udpate;
	private boolean asked_for_more_power;
	private boolean check_only_min_temp;

	public HeaterBehaviour heater_behaviour;
	public DoNothingBehaviour do_nothing_behaviour;
	public OnlyMinTempHeaterBehaviour check_only_min_temp_behaviour;

	public Agent() {

	}

	public void init(Room room_in, Boolean powered_on, Integer max_power, boolean check_only_min_temp) {
		this.room_in = room_in;

		this.id = room_in.getId();
		this.num_of_people_currently_in_room = room_in.getNum_of_people();
		this.max_pow_consumption = max_power;

		this.current_pow_consumption = 400;
		this.current_time_interval = -1;
		this.max_num_people_in_room = -1;

		calls = 0;
		to_udpate = false;

		this.room_in.setAgent(this);
		this.auto_power_on = (boolean) powered_on;

		this.asked_for_more_power = false;
		this.check_only_min_temp = check_only_min_temp;
	}

	/**
	 * @param powered_on
	 *            the powered_on to set
	 */
	public void setPowered_on(boolean powered_on) {

		TimeHandler t = TimeHandler.getInstance();
		GregorianCalendar c = t.getCalendar();

		if (calls > 0) {
			// turning OFF the agent
			if (this.auto_power_on && !powered_on) {
				to_udpate = true;
			}
			// turning ON the agent
			else if (!this.auto_power_on && powered_on) {
				// check whether the agent has been turned ON before, but in the
				// same day, and so there are already stats for that day

				// yes, it's the same day. simply turn on the agent
				if (c.getTimeInMillis() >= date
						&& c.getTimeInMillis() - date < 86400000
						&& t.getDay().equals(day)) {
				}
				// more than one day has passed since the agent was turned off.
				// no, it's another day. Update the date with the new value!
				else {
					date = t.getCalendar().getTimeInMillis();
					day = t.getDay();
					this.resetCurrPeopleInRoomStats(day);

				}
				to_udpate = true;
				calls++;
			}
		} else {
			// System.err.println("First call for setPower of Agent " +
			// this.id);
			// initialize the hashmap for the current day stats
			people_in_room_stats = XMLHandler.readPeopleXMLFile(this.id);
			if (powered_on) {
				date = t.getCalendar().getTimeInMillis();
				day = t.getDay();
				resetCurrPeopleInRoomStats(day);
				to_udpate = true;
				calls++;
			} else {
				// System.err.println("Agent " + id + " powered OFF");
				to_udpate = false;
			}
		}

		this.auto_power_on = powered_on;
	}

	public void updateStats(Day day) {
		if (this.isAutoPowerOn() || this.to_udpate) {
			super.updateStats();
		}
		if (this.isAutoPowerOn()) {
			TimeHandler t = TimeHandler.getInstance();
			date = t.getCalendar().getTimeInMillis();
			day = t.getDay();
			resetCurrPeopleInRoomStats(day);
			to_udpate = true;
		} else
			to_udpate = false;
	}

	public void writeStatsFile() {
		if (calls > 0) {
			this.updateStats();
			XMLHandler.createPeopleDataXMLFile(this);
		}
	}

	/**
	 * @return the asked_for_more_power
	 */
	public boolean isAsked_for_more_power() {
		return asked_for_more_power;
	}

	/**
	 * @param asked_for_more_power
	 *            the asked_for_more_power to set
	 */
	public void setAsked_for_more_power(boolean asked_for_more_power) {
		this.asked_for_more_power = asked_for_more_power;
	}

	/**
	 * @return the only_min_temp_check
	 */
	public boolean isCheckOnlyMinTemp() {
		return check_only_min_temp;
	}

	/**
	 * @param only_min_temp_check the only_min_temp_check to set
	 */
	public void setCheckOnlyMinTemp(boolean check_only_min_temp) {
		this.check_only_min_temp = check_only_min_temp;
	}

	/**
	 * Changes the state of the heater, switching it on if it is off, and
	 * viceversa.
	 */
	public void switchHeating() {
		this.heating_on = !this.heating_on;
	}

	public Integer lookForward(Integer forward) {
		int current_interval, target_interval;
		Day current_day, target_day;

		current_interval = TimeHandler.getInstance().getCurrentInterval();
		current_day = TimeHandler.getInstance().getDay();

		target_interval = current_interval + forward;
		if (target_interval > 48) {
			target_interval %= 48;
			if (current_day.getDayNum() + 1 > 7)
				target_day = Day
						.getDayByNumber((current_day.getDayNum() + 1) % 7);
			else
				target_day = Day.getDayByNumber(current_day.getDayNum() + 1);
		} else
			target_day = current_day;

		Integer[] values = this.people_in_room_stats.get(target_day).get(
				target_interval);
		float people = 0;

		// give a weight to each value: 10%, 20%, 25%, 45%
		for (int i = values.length - 1; i >= 0; i--) {
			if (values[i] != -1)
				switch (i) {
				case 3:
					people += values[i] * 0.45F;
					break;
				case 2:
					people += values[i] * 0.25F;
					break;
				case 1:
					people += values[i] * 0.20F;
					break;
				case 0:
					people += values[i] * 0.10F;
					break;
				default:
					break;
				}
		}
		// so that if there's been one person last week, the total given is one
		people += 0.06F;
		return Math.round(people);
	}

	/******************** JADE ********************/

	@Override
	protected void setup() {

		Object[] args = getArguments();
		if (args != null && args.length == 4) {
			init((Room) args[0], (Boolean) args[1], (Integer) args[2], (Boolean) args[3]);
		} else {
			// Make the agent terminate immediately
			System.out.println("Parameters error");
			doDelete();
		}

		// Register the book-selling service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

		ServiceDescription sd = new ServiceDescription();
		sd.setType("heater");
		sd.setName(getAgentName(id));

		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

//		System.out.println("Created " + getAgentName(this.id) + " in room "
//				+ room_in.getName() + ". Enjoy Auto-Heating service!");
	}

	/**
	 * Initialize behaviours and start the agent adding the "doNothingBehaviour"
	 */
	public void firstTimeStart() {
		this.heater_behaviour = new HeaterBehaviour(this, 3000);
		this.do_nothing_behaviour = new DoNothingBehaviour(this, 5000);
		this.check_only_min_temp_behaviour = new OnlyMinTempHeaterBehaviour(this, 3000);
		this.addBehaviour(do_nothing_behaviour);
		setPowered_on(auto_power_on);
	}

	@Override
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("Shutting down " + getAgentName(id));
	}

	public static String getAgentName(Integer id) {
		return "Heating-Heater_" + id;
	}

}
