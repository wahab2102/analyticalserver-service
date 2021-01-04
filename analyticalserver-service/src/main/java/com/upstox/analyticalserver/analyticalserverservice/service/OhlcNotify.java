package com.upstox.analyticalserver.analyticalserverservice.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.upstox.analyticalserver.analyticalserverservice.service.BarChartResponseGeneratorService.BarChartResponse;
import com.upstox.analyticalserver.analyticalserverservice.utils.State;

@Service
@Scope(scopeName = "singleton")
public class OhlcNotify {
	private static final Logger logger = LoggerFactory.getLogger(OhlcNotify.class);
	@Autowired
	SimpMessagingTemplate template;

	@Autowired
	BarChartResponseGeneratorService barChartResponseGeneratorService;

	@Autowired
	FSM fsm;

	@Autowired
	GenerateFeeds feeds;

	private boolean isRunning = false;

	private ExecutorService executorService;

	private List<String> subscribers = new ArrayList<>();

	public OhlcNotify() {
		this.executorService = Executors.newSingleThreadExecutor();
	}

	public void run() {
		isRunning = true;
		executorService.execute(() -> read());
		fsm.run();
	}

	private void read() {
		while (isRunning) {
			Iterator<String> itr = subscribers.iterator();
			while (itr.hasNext()) {
				String string = itr.next();
				try {
					List<BarChartResponse> res = barChartResponseGeneratorService.receive(string);
					if (res != null && res.size() > 0) {
						logger.info("Send to {}", string);
						template.convertAndSend("/topic/" + string, res);
					}
				} catch (InterruptedException e) {
					logger.error("Notify thread Interrupted >> " + e.getMessage());
				}
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void addSubscriber(String subscriber) {
		fsm.setCurrentState(State.SUBSCRIBE);
		if (!subscribers.contains(subscriber)) {
			subscribers.add(subscriber);
			feeds.setSymbolsToParse(subscribers);
			if (!isRunning) {
				run();
			}
		}

	}

	public void removeSubscriber(String subscriber) {
		subscribers.remove(subscriber);
		feeds.setSymbolsToParse(subscribers);
		if (subscribers.size() == 0) {
			fsm.setCurrentState(State.UNSUBSCRIBE);
			isRunning = false;
			shutdomn();
		}
	}

	public List<String> getSubscribers() {
		return subscribers;
	}

	private void shutdomn() {
		isRunning = false;
		fsm.shutdown();
	}

}
