package graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utils.Settings;

import environment.Environment;

public class SettingsPanel extends JPanel implements ChangeListener {
	private static final long serialVersionUID = -3541107260182418396L;

	private static JLabel solar_energy_label, other_consumption,
			total_available_energy, external_temperature;

	private JPanel name_panel;
	private static JSlider external_temp_slider, other_consumption_slider;

	private static JButton auto_temp_button;
	
	private static int min_slider_temp = -10, max_slider_temp = 50;
	
	public SettingsPanel() {
		super(new BorderLayout());

		init_name_panel();
		this.add(name_panel, BorderLayout.PAGE_START);

		JPanel values_panel = new JPanel();
		values_panel
				.setLayout(new BoxLayout(values_panel, BoxLayout.PAGE_AXIS));

		Settings.init();

		Dimension minSize = new Dimension(5, 5);
		Dimension prefSize = new Dimension(5, 5);
		Dimension maxSize = new Dimension(Short.MAX_VALUE, 5);
		values_panel.add(new Box.Filler(minSize, prefSize, maxSize));

		solar_energy_label = new JLabel();
		solar_energy_label.setAlignmentX(Component.LEFT_ALIGNMENT);
		solar_energy_label.setBounds(0, 0, solar_energy_label.getWidth(),
				solar_energy_label.getHeight() + 10);
		other_consumption = new JLabel();
		other_consumption.setAlignmentX(Component.LEFT_ALIGNMENT);
		total_available_energy = new JLabel();
		total_available_energy.setAlignmentX(Component.LEFT_ALIGNMENT);
		external_temperature = new JLabel();
		external_temperature.setAlignmentX(Component.LEFT_ALIGNMENT);

		values_panel.add(solar_energy_label);
		values_panel.add(new Box.Filler(minSize, prefSize, maxSize));

		JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pane.setAlignmentX(Component.LEFT_ALIGNMENT);

		other_consumption_slider = new JSlider(JSlider.HORIZONTAL, 0,
				Settings.getEnel_energy(), 200);
		other_consumption_slider.setName("other_consumption_slider");
		other_consumption_slider.setPreferredSize(new Dimension(200, 45));
		other_consumption_slider.addChangeListener(this);
		other_consumption_slider
				.setMajorTickSpacing(Settings.getEnel_energy() / 3);
		other_consumption_slider.setMinorTickSpacing(100);
		other_consumption_slider.setPaintTicks(true);
		other_consumption_slider.setPaintLabels(true);
		Font font = new Font("Serif", Font.ITALIC, 12);
		other_consumption_slider.setFont(font);

		pane.add(other_consumption);
		pane.add(other_consumption_slider);

		values_panel.add(pane);

		values_panel.add(total_available_energy);
		values_panel.add(new Box.Filler(minSize, prefSize, maxSize));

		pane = new JPanel(new FlowLayout(FlowLayout.LEFT));

		external_temp_slider = new JSlider(JSlider.HORIZONTAL, min_slider_temp, max_slider_temp,
				Environment.getExternal_temperature().intValue());
		external_temp_slider.setName("external_temp_slider");
		external_temp_slider.setPreferredSize(new Dimension(150, 45));
		external_temp_slider.addChangeListener(this);
		external_temp_slider.setMajorTickSpacing(20);
		external_temp_slider.setMinorTickSpacing(5);
		external_temp_slider.setPaintTicks(true);
		external_temp_slider.setPaintLabels(true);
		external_temp_slider.setFont(font);

		JLabel space_label = new JLabel("    ");

		auto_temp_button = new JButton();
		auto_temp_button.setPreferredSize(new Dimension(120, 30));
		auto_temp_button.setForeground(Color.black);
		auto_temp_button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		auto_temp_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Environment.isAuto_set_external_temp())
					Environment.setAuto_set_external_temp(false);
				else
					Environment.setAuto_set_external_temp(true);
			}
		});

		pane.add(external_temperature);
		pane.add(external_temp_slider);
		pane.add(space_label);
		pane.add(auto_temp_button);

		pane.setAlignmentX(Component.LEFT_ALIGNMENT);

		values_panel.add(pane);

		this.setVisible(true);

		Border raisedbevel, loweredbevel, compound;
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();
		compound = BorderFactory
				.createCompoundBorder(raisedbevel, loweredbevel);
		this.setBorder(compound);

		this.add(values_panel);

		updateValues();
	}

	public static void updateValues() {
		solar_energy_label.setText(" Current Solar Energy available:  "
				+ Environment.getCurrentSolarPower());
		other_consumption.setText("Current Power in use:  "
				+ Environment.getOther_consumption());

		Settings.init();

		total_available_energy.setText(" Total energy currently available:  "
				+ Environment.getAvailableEnergy().toString());

		DecimalFormat dec_format = new DecimalFormat("##.#");
		external_temperature.setText("External Temp:  "
				+ dec_format.format(Environment.getExternal_temperature())
				+ "        ");
		
		if (Environment.isAuto_set_external_temp()) {
			auto_temp_button.setBackground(new Color(50, 200, 0));
			auto_temp_button.setText("Auto set: ON");
		}
		else {
			auto_temp_button.setBackground(new Color(255, 85, 85));
			auto_temp_button.setText("Auto set: OFF");			
		}
	}

	private void init_name_panel() {
		name_panel = new JPanel();

		JLabel title = new JLabel("General values");

		name_panel.add(title, BorderLayout.CENTER);

		Border raisedbevel, loweredbevel, compound;
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();
		compound = BorderFactory
				.createCompoundBorder(raisedbevel, loweredbevel);
		name_panel.setBorder(compound);

		Font font = new Font("Serif", Font.BOLD, 18);
		title.setForeground(Color.BLUE);
		title.setFont(font);
		name_panel.setBackground(Color.lightGray);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider slider = (JSlider) e.getSource();
		if (slider.getName().equals("external_temp_slider")) {
			Environment.setExternal_temperature((float) slider.getValue());
		} else if (slider.getName().equals("other_consumption_slider")) {
			Environment.setOther_consumption(slider.getValue());
		}
		updateValues();
	}

}
