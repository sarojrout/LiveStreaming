/**
 * 
 */
package com.saroj.controller;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.saroj.dao.DataStore;
import com.saroj.dao.MongoDataStore;
import com.saroj.service.EventProducer;
import com.saroj.service.KafkaEventProducer;




/**
 * @author sarojrout
 *
 */
@Controller
public class BaseController {

	private static final String KAFKA_PORT = "2181";
	private static final String KAFKA_HOST = "127.0.0.1";
	private static final String MONGO_HOST = "localhost";
	private static final int MONGO_PORT = 27017;
	private static final String VIEW_INDEX = "index";
	private static final String VIEW_DASHBOARD = "dashboard";
	private static final String VIEW_DATA = "allData";
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(BaseController.class);
	private final static EventProducer eventProducer = KafkaEventProducer.getConnection(KAFKA_HOST, KAFKA_PORT);
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(ModelMap model) {
		return VIEW_INDEX;
	}

	/**
	 * Events sent to kafka Cluster
	 * @param event
	 * @return
	 */
	@RequestMapping(value = "/send", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public String sendEvent(@RequestBody ServiceEventRequest event) {				
		String jsonData;
		try {
			ObjectWriter ow = new ObjectMapper().writer();
			jsonData = ow.writeValueAsString(event);
		} catch (IOException e) {
			logger.error("Invalid request:" + e.getStackTrace());
			return "Error";
		}

		eventProducer.send("test-events", jsonData);

		return "success";
	}
	
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	public String dashboard(
			@RequestParam(value = "eventName") String eventName,
			@RequestParam(value = "bucket") String bucket,
			@RequestParam(required = false, value = "value") Integer value,
			ModelMap model) {

		DataStore mongo = null;
		try {
			mongo = MongoDataStore.getInstance(MONGO_HOST,MONGO_PORT );
		} catch (UnknownHostException e) {
			logger.error("Error in connection to mongoDb" + e.getStackTrace());
			model.addAttribute("message", "Invalid request");
			return VIEW_DASHBOARD;
		}

		model.addAttribute("eventName", eventName);

		if (bucket.equals("hourly")) {
			model.addAttribute("bucket", "hour");
			model.addAttribute("counter",
					mongo.getHourlyCount(eventName, new Date(), value));
		} else if (bucket.equals("daily")) {
			model.addAttribute("bucket", "day");
			model.addAttribute("counter",
					mongo.getDailyCount(eventName, new Date()));
		} else {
			model.addAttribute("message", "Invalid request");
		}

		return VIEW_DASHBOARD;
	}

	 @RequestMapping(value = "/dashboard/all", method = RequestMethod.GET)  
		public String allData(ModelMap model) { 
		 DataStore mongo = null;
		 try {
				mongo = MongoDataStore.getInstance(MONGO_HOST,MONGO_PORT );
			} catch (UnknownHostException e) {
				logger.error("Error in connection to mongoDb" + e.getStackTrace());
				model.addAttribute("message", "Invalid request");
				return VIEW_DATA;
			}
	        model.addAttribute("eventList", mongo.getAll()); 
	        System.out.println("Controller:"+mongo.getAll());
	        return VIEW_DATA;  
	    }  
	 
}
