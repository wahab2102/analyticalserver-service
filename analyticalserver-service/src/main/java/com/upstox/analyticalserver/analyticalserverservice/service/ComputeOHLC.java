package com.upstox.analyticalserver.analyticalserverservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.upstox.analyticalserver.analyticalserverservice.model.OHLCDataItem;
import com.upstox.analyticalserver.analyticalserverservice.model.Trade;
import com.upstox.analyticalserver.analyticalserverservice.utils.MathUtils;

/**
 * @author Wahab
 *
 */
public class ComputeOHLC {
	private static final Logger logger = LoggerFactory.getLogger(ComputeOHLC.class);

	private Trade barChartIntervalFirstPrint = null;
	private double open = 0.0;
	private double close = 0.0;
	private double low = 0.0;
	private double high = 0.0;
	private double volume = 0;

	public void open(Trade input) {
		logger.debug("Open {} ", input.getSym());
		double price = input.getPrice();
		barChartIntervalFirstPrint = input;
		// the first trade price in the day (day open price)
		open = MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT);
		// the interval low
		low = MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT);
		// the interval high
		high = MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT);
		// set the initial volume
		volume = input.getQuantity();
	}

	public void alwaysRunning(Trade input) {
		logger.debug("Run >>> " + input.getSym());
		double price = input.getPrice();
		// Set the current low price
		if (MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT) < low)
			low = MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT);

		// Set the current high price
		if (MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT) > high)
			high = MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT);

		volume += input.getQuantity();
	}

	public void close(Trade input) {
		logger.debug("Close >>> " + input.getSym());
		close = MathUtils.roundDouble(input.getPrice(), MathUtils.TWO_DEC_DOUBLE_FORMAT);
		volume += input.getQuantity();
	}

	public OHLCDataItem createOHLCDataItem() {
		OHLCDataItem item = new OHLCDataItem(barChartIntervalFirstPrint.getTime(), open, high, low, close, volume);
		logger.info("OHLCItem >>> " + item.toString());
		return item;
	}

	public boolean isOpen() {
		return barChartIntervalFirstPrint != null;
	}

	public double getOpen() {
		return open;
	}

	public double getClose() {
		return close;
	}

	public double getLow() {
		return low;
	}

	public double getHigh() {
		return high;
	}

	public double getVolume() {
		return volume;
	}

}
