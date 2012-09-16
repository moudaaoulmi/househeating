package listeners;

import graphics.HousePanel;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MyComponentAdapter extends ComponentAdapter {

	Object component;

	public MyComponentAdapter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MyComponentAdapter(Object obj) {
		super();
		// TODO Auto-generated constructor stub
		this.component = obj;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		super.componentResized(e);
		if (this.component != null && this.component instanceof HousePanel)
			((HousePanel) (this.component)).adjustSize();
	}

	/**
	 * @param component the component to set
	 */
	public void setComponent(Object component) {
		this.component = component;
	}
	
	

}
