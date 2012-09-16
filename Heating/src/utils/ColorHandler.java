package utils;

import java.awt.Color;

public class ColorHandler {

	public static Color getRGBFromTemp(Float current_temp, Integer desired_temp) {
		int max_temp = desired_temp + 10;
		int min_temp = desired_temp - 10;

		if (current_temp > max_temp)
			current_temp = (float)max_temp;

		if (current_temp < min_temp)
			current_temp = (float)min_temp;
		
		int r, g, b;

		if (current_temp >= desired_temp)
			r = (int) (255 * (current_temp - desired_temp) / (max_temp - desired_temp));
		else
			r = 0;
		if (current_temp <= desired_temp)
			b = (int) (255 * (desired_temp - current_temp) / (desired_temp - min_temp));
		else
			b = 0;
		
		g = 255 - r - b;

		return new Color(r, g, b);

	}

}
