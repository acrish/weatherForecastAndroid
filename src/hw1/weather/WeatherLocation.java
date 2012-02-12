package hw1.weather;

import java.io.Serializable;

/**
 * Location in the world in format town/city - country/state, which represents all information needed to find out 
 * weather forecast for the location. It implements Serializable in order to be passed between activities through 
 * intents. 
 * 
 * @author Andreea Hodea
 *
 */
public class WeatherLocation implements Comparable<WeatherLocation>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String town, country;	

	public WeatherLocation(String town, String country) {
		this.town = town;
		this.country = country;
	}

	public String getTown() {
		return town;
	}

	public String getCountry() {
		return country;
	}

	public String toString() {
		return town + " (" + country + ")";
	}

	public int compareTo(WeatherLocation another) {
		int result = town.compareToIgnoreCase(another.town);
		if (result == 0)
			return country.compareToIgnoreCase(another.country);
		return result;
	}

	public boolean equals(Object o) {
		if (o instanceof WeatherLocation){
			WeatherLocation loc = (WeatherLocation)o;
			return town.equals(loc.town) && country.equals(loc.country);
		}
		
		return false;
	}
	
	public int hashCode() {
		return town.hashCode() + 31 * country.hashCode();
	}
}
