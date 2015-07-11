# REST API
Representational State Transfer (REST)

표현 가능한 상태 전송

**네트워크 아키텍처 원리**의 모음

* 자원을 정의하고 자원에 대한 주소를 지정하는 방법

##### REST의 요소
Resource, Method, Representation of Resource (Resource의 상태, Message)

##### REST의 목표
* 구성 요소 상호작용의 규모 확장성
* 인터페이스의 범용성
* 구성 요소의 독립적인 배포
* 중간적 구성요소를 이용해 응답 지연 감소, 보안을 강화, 레거시 시스템을 인캡슐레이션

##### REST의 제한조건
다음 6가지 조건을 준수하는 한 개별 컴포넌트는 자유롭게 구현할 수 있다.

* 클라이언트 / 서버 구조
* 무상태 (Stateless)
	* 각 요청간 클라이언트의 Context가 서버에 저장되면 안됨
	* 잘 관리되는 캐싱은 클라이언트-서버 간 상호작용을 부분적으로 또는 완전하게 제거하여 scalability와 성능을 향상시킨다.
	* 세션과 쿠키를 사용해서는 안된다.
* Cacheable
	* 클라이언트는 응답을 캐싱할 수 있어야 함
* 계층화 
	* 클라이언트는 보통 대상 서버에 직접 연결되었는지, 또는 중간 서버를 통해 연결되었는지를 알 수 없다.
* Code on demand (조건부)
	* 자바 애플릿이나 자바스크립트의 제공을 통해 서버가 클라이언트가 실행시킬 수 있는 로직을 전송하여 기능을 확장시킬 수 있다.
* 인터페이스 일관성
	* 아키텍처를 단순화시키고 작은 단위로 분리함으로써 클라이언트-서버의 각 파트가 독립적으로 개선될 수 있도록 해준다.

##### REST 인터페이스의 원칙에 대한 가이드
* 자원의 식별
* 메시지를 통한 리소스의 조작
* 자기서술적 메시지
* 애플리케이션의 상태에 대한 엔진으로서 하이퍼미디어

##### 중심 규칙
* URI는 **정보의 자원**을 표현
* 자원에 대한 행위는 HTTP Method(GET / POST / PUT / DELETE)로 표현

##### 문제점
* 구형 브라우저가 지원 안할수도 있음. (PUT, DELETE)

