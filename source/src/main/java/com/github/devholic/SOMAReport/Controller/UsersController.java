package com.github.devholic.SOMAReport.Controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.github.devholic.SOMAReport.Database.DocumentUtil;
import com.github.devholic.SOMAReport.Database.ReferenceUtil;
import com.github.devholic.SOMAReport.Model.Users;
import com.google.gson.JsonObject;

@Path("/users")
public class UsersController {
	
	private final Logger logger = Logger.getLogger(UsersController .class);
	
	ReferenceUtil reference_util = new ReferenceUtil("");
	DocumentUtil doc_util = new DocumentUtil("");

	/**
	 * 로그인
	 * 
	 * @param email
	 * @param password
	 * @return
	 */
	public static String login(String email, String password) {
		Logger static_logger = Logger.getLogger(UsersController .class);
		DocumentUtil doc_util = new DocumentUtil("");
		
		static_logger.debug("email :" + email + "/ password:" + password);
		
//		boolean result = false;
//		Users us = new Users(email);
//		if (us.getUserId() != null) {
//			result = true;
//		} else {
//			result = false;
//		}
//		return result;
		return doc_util.userAuthentication(email, password);
	}

	/**************************************************************************
	 * 멘티 전체 리스트를 가져온다
	 * 
	 * @return List<Users>
	 *************************************************************************/
	@GET
	@Path("/mentee")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public List<JsonObject> getMenteeList() {

		List<JsonObject> result = new ArrayList<JsonObject>();

		try {
			result = reference_util.getAllMentee();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**************************************************************************
	 * 멘토 전체 리스트를 가져온다
	 * 
	 * @return List<Users>
	 *************************************************************************/
	@GET
	@Path("/mento")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public List<JsonObject> getMentoList() {

		List<JsonObject> result = new ArrayList<JsonObject>();

		try {
			result = reference_util.getAllMentor();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**************************************************************************
	 * 사용자 상세정보를 가져온다
	 * 
	 * @param userId
	 * @return Users
	 *************************************************************************/
	@GET
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Users getUserDetails(@PathParam("userId") String userId) {

		Users user = new Users();

		try {
			logger.debug("param userId =[" + userId + "]");

			user.setUserId(userId);
			user.setUserName("min");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return user;
	}

	/*************************************************************************
	 * 사용자를 입력한다
	 * 
	 * @param document
	 * @return
	 ************************************************************************/
	public boolean insertUser(JsonObject document) {
		boolean result = false;
		try {
			String id = doc_util.putDoc(document);
			logger.debug("inserted report id = " + id);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@PUT
	public Response updateUser() {
		try {
			return Response.status(200).type(MediaType.APPLICATION_JSON)
					.entity("put : 200").build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(500).type(MediaType.APPLICATION_JSON)
				.entity("put : 500").build();
	}

	/************************************************************************
	 * 사용자 아이디로 사용자를 삭제한다.
	 * 
	 * @param reportId
	 * @return
	 ***********************************************************************/
	@DELETE
	public boolean deleteUser(String reportId) {
		boolean result = false;

		try {
			logger.debug("delete | user id = " + reportId + "\n");
			doc_util.deleteDoc(reportId);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
