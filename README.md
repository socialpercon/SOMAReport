# SOMAReport

[trello](https://trello.com/b/0mX9BKo1) | [slack](https://somareport.slack.com) | [지난 과제 확인](https://github.com/devholic/SOMAReport/tree/master/mentoring)

## Release
* 7/31(금) 1차버전
* 8/17(월) 2차버전 (기능완성되어야함)

## 발표
* usecase부터 시작해서
* 1인당 발표 한시간 (Q&A 포함)
* (7/20) 월요일
	* CouchDB / 이재연
		* 데이터 모델링
		* 코드화 시키기
		* _changes (변경 알림)
	* DB Changelog backup(node.js) + REST Demo / 강성훈
		* 뭘로 만드는지가 핵심 
			* node.js로 짤 것
				* Socket이 끊기는 경우 가장 최근 데이터를 저장해서 체크하면 되는부분
		* Couch DB
			* Insert / Update / Delete
				* _changes 호출하면 log를 file로
			* git으로 백업했다가 추후 clone 해서 복원
		* 백업이라는 개념을 더 쉽게
		* **코드까지 나와야함**
		* + Zookeeper (시간 나면)
			* Node가 떠있는지 죽었는지를 체크하게
			* HA 환경 (자동화)
		* 선정된 기술로 REST 데모를 만들기
	* Google Drive API / 민종현
		* curl로 시도해보기
		* Google API Test 완료 
		* OAuth 2.0
			* Spec 읽어보기
		* Elastic Search
	* 공통과제
		* **Maven**
			* [자바 세상의 빌드를 이끄는 메이븐 / 박재성](http://book.naver.com/bookdb/book_detail.nhn?bid=6600936)
		* 코드리뷰
		* UI 구성 및 프로젝트 기능 요건 추출
			* 기본 개발 Spec을 적기