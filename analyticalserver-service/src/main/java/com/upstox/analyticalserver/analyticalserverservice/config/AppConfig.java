package com.upstox.analyticalserver.analyticalserverservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "analyticalservice")
public class AppConfig {

	private String stockTradeFilePath;

	public String getStockTradeFilePath() {
		return stockTradeFilePath;
	}

	public void setStockTradeFilePath(String stockTradeFilePath) {
		this.stockTradeFilePath = stockTradeFilePath;
	}

}
