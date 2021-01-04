package com.upstox.analyticalserver.analyticalserverservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.upstox.analyticalserver.analyticalserverservice.config.AppConfig;

@SpringBootTest
public class AnalyticalserverServiceApplicationTests {

	@Autowired
	AppConfig config;

	@Test
	void contextLoads() {
	}

}
