package com.upstox.analyticalserver.analyticalserverservice.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wahab
 *
 */
public class OHLCList {

	private int maximumItemCount = Integer.MAX_VALUE;
	private List<OHLCDataItem> data;

	private String symbol;

	public OHLCList(String symbol) {
		this.symbol = symbol;
		this.data = new ArrayList<>();
	}

	public int getItemsCount() {
		return this.data.size();
	}

	public void add(OHLCDataItem item) {
		if (this.getItemsCount() > this.maximumItemCount) {
			this.data.remove(0);
		}
		data.add(item);
	}

	public List<OHLCDataItem> getData() {
		return data;
	}

	public void setData(List<OHLCDataItem> data) {
		this.data = data;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return "OHLCList [data=" + data + ", symbol=" + symbol + "]";
	}

}
