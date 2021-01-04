package com.upstox.analyticalserver.analyticalserverservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Trade implements Comparable<Trade> {

	private String sym;
	@JsonProperty(value = "P")
	private Double price;
	@JsonProperty(value = "Q")
	private Double quantity;
	@JsonProperty(value = "TS2")
	private long time;

	public String getSym() {
		return sym;
	}

	public Double getPrice() {
		return price;
	}

	public Double getQuantity() {
		return quantity;
	}

	public long getTime() {
		return time;
	}

	public void setSym(String sym) {
		this.sym = sym;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "Trade [sym=" + sym + ", price=" + price + ", quantity=" + quantity + ", time=" + time + "]";
	}

	@Override
	public int compareTo(Trade o) {
		return new Long(o.getTime()).compareTo(new Long(this.getTime()));
	}

}
