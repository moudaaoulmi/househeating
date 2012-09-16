package agents.behaviours;

import utils.Settings;
import agents.Agent;
import jade.core.behaviours.TickerBehaviour;

/**
 * Behaviour that on each tick cleans the agent's queue of messages
 * 
 * @author falkor
 * 
 */
public class DoNothingBehaviour extends TickerBehaviour {
	private static final long serialVersionUID = 1L;

	agents.Agent agent;

	public DoNothingBehaviour(agents.Agent a, long period) {
		super(a, period);
		this.agent = a;
	}

	@Override
	protected void onTick() {
		if (this.agent.isAutoPowerOn()) {
			agent.removeBehaviour(this);

			if (!agent.isCheckOnlyMinTemp()) {
				agent.addBehaviour(agent.heater_behaviour);
				if (Settings.isLog())
					System.out.println("Agent "
							+ Agent.getAgentName(this.agent.getId())
							+ ": Heater Behaviour");

			} else {
				agent.addBehaviour(agent.check_only_min_temp_behaviour);
				if (Settings.isLog())
					System.out.println("Agent "
							+ Agent.getAgentName(this.agent.getId())
							+ ": Check Only Min Temp Behaviour");
			}

			agent.setHeating(false);
			agent.setCurrentPowerConsumption(0);
		}
		while (agent.receive() != null)
			;
	}

}