##### 보안
* 인증 (Authentication)
	* 누가(단말, 다른 서버, 사용자) 서비스를 사용하는지를 확인 
	* 방법
		* API Key
			* API를 호출할 때 API Key를 메시지 안에 넣어 호출
			* 클라이언트들이 같은 API Key를 공유하기 때문에 API Key가 한번이라도 유출되면 전체가 뚫려버림
		* API Token
			* ID / 비밀번호 같은걸로 사용자를 인증한 다음
			* 사용 가능한 기간이 있는 Token을 발급해서, 해당 Token으로 사용자를 인증
				* 비밀번호 같은 경우 주기적으로 바뀔 수 있고, 매번 계정정보를 네트워크로 공유하는건 해킹당할 위험성이 있음.
			* Token이 탈취당하면 API를 호출할수는 있지만, 계정정보는 탈취당하지 않는다.
		![API Token auth flow](http://cfile9.uf.tistory.com/image/27740E40542422CF1900E3)
		* HTTP Basic Auth
			* ID / 비밀번호를 헤더에 Base64형태로 넣어서 인증 요청
			* 중간에 패킷 가로채서 헤더 디코딩하면 ID / 비밀번호가 나오므로 **HTTPS** 사용해야됨
		* 3rd-party Auth
			* Facebook, Twitter같은 API를 활용할 때 사용
				* 예를들면 페이스북 로그인
					* 페이스북 사용자임을 인증해주지만, 사용자가 사용할 서비스는 비밀번호를 받지 않고, 페이스북에서 인증한 뒤 해당 서비스에게 알려줌
			* 대표적으로 OAuth 2.0
		* Bi-diretional Certification
			* 클라이언트와 서버에 모두 인증서를 놓고 양방향 SSL을 제공
			* API 호출에 대한 인증을 클라이언트의 인증서를 이용
* 인가 (Authorization)
	* 사용자가 그 리소스를 사용할 권한이 있는지 체크하는 과정
		* 예를들면 관리자 페이지
	* 방법
		* Role Based Access Control
			* 정해진 Role에 권한을 연결
			* 이 Role에 해당하는 사용자에게 해당 권한을 부여
			![Role Based Access Control](http://cfile1.uf.tistory.com/image/255F0940542422D03119EC)
		* Access Control List
			* 사용자(또는 그룹과 같은 권한의 부여 대상) 에게 직접 권한을 부여
			![Access Control List](http://cfile1.uf.tistory.com/image/26073B40542422CD08D6FA)
	* 처리 위치
		* **API Server**
			* 각 비지니스 로직에서 API 메세지를 각각 파싱하기 때문에, API 별로 권한 인가 로직을 구현하기가 용이함
			* 권한 인가에 필요한 필드들을 api gateway에서 변환해서 API 서버로 전달해줌으로써 구현을 간략하게 할 수 있음
		* Gateway
			* API 호출이 들어오면, API Token을 사용자 정보와 권한 정보로 변환 한 후, 접근하고자 하는 API에 대해서 권한 인가
			* 공통 필드등으로 API 권한 처리를 하지 않는 경우에는 HTTP 메세지 전체를 일일이 파싱해야 하는 오버로드가 발생하기 때문에,  사용하기 어려움
		* Client
			* Client가 신뢰 가능한 경우에만 사용 가능
* 네트워크 레벨 암호화
	* HTTPS
		* 메시지 자체를 암호화 해서 전송하기 때문에 해킹으로 인한 메시지 노출 위협 최소화 가능
* 메시지 무결성 보장
* 메시지 본문 암호화
	* 암호화가 필요한 특정 필드만 애플리케이션단에서 암호화하여 보내기.
	* 클라이언트와 서버가 암호화 키를 가져야 함
		* 대칭키
		* 비대칭키 

## RESTful
REST의 기본 원칙을 지킨 서비스를 RESTful 하다고 표현

##### Mobile 환경에서의 REST
Request Header의 User-Agent를 참조하면 된다.

##### API가 RESTful한지 체크하는 방법
1. API Endpoint가 한개인가?
	* REST의 요소에는 Resource가 있음.
		* Resource는 말 그대로 서비스를 제공하는 시스템의 자원.
	* Expensify 시스템의 경우를 예로 들면
		* /user (사용자)
		* /team (프로젝트 그룹 / 팀)
		* /receipt (영수증)
	* 대부분 명사(noun) 형태의 특성을 가짐
2. 모든 요청을 POST방식으로만 요청하는가?
	* Resource를 핸들링 하기 위해 HTTP Method를 사용.
		* 생성은 **POST**
		* 수정은 **PUT**
		* 조회는 **GET**
		* 삭제는 **DELETE**
3. 응답에 대한 메타데이터를 Body에 포함 하는가?
	* Request/Response 메타데이터는 최대한 HTTP 프로토콜 방식을 준수
		* 예를들면 요청 처리 결과값을 Body에 포함하는것이 아니라 HTTP Status값으로 표현
	* Body 데이터는 최대한 순수한 Resource 데이터
4. URL에 동사가 포함되어있는가?
	* 행위적 표현이기 때문에 RPC(메서드)를 의미하는건지 Resource를 의미하는건지 구분이 모호해짐
		* Expensify 시스템의 경우를 예로 들면
			* /receipt/1/approved (X)
			* /receipt/1/status (O)
5. URL에 RPC 호출 메서드명이 있는가?
	* 아래와 같이 URL에 메서드가 포함된 경우나
		* ?action=createReceipt
	* URL에 노출되지 않지만, Body안에 선언되는 경우가 있는데,
	* RESTful 설계 컨셉은 RPC가 아닌 Resource의 bucket화
	
## 참고한 링크
* [RestFul이란 무엇인가?](http://blog.remotty.com/blog/2014/01/28/lets-study-rest/#crud)
* [REST 아키텍처를 훌륭하게 적용하기 위한 몇 가지 디자인 팁](http://spoqa.github.io/2012/02/27/rest-introduction.html)
* [당신의 API가 Restful 하지 않은 5가지 증거](https://beyondj2ee.wordpress.com/2013/03/21/%EB%8B%B9%EC%8B%A0%EC%9D%98-api%EA%B0%80-restful-%ED%95%98%EC%A7%80-%EC%95%8A%EC%9D%80-5%EA%B0%80%EC%A7%80-%EC%A6%9D%EA%B1%B0/)
* [REST API 설계와 구현](http://seminar.eventservice.co.kr/JCO_1/images/track4-1.pdf)
* [REST API의 이해와 설계-#3 API 보안](http://bcho.tistory.com/955)
