package com.github.devholic.SOMAReport.Controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.devholic.SOMAReport.Model.Reports;


@Path("/reports")
public class ReportsController {

	
	/**************************************************************************
	 * 레포트 리스트를 가져온다.
	 * @return List<Reports>
	 *************************************************************************/
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public List<Reports> getUserList(){
		
		List<Reports> report_list = new ArrayList<Reports>();
		
		Reports report1 = new Reports();
		Reports report2 = new Reports();
		Reports report3 = new Reports();
		
		try{

			report1.setReportId("repo1");
			report2.setReportId("repo2");
			report3.setReportId("repo3");

			report_list.add(report1);
			report_list.add(report2);
			report_list.add(report3);
			
		}catch(Exception e){
			e.printStackTrace();
		}
			
		return report_list;
	}
	
	/**************************************************************************
	 * 레포트 상세정보를 가져온다.
	 * @param reportId
	 * @return Reports
	 *************************************************************************/
	@GET
	@Path("/{reportId}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public Reports getUserDetail(@PathParam("reportId") String reportId){
		
		Reports report = new Reports();
		
		try{

			//참석한 멘티들
			String [] attended_mentee = {"min","kang","lee"};
			String inputStr = "11-07-2015";
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date inputDate = dateFormat.parse(inputStr);
			
			report.setReportId(reportId);
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
