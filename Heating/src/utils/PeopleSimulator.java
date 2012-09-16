package utils;

import java.util.ArrayList;
import java.util.Collections;

import environment.Environment;
import environment.Room;
import environment.TimeHandler;

public class PeopleSimulator implements Runnable {

	int time_interval, people, num_of_p;

	public PeopleSimulator() {
		Thread t = new Thread(this);
		t.start();
	}

	public void doIt() {
		time_interval = TimeHandler.getInstance().getCurrentInterval();

		if ((time_interval >= 26 && time_interval <= 29) || (time_interval >= 41 && time_interval <= 44)) {
			Environment.rooms.get("Cucina").setNum_of_people(5);
			System.out.println("adding 5 people in room Letto1");
			
			return;
		}
		else if ((time_interval >= 45 && time_interval <= 48)
				|| (time_interval >= 1 && time_interval <= 15)) {
			
			Environment.rooms.get("Letto1").setNum_of_people(2);
			System.out.println("adding 2 people in room Letto1");

			Environment.rooms.get("Letto2").setNum_of_people(2);
			System.out.println("adding 2 people in room Letto2");

			Environment.rooms.get("Letto3").setNum_of_people(1);
			System.out.println("adding 1 people in room Letto3");
			
			return;
		} else {
			ArrayList<Room> rooms = new ArrayList<Room>();
			rooms.addAll(Environment.rooms.values());
			// remove people from rooms before adding them randomly!
			for (Room room : rooms)
				room.setNum_of_people(0);

			Collections.shuffle(rooms);

			people = (int) (Math.random() * 100) % 5;

			while (people > 0 && !rooms.isEmpty()) {
				if (((int) (Math.random() * 100) % 2) == 1) {
					num_of_p = ((int) (Math.random() * 100) % 3);
					rooms.get(0).setNum_of_people(num_of_p);
					people -= num_of_p;
					System.out.println("adding " + num_of_p
							+ " people in room " + rooms.get(0).getName());
				}
				rooms.remove(0);
			}
		}
	}

	@Override
	public void run() {
		while (true) {
			doIt();
			try {
				// sleep for a number of seconds between 10 and 20
				Thread.sleep(((int) (Math.random() * 1000000) % 30000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
