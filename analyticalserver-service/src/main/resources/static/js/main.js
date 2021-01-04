'use strict';

var messageArea = document.querySelector('#messageArea');
var statusArea = document.querySelector('.status');

var stompClient = null;
function sub(event) {
	connect(event);
}

function unsub(event) {
	stompClient.unsubscribe('sub-0', onMessageReceived);
}

function status() {
	// Create a request variable and assign a new XMLHttpRequest object to it.
	var request = new XMLHttpRequest()

	// Open a new connection, using the GET request on the URL endpoint
	request.open('GET', '/status', true)

	request.onload = function() {
		var textElement = document.createElement('p');
		statusArea.appendChild(textElement);
		textElement.appendChild(myJson);
	}

}

function connect(event) {
	var socket = new SockJS('/ws');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, onConnected, onError);
}

function onError(error) {
	alert('Failed to connect web server');
}

function onConnected() {
	console.log('connected')
	// Subscribe to the Topic with destination
	stompClient.subscribe('/topic/XXBTZUSD', onMessageReceived);

}

function onMessageReceived(payload) {
	var message = JSON.parse(payload.body);

	var messageElement = document.createElement('li');
	var textElement = document.createElement('p');
	messageElement.appendChild(textElement);
	messageArea.appendChild(messageElement);
	console.log(message);
	message.forEach(function(msg) {
		var messageText = document.createTextNode(JSON.stringify(msg));
		textElement.appendChild(messageText);
		messageElement.appendChild(textElement);

		messageArea.appendChild(messageElement);
	})
	messageArea.scrollTop = messageArea.scrollHeight;
}
