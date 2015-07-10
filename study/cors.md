# CORS
Cross-Origin Resource Sharing

* **다른 서버에서**제공하는 자원에 접근
* 물리적인 서버 뿐만 아니라, 서브도메인이 다르거나 포트가 다른것도 다른 Origin으로 간주된다.
	* Origin은 URL이 아니라 Scheme, Host, Port로 구선된 URI 
		* Scheme : 프로토콜 (http / ftp 등등) 
* Mashup을 위해 꼭 필요함
* Same-origin policy를 우회할 수 있는 여러 방법 중 대표적인 방법
	* 브라우저의 Same-origin policy 를 **합법적으로 피해서** 다른 origin 에서 제공하는 자원에 접근하고 싶을 때 사용 

## Same-origin policy 
* Same domain policy / Same 
	* Scheme과 도메인 네임만 같으면 스크립트가 접근 가능
* Same-origin policy
	* **서브도메인** 혹은 **포트**까지 같아야됨 	