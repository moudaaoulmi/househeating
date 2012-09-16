package agents.behaviours;

import java.util.ArrayList;

import environment.TimeHandler;
import utils.Enums.MsgType;
import utils.Enums.Priority;
import utils.Settings;
import agents.RefereeAgent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("unused")
public class ReductionRequestBehaviour extends Behaviour {
	private static final long serialVersionUID = 1L;
	private RefereeAgent agent;

	private AID[] agents;

	private int num_of_sent_requests, num_of_received_confirmations;
	private MessageTemplate priority_reply_template;
	private MessageTemplate interruption_order_template;
	private MessageTemplate reduction_order_template;
	private long time, min_elapsed_time = 3000;
	private int step;
	private java.util.HashMap<AID, PriorityData> priorities;
	private ArrayList<AID> to_turn_off, to_reduce_pow;
	private boolean reduced;

	public ReductionRequestBehaviour(RefereeAgent a) {
		if (Settings.isLog())
			System.out.println("Strong Reduction Behaviour");
		this.agent = a;
		step = 0;
		reduced = false;
	}

	@Override
	public void action() {
		if (Settings.isLog())
			System.out.print("reductionBehaviour STEP " + step + "   ");
		switch (step) {
		// 1) broadcast request of current state, including the date.
		case 0:
			if (Settings.isLog())
				System.out.println("Sending Priority Requests");
			sendBroadcastPriorityRequests();
			step++;
			time = System.currentTimeMillis();
			block(min_elapsed_time);
			if (Settings.isLog())
				System.out.println("waiting for replies");
			break;
		case 1:
			if (System.currentTimeMillis() - time < min_elapsed_time) {
				block(min_elapsed_time - (System.currentTimeMillis() - time));
				return;
			} else {
				step++;
			}
			break;
		// 2) collect all the answers
		case 2:
			if (Settings.isLog())
				System.out.println(" collecting answers");
			priorities = getPriorityReplies();
			if (priorities.isEmpty())
				step = 5;
			else
				step++;
			break;
		// 3) determine if someone can be turned off, usually because of a very
		// small temp difference, but however because of a "low" priority level
		case 3: {
			if (Settings.isLog())
				System.out.println(" can some1 be turned off?");
			if (!(to_turn_off = canBeTurnedOff()).isEmpty()) {
				// send a mex to this aids and remove it from the list of active
				// agents (priorities hashmap)
				for (AID aid : to_turn_off) {
					this.agent.send(createTurnOffOrderMessage(aid));
					priorities.remove(aid);
					reduced = true;
				}
			}
			step++;
			break;
		}
			// 4) select the three smallest priority, basing on priority type
			// and value, and send them a reduction power order, with a
			// respective reduction of 50%, 30% and 20% of their current power.
		case 4:
			if (Settings.isLog())
				System.out.print(" can some1 be reduced power?");
			AID tmp_aid;
			float[] reduction_factor = { 0.5F, 0.3F, 0.2F };

			for (int i = 0; i < reduction_factor.length; i++)
				if ((tmp_aid = getMinPriorityAid(false)) != null) {
					// send a mex to this aid
					this.agent.send(createReducePowOrderMessage(tmp_aid,
							reduction_factor[i]));
					if (Settings.isLog())
						System.out.print("Yes:  "
								+ tmp_aid.getName().split("@")[0] + ", ");
					// remove it from the list of priorities to verify
					priorities.remove(tmp_aid);
					reduced = true;
				}

			// if no agent has been turned off or reduced power, force the turn
			// off of also min temp heating agents
			if (!reduced) {
				for (int i = 0; i < reduction_factor.length; i++)
					if ((tmp_aid = getMinPriorityAid(true)) != null) {
						// send a mex to this aid
						this.agent.send(createReducePowOrderMessage(tmp_aid,
								reduction_factor[i]));
						if (Settings.isLog())
							System.out.print("Yes:  "
									+ tmp_aid.getName().split("@")[0] + ", ");
						// remove it from the list of priorities to verify
						priorities.remove(tmp_aid);
					}
			}
			step++;
			break;
		case 5:
			this.agent.removeBehaviour(this);
			this.agent.addBehaviour(RefereeBehaviour.getInstance(null));
			// while (agent.receive() != null);
			if (Settings.isLog())
				System.out.println("Referee Behaviour STARTED");
			break;
		default:
			System.err.println("ReductionRequest bad state error");
			step = 5;
			break;
		}
	}

	/**
	 * Retrieve the list of all the agents, and request them their current
	 * priority
	 */
	private void sendBroadcastPriorityRequests() {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("heater");
		template.addServices(sd);
		DFAgentDescription[] result;
		try {
			result = DFService.search(this.agent, template);

			agents = new AID[result.length];
			for (int i = 0; i < result.length; ++i) {
				agents[i] = result[i].getName();
			}
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		for (AID current_agent : agents) {
			agent.send(createPriorityRequestMessage(current_agent));
		}

	}

	/**
	 * Creates a Message of priority request. timestamp(long)
	 * 
	 * @param a
	 * @return
	 */
	private ACLMessage createPriorityRequestMessage(AID a) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(a);
		msg.setConversationId(MsgType.PRIORITY_REQUEST.toString());
		msg.setContent(((Long) TimeHandler.getInstance().getCalendar()
				.getTimeInMillis()).toString());
		return msg;
	}

