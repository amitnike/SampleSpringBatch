package com.amit.demoapp.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.amit.demoapp.dto.EventDTO;

public class EventItemProcessor implements ItemProcessor<EventDTO, EventDTO> {

	private static final Logger log = LoggerFactory.getLogger(EventItemProcessor.class);

	@Override
	public EventDTO process(final EventDTO event) throws Exception {

		EventDTO returnedEvent;

		log.info("Converting (" + event + ") into (" + event.getId() + ")");
		if (event.getState().equalsIgnoreCase("FINISHED")) {
			returnedEvent = event;
		} else {
			returnedEvent = null;
		}

		return returnedEvent;
	}
}
