package utils;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import environment.Environment;
import environment.Floor;
import environment.Room;
import environment.Thing;

import utils.Enums.Day;
import utils.Enums.Month;

import agents.Agent;

@SuppressWarnings("rawtypes")
public class XMLHandler {

	/**
	 * Writes the settings file
	 */
	public static void createSettingsXMLFile() {

		Document doc = new Document();

		Element settings = new Element("Settings");

		Element interval_length = new Element("interval_length");
		interval_length.setText(Settings.getInterval_length().toString());

		Element max_power_consumption = new Element("max_power_consumption");
		max_power_consumption.setText(Settings.getMax_power_consumption()
				.toString());

		Element min_temperature = new Element("min_temperature");
		min_temperature.setText(Settings.getMin_temperature().toString());

		Element max_temperature = new Element("max_temperature");
		max_temperature.setText(Settings.getMax_temperature().toString());

		Element enel_energy = new Element("enel_energy");
		enel_energy.setText(Settings.getEnel_energy().toString());

		Element max_solar_energy = new Element("max_solar_energy");
		max_solar_energy.setText(Settings.getMax_solar_energy().toString());

		Element stats_interval = new Element("stats_interval");
		stats_interval.setText(Settings.getStats_interval().toString());

		Element rooms_height = new Element("rooms_height");
		rooms_height.setText(Settings.getRooms_height().toString());

		Element min_energy = new Element("min_energy");
		min_energy.setText(Settings.getMin_energy().toString());

		Element log = new Element("log");
		log.setText(((Boolean) Settings.isLog()).toString());

		Element people_simulation = new Element("people_simulation");
		people_simulation.setText(((Boolean) Settings.isPeople_simulation())
				.toString());

		settings.addContent(interval_length);
		settings.addContent(max_power_consumption);
		settings.addContent(min_temperature);
		settings.addContent(max_temperature);
		settings.addContent(enel_energy);
		settings.addContent(max_solar_energy);
		settings.addContent(stats_interval);
		settings.addContent(rooms_height);
		settings.addContent(min_energy);
		settings.addContent(log);
		settings.addContent(people_simulation);

		// create the basic XML settings file
		doc.setRootElement(settings);

		try {
			writeDoc(doc, Files.settings_file);
			System.out.println("Created " + Files.settings_file + " file");
			// Format f = Format.getPrettyFormat();
			// XMLOutputter outputter = new XMLOutputter(f);
			// System.out.println(outputter.outputString(doc));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Reads the settings file
	 * 
	 * @return
	 */
	public static void readSettingsFile() {

		Document doc = readDoc(Files.settings_file);

		Element root = doc.getRootElement();

		Settings.setInterval_length(Integer.parseInt(root.getChild(
				"interval_length").getText()));
		Settings.setMax_power_consumption(Integer.parseInt(root.getChild(
				"max_power_consumption").getText()));
		Settings.setMin_temperature(Integer.parseInt(root.getChild(
				"min_temperature").getText()));
		Settings.setMax_temperature(Integer.parseInt(root.getChild(
				"max_temperature").getText()));
		Settings.setEnel_energy(Integer.parseInt(root.getChild("enel_energy")
				.getText()));
		Settings.setMax_solar_energy(Integer.parseInt(root.getChild(
				"max_solar_energy").getText()));
		Settings.setStats_interval(Integer.parseInt(root.getChild(
				"stats_interval").getText()));
		Settings.setRooms_height(Integer.parseInt(root.getChild("rooms_height")
				.getText()));
		Settings.setMin_energy(Integer.parseInt(root.getChild("min_energy")
				.getText()));
		Settings.setPeople_simulation(Boolean.parseBoolean(root.getChild(
				"people_simulation").getText()));
		Settings.setLog(Boolean.parseBoolean(root.getChild("log").getText()));
	}

	{
		/**
		 * Writes the file containing the power consumptions stats for each
		 * agent
		 * 
		 * @param agents
		 */
		// public static void createTimeFile(HashMap<Integer, Agent> agents) {
		//
		// Document doc = new Document();
		//
		// Element root = new Element("Power_consumptions");
		//
		// Element elem, child;
		//
		// Agent curr_agent = null;
		//
		// // take one agent per time
		// for (Integer agent_num : agents.keySet()) {
		//
		// curr_agent = agents.get(agent_num);
		// elem = new Element("Agent_" + curr_agent.getId());
		//
		// // take agent's pow_consumption values and write them
		// for (Integer curr_time_interval : curr_agent.getPow_consumption()
		// .keySet()) {
		// child = new Element(curr_time_interval.toString());
		// child.setText(curr_agent.getPow_consumption()
		// .get(curr_time_interval).toString());
		//
		// elem.addContent(child);
		// }
		//
		// root.addContent(elem);
		// }
		//
		// // create the XML power consumption file
		//
		// doc.setRootElement(root);
		//
		// try {
		// writeDoc(doc, Files.agent_consumptions_file);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// }
	}

	/**
	 * Writes the file containing an estimate of the energy produced by the
	 * solar panels (to fill manually month by month)
	 */
	public static void createSolarPowXMLFile() {
		Document doc = new Document();

		Element solar_pow = new Element("Solar_pow");

		// create the basic XML settings file

		doc.setRootElement(solar_pow);

		Element current_day, time_interval, medium_external_temp;

		for (Enums.Month curr_month : Month.values()) {
			current_day = new Element(curr_month.toString());

			medium_external_temp = new Element("external_temp");
			medium_external_temp.setText(Environment.external_medium_temp.get(
					curr_month).toString());
			current_day.addContent(medium_external_temp);

			for (int i = 1; i <= 48; i++) {
				time_interval = new Element("interval_"
						+ ((Integer) i).toString());
				time_interval.setText("0");
				current_day.addContent(time_interval);
			}
			solar_pow.addContent(current_day);
		}

		try {
			writeDoc(doc, Files.solar_pow_file);
			System.out.println("Created " + Files.solar_pow_file + " file");
			// Format f = Format.getPrettyFormat();
			// XMLOutputter outputter = new XMLOutputter(f);
			// System.out.println(outputter.outputString(doc));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Reads the file containing an estimate of the energy produced by the solar
	 * panels (to fill manually month by month)
	 * 
	 * @return
	 */
	public static HashMap<Month, HashMap<Integer, Integer>> readSolarPowXMLFile() {

		HashMap<Month, HashMap<Integer, Integer>> values = new HashMap<Month, HashMap<Integer, Integer>>();

		Document doc = readDoc(Files.solar_pow_file);
		Element root = doc.getRootElement();

		HashMap<Integer, Integer> current_month_values = null;

		for (Month current_month : Month.values()) {

			current_month_values = new HashMap<Integer, Integer>();
			for (int i = 1; i <= 48; i++) {
				current_month_values.put(i, Integer
						.parseInt(root.getChild(current_month.toString())
								.getChildText("interval_" + i)));
			}
			values.put(current_month, current_month_values);
		}

		return values;
	}

	/**
	 * Writes the file containing an estimate of medium temperature for each
	 * month of the year
	 */
	public static void createExternalTempFile() {
		Document doc = new Document();

		Element temperatures = new Element("External_temp");

		// create the basic XML settings file
		doc.setRootElement(temperatures);

		Element month;

		for (Enums.Month curr_month : Month.values()) {
			month = new Element(curr_month.toString());

			month.setText(Environment.external_medium_temp.get(curr_month)
					.toString());

			temperatures.addContent(month);
		}

		try {
			writeDoc(doc, Files.external_temp_file);
			System.out.println("Created " + Files.external_temp_file + " file");
			// Format f = Format.getPrettyFormat();
			// XMLOutputter outputter = new XMLOutputter(f);
			// System.out.println(outputter.outputString(doc));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Reads the file containing an estimate of medium temperature for each
	 * month of the year
	 * 
	 * @return
	 */
	public static HashMap<Month, Integer> readExternalTempFile() {

		HashMap<Month, Integer> external_temperature = new HashMap<Enums.Month, Integer>();

		Document doc = readDoc(Files.external_temp_file);
		Element root = doc.getRootElement();

		for (Month current_month : Month.values())
			external_temperature.put(current_month, Integer.parseInt(root
					.getChildText(current_month.toString())));

		return external_temperature;
	}

	/**
	 * Given the rooms, produces an xml file containing all their specifications
	 * 
	 * @param rooms
	 */
	public static void createRoomsXMLFile() {

		Element root = new Element("Rooms");

		for (Room room : Environment.rooms.values())
			root.addContent(getRoomXMLElement(room));

		Document doc = new Document();
		doc.setRootElement(root);

		try {
			writeDoc(doc, Files.rooms_file);
			System.out.println("Created " + Files.rooms_file + " file");
			// Format f = Format.getPrettyFormat();
			// XMLOutputter outputter = new XMLOutputter(f);
			// System.out.println(outputter.outputString(doc));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static Element getRoomXMLElement(Room room) {
		Element root = new Element("Room");

		Element id = new Element("id");
		id.setText(room.getId().toString());
		root.addContent(id);

		Element name = new Element("name");
		name.setText(room.getName());
		root.addContent(name);

		Element sq_m = new Element("squared_meters");
		sq_m.setText(room.getSquared_meters().toString());
		root.addContent(sq_m);

		Element x_coord = new Element("x_coordinates");
		Element y_coord = new Element("y_coordinates");
		root.addContent(x_coord);
		root.addContent(y_coord);

		String textual_x_coord = "";
		for (Integer curr : room.getX_coord())
			textual_x_coord += curr.toString() + ",";
		textual_x_coord = textual_x_coord.substring(0,
				textual_x_coord.length() - 1);
		x_coord.setText(textual_x_coord);

		String textual_y_coord = "";
		for (Integer curr : room.getY_coord())
			textual_y_coord += curr.toString() + ",";
		textual_y_coord = textual_y_coord.substring(0,
				textual_y_coord.length() - 1);
		y_coord.setText(textual_y_coord);

		Element x_pos = new Element("x_pos");
		Element y_pos = new Element("y_pos");
		root.addContent(x_pos);
		root.addContent(y_pos);

		x_pos.setText(room.getInitial_x_pos().toString());
		y_pos.setText(room.getInitial_y_pos().toString());

		Element color = new Element("color");
		String textual_color = "";
		textual_color += room.getColor().getRed() + ",";
		textual_color += room.getColor().getGreen() + ",";
		textual_color += room.getColor().getBlue();
		color.setText(textual_color);
		root.addContent(color);

		Element thing;
		for (String curr : room.getThings()) {
			thing = new Element("thing");
			thing.setText(curr);
			root.addContent(thing);
		}

		Element temp = new Element("current_temp");
		Float f = room.getCurrent_temperature();
		DecimalFormat dec_format = new DecimalFormat("##.#");
		temp.setText(dec_format.format(f).replace(",", "."));
		root.addContent(temp);

		Element num_of_people = new Element("num_of_people");
		num_of_people.setText(room.getNum_of_people().toString());
		root.addContent(num_of_people);

		Element energy_absorbed_by_room = new Element("energy_absorbed_by_room");
		energy_absorbed_by_room.setText(room.getIsolationValue().toString());
		root.addContent(energy_absorbed_by_room);

		Element desired_temp = new Element("desired_temp");
		desired_temp.setText(room.getDesired_temp().toString());
		root.addContent(desired_temp);

		Element min_temp = new Element("min_temp");
		min_temp.setText(room.getMin_temp().toString());
		root.addContent(min_temp);

		Element agent = new Element("agent");
		if (room.getAgent().isAutoPowerOn())
			agent.setText("ON");
		else
			agent.setText("OFF");
		root.addContent(agent);

		Element max_power = new Element("agent_max_power");
		max_power.setText(room.getAgent().getMaxPowConsumption().toString());
		root.addContent(max_power);

		Element check_only_min_temp = new Element("check_only_min_temp");
		check_only_min_temp.setText(((Boolean) room.getAgent()
				.isCheckOnlyMinTemp()).toString());
		root.addContent(check_only_min_temp);

		return root;
	}

	/**
	 * Reads the xml file containing the specifications of the rooms and
	 * instantiates the corresponding room objects
	 * 
	 * @return
	 */
	public static ArrayList<Room> readRoomsXMLFile() {

		ArrayList<Room> rooms = new ArrayList<Room>();

		Document doc = readDoc(Files.rooms_file);
		Element root = doc.getRootElement();

		java.util.Iterator itr = (root.getChildren("Room")).iterator();
		while (itr.hasNext()) {
			rooms.add(getRoom((Element) itr.next()));
		}

		return rooms;
	}

	/**
	 * get a single room from the related xml element
	 * 
	 * @param root
	 * @return
	 */
	private static Room getRoom(Element root) {

		Room room = new Room();

		room.setId(Integer.parseInt(root.getChildText("id")));

		room.setName(root.getChildText("name"));

		room.setSquared_meters(Integer.parseInt(root
				.getChildText("squared_meters")));

		room.setInitial_x_pos(Integer.parseInt(root.getChildText("x_pos")));
		room.setInitial_y_pos(Integer.parseInt(root.getChildText("y_pos")));

		String[] x_textual_coords = root.getChildText("x_coordinates").split(
				",");
		;
		String[] y_textual_coords = root.getChildText("y_coordinates").split(
				",");
		;

		Integer[] x_coords = new Integer[x_textual_coords.length];
		Integer[] y_coords = new Integer[y_textual_coords.length];

		if (x_coords.length != y_coords.length)
			try {
				throw new Exception("Coordinates lenghts mismatch");
			} catch (Exception e) {
				System.err.println("Coordinates don't match for room "
						+ root.getChildText("name"));
				e.printStackTrace();
			}

		for (int i = 0; i < x_textual_coords.length; i++) {
			x_coords[i] = Integer.parseInt(x_textual_coords[i]);
			y_coords[i] = Integer.parseInt(y_textual_coords[i]);
		}

		room.setX_coord(x_coords);
		room.setY_coord(y_coords);

		String[] textual_color = root.getChildText("color").split(",");
		int r = Integer.parseInt(textual_color[0]);
		int g = Integer.parseInt(textual_color[1]);
		int b = Integer.parseInt(textual_color[2]);

		Color color = new Color(r, g, b);
		room.setColor(color);

		ArrayList<String> things = new ArrayList<String>();

		java.util.Iterator itr = (root.getChildren("thing")).iterator();
		while (itr.hasNext())
			things.add(((Element) itr.next()).getText());

		room.setThings(things);

		room.setCurrent_temperature(Float.parseFloat(root
				.getChildText("current_temp")));

		room.setNum_of_people(Integer.parseInt(root
				.getChildText("num_of_people")));

		room.setIsolationValue(Integer.parseInt(root
				.getChildText("energy_absorbed_by_room")));

		room.setDesired_temp(Integer.parseInt(root.getChildText("desired_temp")));

		room.setMin_temp(Integer.parseInt(root.getChildText("min_temp")));

		int max_power = Integer.parseInt(root.getChildText("agent_max_power"));

		boolean check_only_min_temp = Boolean.parseBoolean(root
				.getChildText("check_only_min_temp"));

		if (root.getChildText("agent").equals("ON")) {
			room.initAgent(true, max_power, check_only_min_temp);
		} else if (root.getChildText("agent").equals("OFF")) {
			room.initAgent(false, max_power, check_only_min_temp);
		} else
			try {
				throw new Exception("Undefined Agent State");
			} catch (Exception e) {
				System.err.println("Agent state set on "
						+ root.getChildText("agent") + " for room "
						+ room.getName());
				e.printStackTrace();
			}

		return room;
	}

	public static void createThingsXMLFile() {

		Document doc = new Document();
		Element root = new Element("Things");
		doc.setRootElement(root);

		Element current, tmp;

		for (Thing thing : Environment.things.values()) {
			current = new Element("thing");

			tmp = new Element("name");
			tmp.setText(thing.getName());
			current.addContent(tmp);

			tmp = new Element("thermal_power");
			tmp.setText(thing.getThermal_power().toString());
			current.addContent(tmp);

			tmp = new Element("energy");
			tmp.setText(thing.getEnergy().toString());
			current.addContent(tmp);

			root.addContent(current);
		}

		try {
			writeDoc(doc, Files.things_file);
			System.out.println("Created " + Files.things_file + " file");
			// Format f = Format.getPrettyFormat();
			// XMLOutputter outputter = new XMLOutputter(f);
			// System.out.println(outputter.outputString(doc));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Instantiates the things object reading their specification from the
	 * related xml file.
	 * 
	 * @return
	 */
	public static ArrayList<Thing> readThingsXMLFile() {
		ArrayList<Thing> things = new ArrayList<Thing>();

		Document doc = readDoc(Files.things_file);
		Element root = doc.getRootElement();

		String name;
		int thermal_power, energy;

		Element current;

		java.util.Iterator itr = (root.getChildren("thing")).iterator();
		while (itr.hasNext()) {
			current = (Element) itr.next();
			name = current.getChildText("name");
			thermal_power = Integer.parseInt(current
					.getChildText("thermal_power"));
			energy = Integer.parseInt(current.getChildText("energy"));
			things.add(new Thing(name, thermal_power, energy));
		}

		return things;
	}

	/**
	 * Create the file containing the floors and the rooms that evryone of them
	 * contains.
	 * 
	 * @param things
	 */
	public static void createFloorsXMLFile(ArrayList<Floor> floors) {

		Document doc = new Document();
		Element root = new Element("Floors");
		doc.setRootElement(root);

		Element current, tmp;

		for (Floor floor : floors) {
			current = new Element("Floor");

			tmp = new Element("num");
			tmp.setText(floor.getNum().toString());
			current.addContent(tmp);

			for (Room room : floor.getRooms()) {
				tmp = new Element("room");
				tmp.setText(room.getName());
				current.addContent(tmp);
			}

			root.addContent(current);
		}

		try {
			writeDoc(doc, Files.floors_file);
			System.out.println("Created " + Files.floors_file + " file");
			// Format f = Format.getPrettyFormat();
			// XMLOutputter outputter = new XMLOutputter(f);
			// System.out.println(outputter.outputString(doc));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Instantiates the floors reading their specification from the related xml
	 * file.
	 * 
	 * @return
	 */
	public static ArrayList<Floor> readFloorsXMLFile() {
		ArrayList<Floor> floors = new ArrayList<Floor>();

		Document doc = readDoc(Files.floors_file);
		Element root = doc.getRootElement();

		Element current;

		Integer num;
		String room_name;
		ArrayList<Room> rooms;

		java.util.Iterator itr = (root.getChildren("Floor")).iterator();
		while (itr.hasNext()) {
			current = (Element) itr.next();
			num = Integer.parseInt(current.getChildText("num"));

			rooms = new ArrayList<Room>();

			java.util.Iterator itr2 = (current.getChildren("room")).iterator();
			while (itr2.hasNext()) {
				room_name = ((Element) itr2.next()).getText();
				rooms.add(Environment.rooms.get(room_name));
			}

			floors.add(new Floor(num, rooms));

		}

		return floors;
	}

	/**
	 * Creates the stats file, i.e. a file that contains for each interval of
	 * time of each day the number of people in the room it is referred to.
	 * 
	 * @param stats
	 *            the agent weekly stats
	 */
	public static void createPeopleDataXMLFile(Agent a) {
		Integer id = a.getId();
		HashMap<Day, HashMap<Integer, Integer[]>> stats = a
				.getPeople_in_room_stats();

		Document doc = new Document();
		Element people = new Element("People");
		doc.setRootElement(people);

		Element current_day, time_interval;
		HashMap<Integer, Integer[]> values_for_that_day;
		Integer[] values_for_that_interval;

		for (Enums.Day curr_day : Day.values()) {
			current_day = new Element(curr_day.toString());

			values_for_that_day = stats.get(curr_day);

			for (int i = 1; i <= 48; i++) {
				values_for_that_interval = values_for_that_day.get(i);

				for (int j = 1; j <= values_for_that_interval.length; j++) {
					time_interval = new Element("interval_" + i + "_" + j);
					if (values_for_that_interval[j - 1] != null)
						time_interval.setText(values_for_that_interval[j - 1]
								.toString());
					else
						time_interval.setText("-1");

					current_day.addContent(time_interval);
				}
			}
			people.addContent(current_day);
		}

		try {
			File dir = new File(Files.agents_stats_dir);
			if (!dir.exists())
				dir.mkdir();

			writeDoc(doc, Files.agents_stats_dir + Files.agent_stats_file + id
					+ ".xml");
			System.out.println("Created " + Files.agents_stats_dir
					+ Files.agent_stats_file + id + ".xml" + " file");
			// Format f = Format.getPrettyFormat();
			// XMLOutputter outputter = new XMLOutputter(f);
			// System.out.println(outputter.outputString(doc));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Reads the file containing the simulation of the presence of people in the
	 * various rooms of the house in different moment of time.
	 * 
	 * @return
	 */
	public static HashMap<Day, HashMap<Integer, Integer[]>> readPeopleXMLFile(
			Integer id) {
		HashMap<Day, HashMap<Integer, Integer[]>> stats = new HashMap<Enums.Day, HashMap<Integer, Integer[]>>();

		File f = new File(Files.agents_stats_dir + Files.agent_stats_file + id
				+ ".xml");
		Document doc = null;
		Element root = null, curr_day_elem = null;

		if (f.exists()) {
			doc = readDoc(Files.agents_stats_dir + Files.agent_stats_file + id
					+ ".xml");
			root = doc.getRootElement();
		}

		for (Day curr_day : Day.values()) {
			if (root != null) {
				curr_day_elem = root.getChild(curr_day.toString());
			}

			HashMap<Integer, Integer[]> intervals_of_time = new HashMap<Integer, Integer[]>();

			for (int i = 1; i <= 48; i++) {
				Integer[] values = new Integer[Settings.getStats_interval() / 7];

				for (int j = 1; j <= Settings.getStats_interval() / 7; j++) {

					if (root != null
							&& curr_day_elem
									.getChild("interval_" + i + "_" + j) != null) {
						values[j - 1] = Integer.parseInt(curr_day_elem
								.getChildText("interval_" + i + "_" + j));
					} else {
						values[j - 1] = -1;
					}
				}

				intervals_of_time.put(i, values);
			}
			stats.put(curr_day, intervals_of_time);
		}

		return stats;
	}

	/**
	 * Reads the specified xml file and returns the related doc already parsed
	 * by jDom.
	 * 
	 * @param file_name
	 * @return
	 */
	public static Document readDoc(String file_name) {
		SAXBuilder parser = new SAXBuilder();
		Document doc = null;
		try {
			doc = parser.build(file_name);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return doc;
	}

	/**
	 * Writes on file the XML document passed
	 * 
	 * @param doc
	 * @param file_name
	 * @throws IOException
	 */
	public static void writeDoc(Document doc, String file_name)
			throws IOException {
		Format f = Format.getPrettyFormat();
		f.setIndent("  ");
		f.setLineSeparator("\n");
		XMLOutputter outputter = new XMLOutputter(f);

		FileWriter fstream = new FileWriter(file_name);

		outputter.output(doc, fstream);
	}

}
