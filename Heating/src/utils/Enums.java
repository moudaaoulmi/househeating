package utils;

public class Enums {

	public enum Day {
		SUNDAY(1), MONDAY(2), TUESDAY(3), WEDNESDAY(4), THURSDAY(5), FRIDAY(6), SATURDAY(
				7);

		private int num;

		Day(int num) {
			this.num = num;
		}

		public Integer getDayNum() {
			return this.num;
		}

		public static Day getDayByNumber(Integer num) {
			for (Day day : Day.values()) {
				if (day.getDayNum() == num)
					return day;
			}
			return null;
		}
	}

	public enum Month {
		JANUARY(1), FEBRUARY(2), MARCH(3), APRIL(4), MAY(5), JUNE(6), JULY(7), AUGUST(
				8), SEPTEMBER(9), OCTOBER(10), NOVEMBER(11), DECEMBER(12);

		private int num;

		Month(int num) {
			this.num = num;
		}

		public Integer getNumericMonth() {
			return this.num;
		}

		public static Month getMonthByNumber(Integer num) {
			for (Month month : Month.values()) {
				if (month.getNumericMonth() == num)
					return month;
			}
			return null;
		}
	}

	public enum Priority {

		 SOMEONE_IN_ROOM(2, 0), SOMEONE_IN_1(3, 0), SOMEONE_IN_2(4, 0), SOMEONE_IN_3(
				5, 0), SOMEONE_IN_4(6, 0), MIN_TEMP(7, 0), NOTHING(8, 0);

		private int type;
		private int level;

		Priority(int type, int level) {
			this.type = type;
			this.level = level;
		}

		public Integer getType() {
			return this.type;
		}

		public Integer getLevel() {
			return this.level;
		}

		public void setLevel(Integer level) {
			this.level = level;
		}

		public static Priority getPriorityByType(Integer type) {
			for (Priority p : Priority.values()) {
				if (p.getType() == type)
					return p;
			}
			return null;
		}

		public String serialize() {
			return this.name() + ":" + this.type + ":" + this.level;
		}

		public static Priority deSerialize(String str) {
			String[] splitted = str.split(":");

			Priority p = Priority.getPriorityByType(Integer
					.parseInt(splitted[1]));
			p.setLevel(Integer.parseInt(splitted[2]));

			return p;
		}

		@Override
		public String toString() {
			return this.name() + " (" + this.type + "). Level: " + this.level;
		}
	}

	public enum MsgType {
		ENERGY_REQUEST, PRIORITY_REQUEST, PRIORITY_REPLY, TURN_OFF_ORDER, 
		REDUCTION_ORDER, PRIORITY_ANSWER, MORE_POWER, WEAK_PRIORITY_REQUEST,
		WEAK_REDUCTION_ORDER, REDUCTION_CONFIRMATION, REPLY_OK;
	}

}
