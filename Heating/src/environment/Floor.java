package environment;

import java.util.ArrayList;

public class Floor {

	Integer num;
	ArrayList<Room> rooms;

	/**
	 * Default constructor
	 * @param num
	 * @param rooms
	 */
	public Floor(Integer num, ArrayList<Room> rooms) {
		this.num = num;
		this.rooms = rooms;
	}

	/**
	 * @return the num of the floor
	 */
	public Integer getNum() {
		return num;
	}
	/**
	 * @param num the num to set
	 */
	public void setNum(Integer num) {
		this.num = num;
	}
	/**
	 * @return the rooms
	 */
	public ArrayList<Room> getRooms() {
		return rooms;
	}
	/**
	 * @param rooms the rooms to set
	 */
	public void setRooms(ArrayList<Room> rooms) {
		this.rooms = rooms;
	}
	
	@Override
	public String toString() {
		String to_return = "Floor num " + num + " having rooms: ";
		for (Room curr_room : rooms) {
			to_return += curr_room.getName() + ", ";
		}
		to_return = to_return.substring(0, to_return.length() - 2);
		
		return to_return;
	}
	
	
	
}
