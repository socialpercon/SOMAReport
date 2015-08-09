package com.github.devholic.SOMAReport.Controller;

import java.io.FileInputStream;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.cloudant.client.api.model.SearchResult;
import com.github.devholic.SOMAReport.Database.DocumentUtil;
import com.google.gson.JsonObject;



@Path("/to_test")
public class TestController {

	//Log4j setting
	private final Logger logger = Logger.getLogger(TestController .class);
	DocumentUtil doc_util = new DocumentUtil("");
	
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
	@Path("/elasticsearch_searchTest")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public Response elasticsearch_searchTest(){
		logger.debug("elasticsearch_searchTest invoked..");
		
		try{
			SearchResult<JsonObject> result = doc_util.searchReport("북경");
			System.out.println(result);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return Response.status(200).type(MediaType.APPLICATION_JSON).build();
	}
}
