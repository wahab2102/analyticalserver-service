package com.upstox.analyticalserver.analyticalserverservice.controller;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class OHLCController {

	@MessageExceptionHandler
	@SendToUser(value = "/topic/errors", broadcast = false)
	public String handleProfanity(Exception e) {
		return e.getMessage();
	}

}
