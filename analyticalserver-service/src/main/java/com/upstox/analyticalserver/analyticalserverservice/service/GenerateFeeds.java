package com.upstox.analyticalserver.analyticalserverservice.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upstox.analyticalserver.analyticalserverservice.config.AppConfig;
import com.upstox.analyticalserver.analyticalserverservice.model.Trade;

/**
 * @author Wahab
 *
 */
@Service
public class GenerateFeeds {

	@Autowired
	private AppConfig config;

	@Autowired
	private FSM fsm;

	private long sleepTime;
	private List<String> symbolsToParse;

	private ObjectMapper mapper;
	private ExecutorService executorService;

	private boolean terminate = false;

	private static final Logger logger = LoggerFactory.getLogger(GenerateFeeds.class);

	public GenerateFeeds() {
		this.executorService = Executors.newSingleThreadExecutor();
		this.mapper = new ObjectMapper();
	}

	public void run() {
		terminate = false;
		executorService.execute(() -> read());
	}

	private void read() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(this.config.getStockTradeFilePath())))) {
			while (!terminate) {
				Thread.sleep(getSleepTime());
				String line = br.readLine();
				if (line != null) {
					// Parse line and convert it to trade
					Trade t = this.mapper.readValue(line, Trade.class);
					if (symbolsToParse.contains(t.getSym()))
						fsm.onTrade(t);
					logger.info(t.toString());
				} else {
					executorService.shutdown();
					fsm.shutdown();
					break;
				}
			}
		} catch (IOException e) {
			logger.error("IOException at file read >>> " + e.getMessage());
		} catch (InterruptedException e) {
			logger.error("File Read thread got interrupted >>> " + e.getMessage());
		}

	}

	public static void main(String[] args) {
		new GenerateFeeds().run();
	}

	public long getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}

	public List<String> getSymbolsToParse() {
		return symbolsToParse;
	}

	public void setSymbolsToParse(List<String> symbolsToParse) {
		this.symbolsToParse = symbolsToParse;
	}

	public boolean isTerminate() {
		return terminate;
	}

	public void setTerminate(boolean terminate) {
		this.terminate = terminate;
	}

	public void shutdown() {
		terminate = true;
	}

}
