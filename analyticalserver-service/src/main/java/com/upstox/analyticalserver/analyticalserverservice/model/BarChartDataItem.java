package com.upstox.analyticalserver.analyticalserverservice.model;

public class BarChartDataItem {

	private int barNum;
	private String symbol;
	private OHLCList list;

	public int getBarNum() {
		return barNum;
	}

	public void setBarNum(int barNum) {
		this.barNum = barNum;
	}

	public OHLCList getList() {
		return list;
	}

	public void setList(OHLCList list) {
		this.list = list;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return "BarChartDataItem [barNum=" + barNum + ", symbol=" + symbol + ", list=" + list + "]";
	}

}
