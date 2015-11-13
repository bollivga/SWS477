/*
 * MaraudersPUTServlet.java
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
import java.util.HashMap;
import java.util.Random;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
public class MaraudersPUTServlet extends Servlet{
	public MaraudersPUTServlet(){
		super();
	}
	public MaraudersPUTServlet(String rootDirectory){
		super(rootDirectory, MaraudersPUTServlet.class.getName());
	}

	/* (non-Javadoc)
	 * @see plugins.Servlet#handleRequest(protocol.HttpRequest)
	 */
	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		String body=new String(request.getBody());
		
		ObjectMapper mapper=new ObjectMapper();
		TypeReference<HashMap<String, Object>> typeReference=
                new TypeReference<HashMap<String, Object>>() {
                };
		try{
            HashMap<String, Object> map=mapper.readValue(body, typeReference);
            
           String nameObj=(String)map.get("name");
           String passphraseObj=(String)map.get("passphrase");
           String houseObj=(String)map.get("house");
           
           String locationObj=(String)map.get("location");
           Student s=new Student();
           s.setHouse(houseObj);
           s.setName(nameObj);
           s.setLocation(locationObj);
           
           Data d= MaraudersInteractor.getData();
   		boolean okPassphrase= passphraseObj.equals(d.passphrase);
           if (okPassphrase){
        	   MaraudersInteractor.addStudent(s);
        	   
        	   return HttpResponseFactory.create204NoCon(Protocol.CLOSE);
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
           
		}catch(Exception e){
			e.printStackTrace();
		}
		return HttpResponseFactory.create505NotSupported(Protocol.CLOSE);
	}

}
