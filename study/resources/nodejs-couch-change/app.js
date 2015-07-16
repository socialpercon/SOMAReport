var http = require('http');
var fs = require('fs');
var last_seq = -1;

// 기존 last_seq 불러오기
fs.readFile('./app.pref', 'utf8', function(err, data) {
  	if(err){
	}else{
		last_seq = data;
	}
});

function load(){
	http.get("http://plusquare.com:3001/test_report/_changes?feed=continuous", function(response) {
 		response.on('data', function (data) {
 			try{
				var json = JSON.parse(data);
				if(json.hasOwnProperty('last_seq')){
					last_seq = json.last_seq;
				}else{
					if(json.hasOwnProperty('seq')){
						if(json.seq>last_seq){
							fs.writeFile('./app.pref', json.seq, function(err) {
  								if(err){
 									console.log('pref error'+err);
  								}
							});
							fs.writeFile('./seq-'+json.seq+'.json', JSON.stringify(json),function(err){
								if(err){
									console.log('Error occured during create backup file');
								}
							});
							last_seq = json.seq;
						}
					}
				}
			}catch(e){

			}
		});
	}).on('error', function(e) {
  		console.log("Error: " + e.message);
	}).on('close', function(e) {
  		load();
	});
}

load();