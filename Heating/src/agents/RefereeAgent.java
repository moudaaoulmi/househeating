package agents;

import agents.behaviours.RefereeBehaviour;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class RefereeAgent extends Agent {
	private static final long serialVersionUID = 1567488210570886707L;

	private static float overall_performance_measure;

	// public RefereeAgent() {
	// }

	/**
	 * @return the overall_performance_measure
	 */
	public static float getOverall_performance_measure() {
		return overall_performance_measure;
	}

	/************ Jade ************/

	@Override
	protected void setup() {
		// Register the book-selling service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

		ServiceDescription sd = new ServiceDescription();
		sd.setType("referee");
		sd.setName("Heating-Referee");

		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		addBehaviour(RefereeBehaviour.getInstance(this));

		System.out.println("Created Referee Agent. Enjoy Auto-Heating service!");
	}

	@Override
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("Terminated Referee Agent!");
	}

	public static String getAgentID() {
		return "referee";
	}
	
}
