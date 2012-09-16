package listeners;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import environment.Heating;

public class MyHouseFrameWindowListener implements WindowListener{

	public MyHouseFrameWindowListener() {
		super();
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		Heating.setExit(true);
	}

	@Override
	public void windowClosed(WindowEvent e) {
		Heating.setExit(true);
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
//		Heating.setExit(true);
	}
	

}
