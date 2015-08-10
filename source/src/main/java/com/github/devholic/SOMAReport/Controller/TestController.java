package com.github.devholic.SOMAReport.Controller;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.cloudant.client.api.model.SearchResult;
import com.cloudant.client.api.model.SearchResult.SearchResultRows;
import com.github.devholic.SOMAReport.Database.ReferenceUtil;
import com.google.gson.JsonObject;



@Path("/to_test")
public class TestController {

	//Log4j setting
	private final Logger logger = Logger.getLogger(TestController .class);
//	DocumentUtil doc_util = new DocumentUtil("");
	ReferenceUtil ref_util = new ReferenceUtil("");
	
	@GET
	@Path("/xmlpropertiesTest")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public Response xmlpropertiesTest(){
		logger.debug("xmlpropertiesTest invoked..");
		
		try{

			Properties prop = new Properties();
		    FileInputStream fis = new FileInputStream("config.xml");
		    prop.loadFromXML(fis);
		    logger.debug("\n URL: " + prop.getProperty("test"));
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return Response.status(200).type(MediaType.APPLICATION_JSON).build();
	}
	
	
	@GET
	@Path("/search/{query}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public JsonObject elasticsearch_searchTest(@PathParam("query") String query){

		SearchResult<JsonObject> result = new SearchResult<JsonObject>();
		JsonObject jo = new JsonObject();
		try{
			result = ref_util.searchReport(query);
			jo = result.getRows().get(0).getDoc();
			
			logger.info(query+"'s searchDoc:"+result.getRows().get(0).getDoc());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return jo;
	}
}
