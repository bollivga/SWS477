/*
 * ConnectionHandler.java
 * Oct 7, 2012
 *
 * Simple Web Server (SWS) for CSSE 477
 * 
 * Copyright (C) 2012 Chandan Raj Rupakheti
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
 */
 
package server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

import plugins.Plugin;
import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;
import protocol.ProtocolException;

/**
 * This class is responsible for handling a incoming request
 * by creating a {@link HttpRequest} object and sending the appropriate
 * response be creating a {@link HttpFileResponse} object. It implements
 * {@link Runnable} to be used in multi-threaded environment.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class ConnectionHandler implements Runnable {
	private Server server;
	private Socket socket;

	
	public ConnectionHandler(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		
		
		
//		handlers.put(Protocol.GET, new GetHandler(server));
//		handlers.put(Protocol.POST, new PostHandler(server));
//		handlers.put(Protocol.PUT, new PutHandler(server));
//		handlers.put(Protocol.DELETE, new DeleteHandler(server));
	}
	
	/**
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}


	/**
	 * The entry point for connection handler. It first parses
	 * incoming request and creates a {@link HttpRequest} object,
	 * then it creates an appropriate {@link HttpFileResponse} object
	 * and sends the response back to the client (web browser).
	 */
	public void run() {
		System.out.println("Running");
		try{
		// Get the start time
		long start = System.currentTimeMillis();
		
		InputStream inStream = null;
		OutputStream outStream = null;
		
		try {
			inStream = this.socket.getInputStream();
			outStream = this.socket.getOutputStream();
			if(server.isThrottled(this.socket.getInetAddress().getAddress())){
				logError(new Exception(this.socket.getInetAddress().getAddress()+" has been throttled"));
			return;
			}
		}
		catch(Exception e) {
			// Cannot do anything if we have exception reading input or output stream
			// May be have text to log this for further analysis?
			//e.printStackTrace();
			logError(e);
			
			// Increment number of connections by 1
			server.incrementConnections(1);
			// Get the end time
			long end = System.currentTimeMillis();
			this.server.incrementServiceTime(end-start);
			return;
		}
		
		// At this point we have the input and output stream of the socket
		// Now lets create a HttpRequest object
		HttpRequest request = null;
		HttpResponse response = null;
		try {
			
			request = HttpRequest.read(inStream);
			System.out.println("Got request");
		}
		catch(ProtocolException pe) {
			// We have some sort of protocol exception. Get its status code and create response
			// We know only two kind of exception is possible inside fromInputStream
			// Protocol.BAD_REQUEST_CODE and Protocol.NOT_SUPPORTED_CODE
			int status = pe.getStatus();
			if(status == Protocol.BAD_REQUEST_CODE) {
				response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
			}
			// TODO: Handle version not supported code as well
		}
		catch(Exception e) {
			logError(e);
			// For any other error, we will create bad request response as well
			response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		}
		
		if(response != null) {
			// Means there was an error, now write the response object to the socket
			try {
				response.write(outStream);
			}
			catch(Exception e){
				logError(e);
				// We will ignore this exception
				e.printStackTrace();
			}

			// Increment number of connections by 1
			server.incrementConnections(1);
			// Get the end time
			long end = System.currentTimeMillis();
			this.server.incrementServiceTime(end-start);
			return;
		}
		
		// We reached here means no error so far, so lets process further
		try {
			// Fill in the code to create a response for version mismatch.
			// You may want to use constants such as Protocol.VERSION, Protocol.NOT_SUPPORTED_CODE, and more.
			// You can check if the version matches as follows
			if(!request.getVersion().equalsIgnoreCase(Protocol.VERSION)) {
				// Here you checked that the "Protocol.VERSION" string is not equal to the  
				// "request.version" string ignoring the case of the letters in both strings
				// TODO: Fill in the rest of the code here
			}
			else {
				System.out.println(request.getContextRoot());
				Plugin handler = this.server.handlers.get(request.getContextRoot());
				/*System.out.println(request.getMethod());
				System.out.println(request.getUri());
				
				System.out.println(request.getHeader());*/
				if(handler == null){
					response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
				}/*else{
				if(request.getUri().endsWith("/favicon.ico")){
					logRequest(request);
					response = HttpResponseFactory.create200OK(new File(Server.getRootDirectory()+"/favicon.ico"), Protocol.CLOSE);
				}*/else{
					logRequest(request);
					response = handler.handleRequest(request);
				//}
				}
				
			}
		}
		catch(Exception e) {
			logError(e);
		}
		

		// TODO: So far response could be null for protocol version mismatch.
		// So this is a temporary patch for that problem and should be removed
		// after a response object is created for protocol version mismatch.
		if(response == null) {
			response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		}
		
		try{
			// Write response and we are all done so close the socket
			response.write(outStream);
			socket.close();
		}
		catch(Exception e){
			// We will ignore this exception
			logError(e);
		} 
		
		// Increment number of connections by 1
		server.incrementConnections(1);
		// Get the end time
		long end = System.currentTimeMillis();
		this.server.incrementServiceTime(end-start);
		}catch(Exception e){
			logError(e);
		}
	}
	public static void logError(Exception e){
		File f = new File(Server.getRootDirectory()+"/error.log");
		try {
			FileOutputStream out = new FileOutputStream(f, true);
			out.write((LocalDateTime.now().toString()+" ").getBytes());
			
			String s = e.getMessage();
			out.write(s.getBytes());
			out.write("\n".getBytes());
			out.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
	}
	public static void logRequest(HttpRequest r){
		File f = new File(Server.getRootDirectory()+"/requests.log");
		try {
			FileOutputStream out = new FileOutputStream(f, true);
			out.write((LocalDateTime.now().toString()+" ").getBytes());
			out.write((r.getVersion().toString()+" ").getBytes());
			out.write((r.getUri().toString()+" ").getBytes());
			out.write(r.getHeader().toString().getBytes());
			out.write("\n".getBytes());
			out.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
