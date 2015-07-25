package com.github.devholic.SOMAReport.Controller;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.hk2.utilities.reflection.Logger;

import com.github.devholic.SOMAReport.Model.Users;


@Path("/users")
public class UsersController {

	Logger logger = Logger.getLogger();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public Users getUserList(){
		
		Users user = new Users();
		
		try{
			logger.debug("GET users");
			
			user.setUserAge(23);
			user.setUserName("민종현");
			user.setUserSex("male");
			user.setUserYear("2015");
		}catch(Exception e){
			e.printStackTrace();
		}
			
		return user;
	}
	
}
