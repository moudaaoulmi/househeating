package environment;

import graphics.GraphicEnvironment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import utils.Enums.Day;

public class TimeHandler {

	public static String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
			"Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	
	public static String[] days = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri",
			"Sat" };

	private static TimeHandler instance;

	private static GregorianCalendar calendar;
	private static final SimpleDateFormat date_format = new SimpleDateFormat(
			"E dd MMM, HH:mm:ss", Locale.ENGLISH);

	private static Integer seconds_to_add;

	private Day day_of_the_stats;
	private boolean date_changed;

	private int prec_prec, prec, last;

	private TimeHandler() {
		seconds_to_add = 2;
		initializeTime();
		prec = prec_prec = last = 0;
	}

	public static TimeHandler getInstance() {
		if (instance == null) {
			instance = new TimeHandler();
		}

		return instance;
	}

	public void initializeTime() {
		calendar = new GregorianCalendar();
		date_changed = true;
		System.out.println("Time set to " + calendar.getTime());
		prec = prec_prec = last = 0;
	}

	public void initializeTime(int year, int month, int day, int hour,
			int minute) {
		calendar = new GregorianCalendar(year, month, day, hour, minute);
		date_changed = true;
		System.out.println("Time set to " + calendar.getTime());
		prec = prec_prec = last = 0;
	}

	public void initializeTime(Date date) {
		calendar = new GregorianCalendar();
		calendar.setTime(date);
		date_changed = true;
		System.out.println("Time set to " + calendar.getTime());
		prec = prec_prec = last = 0;
	}

	public void goAwayOfSeconds(int seconds_to_add) {
		calendar.add(Calendar.SECOND, seconds_to_add);
		prec_prec = prec;
		prec = last;
		last = seconds_to_add;
	}

	public void goAwayOfMinutes(int minutes_to_add) {
		calendar.add(Calendar.MINUTE, minutes_to_add);
	}

	public void goAwayOfHours(int hours_to_add) {
		calendar.add(Calendar.HOUR_OF_DAY, hours_to_add);
	}

	public void goAwayOfDays(int days_to_add) {
		calendar.add(Calendar.DAY_OF_YEAR, days_to_add);
	}

	public void goAwayOfMonths(int months_to_add) {
		calendar.add(Calendar.MONTH, months_to_add);
	}

	public void goAwayOfYears(int years_to_add) {
		calendar.add(Calendar.YEAR, years_to_add);
	}

	public void printDate() {
		System.out.println(calendar.getTime());
	}

	public String getMonth() {
		return months[calendar.get(Calendar.MONTH)];
	}

	public String getDayString() {
		return days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
	}

	public Day getDay() {
		return Day.getDayByNumber(calendar.get(Calendar.DAY_OF_WEEK));
	}

	public Integer getNumericMonth() {
		return calendar.get(Calendar.MONTH) + 1;
	}

	public Integer getNumericDay() {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public Integer getHour() {
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public Integer getMinute() {
		return calendar.get(Calendar.MINUTE);
	}

	public String getDateAndTime() {
		return date_format.format(calendar.getTime());
	}

	public Integer getCurrentInterval() {
		int interval = getHour() * 2;
		if (getMinute() >= 30)
			interval += 1;

		// add one because of the solar_pow.xml file format
		interval += 1;

		return interval;
	}

	/**
	 * @return the day_of_the_week_stats
	 */
	public Day getDay_of_the_week_stats() {
		return day_of_the_stats;
	}

	/**
	 * Method that handles the change of date (due to the natural passing of
	 * time or to the selection of a different date), storing the old day of the
	 * week, so that agents can update their people in room information
	 * accordingly to the day they are referred to.
	 * 
	 * @return NULL if the date hasn't changed yet
	 * @return old_day: the day the stats have to refer to when updating, since
	 *         the date has changed
	 */
	public Day isAnotherDay() {
		if (day_of_the_stats == null) {
			day_of_the_stats = getDay();
			date_changed = false;
			return null;
		}
		if (day_of_the_stats != getDay() || date_changed) {
			Day old_day = day_of_the_stats;
			day_of_the_stats = getDay();
			date_changed = false;
			return old_day;
		}
		return null;
	}

	// /**
	// * Sets the numeric day the agent's stats are referring to to the passed
	// * value, causing a subsequent call of isAnotherDay() function.
	// *
	// * @param day
	// */
	// public void setAnotherDay(Integer day) {
	// day_stats = day;
	// }

	/**
	 * @return the calendar
	 */
	public GregorianCalendar getCalendar() {
		return calendar;
	}

	public void goAway() {
		int speed = GraphicEnvironment.getSimulation_speed();
		if (speed == 0) {
			goAwayOfSeconds(1);
		} else if (speed < 20) {
			goAwayOfSeconds(Math.round(seconds_to_add * speed / 3));
		} else if (speed < 50) {
			goAwayOfSeconds(Math.round(seconds_to_add * 1.5F * speed));
		} else {
			goAwayOfSeconds(Math.round(seconds_to_add * speed * 3.5F));
		}

	}

	/**
	 * Method to determine if the passed date is recent or not.
	 * 
	 * @param date
	 * @return
	 */
	public boolean isActual(long old) {
		long current = calendar.getTimeInMillis();
		int to_add = (prec_prec + prec + last + 3) * 1000;

		if (old + to_add < current || old > current)
			return false;
		else
			return true;
	}
	
	public Integer getSecondsSinceLastUpdate() {
		return last;
	}

}
