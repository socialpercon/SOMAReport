package com.github.devholic.SOMAReport.Controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.hk2.utilities.reflection.Logger;

import com.github.devholic.SOMAReport.Model.Reports;


@Path("/reports")
public class ReportsController {

	Logger logger = Logger.getLogger();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	@Encoded
	public Reports getUserList(){
		
		Reports report = new Reports();
		
		try{
			logger.debug("GET reports");
		
			
			//참석한 멘티들
			String [] attended_mentee = {"min","kang","lee"};
			
			String inputStr = "11-07-2015";
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date inputDate = dateFormat.parse(inputStr);
			
			report.setAttended_mentee(attended_mentee);
			report.setCount(1);
			report.setDate(inputDate);
			report.setEnd_date(inputDate);
			report.setEtc("ETCCC");
			report.setExcept_end_date(inputDate);
			report.setExcept_start_date(inputDate);
			report.setForwarding("추진사항");
			report.setLocation("위치");
			report.setMento_opinion("우수하군요");
			report.setPlan("아무 계획이 없다");

		}catch(Exception e){
			e.printStackTrace();
		}
			
		return report;
	}
}
