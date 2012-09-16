package environment;

import agents.RefereeAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.PeopleSimulator;
import utils.Settings;
import utils.XMLHandler;
import graphics.GraphicEnvironment;

/**
 * Main class of the Heating Project
 * 
 * @author falkor
 * 
 */
public class Heating {
	public static boolean exit;

	public static ContainerController cc;

	public static void main(String[] args) throws InterruptedException {
		jade.core.Runtime rt = jade.core.Runtime.instance();
		Profile p = new ProfileImpl();

		p.setParameter(Profile.LOCAL_HOST, "127.0.0.1");
		p.setParameter(Profile.GUI, "");

		cc = rt.createMainContainer(p);

		// Initialize environment
		Settings.init();
		SolarPowHandler.init();

		new Environment();
		new GraphicEnvironment();

		GraphicEnvironment.repaintAll();

		/************ Jade ************/

		Object[] args_array = new Object[0];
		try {
			AgentController referee = Heating.cc.createNewAgent("referee",
					RefereeAgent.class.getName(), args_array);
			referee.start();
		} catch (StaleProxyException e1) {
			e1.printStackTrace();
		}

		// give the referee agent a bit of millis to initialize itself and
		// register to the DF service!
		Thread.sleep(3000);

		for (Room room : Environment.rooms.values()) {
			room.getAgent().firstTimeStart();
		}

		/************ Jade ************/

		exit = false;
		// main loop

		if (Settings.isPeople_simulation())
			new PeopleSimulator();

		while (!exit) {
			Environment.update();
			GraphicEnvironment.repaintAll();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		performExitOperations();
	}

	private static void performExitOperations() {
		XMLHandler.createSettingsXMLFile();
		XMLHandler.createRoomsXMLFile();
		XMLHandler.createThingsXMLFile();
		XMLHandler.createExternalTempFile();

		for (Room curr_room : Environment.rooms.values())
			curr_room.getAgent().writeStatsFile();

		System.out.println();
		System.out.println("Everything Written Correctly");
		System.out.println("Bye Bye to next session!");
		Runtime.getRuntime().exit(0);
	}

	/**
	 * @param exit
	 *            the exit to set
	 */
	public static void setExit(boolean exit) {
		Heating.exit = exit;
	}

}
