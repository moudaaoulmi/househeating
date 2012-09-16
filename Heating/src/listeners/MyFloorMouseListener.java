package listeners;

import environment.Room;
import graphics.GraphicEnvironment;

import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class MyFloorMouseListener extends MouseAdapter {

	Polygon[] polygons;
	JPanel pane;

	public MyFloorMouseListener(Polygon[] polygons, JPanel pane) {
		this.polygons = polygons;
		this.pane = pane;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		for (int i = 0; i < polygons.length; i++) {
			if (polygons[i].contains(e.getX(), e.getY())) {
//				System.out.println("Clicked on "
//						+ GraphicEnvironment.getCurrent_floor().getRooms()
//								.get(i));
				Room room = GraphicEnvironment.getCurrent_floor().getRooms()
				.get(i);
				GraphicEnvironment.setCurrent_room(room);
				GraphicEnvironment.getRoom_panel().setInfo(room);
				pane.repaint();
			}
		}
	}

	/**
	 * @param polygons the polygons to set
	 */
	public void setPolygons(Polygon[] polygons) {
		this.polygons = polygons;
	}
	

}
