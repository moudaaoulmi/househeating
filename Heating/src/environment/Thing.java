package environment;

/**
 * Class that represents a thing that can be present in a certain Room, that has
 * the capability to add or subtract heat to/from the room. Each thing object is
 * provided by a thermal power and an energy value: the thermal power value
 * indicates how the object affect the temperature of the room (considering it
 * like an heating or cooler, with a certain power), while the energy value
 * indicates how many energy the considered object does absorb.
 * 
 * @author falkor
 * 
 */
public class Thing {

	String name;
	Integer thermal_power, energy;

	public Thing(String name, Integer pow, Integer energy) {
		this.name = name;
		this.thermal_power = pow;
		this.energy = energy;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getThermal_power() {
		return thermal_power;
	}

	public void setThermal_power(Integer power) {
		this.thermal_power = power;
	}

	/**
	 * @return the energy
	 */
	public Integer getEnergy() {
		return energy;
	}

	/**
	 * @param energy
	 *            the energy to set
	 */
	public void setEnergy(Integer energy) {
		this.energy = energy;
	}

	@Override
	public String toString() {
		return "Thing " + this.name + " with termal power "
				+ this.thermal_power;
	}

}
