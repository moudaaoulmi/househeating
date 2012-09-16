package graphics;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import listeners.MyComponentAdapter;

public class MyFrame extends JFrame {
	private static final long serialVersionUID = -1326983377969883464L;

	JPanel panel_container;
	MyComponentAdapter adapter;

	public MyFrame() {
		super();
		this.setResizable(true);
		adapter = new MyComponentAdapter();
		this.addComponentListener(adapter);
	}

	@Override
	public void setSize(Dimension d) {
		super.setSize(d);
		if (this.panel_container != null) {
			((HousePanel) (this.panel_container)).setSize(d);
			System.out.println("my frame panel contenuto null");
		}
	}

	/**
	 * @param panel_container
	 *            the panel_container to set
	 */
	public void setPanel_container(JPanel panel_container) {
		this.panel_container = panel_container;
		this.adapter.setComponent(panel_container);
	}

	/**
	 * @return the panel_container
	 */
	public JPanel getPanel_container() {
		return panel_container;
	}

	
}
