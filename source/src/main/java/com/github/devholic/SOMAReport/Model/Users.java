package com.github.devholic.SOMAReport.Model;

import com.github.devholic.SOMAReport.Datbase.DocumentUtil;
import com.github.devholic.SOMAReport.Datbase.ReferenceUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Users {

	public static Integer USER_ROLE_MENTOR = 0;
	public static Integer USER_ROLE_MENTEE = 1;
	
	private Integer role;
	private String sort;
	
	private String userId;
	private String userName;
	private String userSection;
	private String userBelong;
	private String userPhone;
	private Integer[] userYear;
	
	public Users () // >꼭 필요한가?
	{
		role = -1;
		userId = "";
		
		userName = "";
		userSection = "";
		userBelong = "";
		userPhone = "";
		userYear = null;
	}
	
	public Users (String account) 
	{
		DocumentUtil docUtil = new DocumentUtil("somarecord");
		JsonObject userDoc = docUtil.getUserDoc(account);
		
		userId = userDoc.get("_id").getAsString();
		
		if (userDoc.get("type").getAsString().equalsIgnoreCase("mentor")) {
			role = Users.USER_ROLE_MENTOR;
			userSection = userDoc.get("section").getAsString(); 
			JsonArray years = userDoc.get("years").getAsJsonArray();
			userYear = new Integer[years.size()];
			for (int i=0; i<userYear.length; i++) 
				userYear[i] = years.get(i).getAsInt();
		}
		else if (userDoc.get("type").getAsString().equalsIgnoreCase("mentee")) {
			role = Users.USER_ROLE_MENTOR;
			userYear = new Integer[1];
			userYear[0] = userDoc.get("year").getAsInt();
		}
			else {
			// user에 해당하지 않는 문서이므로 exception 처리
		}
		
		userName = userDoc.get("name").getAsString();
		userBelong = userDoc.get("belong").getAsString();
		userPhone = userDoc.get("phone_num").getAsString();	
	}
	
	
	public String getUserId() 
	{
		return userId;
	}

	public String getUserName() 
	{
		return userName;
	}

	public int getUserRole() 
	{
		return role;
	}
	
	public String getUserSection() 
	{ 
		// 멘티는 null 반환
		return userSection;
	}
	
	public String getUserPhone() 
	{
		return userPhone;
	}
	
	public String getUserBelong() 
	{
		return userBelong;
	}
	
	public boolean isBelongToYear(int year) 
	{
		// 사용자가 year년도의 기수에 소속되어있는지 확인
		for (int i=0; i<userYear.length; i++)
			if (userYear[i] == year)	return true;
		
		return false;
	}
	
	public JsonArray getMyProjects () 
	{
		// 사용자가 소속된 프로젝트의 리스트를 리턴
		// 포함된 정보: {id, stage, title, mentor, mentee}
		ReferenceUtil refutil = new ReferenceUtil("somarecord");
		return refutil.getMyProjects(userId);
		
	}
}
