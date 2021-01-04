package com.upstox.analyticalserver.analyticalserverservice.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Service;

import com.upstox.analyticalserver.analyticalserverservice.model.BarChartDataItem;
import com.upstox.analyticalserver.analyticalserverservice.model.OHLCDataItem;

@Service
public class BarChartResponseGeneratorService {

	private int maximumItemCount = Integer.MAX_VALUE;
	private BlockingQueue<BarChartDataItem> data;

	public BarChartResponseGeneratorService() {
		this.data = new LinkedBlockingQueue<>();
	}

	public int getItemsCount() {
		return this.data.size();
	}

	public void add(BarChartDataItem item) {
		if (this.getItemsCount() > this.maximumItemCount) {
			this.data.remove(0);
		}
		data.add(item);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	public List<BarChartResponse> receive(String symbol) throws InterruptedException {
		List<BarChartResponse> response = new ArrayList<BarChartResponseGeneratorService.BarChartResponse>();
		int count = 0;
		while (data.size() > 0 && count < 26) {
			count++;
			BarChartDataItem item = data.take();
			if (symbol.equals(item.getSymbol())) {
				for (OHLCDataItem li : item.getList().getData()) {
					response.add(new BarChartResponseBuilder(symbol, item.getBarNum()).build(li));
				}
			}
		}
		return response;
	}

	class BarChartResponseBuilder {
		private String symbol;
		private int bar_num;

		BarChartResponseBuilder(String symbol, int barNum) {
			this.symbol = symbol;
			this.bar_num = barNum;
		}

		public BarChartResponse build(OHLCDataItem li) {
			return new BarChartResponse(this.symbol, this.bar_num, li.getOhlc().getO(), li.getOhlc().getC(), li.getOhlc().getH(), li.getOhlc().getL(), li.getVolume());
		}
	}

	class BarChartResponse implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5125640580772437170L;

		public BarChartResponse(String symbol, int bar_num, double o, double c, double h, double l, double volume) {
			super();
			this.symbol = symbol;
			this.bar_num = bar_num;
			this.o = o;
			this.c = c;
			this.h = h;
			this.l = l;
			this.volume = volume;
		}

		private String symbol;
		private int bar_num;
		private double o;
		private double c;
		private double h;
		private double l;
		private double volume;

		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		public int getBar_num() {
			return bar_num;
		}

		public void setBar_num(int bar_num) {
			this.bar_num = bar_num;
		}

		public double getO() {
			return o;
		}

		public void setO(double o) {
			this.o = o;
		}

		public double getC() {
			return c;
		}

		public void setC(double c) {
			this.c = c;
		}

		public double getH() {
			return h;
		}

		public void setH(double h) {
			this.h = h;
		}

		public double getL() {
			return l;
		}

		public void setL(double l) {
			this.l = l;
		}

		public double getVolume() {
			return volume;
		}

		public void setVolume(double volume) {
			this.volume = volume;
		}

	}

}
