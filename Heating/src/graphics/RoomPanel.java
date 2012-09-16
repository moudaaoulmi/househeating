package graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import agents.Agent;

import utils.Settings;

import environment.Room;

public class RoomPanel extends javax.swing.JPanel implements ChangeListener,
		ItemListener {
	private static final long serialVersionUID = 1L;

	private static JPanel name_panel, grid_panel, room_panel, agent_panel;
	private static JLabel room_name, temperature, people, agent_energy,
			room_energy, desired_temp_label, max_power,
			performance_measure_label, instant_comfort_label, min_temp;
	private static JSlider max_power_slider, power_slider, desired_temp_slider;
	private JButton auto_heating_on;
	private JButton auto_heating_off;
	private JButton heating;

	private JCheckBox only_min_temp_checkbox;

	public RoomPanel(Room room) {
		super(new BorderLayout());
		init_name_panel(room.getName());
		this.add(name_panel, BorderLayout.PAGE_START);

		grid_panel = new JPanel();
		this.add(grid_panel, BorderLayout.CENTER);

		// grid_panel.setLayout(new BoxLayout(grid_panel, BoxLayout.Y_AXIS));
		// grid_panel.setLayout(new GridLayout(2,1));

		grid_panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		init_room_panel(room);
		grid_panel.add(room_panel, c);

		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 1;
		init_agent_panel(room);
		grid_panel.add(agent_panel, c);

		setInfo(room);
	}

	private void init_name_panel(String name) {
		name_panel = new JPanel();
		name_panel.setLayout(new BoxLayout(name_panel, BoxLayout.Y_AXIS));
		room_name = new JLabel(name);
		room_name.setAlignmentX(Component.CENTER_ALIGNMENT);

		name_panel.add(Box.createHorizontalGlue());
		name_panel.add(room_name, BorderLayout.CENTER);

		Border raisedbevel, loweredbevel, compound;
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();
		compound = BorderFactory
				.createCompoundBorder(raisedbevel, loweredbevel);
		name_panel.setBorder(compound);

		Font font = new Font("Serif", Font.BOLD, 18);
		room_name.setForeground(Color.white);
		room_name.setFont(font);
		name_panel.setBackground(Color.DARK_GRAY);
	}

	private void init_room_panel(Room room) {
		room_panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JPanel temperature_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		temperature = new JLabel();
		temperature_panel.add(temperature);

		Settings.init();
		int initial_value = Math
				.round(((Settings.getMax_temperature() - Settings
						.getMin_temperature()) / 2));

		desired_temp_slider = new JSlider(JSlider.HORIZONTAL,
				Settings.getMin_temperature(), Settings.getMax_temperature(),
				Settings.getMin_temperature());
		desired_temp_slider.setName("desired_temp_slider");

		desired_temp_slider.setPreferredSize(new Dimension(150, 60));

		desired_temp_slider.addChangeListener(this);
		desired_temp_slider.setMajorTickSpacing(initial_value);
		desired_temp_slider.setMinorTickSpacing(3);
		desired_temp_slider.setPaintTicks(true);
		desired_temp_slider.setPaintLabels(true);
		Font font = new Font("Serif", Font.ITALIC, 12);
		desired_temp_slider.setFont(font);

		desired_temp_label = new JLabel("Desired Tempetarure: "
				+ room.getDesired_temp());

		temperature_panel.add(desired_temp_label);
		temperature_panel.add(desired_temp_slider);

		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 5;
		room_panel.add(temperature_panel, c);

		JPanel room_energy_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		room_energy = new JLabel();
		JButton other_things_button = new JButton("Things in the Room");
		other_things_button.setBackground(Color.yellow);
		other_things_button.setForeground(Color.BLACK);
		other_things_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShowThingsFrame.getInstance().openFrame(
						GraphicEnvironment.getCurrentRoom());
			}
		});

		room_energy_panel.add(room_energy);
		room_energy_panel.add(other_things_button);

		c.gridy = GridBagConstraints.RELATIVE;
		c.ipady = 10;
		room_panel.add(room_energy_panel, c);

		ImageIcon plus_icon, minus_icon;
		plus_icon = new ImageIcon("img/plus.png");
		minus_icon = new ImageIcon("img/minus.png");

		JPanel people_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		people = new JLabel("People in the room: " + room.getNum_of_people()
				+ "   ");

		JButton plus_button = new JButton(plus_icon);
		plus_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Room r = GraphicEnvironment.getCurrentRoom();
				r.setNum_of_people(GraphicEnvironment.getCurrentRoom()
						.getNum_of_people() + 1);
				setInfo(r);
			}
		});

		JButton minus_button = new JButton(minus_icon);
		minus_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Room r = GraphicEnvironment.getCurrentRoom();

				if (r.getNum_of_people() > 0) {
					r.setNum_of_people(r.getNum_of_people() - 1);
					setInfo(r);
				}
			}
		});

		people_panel.add(people);
		people_panel.add(plus_button);
		people_panel.add(minus_button);

		c.gridy = GridBagConstraints.RELATIVE;
		room_panel.add(people_panel, c);

		JPanel min_temp_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		min_temp = new JLabel("Min temp: " + room.getMin_temp() + " °C");
		min_temp_panel.add(min_temp);

		c.gridy = GridBagConstraints.RELATIVE;
		c.ipady = 0;
		room_panel.add(min_temp_panel, c);

		Border raisedbevel, loweredbevel, compound;
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();
		compound = BorderFactory
				.createCompoundBorder(raisedbevel, loweredbevel);
		room_panel.setBorder(compound);
	}

	private void init_agent_panel(Room room) {
		agent_panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JPanel agent_pow = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JLabel agent_pow_label = new JLabel("Agent: ");
		auto_heating_on = new JButton("ON");
		auto_heating_off = new JButton("OFF");

		JLabel heater_label = new JLabel("   Heater:  ");

		heating = new JButton("OFF");
		heating.setPreferredSize(new Dimension(70, 25));

		auto_heating_on.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GraphicEnvironment.getCurrentRoom().powerOnAutoHeating();
				setInfo(GraphicEnvironment.getCurrentRoom());
			}
		});
		auto_heating_off.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GraphicEnvironment.getCurrentRoom().powerOffAutoHeating();
				setInfo(GraphicEnvironment.getCurrentRoom());
			}
		});
		heating.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Agent agent = GraphicEnvironment.getCurrentRoom().getAgent();
				agent.switchHeating();
				setInfo(GraphicEnvironment.getCurrentRoom());
			}
		});

		JLabel only_min_temp_label = new JLabel("     ");
		only_min_temp_checkbox = new JCheckBox("Check only min temp");
		only_min_temp_checkbox.addItemListener(this);

		agent_pow.add(agent_pow_label);
		agent_pow.add(auto_heating_on);
		agent_pow.add(auto_heating_off);
		agent_pow.add(heater_label);
		agent_pow.add(heating);
		agent_pow.add(only_min_temp_label);
		agent_pow.add(only_min_temp_checkbox);

		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.ipady = 5;
		agent_panel.add(agent_pow, c);

		JPanel agent_energy_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		agent_energy = new JLabel();
		agent_energy_panel.add(agent_energy);

		c.gridy = GridBagConstraints.RELATIVE;
		c.ipady = -5;
		agent_panel.add(agent_energy_panel, c);

		JPanel agent_energy_panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));

		max_power = new JLabel();
		// max_power.setAlignmentX(Component.CENTER_ALIGNMENT);
		// agent_max_pow.add(max_power);

		agent_energy_panel1.add(max_power);

		max_power_slider = new JSlider(JSlider.HORIZONTAL, 900, 1800, 1200);
		max_power_slider.setPreferredSize(new Dimension(150, 60));
		max_power_slider.setName("max_power_slider");
		max_power_slider.setMajorTickSpacing(300);
		max_power_slider.setMinorTickSpacing(300);
		max_power_slider.setSnapToTicks(true);
		max_power_slider.addChangeListener(this);
		max_power_slider.setPaintTicks(true);
		max_power_slider.setPaintLabels(true);
		Font font2 = new Font("Serif", Font.ITALIC, 12);
		max_power_slider.setFont(font2);

		power_slider = new JSlider(JSlider.HORIZONTAL, 0, 1800, 800);
		power_slider.setPreferredSize(new Dimension(180, 60));
		power_slider.setName("power_slider");
		power_slider.setMajorTickSpacing(600);
		power_slider.setMinorTickSpacing(100);
		power_slider.setSnapToTicks(true);
		power_slider.addChangeListener(this);
		power_slider.setPaintTicks(true);
		power_slider.setPaintLabels(true);
		power_slider.setFont(font2);

		// agent_max_pow.add(max_power_slider);
		agent_energy_panel1.add(max_power_slider);
		agent_energy_panel1.add(power_slider);

		c.gridy = 2;
		c.ipady = 10;
		agent_panel.add(agent_energy_panel1, c);

		JPanel performance_measure_panel = new JPanel(new FlowLayout(
				FlowLayout.LEFT));
		performance_measure_label = new JLabel("Performance Measure: ");
		performance_measure_panel.add(performance_measure_label);

		instant_comfort_label = new JLabel("Instant Comfort: ");
		performance_measure_panel.add(instant_comfort_label);

		c.gridy = 3;
		agent_panel.add(performance_measure_panel, c);

		Border raisedbevel, loweredbevel, compound;
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();
		compound = BorderFactory
				.createCompoundBorder(raisedbevel, loweredbevel);
		agent_panel.setBorder(compound);
	}

	/**
	 * Updates the overall performance measure label
	 */
	DecimalFormat dec_format = new DecimalFormat("###.#");
	String str;

	public void setInfo(Room room) {
		// name panel
		room_name.setText(room.getName() + " .. ID:  " + room.getId());

		// room panel

		temperature.setText("Actual Temp:  "
				+ dec_format.format(room.getCurrent_temperature())
				+ " °C      |   ");

		desired_temp_label.setText("   Desired Temp: " + room.getDesired_temp()
				+ " °C");

		desired_temp_slider.setValue(room.getDesired_temp());

		people.setText("Num of people currently in the room:  "
				+ room.getNum_of_people().toString() + "    ");

		room_energy.setText("Room's Isolation Value:  "
				+ (100 - room.getIsolationValue()) + "%     |     ");

		min_temp.setText("Min temp: " + room.getMin_temp() + " °C");

		// agent panel

		int curr_pow;
		if (!room.getAgent().isHeating())
			curr_pow = 0;
		else
			curr_pow = room.getAgent().getCurrent_pow_consumption();

		agent_energy.setText("Current agent's energy:  " + curr_pow);

		Agent agent = room.getAgent();

		if (agent.isAutoPowerOn()) {
			auto_heating_on.setEnabled(false);
			auto_heating_off.setEnabled(true);
			heating.setEnabled(false);
			max_power.setText("Agent's Max Power: "
					+ room.getAgent().getMaxPowConsumption().toString());
			max_power_slider.setEnabled(true);
		} else {
			auto_heating_on.setEnabled(true);
			auto_heating_off.setEnabled(false);
			heating.setEnabled(true);
			max_power.setText("Agent's Power: "
					+ room.getAgent().getCurrent_pow_consumption().toString());
			max_power_slider.setEnabled(false);
		}
		if (agent.isHeating()) {
			heating.setBackground(new Color(50, 200, 0));
			heating.setText("ON");
			if (!agent.isAutoPowerOn())
				power_slider.setEnabled(true);
		} else {
			heating.setBackground(new Color(255, 85, 85));
			heating.setText("OFF");
			power_slider.setEnabled(false);
		}

		only_min_temp_checkbox.setSelected(agent.isCheckOnlyMinTemp());
		
		if (agent.getPerformance_measure() == -1)
			str = "N.D.";
		else
			str = dec_format.format(agent.getPerformance_measure() * 100);
		performance_measure_label.setText("Performance Measure: " + str);

		if (agent.getInstantPM() == -1)
			str = "N.D.";
		else
			str = dec_format.format(agent.getInstantPM() * 100);
		instant_comfort_label.setText("   Instant comfort: " + str);

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (source.getName().equals("max_power_slider")) {
			GraphicEnvironment.getCurrentRoom().getAgent()
					.setMaxPowerConsumption((int) source.getValue());
		} else if (source.getName().equals("power_slider")) {
			GraphicEnvironment.getCurrentRoom().getAgent()
					.setCurrentPowerConsumption((int) source.getValue());
		} else if (source.getName().equals("desired_temp_slider")) {
			GraphicEnvironment.getCurrentRoom().setDesired_temp(
					source.getValue());
		}
		setInfo(GraphicEnvironment.getCurrentRoom());

	}

	public void repaintAll() {
		name_panel.repaint();
		grid_panel.repaint();
		room_panel.repaint();
		agent_panel.repaint();
		setInfo(GraphicEnvironment.getCurrentRoom());
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		JCheckBox source = (JCheckBox) e.getItemSelectable();
		if (source == only_min_temp_checkbox) {
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				GraphicEnvironment.getCurrentRoom().getAgent()
						.setCheckOnlyMinTemp(false);
			} else if (e.getStateChange() == ItemEvent.SELECTED) {
				GraphicEnvironment.getCurrentRoom().getAgent()
						.setCheckOnlyMinTemp(true);

			}
		}
	}

}
