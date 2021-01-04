# analyticalserver-service
 Spring Boot project implementing websocket based connection to preview OHLC datasets for ticks

## Instructions to setup and run 
* To run this project, build it using maven. The generated jar can then be used to start the application (java -jar <appname>). 
* Once the application is started on predefined port, access the webpage from http://localhost:8080/.


# Summary
* To test the code assignment, I've create a plain webpage. There is only one main page on root context . Here on the top left there are two buttons (SUBSCRIBE, UNSUBSCRIBE) 
##### Worflow
* Once the subscribe button is hit, a websocket connection is established with the server and subscribed to /topic/symbol. 
 where 'symbol' is hard coded on UI for demonstration purpose.
* A sample response output looks like this:
* {"symbol":"XXBTZUSD","bar_num":1,"o":6538.2,"c":0,"h":6538.2,"l":6538.2,"volume":0.498558}{"symbol":"XXBTZUSD","bar_num":1,"o":6538.2,"c":0,"h":6538.2,"l":6537.9,"volume":3.556558}
....
{"symbol":"XXBTZUSD","bar_num":1,"o":6538.2,"c":6535,"h":6539.7,"l":6535,"volume":14.88299784}
* The data is continuously populated on the UI, till we Unsubscribe.

##### Design

* At the backend, the workflow is divided into 3 workers. Once the spring boot application is started, all the worker beans are initialized, but not started.
* All the workers start their execution only when the user subscribe to the websocket (/topic/'destination').
* Worker-1: This thread is responsible to generate feeds for Worker-2. The thread only pass on the trades based on the 'destination' subscribed. For eg: "symbol":"XXBTZUSD"
* Worker-2: FSM thread continuosly iterates and aggregates the OHLC datasets. There are four states on which the transition happens and different set of OHLC datasets are computed. 1st state: Open (At every new interval), 2nd State: AlwaysRunning (Till the interval expires), 3rd State (Interval Expired and changed to exit status), 4th State: Create BarChart for an the interval and increment the bar_num for each symbol. The bar chart is normalized and pushed to BarChartResponseGeneratorService for furthur consumption. Before creating the bar chart, the OHLC and volume are maintined in list and then are sorted based on Timestamp TS2. 


* Worker-3: The WebSocketListener handles subscribe and unsubscribe events. Based on an event the Worker-3 thread (OhlcNotify) thread starts the execution, and reads the BarChartResponse from BarChartResponseGeneratorService. All the BarChartResponse aggregated based on symbols are pushed to all the users subscribed to a topic ending with symbol.

##### Config

Worker-1 thread reads the trader json from resource folder. The path to read the file is configured in application.yml file.

##### Controlling the threads
* Each worker thread is only started when a user has subscribed to topic by passing stompClient.subscribe('/topic/XXBTZUSD', onMessageReceived); the symbol to stompclient's subscribe method.

* The Worker-1 thread continously pass the trade tick to Worker-2 thread. In onTrade method of FSM (Worker2) we maintain the ticks grouped by symbol. 
We do this by having a ConcurrentHashMap of string and LinkedBlockingQueue. Then the runScheduledTask performs the repitative task to compute ohlc, volume and then generating the barchart.
* The Worker-3 (OhlcNotify) thread is responsible to start the execution of self and FSM thread which eventually is responsible to start GenerateFeeds thread. We think this as a subscription event intializing all the worker threads.



