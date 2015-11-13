/*
 * Data.java
 * Nov 11, 2015
 *
 * Simple Web Server (SWS) for EE407/507 and CS455/555
 * 
 * Copyright (C) 2011 Chandan Raj Rupakheti, Clarkson University
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 * Contact Us:
 * Chandan Raj Rupakheti (rupakhcr@clarkson.edu)
 * Department of Electrical and Computer Engineering
 * Clarkson University
 * Potsdam
 * NY 13699-5722
 * http://clarkson.edu/~rupakhcr
 */
 
package plugins.marauders;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 * Chandan-- change me! 
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Data {
	int lastStudentID;
	int maxInsultID;
	String passphrase;
	public Data(){}
	
	@JsonProperty("lastStudentID")
	public void setLastStudentID(int lastStudentID){
		this.lastStudentID=lastStudentID;
	}
	/**
	 * @return the lastStudentID
	 */
	public int getLastStudentID() {
		return lastStudentID;
	}

	/**
	 * @return the maxInsultID
	 */
	public int getMaxInsultID() {
		return maxInsultID;
	}

	/**
	 * @param maxInsultID the maxInsultID to set
	 */
	@JsonProperty("maxInsultID")
	public void setMaxInsultID(int maxInsultID) {
		this.maxInsultID = maxInsultID;
	}

	/**
	 * @return the passphrase
	 */
	@JsonProperty("passphrase")
	public String getPassphrase() {
		return passphrase;
	}

	/**
	 * @param passphrase the passphrase to set
	 */
	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	
	
}
