
 
package plugins.marauders;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 * Chandan-- change me! 
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Student {
	String name;
	String house;
	String location;
	/**
	 * 
	 */
	public Student() {
		
	}
	/**
	 * @return the name
	 */
	@JsonProperty("name")
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the house
	 */
	@JsonProperty("house")
	public String getHouse() {
		return house;
	}
	/**
	 * @param house the house to set
	 */
	public void setHouse(String house) {
		this.house = house;
	}
	/**
	 * @return the location
	 */
	@JsonProperty("location")
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	public String toString(){
		return String.format("{\"name\": \"%s\", \"house\": \"%s\", \"location\": \"%s\"}", this.name, this.house, this.location);
	}
	
	

}
