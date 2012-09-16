package agents.behaviours;

import environment.Environment;
import environment.TimeHandler;
import utils.Settings;
import utils.Enums.MsgType;
import utils.Enums.Priority;
import agents.RefereeAgent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class WeakReductionReqBehaviour extends Behaviour {
	private static final long serialVersionUID = 1L;
	private RefereeAgent agent;

	private AID applicant;
	private ACLMessage applicant_mex;
	String[] applicant_values;

	private AID[] agents;
	private long time, min_elapsed_time = 3000;
	private int step;
	private java.util.HashMap<AID, PriorityData> priorities;

	public WeakReductionReqBehaviour(RefereeAgent a, ACLMessage mex) {
		this.agent = a;
		step = 0;
		applicant = mex.getSender();
		applicant_mex = mex;
		applicant_values = mex.getContent().split(":");

		if (Settings.isLog()) {
			System.out.println();
			System.out.println("Weak Reduction Behaviour");
			System.out.println("Applicant mex: ");
			printMex(applicant_mex);
			System.out.println();
		}
	}

	@Override
	public void action() {
		if (Settings.isLog())
			System.out.print("WEAKreductionBehaviour STEP " + step + "   ");
		switch (step) {
		// 1) broadcast request of current state, including the date.
		case 0:
			if (Settings.isLog())
				System.out.println("Sending Priority Requests");
			sendBroadcastPriorityRequests(applicant);
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
				step = 6;
			else
				step++;
			break;
		case 3:
			if (Settings.isLog())
				System.out.print(" can someone be reduced power?");
			// 3) select the smallest priority, basing on p. type and value.
			// Moreover, the agent must be heating since >= 1 time interval.
			// 3.b) send reduction power order
			AID reducing_aid;
			boolean send = false;

			if ((reducing_aid = canReducePower()) != null) {

				PriorityData p = priorities.get(reducing_aid);

				if (p.priority_type > Integer.parseInt(applicant_values[1]))
					send = true;
				else if (p.priority_type == Integer
						.parseInt(applicant_values[1])
						&& p.priority_level < Integer
								.parseInt(applicant_values[2]))
					send = true;

				if (send) {
					if (Settings.isLog())
						System.out.print("Yes:  "
								+ reducing_aid.getName().split("@")[0] + ", ");
					this.agent
							.send(createWeakReducePowOrderMessage(reducing_aid));
					step++;
					time = System.currentTimeMillis();
					block(min_elapsed_time);
					return;
				} else {
					step = 6;
					if (Settings.isLog())
						System.out.println("NOPE!");
				}
			} else {
				step = 6;
				if (Settings.isLog())
					System.out.println("NO!");
			}
			break;
		case 4:
			boolean printed = false;
			if (System.currentTimeMillis() - time < min_elapsed_time) {
				if (Settings.isLog() && !printed) {
					System.out.println("Waiting for Reduction Confirmation");
					printed = true;
				}
				block(min_elapsed_time - (System.currentTimeMillis() - time));
				return;
			} else {
				step++;
			}
			break;
		case 5:
			if (Settings.isLog())
				System.out
						.println("Receiving Message of reducing confirmation");
			ACLMessage msg;
			while ((msg = agent.receive(MessageTemplates.reduction_order_templ)) != null) {
				printMex(msg);
				if (TimeHandler.getInstance().isActual(
						Long.parseLong(msg.getContent()))) {
					if (Settings.isLog())
						System.out.println(":  Accepted");
					this.agent.send(createOkWeakRequestWithEnergy());
					block(2000);
					return;
				}
			}
			step++;
			break;
		case 6:
			this.agent.removeBehaviour(this);
			this.agent.addBehaviour(RefereeBehaviour.getInstance(null));
			// while (agent.receive() != null);
			if (Settings.isLog())
				System.out.println("Referee Behaviour");
			break;
		default:
			System.err.println("ReductionRequest bad state error");
			step = 6;
			break;
		}
	}

	/**
	 * Retrieve the list of all the agents, and request them their current
	 * priority
	 */
	private void sendBroadcastPriorityRequests(AID applicant) {
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

		// send the message to everybody except the agent requesting for energy
		for (AID current_agent : agents) {
			if (current_agent != applicant)
				this.agent.send(createPriorityRequestMessage(current_agent));
		}

	}

	/**
	 * Creates a Message of priority request timestamp(long)
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
		java.util.HashMap<AID, PriorityData> priorities = new java.util.HashMap<AID, WeakReductionReqBehaviour.PriorityData>();
		ACLMessage msg;
		long date;
		String[] content_values;
		PriorityData p;

		if (Settings.isLog())
			System.out.println("Priority replies: ");
		while ((msg = this.agent
				.receive(MessageTemplates.priority_request_templ)) != null) {
			content_values = msg.getContent().split(":");
			if (Settings.isLog())
				printMex(msg);

			date = Long.parseLong(content_values[3]);
			if (TimeHandler.getInstance().isActual(date)) {

				if (!priorities.containsValue(msg.getSender())) {
					p = new PriorityData();
					p.msg = msg;
					p.priority = Priority.valueOf(content_values[0]);
					p.priority_type = Integer.parseInt(content_values[1]);
					p.priority_level = Integer.parseInt(content_values[2]);
					p.energy = Integer.parseInt(content_values[4]);
					p.heating_since = Integer.parseInt(content_values[5]);

					if (Settings.isLog())
						System.out.println("   => " + p.toString());

					priorities.put(msg.getSender(), p);
				}
			}
			System.out.println();
		}
		return priorities;
	}

	/**
	 * Determine the agent with the lesser priority between SOMEONE_IN_ROOM ...
	 * SOMEONE_IN_4 and NOTHING, and for the same priority type, basing on the
	 * level.
	 * 
	 * @return
	 */
	private AID canReducePower() {
		AID min_priority_aid = null;
		int max_type = 0, min_level = Integer.MAX_VALUE;
		PriorityData p;

		for (AID curr_aid : priorities.keySet()) {
			p = priorities.get(curr_aid);

			if (p.heating_since >= 1
					|| p.priority_type != Priority.NOTHING.getType()) {
				if (p.priority_type != Priority.MIN_TEMP.getType()) {

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
		}

		return min_priority_aid;
	}

	/**
	 * Create a weak reduction order message, meaning that the receiver has to
	 * turn down heating of 1/3
	 * timestamp(long):reductionFactor(float):reply(boolean)
	 * 
	 * @param a
	 * @return
	 */
	private ACLMessage createWeakReducePowOrderMessage(AID a) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(a);
		msg.setConversationId(MsgType.REDUCTION_ORDER.toString());

		float f = 0.3F;
		Boolean t = true;

		String content = ((Long) TimeHandler.getInstance().getCalendar()
				.getTimeInMillis()).toString();
		content += ":" + f;
		content += ":" + t.toString();

		msg.setContent(content);

		return msg;
	}

	/**
	 * Creates a Message of Request-Acceptance. Its form is as follow:
	 * TimeStamp(long):available_energy(int) This message means that the
	 * applicant can use at most the indicated energy. Take in mind that it will
	 * use as few as possible energy.
	 * timestamp(long):available_energy(int):priority(string)
	 * 
	 * @return
	 */
	private ACLMessage createOkWeakRequestWithEnergy() {
		ACLMessage msg = applicant_mex.createReply();

		int available_energy = Environment.getAvailableEnergy()
				- Settings.getMin_energy();

		String content = ((Long) TimeHandler.getInstance().getCalendar()
				.getTimeInMillis()).toString();
		content += ":" + available_energy;
		content += ":" + applicant_values[0];

		msg.setContent(content);
		return msg;
	}

	@Override
	public boolean done() {
		return false;
	}

	@SuppressWarnings("unused")
	private class PriorityData {
		PriorityData() {
		}

		ACLMessage msg;
		Priority priority;
		Integer priority_type, priority_level;
		Integer energy;
		Integer heating_since;

		@Override
		public String toString() {
			return "Priority: " + priority.toString() + "(type "
					+ priority_type + ", level " + priority_level + ") energy "
					+ energy + " heating since " + heating_since;
		}

	}

	private void printMex(ACLMessage msg) {
		String str = "From: " + msg.getSender().getName().split("@")[0];
		str += " To: Referee";
		str += " => " + msg.getContent();
		if (Settings.isLog())
			System.out.print(str);
		;
	}

}
