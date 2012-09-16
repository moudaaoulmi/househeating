package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.JPanel;

import utils.ColorHandler;

import listeners.MyFloorMouseListener;

import environment.Room;

/**
 * Panel that represents the selected floor and draws the rooms according to the
 * panel size, putting them always in the center of the panel
 * 
 * @author falkor
 * 
 */
public class FloorPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	Polygon[] polygons;
	Color[] colors;
	MyFloorMouseListener mouse_listener;
	Font font;

	int min_x, max_x, min_y, max_y, font_size;

	public FloorPanel() {
		super(null);
		mouse_listener = new MyFloorMouseListener(polygons, this);
		this.addMouseListener(mouse_listener);

		calculatePolygons();

		font_size = 12;
		font = new Font(Font.SERIF, Font.BOLD, font_size);
	}

	public void calculatePolygons() {
		// for each room of the currently selected floor, calculate the polygons
		// coordinates!
		polygons = new Polygon[GraphicEnvironment.getCurrent_floor().getRooms()
				.size()];
		colors = new Color[GraphicEnvironment.getCurrent_floor().getRooms()
				.size()];
		Room r;
		for (int i = 0; i < polygons.length; i++) {
			r = GraphicEnvironment.getCurrent_floor().getRooms().get(i);

			colors[i] = r.getColor();

			int[] x_coords = r.getX_coord();
			int[] y_coords = r.getY_coord();

			for (int j = 0; j < r.getX_coord().length; j++)
				x_coords[j] *= GraphicEnvironment.getMultiplicative_factor();

			for (int j = 0; j < r.getY_coord().length; j++)
				y_coords[j] *= GraphicEnvironment.getMultiplicative_factor();

			polygons[i] = new Polygon(x_coords, y_coords, x_coords.length);

			polygons[i].translate(
					r.getInitial_x_pos()
							* GraphicEnvironment.getMultiplicative_factor(),
					r.getInitial_y_pos()
							* GraphicEnvironment.getMultiplicative_factor());
		}

		min_x = Integer.MAX_VALUE;
		max_x = 0;
		min_y = Integer.MAX_VALUE;
		max_y = 0;

		for (Polygon poly : polygons) {
			if (poly.getBounds().getMinX() < min_x)
				min_x = (int) poly.getBounds().getMinX();
			if (poly.getBounds().getMaxX() > max_x)
				max_x = (int) poly.getBounds().getMaxX();
			if (poly.getBounds().getMinY() < min_y)
				min_y = (int) poly.getBounds().getMinY();
			if (poly.getBounds().getMaxY() > max_y)
				max_y = (int) poly.getBounds().getMaxY();
		}

		int transl_x = (this.getSize().width - (max_x - min_x)) / 2;
		int transl_y = (this.getSize().height - (max_y - min_y)) / 2;

		for (Polygon poly : polygons)
			poly.translate(transl_x, transl_y);

		this.mouse_listener.setPolygons(polygons);

	}

	private Room tmp_room;
	private Color tmp_color;
	private String str;

	@Override
	public void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponents(g);
		int i = 0;

		for (i = 0; i < polygons.length; i++) {
			tmp_room = GraphicEnvironment.getCurrent_floor().getRooms().get(i);
			tmp_color = ColorHandler.getRGBFromTemp(
					tmp_room.getCurrent_temperature(),
					tmp_room.getDesired_temp());

			// g.setColor(colors[i]);
			g.setColor(tmp_color);
			g.fillPolygon(polygons[i]);
			g.setColor(Color.black);
			g.drawPolygon(polygons[i]);
			g.setFont(font);

			if (tmp_room == GraphicEnvironment.getCurrentRoom()) {
				g.setColor(new Color(255 - tmp_color.getRed(), 255 - tmp_color
						.getGreen(), 255 - tmp_color.getBlue()));
			} else if (tmp_room.getAgent().isHeating()) {
				g.setColor(Color.RED);				
			}

			str = GraphicEnvironment.getCurrent_floor().getRooms().get(i)
					.getName();

			g.drawString(str, polygons[i].xpoints[0] + 5,
					polygons[i].ypoints[0] + font_size);

			g.setColor(Color.black);

			str = "";
			if (tmp_room.getAgent().isAutoPowerOn())
				str += "ag. ON";
			else
				str += "ag. OFF";

			g.drawString(str, polygons[i].xpoints[0] + 5,
					polygons[i].ypoints[0] + font_size * 2 + 5);

			if (tmp_room.containsThings()) {
				str = "";
				str += "(*";
				if (tmp_room.getEnergyAbsorbedByObjects() > 0)
					str += ".*)";
				else
					str += ")";
				g.drawString(str, polygons[i].xpoints[0] + 5,
						polygons[i].ypoints[0] + font_size * 4);
			}
		}

	}

	public void adjustSize() {
		calculatePolygons();
		repaint();
	}

}
