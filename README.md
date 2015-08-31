# SOMAReport

* [trello](https://trello.com/b/0mX9BKo1)
* [slack](https://somareport.slack.com)
* [Test List](https://docs.google.com/spreadsheets/d/1AhNqZxIMev22Ik77Leh_Sm8eLk3JGKORaqlocAzi0K0)
* [Server Source](https://github.com/devholic/SOMAReport/tree/master/source)
* [Android Source](https://github.com/devholic/SOMAReport/tree/master/android-source)
* [Couchdb Loader](https://github.com/devholic/couchdb-loader)


# 설치방법
**1. CouchDB - Backup Demon**

CouchDB의 변경사항을 backup 하기 위해 존재하는 것으로, CouchDB에서 _changes가 발생하는 히스토리를 GitLab에 저장하는 역할을 한다.	
		
**2. ElasticSearch**

CouchDB를 기본 DB로 사용하지만, 검색을 위해서 ElasticSearch를 이용한다. CouchDB와의 데이터싱크를 맞추기 위해, logstash를 사용한다. Server에는 logstash와 elastic search를 모두 띄워놓은 상태여야된다. SomaReport Server config.xml에서 elastic의 호스트와 포트 값을 변경시켜준다.

Elasticsearch를 띄우는 방법은 우선 홈페이지에 접속하여 다운받은 다음, 압축을 풀고 /bin/ 파일 아래에 elasticsearch를 실행시키면 자동으로 실행이된다.

참고자료

1. https://www.elastic.co/
2. https://www.elastic.co/products/logstash

**3. SomaReport Server**

SomaReport를 실행시키기 위해서는 우선 개발된 프로젝트를 jar 파일로 묶어야 한다. eclipse에서 오른쪽 버튼 클릭 후, export를 하여 jar 파일로 묶는다. 그리고 서버로 옮긴 이후에, maven clean 과 install을 한 후에, jar 파일을 실행시킨다.