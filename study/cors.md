# CORS
Cross-Origin Resource Sharing

* **다른 서버에서**제공하는 자원에 접근
* 물리적인 서버 뿐만 아니라, 서브도메인이 다르거나 포트가 다른것도 다른 Origin으로 간주된다.
	* Origin은 URL이 아니라 Scheme, Host, Port로 구선된 URI 
		* Scheme : 프로토콜 (http / ftp 등등) 
* Mashup을 위해 꼭 필요함
* Same-origin policy를 우회할 수 있는 여러 방법 중 대표적인 방법
	* 브라우저의 Same-origin policy 를 **합법적으로 피해서** 다른 origin 에서 제공하는 자원에 접근하고 싶을 때 사용 

##### Same-origin policy 
* Same domain policy / Same 
	* Scheme과 도메인 네임만 같으면 스크립트가 접근 가능
* Same-origin policy
	* **서브도메인** 혹은 **포트**까지 같아야됨 	

##### JSONP
* Same-origin policy를 우회하기 위해 보통 JSONP를 사용함
	* 자바스크립트를 사용해서 직접 요청 (X) ->  script 태그를 DOM에 삽입하여 브라우저가 script를 load하는 방식
* **자바스크립트로 요청하는 것이 아니므로**, Same-origin policy 를 우회가능
* 단점
	* GET Method만 사용 가능
	* 웹 보안 위협
		* CSRF (Cross-Site Request Forgery)
			* 위조된 요청이 믿을 수 있는 사용자로부터 발송된 것으로 판단하게 되어 공격에 노출됨.
		* XSS (Cross-Site Scripting) 
			* 사용자로부터 입력 받은 값을 제대로 검사하지 않고 사용할 경우 나타남.
			* 주로 다른 웹사이트와 정보를 교환하는 식으로 작동함
		* XSS가 사용자가 특정 웹사이트를 신용하는 점을 노린거라면, CSRF는 웹사이트가 사용자의 웹 브라우저를 신용하는 상태를 노림.

##### 작동 방식

![flow](http://cfile21.uf.tistory.com/image/2311DE4D550E9F1D1E9EA3)

##### Preflight Request (사전 요청)
* 외부 도메인인 경우 웹브라우저는 먼저 Preflight Request를 하게된다.
* Preflight Request는 실제로 요청하려는 경로와 같은 URL에 OPTIONS 메서드로 요청을 날려서 요청을 할 수 있는 권한이 있는지 확인