package com.github.devholic.SOMAReport.Controller;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import Model.Users;


@Path("/users")
public class UsersController {

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public Users getUserList(){
		
		Users user = new Users();
		
		try{
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
