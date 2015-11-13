/*
 * MaraudersInteractor.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.internal.LinkedTreeMap;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedString;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.Response;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 * Chandan-- change me! 
 */
public class MaraudersInteractor {
	
	public static Data getData(){
		StringBuilder json=new StringBuilder();
		try{
		URL server = new URL("http://s40server.csse.rose-hulman.edu:9200/marauders/data/0");
        URLConnection yc = server.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                yc.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null && inputLine !=""){
        	System.out.println(inputLine);
        	 json.append(inputLine);
        }
           
        in.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		ObjectMapper mapper=new ObjectMapper();
		TypeReference<HashMap<String, Object>> typeReference=
                new TypeReference<HashMap<String, Object>>() {
                };
		try{
            HashMap<String, Object> map=mapper.readValue(json.toString(), typeReference);
            HashMap<String, Object> source=(HashMap)map.get("_source");
            Data d=new Data();
            d.setLastStudentID(Integer.parseInt((String)source.get("lastStudentID")));
            d.setMaxInsultID(Integer.parseInt((String)source.get("maxInsultID")));
            d.setPassphrase((String)source.get("passphrase"));
            return d;
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
		
	}
	public static void addStudent(Student s){
		
		
		
		try{
			Data d= getData();
			d.getLastStudentID();
			RestAdapter adapter = new RestAdapter.Builder()
		            .setEndpoint("http://s40server.csse.rose-hulman.edu:9200")
		            .build();
		    MaraudersService service=adapter.create(MaraudersService.class);
		    LinkedTreeMap o=(LinkedTreeMap) service.addStudent(d.getLastStudentID()+1, new TypedJsonString(s.toString()));
		    System.out.println(o.getClass().getName());
//		    ArrayList list=(ArrayList)((LinkedTreeMap)o.get("hits")).get("hits");
//		    for (Object obj: list){
//		    	LinkedTreeMap obj2=(LinkedTreeMap) obj;
//		    	LinkedTreeMap source=(LinkedTreeMap) obj2.get("_source");
//		    	Student s=new Student();
//                s.setHouse((String) source.get("house"));
//                s.setLocation((String) source.get("location"));
//                s.setName((String)source.get("name"));
//                System.out.println(s.toString());
//		    	return s;
//		    }
//		    return null;
		  } catch (Exception e) {
		    e.printStackTrace();
//		    return null;
		  }
	}
	public static void updateStudentIDData(int newStudentMax){
		String doc=String.format("{\"doc\": {\"lastStudentID\": \"%s\"}}", newStudentMax+"");
		
		
		
		try{
			Data d= getData();
			d.getLastStudentID();
			RestAdapter adapter = new RestAdapter.Builder()
		            .setEndpoint("http://s40server.csse.rose-hulman.edu:9200")
		            .build();
		    MaraudersService service=adapter.create(MaraudersService.class);
		    LinkedTreeMap o=(LinkedTreeMap) service.updateData(new TypedJsonString(doc.toString()));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static Student getStudent(String searchTerms){
		String search=String.format("{\"query\": {\"filtered\": {\"filter\": {\"term\": "+
				"{\"name\": \"%s\"}}}}}", searchTerms);
		StringBuilder json=new StringBuilder();
		try{
			RestAdapter adapter = new RestAdapter.Builder()
		            .setEndpoint("http://s40server.csse.rose-hulman.edu:9200")
		            .build();
		    MaraudersService service=adapter.create(MaraudersService.class);
		    LinkedTreeMap o=(LinkedTreeMap) service.getStudent(new TypedJsonString(search));
		    ArrayList list=(ArrayList)((LinkedTreeMap)o.get("hits")).get("hits");
		    for (Object obj: list){
		    	LinkedTreeMap obj2=(LinkedTreeMap) obj;
		    	LinkedTreeMap source=(LinkedTreeMap) obj2.get("_source");
		    	Student s=new Student();
                s.setHouse((String) source.get("house"));
                s.setLocation((String) source.get("location"));
                s.setName((String)source.get("name"));
                System.out.println(s.toString());
		    	return s;
		    }
		    return null;
		  } catch (Exception e) {
		    e.printStackTrace();
		    return null;
		  }
//		  return null;
		
		
	}
	public static List<Student> getAllStudents(){
//		String search=String.format("{\"query\": {\"filtered\": {\"filter\": {\"term\": "+
//				"{\"name\": \"%s\"}}}}}", searchTerms);
		StringBuilder json=new StringBuilder();
		try{
			RestAdapter adapter = new RestAdapter.Builder()
		            .setEndpoint("http://s40server.csse.rose-hulman.edu:9200")
		            .build();
		    MaraudersService service=adapter.create(MaraudersService.class);
		    LinkedTreeMap o=(LinkedTreeMap) service.getAllStudent();
		    ArrayList list=(ArrayList)((LinkedTreeMap)o.get("hits")).get("hits");
		    List<Student> students= new ArrayList<Student>();
		    for (Object obj: list){
		    	LinkedTreeMap obj2=(LinkedTreeMap) obj;
		    	LinkedTreeMap source=(LinkedTreeMap) obj2.get("_source");
		    	Student s=new Student();
                s.setHouse((String) source.get("house"));
                s.setLocation((String) source.get("location"));
                s.setName((String)source.get("name"));
                System.out.println(s.toString());
		    	students.add(s);
		    }
		    return students;
		  } catch (Exception e) {
		    e.printStackTrace();
		    return null;
		  }
//		  return null;
		
		
	}
	
	public static Insult getInsult(String id){
		StringBuilder json=new StringBuilder();
		try{
			RestAdapter adapter = new RestAdapter.Builder()
		            .setEndpoint("http://s40server.csse.rose-hulman.edu:9200")
		            .build();
		    MaraudersService service=adapter.create(MaraudersService.class);
		    com.google.gson.internal.LinkedTreeMap o=(com.google.gson.internal.LinkedTreeMap)service.getInsult(Integer.parseInt(id));
		    System.out.println("Obj"+o);
		    System.out.println("Type: "+o.getClass().getName());
		    json.append(o.toString());
		    
//		URL server = new URL("http://s40server.csse.rose-hulman.edu:9200/marauders/insult/"+id);
//        URLConnection yc = server.openConnection();
//        BufferedReader in = new BufferedReader(
//                                new InputStreamReader(
//                                yc.getInputStream()));
//        String inputLine;
//
//        while ((inputLine = in.readLine()) != null && inputLine !="") {
//        	System.out.println(inputLine);
//            json.append(inputLine);
//        }
//        in.close();
		System.out.println("JSON: "+json.toString());
            
            LinkedTreeMap source=(LinkedTreeMap) o.get("_source");
            System.out.println("type: "+source.getClass().getName());
            Insult i=new Insult();
            i.setCode((String) source.get("code"));
            i.setMessage((String) source.get("message"));
            System.out.println(i.toString());
            return i;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	
	public static class TypedJsonString extends TypedString {
        public TypedJsonString(String body) {
            super(body);
        }

        @Override public String mimeType() {
            return "application/json";
        }
    }
   
	public interface MaraudersService{
		@GET("/marauders/insult/{id}")
		public Object getInsult(@Path("id") int id);
		
		@POST("/marauders/student/_search")
	    @Headers("Accept: application/json")
		public Object getStudent(@Body TypedString body);
		
		@GET("/marauders/student/_search")
	    @Headers("Accept: application/json")
		public Object getAllStudent();
		
		@PUT("/marauders/student/{id}")
		@Headers("Accept: application/json")
		public Object addStudent(@Path("id") int id, @Body TypedString body);
		
		@POST("/marauders/student/{id}/_update")
		@Headers("Accept: application/json")
		public Object updateStudent(@Body TypedString body);
		
		@POST("/marauders/data/0/_update")
		@Headers("Accept: application/json")
		public Object updateData(@Body TypedString body);
		
		
		
	}

}
