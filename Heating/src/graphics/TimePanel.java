package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import environment.Environment;

public class TimePanel extends JPanel implements ChangeListener {
	private static final long serialVersionUID = 265469932690282170L;

	private static JLabel date_and_time_label;

	private static JSlider speed_slider;

	public TimePanel() {
		super(new FlowLayout());

		Font font = new Font(Font.MONOSPACED, Font.BOLD, 24);

		JPanel time_pane = new JPanel(new FlowLayout());
		
		date_and_time_label = new JLabel();
		date_and_time_label.setFont(font);
		date_and_time_label.setForeground(Color.BLACK);

		time_pane.add(date_and_time_label);

		updateDateAndTime();

		JButton change_date = new JButton("Change Time");
		change_date.setBackground(Color.orange);
		change_date.setForeground(Color.BLACK);

		change_date.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SelectDateFrame.getInstance().openFrame();
			}
		});

		time_pane.add(change_date);

		JLabel space = new JLabel(" ");
		time_pane.add(space);
		
		TitledBorder titled_border = new TitledBorder("Date and Time");
		titled_border.setTitleJustification(TitledBorder.CENTER);
		time_pane.setBorder(titled_border);

		this.add(time_pane);
		
		JPanel slider_pane = new JPanel();

		speed_slider = new JSlider(JSlider.HORIZONTAL, 0, 100,
				GraphicEnvironment.getSimulation_speed());
		speed_slider.setName("speed_slider");

		speed_slider.setPreferredSize(new Dimension(150, 50));

		speed_slider.addChangeListener(this);
		speed_slider.setMajorTickSpacing(50);
		speed_slider.setMinorTickSpacing(10);
		speed_slider.setPaintTicks(true);
		speed_slider.setPaintLabels(true);
		Font font1 = new Font("Serif", Font.ITALIC, 10);
		speed_slider.setFont(font1);
		speed_slider.setPreferredSize(new Dimension(120, 40));
		
		slider_pane.add(speed_slider);
		
		TitledBorder titled_border1 = new TitledBorder("Simulation Speed");
		titled_border1.setTitleJustification(TitledBorder.CENTER);
		
		slider_pane.setBorder(titled_border1);

		this.add(slider_pane);
	}

	public static void updateDateAndTime() {
		if (date_and_time_label != null)
			date_and_time_label.setText(Environment.time_handler
					.getDateAndTime() + " ");
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider slider = (JSlider) e.getSource();
		if (slider.getName().equals("speed_slider")) {
			GraphicEnvironment.setSimulation_speed(slider.getValue());
			this.repaint();
		}
	}

	@Override
	public void repaint() {
		updateDateAndTime();
		super.repaint();
	}

}
