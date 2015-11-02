/*
 * Server.java
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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import gui.WebServer;
import plugins.Plugin;

/**
 * This represents a welcoming server for the incoming TCP request from a HTTP
 * client such as a web browser.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class Server implements Runnable {
	private static String rootDirectory;
	private int port;
	private boolean stop;
	private ServerSocket welcomeSocket;
	public Map<String, Plugin> handlers = new HashMap<String, Plugin>();
	private Map<byte[], Integer> tracker = new HashMap<byte[], Integer>();
	private long connections;
	private long serviceTime;

	private WebServer window;

	/**
	 * @param rootDirectory
	 * @param port
	 */
	public Server(String rootDirectory, int port, WebServer window) {
		this.rootDirectory = rootDirectory;
		this.port = port;
		this.stop = false;
		this.connections = 0;
		this.serviceTime = 0;
		this.window = window;
	}

	/**
	 * Gets the root directory for this web server.
	 * 
	 * @return the rootDirectory
	 */
	public static String getRootDirectory() {
		return rootDirectory;
	}

	/**
	 * Gets the port number for this web server.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Returns connections serviced per second. Synchronized to be used in
	 * threaded environment.
	 * 
	 * @return
	 */
	public synchronized double getServiceRate() {
		if (this.serviceTime == 0)
			return Long.MIN_VALUE;
		double rate = this.connections / (double) this.serviceTime;
		rate = rate * 1000;
		return rate;
	}

	/**
	 * Increments number of connection by the supplied value. Synchronized to be
	 * used in threaded environment.
	 * 
	 * @param value
	 */
	public synchronized void incrementConnections(long value) {
		this.connections += value;
	}

	/**
	 * Increments the service time by the supplied value. Synchronized to be
	 * used in threaded environment.
	 * 
	 * @param value
	 */
	public synchronized void incrementServiceTime(long value) {
		this.serviceTime += value;
	}

	/**
	 * The entry method for the main server thread that accepts incoming TCP
	 * connection request and creates a {@link ConnectionHandler} for the
	 * request.
	 */
	public void run() {
		try {
			ThreadThrottler tt=new ThreadThrottler(this);
			new Thread(tt).start();
			Path dir = Paths.get(rootDirectory + "/plugins");
			File x = dir.toFile();
			File[] jars = x.listFiles();
			for (File q : jars) {
				Path filename = q.toPath();
				if (filename.toString().endsWith(".jar")) {
					JarClassLoader loader = new JarClassLoader(filename.toString());
					String classname = filename.toString();
					classname = classname.substring(classname.lastIndexOf("\\") + 1, classname.lastIndexOf("."));
					try {
						Class c = loader.loadClass(classname, true);

						Object o = c.newInstance();
						if (o instanceof Plugin) {
							Plugin p = (Plugin) o;
							p.setContextRoot(p.getClass().getName());
							p.setRootDirectory(this.getRootDirectory());
							p.registerSelf(this.handlers);
						} else {
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						ConnectionHandler.logError(e);
					}
				}
			}
			WatchDir watchDir = new WatchDir(dir, true);
			watchDir.setServer(this);
			new Thread(watchDir).start();
			this.welcomeSocket = new ServerSocket(this.port);

			// Now keep welcoming new connections until stop flag is set to true
			while (true) {
				// Listen for incoming socket connection
				// This method block until somebody makes a request

				Socket connectionSocket = this.welcomeSocket.accept();

				// Come out of the loop if the stop flag is set
				if (this.stop)
					break;
				// Create a handler for this incoming connection and start the
				// handler in a new thread
				ConnectionHandler handler = new ConnectionHandler(this, connectionSocket);
				new Thread(handler).start();

			}
			this.welcomeSocket.close();
		} catch (Exception e) {
			ConnectionHandler.logError(e);
			this.window.showSocketException(e);
		}
	}

	/**
	 * Stops the server from listening further.
	 */
	public synchronized void stop() {
		if (this.stop)
			return;

		// Set the stop flag to be true
		this.stop = true;
		try {
			// This will force welcomeSocket to come out of the blocked accept()
			// method
			// in the main loop of the start() method
			Socket socket = new Socket(InetAddress.getLocalHost(), this.port);

			// We do not have any other job for this socket so just close it
			socket.close();
		} catch (Exception e) {
			ConnectionHandler.logError(e);
		}
	}

	/**
	 * Checks if the server is stopeed or not.
	 * 
	 * @return
	 */
	public boolean isStoped() {
		if (this.welcomeSocket != null)
			return this.welcomeSocket.isClosed();
		return true;
	}

	/**
	 * @param bs
	 * @return
	 */
	public boolean isThrottled(byte[] bs) {
		boolean contains = false;
		byte[] ar = new byte[1];
		for (byte[] x : this.tracker.keySet()) {
			if (Arrays.equals(x, bs)) {
				contains = true;
				ar = x;
			}
		}
		if (contains) {
			if (this.tracker.get(ar) > 50) {
				return true;
			}
			this.tracker.put(bs, this.tracker.remove(ar) + 1);
		} else {
			this.tracker.put(bs, 1);

		}
		return false;

	}

	/**
	 * 
	 */
	public void clearTrackers() {
		this.tracker.clear();
		
	}
	public long getConnections(){
		return this.connections;
	}
	public int getThreadCount(){
		return Thread.activeCount();
	}
}
