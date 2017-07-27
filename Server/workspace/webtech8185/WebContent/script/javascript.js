var wSocket;

function connectToServer() {
	wSocket = new WebSocket("ws://localhost:8080/webtech8185/mysocket");
    setupConnection(wSocket);
}

function setupConnection(wSocket) {
    
	wSocket.binaryType = "arraybuffer";
    
	wSocket.onopen = function() {
		
		var log = document.getElementById('panelLog');
    	log.classList.add('panel-success');
    	log.classList.remove('panel-danger');
		
    	var label = document.getElementById('panelLabel');
		label.innerHTML = "Server Communication (CONNECTED)";
		
		logMessage("Connected.")
    };

    wSocket.onclose = function() {
    	
    	var log = document.getElementById('panelLog');
    	log.classList.add('panel-danger');
    	log.classList.remove('panel-success');
		
    	var label = document.getElementById('panelLabel');
		label.innerHTML = "Server Communication (DISCONNECTED)";
    	
		logMessage("Disconnected.");
    };
    
    wSocket.onerror = function(e) {
    	logMessage(e.msg);
    }

    wSocket.onmessage = function(msg) {
    	if (msg && msg.data.length > 0) {
    		logMessage(msg.data);
    	}
    };	
}

function logMessage(msg) {
	var textArea = document.getElementById('textArea');
	textArea.value += "\n" + msg;	
	textArea.scrollTop = textArea.scrollHeight;
}

function sendFile() {
	
	var file = document.getElementById('fileInput').files[0];
	
	if (!file) return;
	
	sendFileName(file);
	sendFileContent(file);
}

function sendFileName(file) {
	socketSend('filename:' + file.name);
}

function sendFileContent(file) {

	var reader = new FileReader();

    reader.onload = function(evt) {
        var content = new ArrayBuffer();            
        content = evt.target.result;
        
        socketSend(content);
        socketSend('eof');
    }

    reader.loadend = function() {}

    reader.readAsArrayBuffer(file);	
}

function viewUploadedFiles() {
	socketSend('listFiles');
}

function disconnectFromServer() {
	wSocket.close();
}

function logIn() {
	
	connectToServer();

	setTimeout(function(){
			var user = document.getElementById('username').value;
			var pass = document.getElementById('password').value;
			socketSend("login:" + user + "SEPARATOR" + pass);
		}, 500); 
}

function socketSend(content) {
	if (wSocket)
		wSocket.send(content);
}


