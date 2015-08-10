package com.github.devholic.SOMAReport.Controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.cloudant.client.api.model.SearchResult;
import com.github.devholic.SOMAReport.Database.DocumentUtil;
import com.github.devholic.SOMAReport.Database.ReferenceUtil;
import com.google.gson.JsonObject;

@Path("/search")
public class SearchController {

	private final Logger logger = Logger.getLogger(SearchController.class);

	ReferenceUtil ref_util = new ReferenceUtil("");
	DocumentUtil doc_util = new DocumentUtil("");

	@GET
	@Path("/report/{query}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public ArrayList<JsonObject> searchReportController(@PathParam("query") String query){

		SearchResult<JsonObject> result = new SearchResult<JsonObject>();
		JsonObject jo = new JsonObject();
		ArrayList<JsonObject> jo_list = new ArrayList<JsonObject>();
		
		try{
			result = ref_util.searchReport(query);

			for(int i=0; i<result.getTotalRows(); i++){
				jo = result.getRows().get(i).getDoc();
				logger.info(query+"'s report searchDoc:"+result.getRows().get(i).getDoc()+"\n");
				jo_list.add(jo);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return jo_list;
	}
	
	@GET
	@Path("/project/{query}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public ArrayList<JsonObject> searchProjectController(@PathParam("query") String query){

		SearchResult<JsonObject> result = new SearchResult<JsonObject>();
		JsonObject jo = new JsonObject();
		ArrayList<JsonObject> jo_list = new ArrayList<JsonObject>();
		
		try{
			result = ref_util.searchProject(query);

			for(int i=0; i<result.getTotalRows(); i++){
				jo = result.getRows().get(i).getDoc();
				logger.info(query+"'s project searchDoc:"+result.getRows().get(i).getDoc()+"\n");
				jo_list.add(jo);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return jo_list;
	}
}
