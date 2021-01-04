package com.upstox.analyticalserver.analyticalserverservice.model;

public class WebSocketPayload {

	private String symbol;
	private int interval;
	private String event;

	public String getSymbol() {
		return symbol;
	}

	public int getInterval() {
		return interval;
	}

	public String getEvent() {
		return event;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public void setEvent(String event) {
		this.event = event;
	}

}
