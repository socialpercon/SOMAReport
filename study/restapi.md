# REST API
Representational State Transfer (REST)

표현 가능한 상태 전송

**네트워크 아키텍처 원리**의 모음

* 자원을 정의하고 자원에 대한 주소를 지정하는 방법

## REST의 요소
Resource, Method, Representation of Resource (Resource의 상태, Message)

## REST의 목표
* 구성 요소 상호작용의 규모 확장성
* 인터페이스의 범용성
* 구성 요소의 독립적인 배포
* 중간적 구성요소를 이용해 응답 지연 감소, 보안을 강화, 레거시 시스템을 인캡슐레이션

## REST의 제한조건
다음 6가지 조건을 준수하는 한 개별 컴포넌트는 자유롭게 구현할 수 있다.

* 클라이언트 / 서버 구조
* 무상태 
	* 각 요청간 클라이언트의 Context가 서버에 저장되면 안됨
	* 잘 관리되는 캐싱은 클라이언트-서버 간 상호작용을 부분적으로 또는 완전하게 제거하여 scalability와 성능을 향상시킨다.
* Cacheable
	* 클라이언트는 응답을 캐싱할 수 있어야 함
* 계층화 
	* 클라이언트는 보통 대상 서버에 직접 연결되었는지, 또는 중간 서버를 통해 연결되었는지를 알 수 없다.
* Code on demand
	* 자바 애플릿이나 자바스크립트의 제공을 통해 서버가 클라이언트가 실행시킬 수 있는 로직을 전송하여 기능을 확장시킬 수 있다.
* 인터페이스 일관성
	* 아키텍처를 단순화시키고 작은 단위로 분리함으로써 클라이언트-서버의 각 파트가 독립적으로 개선될 수 있도록 해준다.

## REST 인터페이스의 원칙에 대한 가이드
* 자원의 식별
* 메시지를 통한 리소스의 조작
* 자기서술적 메시지
* 애플리케이션의 상태에 대한 엔진으로서 하이퍼미디어

## 중심 규칙
* URI는 **정보의 자원**을 표현
* 자원에 대한 행위는 HTTP Method(GET / POST / PUT / DELETE)로 표현

## RESTful
REST의 기본 원칙을 지킨 서비스를 RESTful 하다고 표현

## API가 RESTful한지 체크하는 방법
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