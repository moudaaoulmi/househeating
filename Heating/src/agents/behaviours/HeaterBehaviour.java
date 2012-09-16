package agents.behaviours;

import environment.Room;
import environment.SolarPowHandler;
import environment.TimeHandler;
import utils.Settings;
import utils.Enums.MsgType;
import utils.Enums.Priority;
import agents.Agent;
import agents.RefereeAgent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class HeaterBehaviour extends Behaviour {
	private static final long serialVersionUID = 1L;

	boolean turned_ON;

	boolean heating;
	long heating_start_time;
	int heating_energy;
	Priority heating_priority;

	agents.Agent agent;
	Room room_in;
	Float diff, solar_pow_gain;
	ACLMessage mex;
	AID referee;

	private long min_elapsed_time = 5000;

	private boolean more_power_mex_sent;

	public HeaterBehaviour(agents.Agent a, long period) {
		super(a);
		this.agent = a;
		turned_ON = false;
		heating = false;
		room_in = this.agent.getRoom_in();
		init();
	}

	/********** start action **********/

	@Override
	public void action() {
		if (!this.agent.isAutoPowerOn()) {
			turnOffHeating();
			agent.removeBehaviour(this);
			agent.addBehaviour(agent.do_nothing_behaviour);
			if (Settings.isLog())
				System.out.println("Agent "
						+ Agent.getAgentName(this.agent.getId())
						+ ": Do Nothing Behaviour");
			return;
		}
		if (agent.isCheckOnlyMinTemp()) {
			turnOffHeating();
			agent.removeBehaviour(this);
			agent.addBehaviour(agent.check_only_min_temp_behaviour);
			if (Settings.isLog())
				System.out.println("Agent "
						+ Agent.getAgentName(this.agent.getId())
						+ ": Check Only Min Temp Behaviour");
			return;
		}

		handleTime();
		/************** NORMAL ACTION **************/

		// get temperature difference
		diff = room_in.getDesired_temp() - room_in.getCurrent_temperature();

		// determine a solar power gain, that is an increment of desired temp,
		// and also of min temp. It is available only if solar energy is.
		if (SolarPowHandler.getCurrentSolarEnergy() > 0)
			solar_pow_gain = 3F;
		else
			solar_pow_gain = 0F;

		// Heating ON
		if (heating) {
			// 1) check whether the desired temp has been reached
			// 1.a) desired temp reached => STOP heating
			if (diff + solar_pow_gain <= 0) {
				System.out
						.println("Agent "
								+ Agent.getAgentName(this.agent.getId())
								+ ": Turn off heating because of DESIRED temp reached!");
				turnOffHeating();
				block(1000);
				return;
			}
			if (heating_priority == Priority.MIN_TEMP) {
				int d = Math.round(room_in.getCurrent_temperature())
						- room_in.getMin_temp();
				if (d + solar_pow_gain >= 2) {
					System.out
							.println("Agent "
									+ Agent.getAgentName(this.agent.getId())
									+ ": Turn off heating because of MIN temp reached!");
					turnOffHeating();
					block(1000);
					return;
				}
			}
			// 1.b) desired temp not reached => do the following
			// 2) check for new messages from the referee regarding:
			// 2.a.i) priority request
			while ((mex = agent
					.receive(MessageTemplates.priority_request_templ)) != null) {
				printMex(mex);
				if (TimeHandler.getInstance().isActual(
						Long.parseLong(mex.getContent()))) {
					if (Settings.isLog())
						System.out.println(" p request");
					Priority p = getPriority();
					agent.send(createPriorityReplyMessage(mex, p));
					block(1000);
					return;
				} else if (Settings.isLog())
					System.out.println(" REFUSED");
			}
			// 2.b) turning off order
			while ((mex = agent.receive(MessageTemplates.turn_off_order_templ)) != null) {
				printMex(mex);
				if (TimeHandler.getInstance().isActual(
						Long.parseLong(mex.getContent()))) {
					if (Settings.isLog())
						System.out.println(" turn off order ");
					this.turnOffHeating();
					block(1000);
					return;
				} else if (Settings.isLog())
					System.out.println(" REFUSED");
			}
			// 2.c) power reduction order
			// timestamp(long):reductionFactor(float):reply(boolean)
			while ((mex = agent.receive(MessageTemplates.reduction_order_templ)) != null) {
				printMex(mex);
				String[] content_values = mex.getContent().split(":");
				if (TimeHandler.getInstance().isActual(
						Long.parseLong(content_values[0]))) {
					if (Settings.isLog())
						System.out.println(" power red order ");
					// adjust current power basing on content_values[1],
					// that is the reduction factor
					int new_heating_energy = getMaxAvailablePower((int) (heating_energy * Float
							.parseFloat(content_values[1])));

					if (new_heating_energy == -1) {
						turnOffHeating();
					} else {
						turnOnHeating(new_heating_energy, heating_priority,
								false);
					}

					if (Boolean.parseBoolean(content_values[2])) {
						this.agent.send(createReductionOrderReplyMessage(mex));
					}

					block(1000);
					return;
				} else if (Settings.isLog())
					System.out.println(" REFUSED");

			}
			// 2.a) check for request acceptance (wanting to increment
			// power, a request of extra energy has been done)
			while ((mex = agent.receive(MessageTemplates.energy_request_templ)) != null) {
				printMex(mex);
				// timestamp(long):energy(int):priority(string)
				String[] content_values = mex.getContent().split(":");
				if (TimeHandler.getInstance().isActual(
						Long.parseLong(content_values[0]))) {
					if (Settings.isLog())
						System.out.println(" more energy req ok ");
					Priority tmp_p = Priority.valueOf(content_values[2]);

					int energy = getMaxAvailablePower(heating_energy
							+ Integer.parseInt(content_values[1]));
					if (energy != -1 && energy > heating_energy) {
						turnOnHeating(energy, tmp_p, true);
						block(1000);
						return;
					}
				} else if (Settings.isLog())
					System.out.println(" REFUSED");

			}
			// Adjust Power
			int des_pow = getDesiredPower();
			// 1) fewer energy is enough
			if (des_pow < heating_energy) {
				if (Settings.isLog())
					System.out.println("Agent "
							+ Agent.getAgentName(this.agent.getId())
							+ ": Decreasing Energy from " + heating_energy
							+ " to " + des_pow);
				turnOnHeating(des_pow, heating_priority, false);
				block(1000);
				return;
			}
			// 2) more energy is required.
			else if (des_pow > heating_energy && !more_power_mex_sent) {
				Priority p = getPriority();
				if ((p = getPriority()) != Priority.NOTHING) {
					int energy_to_require = des_pow - heating_energy;
					if (Settings.isLog())
						System.out.println("Agent "
								+ Agent.getAgentName(this.agent.getId())
								+ ": Asking for more power");
					agent.send(createRequestMessage(p, energy_to_require));
					more_power_mex_sent = true;
				}
			}
			block(1000);
		}

		// Heating OFF
		else {
			// 4) check for request acceptance
			while ((mex = agent.receive(MessageTemplates.energy_request_templ)) != null) {
				printMex(mex);
				// timestamp(long):energy(int):priority(string)
				String[] content_values = mex.getContent().split(":");
				if (TimeHandler.getInstance().isActual(
						Long.parseLong(content_values[0]))
						&& content_values.length == 3) {
					if (Settings.isLog())
						System.out.println(" energy req ok");
					Priority tmp_p = Priority.valueOf(content_values[2]);

					int energy = getMaxAvailablePower(Integer
							.parseInt(content_values[1]));
					if (energy != -1) {
						turnOnHeating(energy, tmp_p, true);
						block(1000);
						return;
					} else {
						block(1000);
						return;
					}
				} else if (Settings.isLog())
					System.out.println(" REFUSED");

			}

			if (diff > 2 && !more_power_mex_sent) {

				// 5) determine the situation in the room, i.e. the priority.
				Priority p = getPriority();
				if (p != Priority.NOTHING) {
					// int energy_to_require = getDesiredPower();
					// agent.send(createRequestMessage(p, energy_to_require));

					// TODO verificare quale dei due é meglio.. partire da poca
					// energia e dopo richiederne ancora, o partire chiedendo il
					// max e non accendersi se non si dispone di tutta
					// l'energia?
					agent.send(createRequestMessage(p,
							agent.getMaxPowConsumption() / 3));
					more_power_mex_sent = true;

					block(1000);
					return;
				}
			}
			block(1000);
		}
	}

	/*********** end action ***********/

	/**
	 * Get max power basing on agent's max_power value and on the available
	 * energy. As much is the max_power value, as much is the minimum energy
	 * value required for turning on the heater.
	 * 
	 * @param available
	 * @param desired
	 * @return
	 */
	private int getMaxAvailablePower(int available) {
		int min, med, max;

		min = this.agent.getMaxPowConsumption() / 3;
		med = 2 * this.agent.getMaxPowConsumption() / 3;
		max = this.agent.getMaxPowConsumption();

		if (max <= available)
			return max;
		else if (med <= available)
			return med;
		else if (min < available)
			return min;
		else
			return -1;
	}

	/**
	 * Turns heating on, setting the heater current power consumption to the
	 * passed value 'e', storing that the heating has started with priority p.
	 * If reset time is true, the time field of heating is resetted at the
	 * current value.
	 * 
	 * @param e
	 * @param p
	 * @param reset_time
	 */
	private void turnOnHeating(int e, Priority p, boolean reset_time) {
		heating_energy = e;
		heating_priority = p;
		this.agent.setCurrentPowerConsumption(heating_energy);
		heating = true;
		this.agent.setHeating(true);
		if (reset_time)
			heating_start_time = TimeHandler.getInstance().getCalendar()
					.getTimeInMillis();
		System.out.println("Agent " + Agent.getAgentName(this.agent.getId())
				+ ": Turn ON heating with pr " + heating_priority
				+ " and energy " + heating_energy);
	}

	/**
	 * Turns heating off. This methods also resets the temporary variables
	 */
	private void turnOffHeating() {
		heating_energy = 0;
		heating_priority = null;
		this.agent.setCurrentPowerConsumption(heating_energy);
		heating = false;
		this.agent.setHeating(false);
		while (agent.receive() != null)
			;
	}

	/**
	 * Calculate the desired power basing on the temp difference. For higher
	 * diff an higher energy is required
	 * 
	 * @return
	 */
	private Integer getDesiredPower() {
		int min, med, max;
		min = this.agent.getMaxPowConsumption() / 3;
		med = 2 * this.agent.getMaxPowConsumption() / 3;
		max = this.agent.getMaxPowConsumption();

		if (diff <= 3F)
			return min;
		else if (diff <= 5F)
			return med;
		else
			return max;
	}

	/**
	 * Returns an integer representing the priority of the energy request (if
	 * any).
	 * 
	 * @return
	 */
	private Priority getPriority() {
		// Note: the number of people contributes to the 30% of the
		// priority level. The remaining 70% is given by the difference of temp.
		Priority priority = Priority.NOTHING;
		int p;

		// 1.b) someone has entered in the room
		// else
		if (agent.getRoom_in().getNum_of_people() > 0) {
			priority = Priority.SOMEONE_IN_ROOM;
			priority.setLevel(Math.round((room_in.getNum_of_people() * 0.3F + diff * 0.7F)));
		}
		// 2) there's nobody in the room at the moment. Look forward!
		// 2.a) look forward of one interval of time
		else if ((p = agent.lookForward(1)) > 0) {
			priority = Priority.SOMEONE_IN_1;
			priority.setLevel(Math.round((p * 0.3F + diff * 0.7F)));
		}
		// 2.b) look forward of one interval of time
		else if ((p = agent.lookForward(2)) > 0) {
			priority = Priority.SOMEONE_IN_2;
			priority.setLevel(Math.round((p * 0.3F + diff * 0.7F)));
		}
		// 2.c) look forward of two interval of time
		else if ((p = agent.lookForward(3)) > 0) {
			priority = Priority.SOMEONE_IN_3;
			priority.setLevel(Math.round((p * 0.3F + diff * 0.7F)));
		}
		// 2.d) look forward of three interval of time
		else if ((p = agent.lookForward(4)) > 0) {
			priority = Priority.SOMEONE_IN_4;
			priority.setLevel(Math.round((p * 0.3F + diff * 0.7F)));
		}
		// 3) temp under the minimum value
		else if (room_in.getCurrent_temperature() < room_in.getMin_temp()) {
			priority = Priority.MIN_TEMP;
			priority.setLevel((int) (room_in.getMin_temp() - room_in
					.getCurrent_temperature()));
		}

		return priority;
	}

	/**
	 * Creates a Message of energy request, structured as follows:
	 * P.name(string):P.type(int):P.level(int):timestamp(long):energy(int)
	 * 
	 * @param p
	 * @param energy_to_require
	 * @return
	 */
	private ACLMessage createRequestMessage(Priority p, int energy_to_require) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(new AID(RefereeAgent.getAgentID(), AID.ISLOCALNAME));
		msg.setConversationId(MsgType.ENERGY_REQUEST.toString());

		String content = p.serialize();
		content += ":"
				+ TimeHandler.getInstance().getCalendar().getTimeInMillis();
		content += ":" + energy_to_require;

		msg.setContent(content);
		return msg;
	}

	/**
	 * Creates a Message containing the reply to a priority request. The passed
	 * priority is the newly calculated one. Indeed, sending the oldest one,
	 * maybe it is lesser than the priority of the agent applying for energy.
	 * But at the next step, the new priority of this agent can be again higher.
	 * This would cause a start and stop behaviour that is not what we want. Its
	 * format is as follows:
	 * P.name(string):P.type(int):P.level(int):timestamp(long):heating_energy
	 * (int):heating_intervals(int)
	 * 
	 * @param mex2
	 * @param p
	 * @param energy
	 * @return
	 */
	private ACLMessage createPriorityReplyMessage(ACLMessage mex, Priority p) {
		ACLMessage msg = mex.createReply();

		int intervals = (int) (TimeHandler.getInstance().getCalendar()
				.getTimeInMillis() - heating_start_time) / 1800000;

		String content = p.serialize();
		content += ":"
				+ TimeHandler.getInstance().getCalendar().getTimeInMillis();
		content += ":" + heating_energy;
		content += ":" + intervals;

		msg.setContent(content);
		return msg;
	}

	/**
	 * Creates a Message of confirmation of reduction done after order (by weak
	 * behaviour). timestamp(long)
	 * 
	 * @param mex
	 * @return
	 */
	private ACLMessage createReductionOrderReplyMessage(ACLMessage mex) {
		ACLMessage msg = mex.createReply();

		String content = ""
				+ TimeHandler.getInstance().getCalendar().getTimeInMillis();

		msg.setContent(content);

		return msg;
	}

	/**
	 * Gets referee agent from DF service, and calls MessageTemplates init
	 * method.
	 */
	private void init() {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("referee");
		template.addServices(sd);
		DFAgentDescription[] result;
		try {
			result = DFService.search(this.agent, template);
			if (result.length != 1)
				throw new Exception(
						"Referee Agent not uniquely determined. Answer received for Referee search: "
								+ result.length);

			referee = result[0].getName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		MessageTemplates.init(referee);
	}

	/**
	 * Handle time events, like date changing, or simple time passing.
	 */
	long last_time = -1;
	long more_pow_req_time = -1;

	private void handleTime() {
		if (last_time != -1)
			if (!TimeHandler.getInstance().isActual(last_time)) {
				turnOffHeating();
			}
		last_time = TimeHandler.getInstance().getCalendar().getTimeInMillis();

		if (more_power_mex_sent && more_pow_req_time == -1) {
			more_pow_req_time = last_time;
		} else if (more_power_mex_sent
				&& (last_time - more_pow_req_time) > min_elapsed_time) {
			more_power_mex_sent = false;
			more_pow_req_time = -1;
		}

	}

	@Override
	public boolean done() {
		return false;
	}

	private void printMex(ACLMessage msg) {
		String str = "From: Referee";
		str += " To: " + Agent.getAgentName(this.agent.getId());
		str += " => " + msg.getContent();
		if (Settings.isLog())
			System.out.print(str);
	}

}