	/**
	 * Get all the replies to the last priority request. For each of them,
	 * create a PriorityData object for further handling
	 * 
	 * Note: a priority reply message content is of the form
	 * P.name(string):P.type(int):P.level(int):timestamp(long):heating_energy
	 * (int):heating_intervals(int)
	 * 
	 * @return
	 */
	private java.util.HashMap<AID, PriorityData> getPriorityReplies() {
		java.util.HashMap<AID, PriorityData> priorities = new java.util.HashMap<AID, ReductionRequestBehaviour.PriorityData>();
		ACLMessage msg;
		long date;
		String[] content_values;
		PriorityData p;
		if (Settings.isLog())
			System.out.println(" Priority Replies FROM: ");

		while ((msg = this.agent
				.receive(MessageTemplates.priority_request_templ)) != null) {
			if (Settings.isLog())
				printMex(msg);

			content_values = msg.getContent().split(":");

			date = Long.parseLong(content_values[3]);
			if (TimeHandler.getInstance().isActual(date)) {
				if (Settings.isLog())
					System.out.println(" Accepted ");

					p = new PriorityData();
					p.msg = msg;
					p.priority = Priority.valueOf(content_values[0]);
					p.priority_type = Integer.parseInt(content_values[1]);
					p.priority_level = Integer.parseInt(content_values[2]);
					p.energy = Integer.parseInt(content_values[4]);

					priorities.put(msg.getSender(), p);
					if (Settings.isLog())
						System.out
								.print(msg.getSender().getName().split("@")[0]
										+ ",  ");
			} else if (Settings.isLog())
				System.out.println(" REFUSED ");

		}
		if (Settings.isLog())
			System.out.println();
		return priorities;
	}

	/**
	 * Determine agents that can be turned off, since their priority level is
	 * very low.
	 * 
	 * @return
	 */
	private ArrayList<AID> canBeTurnedOff() {
		ArrayList<AID> to_turn_off = new ArrayList<AID>();
		PriorityData p;

		for (AID curr_aid : priorities.keySet()) {
			p = priorities.get(curr_aid);

			switch (p.priority) {
			case SOMEONE_IN_ROOM:
				if (p.priority_level < 2)
					to_turn_off.add(curr_aid);
				break;
			case SOMEONE_IN_1:
				if (p.priority_level < 2)
					to_turn_off.add(curr_aid);
				break;
			case SOMEONE_IN_2:
				if (p.priority_level < 2)
					to_turn_off.add(curr_aid);
				break;
			case SOMEONE_IN_3:
				if (p.priority_level < 3)
					to_turn_off.add(curr_aid);
				break;
			case SOMEONE_IN_4:
				if (p.priority_level < 4)
					to_turn_off.add(curr_aid);
				break;
			case NOTHING:
				to_turn_off.add(curr_aid);
				break;
			default:
				break;
			}
		}

		if (Settings.isLog())
			System.out.print(" Can be turned off:  ");
		for (AID aid : to_turn_off) {
			if (Settings.isLog())
				System.out.print(aid.getName().split("@")[0] + ",  ");
		}
		if (Settings.isLog())
			System.out.println();
		return to_turn_off;
	}

	/**
	 * Create a Message containing an order of turning off heating. Format:
	 * timestamp(long)
	 * 
	 * @param a
	 * @return
	 */
	private ACLMessage createTurnOffOrderMessage(AID a) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(a);
		msg.setConversationId(MsgType.TURN_OFF_ORDER.toString());
		msg.setContent(((Long) TimeHandler.getInstance().getCalendar()
				.getTimeInMillis()).toString());
		return msg;
	}

	/**
	 * Determine the agent with the lesser priority between SOMEONE_IN_ROOM ...
	 * SOMEONE_IN_4 and NOTHING, and for the same priority type, basing on the
	 * level.
	 * 
	 * @param min_temp
	 *            true if heaters heating because temp < minTemp should be
	 *            reduced power. False otherwise.
	 * @return
	 */
	private AID getMinPriorityAid(boolean min_temp) {
		AID min_priority_aid = null;
		int max_type = 0, min_level = Integer.MAX_VALUE;
		PriorityData p;

		for (AID curr_aid : priorities.keySet()) {
			p = priorities.get(curr_aid);

			if (min_temp || p.priority_type != Priority.MIN_TEMP.getType()) {

				if (p.priority_type > max_type) {
					min_priority_aid = curr_aid;
					max_type = p.priority_type;
					min_level = p.priority_level;
				} else if (p.priority_type == max_type
						&& p.priority_level < min_level) {
					min_priority_aid = curr_aid;
					min_level = p.priority_level;
				}
			}

		}
		return min_priority_aid;
	}

	/**
	 * Create a reduction power Order specifying the reduction factor. Message
	 * of the following form:
	 * timestamp(long):reductionFactor(float):reply(boolean)
	 * 
	 * @param a
	 * @param f
	 * @return
	 */
	private ACLMessage createReducePowOrderMessage(AID a, float f) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(a);
		msg.setConversationId(MsgType.REDUCTION_ORDER.toString());

		Boolean b = false;

		String str = ((Long) TimeHandler.getInstance().getCalendar()
				.getTimeInMillis()).toString();
		str += ":" + f;
		str += ":" + b.toString();

		msg.setContent(str);

		return msg;
	}

	@Override
	public boolean done() {
		return false;
	}

	private class PriorityData {
		PriorityData() {
		}

		ACLMessage msg;
		Priority priority;
		Integer priority_type, priority_level;
		Integer energy;
	}

	private void printMex(ACLMessage msg) {
		String str = "From: " + msg.getSender().getName().split("@")[0];
		str += " To: Referee";
		str += " => " + msg.getContent();
		if (Settings.isLog())
			System.out.println(str);
	}

}
