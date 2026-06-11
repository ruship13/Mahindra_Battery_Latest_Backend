package com.ats.mahindrabattery.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ats.mahindrabattery.entity.TemperatureAlarmMissionRuntimeDetailsEntity;

public interface TemperatureAlarmMissionRuntimeDetailsService {

	public List<TemperatureAlarmMissionRuntimeDetailsEntity> fetchTempratureAlarmMissionRuntimeDetails();
	
	public List<TemperatureAlarmMissionRuntimeDetailsEntity> fetchAllAlarmMission();

	public ResponseEntity<Object> addMockDrillMissionDetails(
			String areaName);
	
	
	public List<TemperatureAlarmMissionRuntimeDetailsEntity> findByAllFilters(
		       
	        String alarmMissionStartDateTime, 
	        String alarmMissionEndDateTime);

	List<TemperatureAlarmMissionRuntimeDetailsEntity> fetchAllAlarmMissionByAutoMission();

	List<TemperatureAlarmMissionRuntimeDetailsEntity> findByAllFiltersForAutoMission(String alarmMissionStartDateTime,
			String alarmMissionEndDateTime);
}
