package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import environment.Environment;
import environment.TimeHandler;

public class SelectDateFrame extends JFrame {
	private static final long serialVersionUID = 7753110054860090890L;

	private static SelectDateFrame instance;

	private JSpinner timeSpinner;
	
	private SelectDateFrame() {
		super();

		Dimension dim = new Dimension(300, 120);
		this.setSize(dim);

		Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x, y;
		x = (screen_dim.width - dim.width) / 2;
		y = (screen_dim.height - dim.height) / 2;
		this.setLocation(x, y);

		this.setVisible(true);

		initPanel();
	}

	private void initPanel() {
		JPanel pane = new JPanel(new FlowLayout());

		JLabel date_label = new JLabel("Select Date and Time:  ");
		pane.add(date_label);

		timeSpinner = new JSpinner(new SpinnerDateModel());

		timeSpinner.setLocale(Locale.ENGLISH);

		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner,
				"dd MMM yyyy, HH:mm:ss");

		timeSpinner.setEditor(timeEditor);
		timeSpinner.setValue(Environment.time_handler.getCalendar().getTime());

		Font font = new Font(Font.MONOSPACED, Font.BOLD, 16);

		timeSpinner.setFont(font);

		pane.add(timeSpinner);

		JButton ok_button = new JButton("Ok");
		ok_button.setBackground(Color.orange);
		ok_button.setForeground(Color.BLACK);
		ok_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TimeHandler time_handler = TimeHandler.getInstance();

				Date new_date = null;
				new_date = (Date) timeSpinner.getValue();

				time_handler.initializeTime(new_date);

				((JButton) e.getSource()).getParent().getParent().getParent()
						.getParent().getParent().setVisible(false);
				
				HousePanel.getDate_and_time_panel().repaint();
			}
		});

		pane.add(ok_button);

		this.add(pane);
	}
	
	public static SelectDateFrame getInstance() {
		if (instance == null)
			instance = new SelectDateFrame();
		
		return instance;
	}
	
	public void openFrame() {
		timeSpinner.setValue(Environment.time_handler.getCalendar().getTime());
		this.setVisible(true);
	}
	
}
