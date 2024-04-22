package org.fipro.eclipse.tutorial.service.inverter.impl;

import java.util.HashMap;
import java.util.Map;

import org.fipro.eclipse.tutorial.service.inverter.InverterService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;

@Component
public class InverterServiceImpl implements InverterService {

	@Reference
	private EventAdmin eventAdmin;
	
	@Override
	public String invert(String input) {
		String result = new StringBuilder(input).reverse().toString();
		 
        String topic = "TOPIC_LOGGING";
        Map<String, Object> data = new HashMap<>();
        data.put(EventConstants.EVENT_TOPIC, topic);
        data.put("org.eclipse.e4.data", "Inverted " + input + " to " + result);
        Event event = new Event(topic, data);
 
        eventAdmin.postEvent(event);
 
        return result;
	}
}