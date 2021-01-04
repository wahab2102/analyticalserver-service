package com.upstox.analyticalserver.analyticalserverservice.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upstox.analyticalserver.analyticalserverservice.model.BarChartDataItem;
import com.upstox.analyticalserver.analyticalserverservice.model.OHLCList;
import com.upstox.analyticalserver.analyticalserverservice.model.Trade;
import com.upstox.analyticalserver.analyticalserverservice.utils.State;

/**
 * @author Wahab
 *
 */
@Service
public class FSM {
	private static final Logger logger = LoggerFactory.getLogger(FSM.class);

	@Autowired
	BarChartResponseGeneratorService barChartResponseGeneratorService;

	@Autowired
	GenerateFeeds feeds;

	@Autowired
	OhlcNotify notifier;

	private State currentState;
	private Map<String, LinkedBlockingQueue<Trade>> trades;
	private ScheduledExecutorService executorService;

	private boolean isAccepting = true;

	private static final int timeInterval = 15;

	public FSM() {
		trades = new ConcurrentHashMap<>();
		this.executorService = Executors.newSingleThreadScheduledExecutor();
	}

	/**
	 * Repitative loop to check current state and run the thread.
	 * 
	 * @param t
	 */
	public void run() {
		feeds.setSleepTime(200);
		feeds.run();
		isAccepting = true;
		executorService.schedule(() -> runScheduledTask(), 5, TimeUnit.SECONDS);
	}

	/**
	 * Aggregate the (open, high, low, close, volume) based on the predefined
	 * time interval (15 sec). Always Running Thread
	 */
	private void runScheduledTask() {
		Map<String, AtomicInteger> barCount = new HashMap<String, AtomicInteger>();
		while (isAccepting) {
			logger.debug("Run task in FSM");
			long start = System.currentTimeMillis();
			long end = start + timeInterval * 1000;

			for (Entry<String, LinkedBlockingQueue<Trade>> entrySet : trades.entrySet()) {
				incrementBarCount(barCount, entrySet.getKey());
				OHLCList list = new OHLCList(entrySet.getKey());
				ComputeOHLC c = new ComputeOHLC();
				BlockingQueue<Trade> intTrade = entrySet.getValue();
				Trade trade = null;
				while (System.currentTimeMillis() < end) {
					if (intTrade.size() > 0) {
						try {
							trade = intTrade.take();
							if (c.isOpen()) {
								c.alwaysRunning(trade);
							} else {
								c.open(trade);
							}
							list.add(c.createOHLCDataItem());
						} catch (Exception e) {
							logger.error(e.getMessage());
						}
					}
				}
				logger.info("Interval completed.");
				try {
					if (intTrade.size() > 0) {
						trade = intTrade.take();
						c.close(trade);
						list.add(c.createOHLCDataItem());
					} else if (trade != null) {
						c.close(trade);
						list.add(c.createOHLCDataItem());
					}
				} catch (Exception e) {
					logger.error(" ************ Error while compute close value *************** " + e.getMessage());
				}
				createChart(list, barCount);
			}
		}
	}

	/**
	 * Increment the bar_counter + 1 grouped by symbol
	 * 
	 * @param barCount
	 * @param symbol
	 */
	private void incrementBarCount(Map<String, AtomicInteger> barCount, String symbol) {
		barCount.putIfAbsent(symbol, new AtomicInteger(0));
		barCount.get(symbol).incrementAndGet();
		logger.info("Increment bar counter {}", barCount.get(symbol));
	}

	/**
	 * 
	 * Create Bar Chart once the interval is completed.
	 * 
	 * @param list
	 * @param barCount
	 */
	private void createChart(OHLCList list, Map<String, AtomicInteger> barCount) {

		try {
			Collections.sort(list.getData());
			BarChartDataItem item = new BarChartDataItem();
			item.setList(list);
			item.setSymbol(list.getSymbol());
			item.setBarNum(barCount.get(list.getSymbol()).get());
			barChartResponseGeneratorService.add(item);
			logger.info("BarChart >> {} ", item);
		} catch (Exception e) {
			logger.error("Error occured while creating bar chart for {} ", list.getSymbol());
		}
	}

	/**
	 * Add each trade to a bucket mapped with sym
	 * 
	 * @param t
	 * @throws InterruptedException
	 */
	public void onTrade(Trade t) throws InterruptedException {
		LinkedBlockingQueue<Trade> tr = null;
		if (trades.containsKey(t.getSym())) {
			tr = trades.get(t.getSym());
			tr.put(t);
		} else {
			tr = new LinkedBlockingQueue<>();
		}
		trades.put(t.getSym(), tr);
	}

	/**
	 * Shutdown the service
	 */
	public void shutdown() {
		feeds.shutdown();
		isAccepting = false;
	}

	public boolean isAccepting() {
		return isAccepting;
	}

	public State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}
}
