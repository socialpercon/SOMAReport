package com.github.devholic.SOMAReport.Controller;


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.devholic.SOMAReport.Model.Users;


@Path("/users")
public class UsersController {

	
	
	public boolean login(String email, String password){
		System.out.println("email :"+ email +"/ password:"+password);
		
		boolean result = false;
		
		Users us = new Users(email);
		
		if(us.getUserName() != null){
			result= true;
		}else{
			result = false;
		}
		
		return result;
		
	}
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
	public Response insertUser( @PathParam("userId") String userId,
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
			return Response.status(200).type(MediaType.APPLICATION_JSON).entity("post : 200").build();
		}catch(Exception e){
			e.printStackTrace();
		}
		return Response.status(500).type(MediaType.APPLICATION_JSON).entity("post : 500").build();
	}
	
	@PUT
	public Response updateUser(){
		try{
			return Response.status(200).type(MediaType.APPLICATION_JSON).entity("put : 200").build();
		}catch(Exception e){
			e.printStackTrace();
		}
		return Response.status(500).type(MediaType.APPLICATION_JSON).entity("put : 500").build();
	}
	
	@DELETE
	public Response deleteUser(){
		try{
			return Response.status(200).type(MediaType.APPLICATION_JSON).entity("delete : 200").build();
		}catch(Exception e){
			e.printStackTrace();
		}
		return Response.status(500).type(MediaType.APPLICATION_JSON).entity("delete : 500").build();
	}
	
}
