package org.gradle;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class CouchDocUtils {
	
	CloudantClient client;
	Database db;
	public JsonObject inputDoc;

	public CouchDocUtils () {
		CloudantClient client = new CloudantClient("http://somareport.cloudant.com", "somareport", "somasoma");

		System.out.println("Connected to Cloudant :: SOMAReport");
		System.out.println("Server Version: " + client.serverVersion());

		String dbName = client.getAllDbs().get(0);
		db = client.database(dbName, false);
	}
	
	public String insertDoc(String fileName) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		
		String id = " ";
		
		// get input data doc
		JsonParser parser = new JsonParser();
		JsonElement json = parser.parse(new FileReader(fileName));
		inputDoc = json.getAsJsonObject();
		Response response = db.save(inputDoc);
		id = response.getId();
		
		return id;
	}
	
	public boolean viewAllLists() {
		
		System.out.println("\n[[ All Mentors ]]");
		List<JsonObject> nameList = db.view("Search_by_name/all_mentor_list")
				.includeDocs(true).reduce(false).query(JsonObject.class);
		for (JsonObject mentor: nameList) 
			System.out.println(mentor.get("name").getAsString() + "\t" 
							+ mentor.get("section").getAsString() + "\t"
							+ mentor.get("account").getAsString() + "\t");
		
		System.out.println();
		System.out.println("\n[[ All Mentees ]]");
		nameList.clear();
		nameList = db.view("Search_by_name/all_mentee_list")
				.includeDocs(true).reduce(false).query(JsonObject.class);
		for (JsonObject mentor: nameList) 
			System.out.println(mentor.get("name").getAsString() + "\t" 
							+ mentor.get("sex").getAsString() + "\t"
							+ mentor.get("account").getAsString() + "\t");
		System.out.println();
		
		return true;
	}
	
	public List<JsonObject> viewMyProjects(String account) {
		List<JsonObject> list = db.view("Search_by_id/view_project_mentor")
				.includeDocs(false).key(" ").reduce(false).query(JsonObject.class);		
//		for (JsonObject project: )
		return list;
	}
	
	
}