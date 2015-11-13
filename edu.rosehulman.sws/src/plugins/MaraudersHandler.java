/*
 * MaraudersHandler.java
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
 
package plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import plugins.marauders.MaraudersDELETEServlet;
import plugins.marauders.MaraudersGETServlet;
import plugins.marauders.MaraudersPOSTServlet;
import plugins.marauders.MaraudersPUTServlet;
import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 * Chandan-- change me! 
 */
public class MaraudersHandler extends Plugin{
	private Map<String, Servlet> servletMap=new HashMap<String, Servlet>();
	private List<Servlet> servlets=new ArrayList<Servlet>();
	public MaraudersHandler(){
		super();
	}
	
	public MaraudersHandler(String rootDirectory) {
		super(rootDirectory, MaraudersHandler.class.getName());
		servletMap.put(Protocol.GET, new MaraudersGETServlet(rootDirectory));
		servletMap.put(Protocol.POST, new MaraudersPOSTServlet(rootDirectory));
		servletMap.put(Protocol.PUT, new MaraudersPUTServlet(rootDirectory));
		servletMap.put(Protocol.DELETE, new MaraudersDELETEServlet(rootDirectory));
		//servlets.add(new LetItGoServlet(rootDirectory));
	}
	@Override
	public void setRootDirectory(String s){
		rootDirectory=s;
		servletMap.put(Protocol.GET, new MaraudersGETServlet(rootDirectory));
		servletMap.put(Protocol.POST, new MaraudersPOSTServlet(rootDirectory));
		servletMap.put(Protocol.PUT, new MaraudersPUTServlet(rootDirectory));
		servletMap.put(Protocol.DELETE, new MaraudersDELETEServlet(rootDirectory));
		//servlets.add(new LetItGoServlet(rootDirectory));
	}
	public HttpResponse handleRequest(HttpRequest request){
		System.out.println("Handling request");
		return (servletMap.get(request.getMethod())).handleRequest(request);
		
	}

	/* (non-Javadoc)
	 * @see plugins.Plugin#registerSelf()
	 */
	@Override
	public void registerSelf(Map<String, Plugin> map) {
		map.put(MaraudersHandler.class.getName(), this);
//		for (Servlet s: servlets){
//			servletMap.put(s.getURI(), s);
//		}
		
	}

}
