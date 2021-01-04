package com.upstox.analyticalserver.analyticalserverservice.listener;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import com.upstox.analyticalserver.analyticalserverservice.service.OhlcNotify;

@Component
public class WebSocketListener {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketListener.class);
	private Map<String, String> destinationTracker = new HashMap<>();

	@Autowired
	OhlcNotify notifier;

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		logger.info("Received a new web socket connection");
	}

	@EventListener
	public void handleWebSocketSubscriptionListener(SessionSubscribeEvent event) {
		logger.info("Received a new web socket subscription");
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
		String des = headers.getDestination();
		String sub = headers.getDestination().substring(des.lastIndexOf("/") + 1, des.length());
		destinationTracker.put(headers.getSessionId(), sub);
		notifier.addSubscriber(sub);
	}

	@EventListener
	public void handleUnsubscribe(final SessionUnsubscribeEvent event) {
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
		String sessionId = headers.getSessionId();
		if (destinationTracker.containsKey(sessionId)) {
			logger.info("Unsubscribed");
			notifier.removeSubscriber(destinationTracker.get(sessionId));
			destinationTracker.remove(sessionId);
		}
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String username = (String) headerAccessor.getSessionAttributes().get("username");
		if (username != null) {
			logger.info("User Disconnected : " + username);

			messagingTemplate.convertAndSend("/topic/public", "left");
		}
	}
}
