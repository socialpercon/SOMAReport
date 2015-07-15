# JAVA Framework

## Play
* 점점 Scala쪽으로 넘어가고 있어 JAVA Framework라고 보기 조금 애매함.
	* 1.X대 버전에서는
	
		![Play1](https://github.com/devholic/SOMAExpensify/blob/master/study/resources/javaframework/play1.png)
	* 2.X대 버전에서는
		
		![Play2](https://github.com/devholic/SOMAExpensify/blob/master/study/resources/javaframework/play2.png)
* Build System : **SBT**
* 그래서 Java기반으로 만들어질 SOMA Expensify에는 적합하지 않다고 생각됨.

## Spring
* 자바 객체를 직접 관리
	* 객체 생성, 소멸같은 라이프사이클을 관리해서 스프링으로부터 필요한 객체 얻어올 수 있음.
* POJO(Plain Old Java Object) 방식의 Framework
	* 기존에 존재하는 라이브러리등을 지원하기에 용이하고 객체가 가벼움
* 제어권이 사용자가 아닌 프레임워크에 있어 필요에 따라 스프링에서 사용자 코드 호출
* 각각의 계층이나 서비스들간에 의존성이 존재할 경우 프레임워크가 서로 연결
* 관점 지향 프로그래밍(AOP)을 지원함
	* 트랜잭션이나 로깅, 보안과 같이 여러 모듈에서 공통적으로 사용하는 기능의 경우 분리해서 관리 가능 
* MVC 패턴 덕분에 View까지 한번에 처리 가능하다.

## Jersey
* JAX-RS 준수
	* RESTful 웹서비스 개발에 최적화
	* JSON / XML / HTML로 결과 전송 가능 
* 제공하지 않던 View 관련된 부분을 Jersey JAX-RS MVC로 제공

## Benchmark
출처 : [TechEmpower - Web Framework Benchmarks](https://www.techempower.com/benchmarks/)

* JSON serialization (높을수록 좋음)
	1. **Jersey** : 165,806
	2. **Play 1 (Siena)** : 151,780
	3. **Spring** : 97,354 
* Single query
	1. **Spring** : 55,291
	2. **Jersey** : 53,642
	3. **Play 1 (Siena)** : 19,044
* Multiple queries
	1. **Jersey** : 6,555
	2. **Spring** : 3,916
	3. **Play 1 (Siena)** : 2,922
* Fortunes
	1. **Jersey** : 45,007
	2. **Spring** : 21,807
	3. **Play 1 (Siena)** : N/A
* 종합
	* 성능은 Jersey > Spring > Play

## 결론
* REST API뿐만 아니라 View도 개발을 해야하는데, Jersey와 Spring 모두 MVC 패턴을 지원한다.
* 둘중 성능이 더 나은 Jersey를 메인 프레임워크로 정하면 좋을것으로 예상된다.