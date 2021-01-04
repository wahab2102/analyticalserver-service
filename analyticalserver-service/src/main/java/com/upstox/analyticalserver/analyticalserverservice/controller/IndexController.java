package com.upstox.analyticalserver.analyticalserverservice.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController implements ErrorController {

	private static final String PATH = "/error";

	@RequestMapping(value = PATH)
	public String error() {
		return "Error occured while accessing the path.";
	}

	@Override
	public String getErrorPath() {
		return PATH;
	}
}