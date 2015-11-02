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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 * Chandan-- change me! 
 */
public class TrickServlet extends Servlet{
	public TrickServlet(){
		super();
	}
	public TrickServlet(String rootDirectory){
		super(rootDirectory, TrickServlet.class.getName());
	}
	public HttpResponse handleRequest(HttpRequest request){
		//System.out.println(request.getHeader());
		if (request.getMethod().equals(Protocol.GET)){
			if (request.getHeader().get("referer")==null && request.getHeader().get("Referer")==null){
				
				
				request.setUri(getNewUri(request));
			}
			return get(request);
		}
		else if (request.getMethod().equals(Protocol.POST)){
			return post(request);
		}
		else if (request.getMethod().equals(Protocol.PUT)){
			return put(request);
		}
		else if (request.getMethod().equals(Protocol.DELETE)){
			return delete(request);
		}
		else{
			return HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		}
	}
	public HttpResponse get(HttpRequest request){
		String uri = request.getCrap();
		
		// Get root directory path from server
		//String rootDirectory = server.getRootDirectory();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + uri);
		// Check if the file exists
		if(file.exists()) {
			if(file.isDirectory()) {
				// Look for default index.html file in a directory
				String location = rootDirectory + uri + System.getProperty("file.separator") + Protocol.DEFAULT_FILE;
				file = new File(location);
				if(file.exists()) {
					// Lets create 200 OK response
					return HttpResponseFactory.create200OK(file, Protocol.CLOSE);
				}
				else {
					// File does not exist so lets create 404 file not found code
					return HttpResponseFactory.create404NotFound(Protocol.CLOSE);
				}
			}
			else { // Its a file
				// Lets create 200 OK response
				return HttpResponseFactory.create200OK(file, Protocol.CLOSE);
			}
		}
		else {
			// File does not exist so lets create 404 file not found code
			return HttpResponseFactory.create404NotFound(Protocol.CLOSE);
		}
	}
	
	public HttpResponse post(HttpRequest request){
		// TODO Auto-generated method stub
				String uri = request.getUri();
				// Get root directory path from server
				//String rootDirectory = server.getRootDirectory();
				// Combine them together to form absolute file path
				File file = new File(rootDirectory + uri);
				// Check if the file exists
				if (file.exists()) {
					if (file.isDirectory()) {
						return HttpResponseFactory.create304NotModified(Protocol.CLOSE);
					} else { // Its a file
								// Lets create 200 OK response
						try {
							BufferedWriter bw = new BufferedWriter(new FileWriter(rootDirectory + uri, true));
							bw.write(request.getBody());
							bw.flush();
							bw.close();
						} catch (IOException e) {
							e.printStackTrace();
							
							return HttpResponseFactory.create304NotModified(Protocol.CLOSE);
						}
						file = new File(rootDirectory + uri);
						return HttpResponseFactory.create200OK(file, Protocol.CLOSE);
					}
				} else {
					try {
						file.createNewFile();
						BufferedWriter bw = new BufferedWriter(new FileWriter(rootDirectory + uri, true));
						bw.write(request.getBody());
						bw.flush();
						bw.close();
						file = new File(rootDirectory + uri);
						return HttpResponseFactory.create200OK(file, Protocol.CLOSE);
					} catch (IOException e) {
						e.printStackTrace();
						return HttpResponseFactory.create304NotModified(Protocol.CLOSE);
					}
				}
	}
	
	public HttpResponse put(HttpRequest request){
		String uri = request.getUri();
		// Get root directory path from server
		//String rootDirectory = server.getRootDirectory();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + uri);
		// Check if the file exists
		if (file.exists()) {
			if (file.isDirectory()) {
				return HttpResponseFactory.create304NotModified(Protocol.CLOSE);
			} else { // Its a file
						// Lets create 200 OK response
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(rootDirectory + uri, false));
					bw.write(request.getBody());
					bw.flush();
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
					
					return HttpResponseFactory.create304NotModified(Protocol.CLOSE);
				}
				file = new File(rootDirectory + uri);
				return HttpResponseFactory.create204NoCon(Protocol.CLOSE);
			}
		} else {
			try {
				file.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(rootDirectory + uri, true));
				bw.write(request.getBody());
				bw.flush();
				bw.close();
				file = new File(rootDirectory + uri);
				return HttpResponseFactory.create204NoCon(Protocol.CLOSE);
			} catch (IOException e) {
				e.printStackTrace();
				return HttpResponseFactory.create304NotModified(Protocol.CLOSE);
			}
		}
	}
	
	public HttpResponse delete(HttpRequest request){
		String uri = request.getUri();
		// Get root directory path from server
		//String rootDirectory = server.getRootDirectory();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + uri);
		// Check if the file exists
		if (file.exists()) {
			if (file.isDirectory()) {
				return HttpResponseFactory.create304NotModified(Protocol.CLOSE);
			} else { // Its a file
						// Lets create 200 OK response
				file.delete();
				return HttpResponseFactory.create204NoCon(Protocol.CLOSE);
			}
		} else {

			return HttpResponseFactory.create304NotModified(Protocol.CLOSE);

		}
	}
	public String getNewUri(HttpRequest request){
		String newURI="/"+request.getContextRoot()+"/"+request.getRelativeURI()+"/";
		Random rand=new Random();
		int next=rand.nextInt(5);
		if (next==0){
			newURI = newURI.concat("newplugin.html");
		}
		else if (next==1){
			newURI = newURI.concat("rick.html");
		}
		else if (next==2){
			newURI = newURI.concat("johncena.html");
		}
		else if (next==3){
			newURI = newURI.concat("howling.html");
		}
		else if (next==4){
			newURI = newURI.concat("hat.html");
		}
		return newURI;
		
		
	}

}
