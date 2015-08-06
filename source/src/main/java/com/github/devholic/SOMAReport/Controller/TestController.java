package com.github.devholic.SOMAReport.Controller;

import java.io.FileInputStream;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;



@Path("/to_test")
public class TestController {

	//Log4j setting
	private final Logger logger = Logger.getLogger(StatisticsController .class);
	
	@GET
	@Path("/xmlpropertiesTest")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public Response xmlpropertiesTest(){
		 
		try{
			logger.debug("xmlpropertiesTest invoked..");
		
			Properties prop = new Properties();
		    FileInputStream fis = new FileInputStream("config.xml");
		    prop.loadFromXML(fis);
		    logger.debug("\n URL: " + prop.getProperty("test"));
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return Response.status(200).type(MediaType.APPLICATION_JSON).build();
	}
}