# node.js

* [다운로드](https://nodejs.org/download/)
	* 해당되는 OS를 선택 후 다운로드를 하면 된다.

##### HelloWorld 출력하기

[helloworld.js](https://github.com/devholic/SOMAExpensify/blob/master/study/resources/node/helloworld.js)

    console.log('Hello World');
    
파일을 다운로드, 혹은 직접 생성한 후 콘솔에서,

    node helloworld.js
    
를 실행시키면, 다음과 같이 콘솔에 Hello World가 출력되는걸 확인할 수 있다.

![helloworld](https://github.com/devholic/SOMAExpensify/blob/master/study/resources/node/helloworld.png)

##### 간단한 웹서버 생성하기

[helloworld_server.js](https://github.com/devholic/SOMAExpensify/blob/master/study/resources/node/helloworld_server.js)

    var http = require('http');

    var server = http.createServer(function (req, res) {
	    res.writeHead(200, { 'Content-Type' : 'text/plain' });
	    res.end('Hello World');
    });

    server.listen(8000);
    
파일을 다운로드, 혹은 직접 생성한 후 콘솔에서,

    node helloworld_server.js
    
를 실행시키면, 위와는 다르게 아래 그림처럼 계속 대기중인데, 이는 **server.listen()** 함수때문에 그렇다. 

![helloworld_server](https://github.com/devholic/SOMAExpensify/blob/master/study/resources/node/helloworld_server.png)

종료는 Ctrl+C로 할 수 있다.

위 소스에서는 8000번 포트로 대기중이라는 의미이므로, 웹 브라우저에서 **[localhost:8000](http://localhost:8000)**으로 접속하면 다음과 같이 웹페이지에 Hello World가 출력된것을 확인할 수 있다.

![helloworld_server_web](https://github.com/devholic/SOMAExpensify/blob/master/study/resources/node/helloworld_server_web.png)
