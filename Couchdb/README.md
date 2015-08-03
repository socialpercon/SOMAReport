#SOMAReport Couchdb api     

- [cloudant-couchdb link](https://somareport.cloudant.com/dashboard.html)  password: somasoma      
- Document Model
- Methods
- Design Documents

##Document Model

###Mentor

	{
	  "_id": "3f4ce9856efb3e2f5d86eeb4d5abb8c6",
	  "_rev": "2-49087f5d965aa7eeee16d1449e888e60",
	  "type": "mentor",
	  "name": "김갹갹",
	  "years": [2010, 2011, 2012, 2013, 2014, 2015],
	  "section": "web",
	  "belong": "SOMA",
	  "phone_num": "010-0000-0000",
	  "account": "gyakk0@gmail.com"
	}
	
- "year"는 멘토가 새로운 기수에 참가할 때마다 연도를 추가

###Mentee

	{
 	  "_id": "4c44d639b77c290955371694d3310194",
 	  "_rev": "2-aca67293cc2d0d1dbf292f1bbe65eb2d",
 	  "type": "mentee",
 	  "name": "이뿅뿅",
 	  "year": 2015,
	  "belong": "Somewhere univ.",
	  "phone_num": "010-0000-0000",
	  "account": "ppyong0@gmail.com"
	}
	

###Project

	{
	  "_id": "4c44d639b77c290955371694d33e4fe9",
	  "_rev": "2-5d9d38063f6d95617ae6ba71aa81927d",
	  "type": "project",
	  "project_type": "regular",
	  "stage": [
	    6,
	    1,
	    1
	  ],
	  "field": "web",
	  "title": "SOMAProject1",
	  "mentor": "3f4ce9856efb3e2f5d86eeb4d5abb8c6",
	  "mentee": [
	    "4c44d639b77c290955371694d3310194",
	    "3f4ce9856efb3e2f5d86eeb4d5b99c53",
	    "3f4ce9856efb3e2f5d86eeb4d5bad432"
	  ]
	}

- 멘토, 멘티의 경우 이름으로 입력받지만 실제로는 id로 저장된다

###Report

	 {
	  "_id": "5e9f05f667364abba459f06cdabc3b05",
	  "_rev": "1-2bf7013eff5de91d47cf3951624e3533",
	  "type": "report",
	  "project": "4c44d639b77c290955371694d33e4fe9",
	  "report_info": {
	    "mentoring_num": 1,
	    "date": "2015-07-20",
	    "place": "aram7-8",
	    "start_time": [
	      2015,
	      7,
	      20,
	      18,
	      0
	    ],
	    "end_time": [
	      2015,
	      7,
	      20,
	      23,
	      0
	    ],
	    "whole_time": 4,
	    "except_time": 0,
	    "total_time": 4
	  },
	  "attendance": [
	    {
	      "id": "4c44d639b77c290955371694d3310194",
	      "attend": false,
	      "absense_reason": "family"
	    },
	    {
	      "id": "3f4ce9856efb3e2f5d86eeb4d5b99c53",
	      "attend": true
	    },
	    {
	      "id": "3f4ce9856efb3e2f5d86eeb4d5bad432",
	      "attend": true
	    }
	  ],
	  "report_details": {
	    "topic": "주제주제주제주제주제",
	    "goal": "향후계획향후계획향후계획향후계획향후계획향후계획향후계획향후계획",
	    "issue": "추진내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용",
	    "opinion": "의견의견의견의견의견의견의견의견의견의견의견의견의견"
	  },
	  "report_attachments": {
	    "photo": "linklinklink",
	    "reference": "refrefref"
	  }
	}


##Methods  
  
###DocumentUtil

문서에 기본적인 CRUD 등을 수행하는 클래스  
데이터베이스 이름을 받아 생성. 해당 이름의 데이터베이스를 불러오며 없으면 생성한다.

	DocumentUtil docutil = new DocumentUtil("dbName");

근데 그냥 user, project, report 클래스에 아예 종속시켜서 만드는 게 더 나을 것 같기도..


#####DocumentUtil.getUserDoc (String account) : JsonObject

로그인한 사용자의 account정보를 통해 사용자 문서를 JsonObject로 받아온다.


#####DocumentUtil.getUserId (String name) : String

사용자의 이름을 통해 _id값을 받아온다.

#####DocumentUtil.getDoc (String id) : JsonObject

id에 해당하는 문서를 조회한다.

#####DocumentUtil.putDoc (JsonObject document) : String

json문서를 데이터베이스에 넣는다.  
생성된 _id를 리턴한다. -> 이것도 그냥 Response를 통째로 리턴할까?

#####DocumentUtil.deleteDoc (String id) : Response

id에 해당하는 문서를 삭제한다.  
response를 리턴. 

#####DocumentUtil.updateDoc (JsonObject document) : JsonObject

수정된 내용의 문서를 입력받아 해당 문서를 update한다. 이때 document는 데이터베이스에 존재하던 문서를 불러온 것으로, 반드시 id, rev를 포함하고 있어야 한다.  

 - 앞으로 생성해야 할 update 기능의 세부사항에 따라 수정 및 확장 필요할듯

###ReferenceUtil

조건에 따라 view를 통해 해당 정보를 조회하는 클래스  
생성 방식은 위와 동일하다.

	ReferenceUtil refutil = new ReferenceUtil("dbName");

현재는 대부분의 method가 검색 대상의 문서를 통쨰로 가져오는데, 각각 필요와 쓰임에 따라 선택적인 정보만 가져오도록 수정 필요

#####ReferenceUtil.getMyProjects (String user_id) : List< JsonObject >

사용자가 속한 프로젝트를 조회하여 List< JsonObject >로 받아온다

 - 멘토, 멘티용

#####ReferenceUtil.getReports (String project_id) : List< JsonObject >

프로젝트에 속한 모든 레포트를 조회하여 List< JsonObject >로 받아온다

#####ReferenceUtil.calTotalMentoring (String project_id) : int

프로젝트에 속한 모든 레포트의 시간을 계산하여 총 멘토링 시간을 리턴  

 - 멘토링 시간을 계산하는 view를 사용  
 - project class에서 사용할듯? 아예 거기서 계산하는 게 나을지도??!

#####ReferenceUtil.getAllMentor () : List< JsonObject >

모든 멘토의 문서를 List< JsonObject > 형태로 받아온다

 - 관리자용

#####ReferenceUtil.getAllMentee () : List< JsonObject >

모든 멘토의 문서를 List< JsonObject > 형태로 받아온다

 - 관리자용

#####ReferenceUtil.getCurrentProjects (int[] stage) : List< JsonObject >

입력된 기수, 단계, 차수에 진행되고 있는 모든 프로젝트를 조회

	int[] currentStage = [6, 1, 1];			//6기 1단계 1차
	refutil.getCurrentProjects(currentStage);

 - 관리자용 
  
  

##Design Documents

Cloudant Dashboard에서 [보기](https://somareport.cloudant.com/dashboard.html#/database/somarecord/_all_docs )

#####_design/get_doc/user_by_account

key | value
--- | ---
account | user._id


#####_design/get_doc/user_by_name

key | value
--- | ---
user.name | user._id


#####_design/user_view/all_my_project

key | value
--- | ---
[ user._id, project.stage ] <br> id: project.mentor, project.mentee[i] | project._id

이때 key는 프로젝트 문서에서 mentor와 mentee[]에 소속된 user의 모든 아이디와 stage([기수, 단계, 차수]).  
user._id는 검색 키 / stage는 정렬 키


#####_design/project_view/all_report

key | value
--- | ---
[ project._id, report.report_info.date ] <br> id: report.project | report._id

project._id는 검색 키 / date는 정렬 키


#####_design/project_view/calculate_total_time

key | value
--- | ---
report.project <br>(project._id)| report.reportinfo.totalTime

*reduce: _sum

#####_design/admin_view/all_docs

key | value
--- | ---
doc.type | doc._id

type에 따라 전체 문서 조회 (멘토 전체, 멘티 전체 등)

#####_design/admin_view/current_project

key | value
--- | ---
project.stage | project._id

기수/단계/차수에 따라 프로젝트 조회

