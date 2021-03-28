package com.amit.demoapp.mapper;

import java.util.Map;

import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.JsonLineMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.amit.demoapp.dto.EventDTO;

public class WrappedJsonLineMapper implements LineMapper<EventDTO> {

	@Autowired
	private JsonLineMapper delegate;

	@Override
	public EventDTO mapLine(String line, int lineNumber) throws Exception {

		Map<String, Object> eventMap = delegate.mapLine(line, lineNumber);

		EventDTO dto = new EventDTO();
		dto.setId((String) eventMap.get("id"));
		dto.setState((String) eventMap.get("state"));
		dto.setType((String) eventMap.get("type"));
		dto.setHost((String) eventMap.get("host"));
		dto.setTimestamp(Long.valueOf((String) eventMap.get("timestamp")));
		return dto;
	}

	public void setDelegate(JsonLineMapper targetEventsLineMapper) {
		// TODO Auto-generated method stub

	}
}
