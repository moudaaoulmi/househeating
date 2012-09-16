package graphics;

import java.awt.Dimension;

import javax.swing.JRootPane;

import listeners.MyHouseFrameWindowListener;

import environment.Environment;
import environment.Floor;
import environment.Room;

public class GraphicEnvironment {

	static MyFrame house, room, settings;
	private static Floor current_floor;
	private static Room current_room;
	private static int multiplicative_factor = 34;

	private static RoomPanel room_panel;
	private static SettingsPanel settings_panel;

	private static Integer simulation_speed;

	public GraphicEnvironment() {
		current_floor = Environment.floors.get(1);
		current_room = Environment.floors.get(1).getRooms().get(0);

		simulation_speed = 0;
		
		initAllFrames();
		initHouseFrame();
		initRoomFrame();
		initSettingsFrame();
	}

	private void initSettingsFrame() {
		settings_panel = new SettingsPanel();
		settings_panel.setSize(settings.getSize());
		settings_panel.setVisible(true);
		settings.add(settings_panel);
		settings_panel.repaint();
	}

	private void initRoomFrame() {
		room_panel = new RoomPanel(current_room);
		room_panel.setSize(room.getSize());
		room_panel.setVisible(true);
		room.add(room_panel);
		room_panel.repaint();
	}

	private void initHouseFrame() {

		HousePanel house_panel = new HousePanel(house);

		house.add(house_panel);
		house.setPanel_container(house_panel);
		house_panel.setSize(house.getSize());

	}

	private static void initAllFrames() {
		int house_x_dim = 650, house_y_dim = 640;
		int room_x_dim = 580, room_y_dim = 402;
		int settings_x_dim = 580, settings_y_dim = 228;
		int initial_x_pos = 50, initial_y_pos = 50;
		int bound = 10;

		// init frames
		house = new MyFrame();
		house.setTitle("House");
		house.setUndecorated(true);
		house.getRootPane().setWindowDecorationStyle(
				JRootPane.INFORMATION_DIALOG);
		house.setVisible(true);
		house.addWindowListener(new MyHouseFrameWindowListener());

		room = new MyFrame();
		room.setTitle("Room");
		room.setUndecorated(true);
		room.getRootPane().setWindowDecorationStyle(
				JRootPane.INFORMATION_DIALOG);
		room.setVisible(true);

		settings = new MyFrame();
		settings.setTitle("Settings");
		settings.setUndecorated(true);
		settings.getRootPane().setWindowDecorationStyle(
				JRootPane.INFORMATION_DIALOG);
		settings.setVisible(true);

		// set frame sizes
		house.setSize(new Dimension(house_x_dim, house_y_dim));
		room.setSize(new Dimension(room_x_dim, room_y_dim));
		settings.setSize(new Dimension(settings_x_dim, settings_y_dim));

		// set frames locations
		house.setLocation(initial_x_pos, initial_y_pos);
		room.setLocation(house_x_dim + initial_x_pos + bound, initial_y_pos);
		settings.setLocation(house_x_dim + initial_x_pos + bound, initial_y_pos
				+ room_y_dim + bound);

	}

	/**
	 * @return the current_floor
	 */
	public static Floor getCurrent_floor() {
		return current_floor;
	}

	/**
	 * @param current_floor
	 *            the current_floor to set
	 */
	public static void setCurrent_floor(Floor current_floor) {
		GraphicEnvironment.current_floor = current_floor;
	}

	/**
	 * @return the current_room
	 */
	public static Room getCurrentRoom() {
		return current_room;
	}

	/**
	 * @param current_room
	 *            the current_room to set
	 */
	public static void setCurrent_room(Room current_room) {
		GraphicEnvironment.current_room = current_room;
	}

	/**
	 * @return the multiplicative_factor
	 */
	public static int getMultiplicative_factor() {
		return multiplicative_factor;
	}

	/**
	 * @param multiplicative_factor
	 *            the multiplicative_factor to set
	 */
	public static void setMultiplicative_factor(int multiplicative_factor) {
		GraphicEnvironment.multiplicative_factor = multiplicative_factor;
	}

	/**
	 * @return the room_name_panel
	 */
	public static RoomPanel getRoom_panel() {
		return room_panel;
	}

	public static void repaintAll() {
		HousePanel.updateInfo();
		house.getContentPane().validate();
		house.getContentPane().repaint();
		HousePanel.getDate_and_time_panel().repaint();
		room.getContentPane().validate();
		room.getContentPane().repaint();
		room_panel.repaintAll();
		SettingsPanel.updateValues();
		settings.getContentPane().validate();
		settings.getContentPane().repaint();
	}

	/**
	 * @return the simulation_speed
	 */
	public static Integer getSimulation_speed() {
		return simulation_speed;
	}

	/**
	 * @param simulation_speed
	 *            the simulation_speed to set
	 */
	public static void setSimulation_speed(Integer simulation_speed) {
		GraphicEnvironment.simulation_speed = simulation_speed;
	}

}
