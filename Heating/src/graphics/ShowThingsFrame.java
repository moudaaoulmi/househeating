package graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import environment.Environment;
import environment.Room;

public class ShowThingsFrame extends JFrame {
	private static final long serialVersionUID = -3703934877920400614L;

	private static ShowThingsFrame instance;

	private JPanel pane, name_panel, things_panel;
	private JLabel room_name_label;
	private JComboBox things_combo_box;
	private JButton add_button;

	private String names[];

	private ShowThingsFrame() {
		super();

		Dimension dim = new Dimension(500, 400);
		this.setSize(dim);

		Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x, y;
		x = (screen_dim.width - dim.width) / 2;
		y = (screen_dim.height - dim.height) / 2;
		this.setLocation(x, y);

		this.setVisible(true);
		this.setName("show_things_frame");

		initPanel(GraphicEnvironment.getCurrentRoom());
	}

	private void initNamePanel(Room room) {
		name_panel = new JPanel();
		name_panel.setLayout(new BoxLayout(name_panel, BoxLayout.Y_AXIS));
		room_name_label = new JLabel();
		room_name_label.setAlignmentX(Component.CENTER_ALIGNMENT);

		name_panel.add(Box.createHorizontalGlue());
		name_panel.add(room_name_label, BorderLayout.CENTER);

		Border raisedbevel, loweredbevel, compound;
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();
		compound = BorderFactory
				.createCompoundBorder(raisedbevel, loweredbevel);
		name_panel.setBorder(compound);

		Font font = new Font("Serif", Font.BOLD, 18);
		room_name_label.setForeground(Color.white);
		room_name_label.setFont(font);
		name_panel.setBackground(Color.DARK_GRAY);
	}

	private void initThingsPanel(Room room) {
		things_panel = new JPanel();
		things_panel
				.setLayout(new BoxLayout(things_panel, BoxLayout.PAGE_AXIS));

		if (room.getThings().isEmpty()) {
			JPanel p = new JPanel();
			JLabel label = new JLabel("No Things present in the room yet.");

			p.add(label);
			things_panel.add(p);
		}

		// for each thing in the room print it's name and it's power, with a
		// button to add a new thing.
		for (final String thing : room.getThings()) {
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

			JLabel label = new JLabel('"' + thing + '"' + " Thermal Power "
					+ Environment.things.get(thing).getThermal_power() + ", Energy Consumption " + Environment.things.get(thing).getEnergy());

			JButton remove_thing = new JButton(new ImageIcon("img/remove.png"));

			remove_thing.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GraphicEnvironment.getCurrentRoom().getThings()
							.remove(thing);
					// ((JButton) e.getSource()).getParent().getParent()
					// .getParent().getParent().getParent().getParent()
					// .getParent().setVisible(false);
					openFrame(GraphicEnvironment.getCurrentRoom());

				}
			});

			p.add(label);
			p.add(remove_thing);

			things_panel.add(p);
		}

		if (things_combo_box == null)
			initThingsComboBox();

		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

		p.add(things_combo_box);
		p.add(add_button);

		things_panel.add(p);
		things_panel.validate();
	}

	private void initThingsComboBox() {
		things_combo_box = new JComboBox();

		names = new String[Environment.things.size()];
		String strings[] = new String[Environment.things.size()];

		int i = 0;
		for (String name : Environment.things.keySet()) {
			names[i] = name;
			strings[i] = "\"" + name + "\"" + " Thermal Pow: "
					+ Environment.things.get(name).getThermal_power()
					+ ", Energy Cons: "
					+ Environment.things.get(name).getEnergy();
			things_combo_box.addItem(strings[i]);
			i++;
		}

		add_button = new JButton("Add selected thing");
		add_button.setBackground(Color.RED);
		add_button.setForeground(Color.BLACK);

		add_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (GraphicEnvironment
						.getCurrentRoom()
						.getThings()
						.contains(
								(Environment.things.get(names[things_combo_box
										.getSelectedIndex()]))))
					JOptionPane.showMessageDialog(
							((JButton) e.getSource()).getParent(),
							names[things_combo_box.getSelectedIndex()]
									+ " is already present in room "
									+ GraphicEnvironment.getCurrentRoom()
											.getName() + "!",
							"Impossible to add", JOptionPane.ERROR_MESSAGE);

				else

					GraphicEnvironment
							.getCurrentRoom()
							.getThings()
							.add((Environment.things.get(names[things_combo_box
									.getSelectedIndex()]).getName()));

				// ((JButton) e.getSource()).getParent().getParent().getParent()
				// .getParent().getParent().getParent().getParent()
				// .setVisible(false);
				openFrame(GraphicEnvironment.getCurrentRoom());
			}
		});

	}

	private void initPanel(Room room) {
		pane = new JPanel(new BorderLayout());

		initNamePanel(room);
		pane.add(name_panel, BorderLayout.PAGE_START);

		initThingsPanel(room);
		pane.add(things_panel, BorderLayout.CENTER);

		this.add(pane);
	}

	public static ShowThingsFrame getInstance() {
		if (instance == null)
			instance = new ShowThingsFrame();

		return instance;
	}

	public void openFrame(Room room) {
		if (name_panel == null)
			initPanel(room);

		room_name_label.setText("Things in room " + room.getName());

		pane.remove(things_panel);
		initThingsPanel(room);
		pane.add(things_panel, BorderLayout.CENTER);

		adjustSize();
	}

	@Override
	public void setSize(Dimension d) {
		super.setSize(d);
		adjustSize();
	}

	private void adjustSize() {
		if (name_panel != null)
			name_panel.validate();
		if (things_panel != null)
			things_panel.validate();
		this.validate();
		this.pack();
		this.validate();

		Dimension dim = this.getSize();

		Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x, y;
		x = (screen_dim.width - dim.width) / 2;
		y = (screen_dim.height - dim.height) / 2;
		this.setLocation(x, y);

		this.setVisible(true);
	}

}
