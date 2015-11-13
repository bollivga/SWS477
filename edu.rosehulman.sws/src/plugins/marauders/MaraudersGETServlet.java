/*
 * MarauderGETServlet.java
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;

import plugins.Servlet;
import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 * Chandan-- change me! 
 */
public class MaraudersGETServlet extends Servlet{
	public MaraudersGETServlet(){
		super();
	}
	public MaraudersGETServlet(String rootDirectory){
		super(rootDirectory, MaraudersGETServlet.class.getName());
	}

	/* (non-Javadoc)
	 * @see plugins.Servlet#handleRequest(protocol.HttpRequest)
	 */
	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		System.out.println(request.getLongerCrap());
		///Marauders/v1/students?passphrase=<passphrase>
		if (request.getLongerCrap().startsWith("/Marauders/v1/student?")){
		
			return handleGetStudent(request);
		}
		return HttpResponseFactory.create505NotSupported(Protocol.CLOSE);
		
	}
	public HttpResponse handleGetStudent(HttpRequest request){
		String uri=request.getLongerCrap();
		System.out.println("uri: "+uri);
		String[] uri2=uri.split("\\?");
		System.out.println(uri2);
		if (uri2.length<=1){
			return HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		}
		String paramsString=uri2[1];
		String[] params=paramsString.split("\\&");
		if (params.length==2){//specific student
			return handleGetSingleStudent(params);
		}
		else if (params.length==1){//all students
			return getAllStudents(params);
		}
		return HttpResponseFactory.create505NotSupported(Protocol.CLOSE);
		
	}
	private HttpResponse handleGetSingleStudent(String[] params){
		String name1;
		String passphrase1;
		
		if (params[0].startsWith("name=")){
			name1=params[0].split("=")[1];
			
		}
		else if (params[1].startsWith("name=")){
			name1=params[1].split("=")[1];
		}
		else {
			return HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		}
		if (params[0].startsWith("passphrase=")){
			passphrase1=params[0].split("=")[1];
			
		}
		else if (params[1].startsWith("passphrase=")){
			passphrase1=params[1].split("=")[1];
		}
		else{
			return HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		}
		String name;
		String passphrase;
		if (name1.contains("\"")){
			
			name=name1.replace("\"", "");
		}
		if (name1.contains("%22")){
			name=name1.replace("%22", "");
			if (name.contains("%20")){
				name=name.replace("%20", " ");
			}
		}
		else{
			name=name1;
		}
		if (passphrase1.contains("\"")){
			passphrase=passphrase1.replace("\"", "");
		}
		if (passphrase1.contains("%22")){
			passphrase=passphrase1.replace("%22", "");
			if (passphrase.contains("%20")){
				passphrase=passphrase.replace("%20", " ");
			}
		}
		else{
			passphrase=passphrase1;
		}
	Data d= MaraudersInteractor.getData();
	if (passphrase.equals(d.passphrase)){
			Student s=MaraudersInteractor.getStudent(name);
			try{
				File f=File.createTempFile("temp", ".txt");
						
				FileOutputStream fos=new FileOutputStream(f);
				fos.write((s.toString()).getBytes());
				fos.flush();
				fos.close();
				Thread.sleep(100);
				HttpResponse res=HttpResponseFactory.create200OK(f, Protocol.CLOSE);
				
				return res;
				}catch(Exception e){
					e.printStackTrace();
					return HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
				}
	}
	else{
		Random rand=new Random();
		int id=rand.nextInt(d.maxInsultID+1);
		
		try{
			Insult i=MaraudersInteractor.getInsult(id+"");
		File f=File.createTempFile("temp", ".txt");
				
		FileOutputStream fos=new FileOutputStream(f);
		fos.write((i.toString()).getBytes());
		fos.flush();
		fos.close();
		
		HttpResponse res=HttpResponseFactory.create394Insult(f,Protocol.CLOSE);
		//f.delete();
		return res;
		}catch(Exception e){
			e.printStackTrace();
			return HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		}
	}
	}
	private HttpResponse getAllStudents(String[] params){
		String passphrase1;
		String passphrase;
		if (params[0].startsWith("passphrase=")){
			passphrase1=params[0].split("=")[1];

		}
		else if (params[1].startsWith("passphrase=")){
			passphrase1=params[1].split("=")[1];
		}
		else{
			return HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		}
		if (passphrase1.contains("\"")){
			passphrase=passphrase1.replace("\"", "");
		}
		if (passphrase1.contains("%22")){
			passphrase=passphrase1.replace("%22", "");
			if (passphrase.contains("%20")){
				passphrase=passphrase.replace("%20", " ");
			}
		}
		else{
			passphrase=passphrase1;
		}
		Data d= MaraudersInteractor.getData();
		if (passphrase.equals(d.passphrase)){
			List<Student> students=MaraudersInteractor.getAllStudents();
			try{
				File f=File.createTempFile("temp", ".txt");

				FileOutputStream fos=new FileOutputStream(f);
				fos.write("[".getBytes());
				for (int i=0; i<students.size()-1; i++){
					Student s=students.get(i);
					fos.write((s.toString()).getBytes());
					fos.write(", ".getBytes());
				}
				Student s= students.get(students.size()-1);
				fos.write(s.toString().getBytes());
				fos.write("]".getBytes());
				fos.flush();
				fos.close();
				Thread.sleep(100);
				HttpResponse res=HttpResponseFactory.create200OK(f,Protocol.CLOSE);
				

				return res;
			}catch(Exception e){
				e.printStackTrace();
				return HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
			}
		}
		else{
			Random rand=new Random();
			int id=rand.nextInt(d.maxInsultID+1);

			try{
				Insult i=MaraudersInteractor.getInsult(id+"");
				File f=File.createTempFile("temp", ".txt");

				FileOutputStream fos=new FileOutputStream(f);
				fos.write((i.toString()).getBytes());
				fos.flush();
				fos.close();
				Thread.sleep(100);
				HttpResponse res=HttpResponseFactory.create394Insult(f, Protocol.CLOSE);
				//f.delete();
				return res;
			}catch(Exception e){
				e.printStackTrace();
				return HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
			}
		}
		
	}
	

}
