package agents.behaviours;

import utils.Settings;
import agents.RefereeAgent;
import environment.Environment;
import environment.TimeHandler;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class RefereeBehaviour extends Behaviour {
	private static final long serialVersionUID = 1L;

	private static RefereeBehaviour instance;

	// preserve the presence of a minimum value of energy
	private int available_energy, released_energy;
	private RefereeAgent agent;
	private ACLMessage mex;

	private RefereeBehaviour(RefereeAgent a) {
		this.agent = a;
	}

	public static RefereeBehaviour getInstance(RefereeAgent a) {
		if (instance == null)
			instance = new RefereeBehaviour(a);
		return instance;
	}

	@Override
	public void action() {
		// 1) is available energy > min_energy?

		// 1.a) No, it isn't. There's too much energy consumption. Reduce it.
		if (Environment.getAvailableEnergy() < Settings.getMin_energy()) {
			if (Settings.isLog()) System.out.println("There's too much energy consumption. Reduce it.");
			this.agent.addBehaviour(new ReductionRequestBehaviour(this.agent));
			this.agent.removeBehaviour(this);
			return;
		}
		// 1.b) Yes, it is!
		else {
			// get energy available for heating
			available_energy = Environment.getAvailableEnergy()
					- Settings.getMin_energy();

			// 2) check for new energy request messages
			// P.name(string):P.type(int):P.level(int):timestamp(long):energy(int)
			released_energy = 0;
			while ((mex = agent.receive(MessageTemplates.energy_request_templ)) != null) {
				printMex(mex);

				String[] content_values = mex.getContent().split(":");
				if (TimeHandler.getInstance().isActual(
						Long.parseLong(content_values[3]))) {
					if (Settings.isLog()) System.out.println(" :    Accepted!");

					int requested_energy = Integer.parseInt(content_values[4]);
					// 3.a) it is satisfiable :)
					if (available_energy - released_energy >= requested_energy) {
						this.agent.send(createRequestOkMessage(mex,
								available_energy, content_values[0]));
						released_energy += requested_energy;
//						while (agent.receive() != null);
					}
					// 3.b) it is not satisfiable at all :/
					else {
						this.agent.removeBehaviour(this);
						this.agent.addBehaviour(new WeakReductionReqBehaviour(
								agent, mex));
						return;
					}

				}
				else
					if (Settings.isLog()) System.out.println(" :  REFUSED");

			}
			block(500);
		}
	}

	/**
	 * Creates a Message meaning that the energy request was satisfiable, so the
	 * heater can be turned on. The message has the following structure:
	 * timestamp(long):available_energy(int):Priority.name(string)
	 * 
	 * @param mex
	 * @param available_energy
	 * @param priority
	 * @return
	 */
	private ACLMessage createRequestOkMessage(ACLMessage mex,
			int available_energy, String priority) {
		ACLMessage msg = mex.createReply();		
		String str = ((Long) TimeHandler.getInstance().getCalendar()
				.getTimeInMillis()).toString();
		str += ":" + available_energy;
		str += ":" + priority;

		msg.setContent(str);

		return msg;
	}
	
	private void printMex(ACLMessage msg) {
		String str = "From: " + msg.getSender().getName().split("@")[0];
		str += " To: Referee";
		str += " => " + msg.getContent();
		if (Settings.isLog()) System.out.print(str);
	}

	/**
	 * Never stop the behaviour, since the referee has to be active all the
	 * time, ready to receive the new requests of energy.
	 */
	@Override
	public boolean done() {
		return false;
	}

}
