package com.github.devholic.SOMAReport.Controller;


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import com.github.devholic.SOMAReport.Model.Users;


@Path("/users")
public class UsersController {

	
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public List<Users> getUserList(){

		List<Users> user_list = new ArrayList<Users>();
		
		Users user1 = new Users();
		Users user2 = new Users();
		Users user3 = new Users();
		
		try{		
			user1.setUserId("123");
			user2.setUserId("456");
			user3.setUserId("789");
			
			user_list.add(user1);
			user_list.add(user2);
			user_list.add(user3);
			
		}catch(Exception e){
			e.printStackTrace();
		}
			
		return user_list;
	}
	
	@GET
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public Users getUserDetails(@PathParam("userId") String userId){
		
		Users user = new Users();
		
		try{
			System.out.println("param userId =["+userId+"]");
			
			user.setUserId(userId);
		}catch(Exception e){
			e.printStackTrace();
		}
			
		return user;
	}
	
}
