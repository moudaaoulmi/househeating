package graphics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import agents.RefereeAgent;

import environment.Environment;

public class HousePanel extends JPanel {
	private static final long serialVersionUID = 8204068333434756216L;

	static JFrame parent_frame;
	static FloorPanel floor_panel;
	static TimePanel date_and_time_panel;
	static JLabel o_p_m;

	public HousePanel(JFrame parent) {
		super(new BorderLayout());

		parent_frame = parent;

		initSettingsPanel();
		initFloorPanel();
		initTimePanel();

		this.setVisible(true);
	}

	private void initTimePanel() {
		date_and_time_panel = new TimePanel();
		this.add(date_and_time_panel, BorderLayout.PAGE_END);
	}

	private void initSettingsPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		JPanel select_floor_panel = new JPanel(
				new FlowLayout(FlowLayout.CENTER));

		JLabel floor_selection_label = new JLabel("Select Floor");
		JComboBox floor_selection_combo_box = new JComboBox(Environment.floors
				.keySet().toArray());

		floor_selection_combo_box.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				GraphicEnvironment.setCurrent_floor(Environment.floors.get(e
						.getItem()));
				floor_panel.adjustSize();
			}
		});

		select_floor_panel.add(floor_selection_label);
		select_floor_panel.add(floor_selection_combo_box);

		panel.add(select_floor_panel, BorderLayout.NORTH);

		JPanel other_panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JPanel legend_panel = new JPanel(new GridLayout(2, 1));

		TitledBorder titled_border = new TitledBorder("Legend");
		titled_border.setTitleJustification(TitledBorder.CENTER);
		legend_panel.setBorder(titled_border);

		JLabel legend1 = new JLabel("    (*) :  the room contains some things");
		JLabel legend2 = new JLabel(
				"(*.*) : some of the things in the room absorb energy");
		legend2.setBorder(BorderFactory.createEmptyBorder(1, 6, 1, 6));
		
		legend_panel.add(legend1);
		legend_panel.add(legend2);

		JPanel overall_performance_measure_panel = new JPanel();

		// new FlowLayout(FlowLayout.CENTER)

		TitledBorder titled_border1 = new TitledBorder("Overall P. M.");
		titled_border1.setTitleJustification(TitledBorder.CENTER);
		overall_performance_measure_panel.setBorder(titled_border1);

		Font font = new Font(Font.MONOSPACED, Font.BOLD, 20);
		o_p_m = new JLabel(
				((Float) RefereeAgent.getOverall_performance_measure())
						.toString());
		o_p_m.setBorder(BorderFactory.createEmptyBorder(0, 70, 0, 70));
		o_p_m.setFont(font);
		overall_performance_measure_panel.add(o_p_m);

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 2;
		other_panel.add(legend_panel, c);
		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		other_panel.add(overall_performance_measure_panel, c);

		panel.add(other_panel, BorderLayout.CENTER);

		this.add(panel, BorderLayout.PAGE_START);
	}

	private void initFloorPanel() {
		floor_panel = new FloorPanel();
		this.add(floor_panel, BorderLayout.CENTER);
	}

	static String str;
	static DecimalFormat dec_format = new DecimalFormat("###.#");
	static float pm;
	public static void updateInfo() {
		pm = Environment.getOverallPM();
		if (pm == -1)
			str = "N.D.";
		else
			str = dec_format.format(pm*100);
		o_p_m.setText(str);
	}

	public void adjustSize() {
		this.setSize(parent_frame.getSize());
		Dimension d = this.getSize();
		floor_panel.adjustSize();
		GraphicEnvironment.setMultiplicative_factor(d.width / 19);
	}

	/**
	 * @return the floor_panel
	 */
	public static FloorPanel getFloor_panel() {
		return floor_panel;
	}

	/**
	 * @return the date_and_time_panel
	 */
	public static TimePanel getDate_and_time_panel() {
		return date_and_time_panel;
	}

}
