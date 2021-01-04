package com.upstox.analyticalserver.analyticalserverservice.model;

import java.io.Serializable;

public class OHLCDataItem implements Comparable<OHLCDataItem>, Serializable {

	private Long timestamp;
	private OHLC ohlc;
	private double volume;

	public OHLCDataItem(Long timestamp, double open, double high, double low, double close, double volume) {
		this.timestamp = timestamp;
		this.ohlc = new OHLC(open, high, low, close);
		this.volume = volume;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6434956723463815193L;

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public OHLC getOhlc() {
		return ohlc;
	}

	public void setOhlc(OHLC ohlc) {
		this.ohlc = ohlc;
	}

	@Override
	public int compareTo(OHLCDataItem o) {
		return this.timestamp.compareTo(o.timestamp);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof OHLCDataItem)) {
			return false;
		} else {
			OHLCDataItem that = (OHLCDataItem) obj;
			return !this.timestamp.equals(that.timestamp) ? false : this.ohlc.equals(that.getOhlc());
		}
	}

	@Override
	public String toString() {
		return "OHLCDataItem [timestamp=" + timestamp + ", ohlc=" + ohlc.toString() + ", volume=" + volume + "]";
	}

}
