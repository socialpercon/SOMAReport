package com.github.devholic.SOMAReport.Controller;


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.devholic.SOMAReport.Model.Users;


@Path("/users")
public class UsersController {

	/**************************************************************************
	 * 사용자 리스트를 가져온다
	 * @return List<Users>
	 *************************************************************************/
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public List<Users> getUserList(){

		List<Users> user_list = new ArrayList<Users>();
		
		Users user1 = new Users();
		Users user2 = new Users();
		Users user3 = new Users();
		
		try{		
			user1.setUserId("user1");
			user2.setUserId("user2");
			user3.setUserId("user3");
			
			user_list.add(user1);
			user_list.add(user2);
			user_list.add(user3);
			
		}catch(Exception e){
			e.printStackTrace();
		}
			
		return user_list;
	}
	
	/**************************************************************************
	 * 사용자 상세정보를 가져온다
	 * @param userId
	 * @return Users
	 *************************************************************************/
	@GET
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public Users getUserDetails(@PathParam("userId") String userId){
		
		Users user = new Users();
		
		try{
			System.out.println("param userId =["+userId+"]");
			
			user.setUserId(userId);
			user.setUserName("min");
		}catch(Exception e){
			e.printStackTrace();
		}
			
		return user;
	}

	/**************************************************************************
	 * 사용자를 입력한다
	 * @param userId,userName,userAge,userSex,userYear
	 *************************************************************************/
	@POST
	@Path("/{userId}/{userName}/{userAge}/{userSex}/{userYear}")
	public void insertUser( @PathParam("userId") String userId,
							@PathParam("userName") String userName,
							@FormParam("userAge") Integer userAge,
							@FormParam("userSex") String userSex,
							@FormParam("userYear") String userYear){
		try{
			System.out.println("post date - userId = ["+ userId + "]");
			System.out.println("post date - userName = ["+ userName + "]");
			System.out.println("post date - userAge = ["+ userAge + "]");
			System.out.println("post date - userSex = ["+ userSex + "]");
			System.out.println("post date - userYear = ["+ userYear + "]");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
