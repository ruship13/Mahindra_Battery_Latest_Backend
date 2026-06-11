package com.ats.mahindrabattery.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ats.mahindrabattery.entity.InfeedMissionRuntimeDetailsEntity;
import com.ats.mahindrabattery.entity.TemperatureAlarmMissionRuntimeDetailsEntity;
import com.ats.mahindrabattery.serviceimpl.TemperatureAlarmMissionRuntimeDetailsServiceImpl;

@CrossOrigin
@RestController
@RequestMapping("/tempratureAlarmMissionDetails")
public class TemperatureAlarmMissionRuntimeDetailsController {

	@Autowired
	TemperatureAlarmMissionRuntimeDetailsServiceImpl temperatureAlarmMissionRuntimeDetailsServiceImpl;

	@GetMapping("/fetchTempratureAlarmMissionStatusRuntimeDetails")
	public List<TemperatureAlarmMissionRuntimeDetailsEntity> fetchTempratureAlarmMissionRuntimeDetails() {

		return temperatureAlarmMissionRuntimeDetailsServiceImpl.fetchTempratureAlarmMissionRuntimeDetails();
	}

	@GetMapping("/getAllAlarmMission")
	public List<TemperatureAlarmMissionRuntimeDetailsEntity> fetchAllAlarmMission() {
		return temperatureAlarmMissionRuntimeDetailsServiceImpl.fetchAllAlarmMission();
	}
	
	
	@GetMapping("/getAllAlarmMissionByAutoMission")
	public List<TemperatureAlarmMissionRuntimeDetailsEntity> fetchAllAlarmMissionByAutoMission(){
		return temperatureAlarmMissionRuntimeDetailsServiceImpl.fetchAllAlarmMissionByAutoMission();
	}

//	@Scheduled(cron = "0 10 * * 1") 
	public void scheduledAddMockDrillMissionDetails() {
		addMockDrillMissionDetails("Area-1");
	}

//	@Scheduled(cron = "5 10 * * 1") 
	public void scheduledAddMockDrillMissionDetails1() {
		addMockDrillMissionDetails("Area-2");
	}

	@PostMapping("/addMockDrillMissionDetails/{areaName}")
	public ResponseEntity<Object> addMockDrillMissionDetails(@PathVariable String areaName) {
		return temperatureAlarmMissionRuntimeDetailsServiceImpl.addMockDrillMissionDetails(areaName);
	}

	@GetMapping("/fetchTemperatureAlarmMissionRuntimeDetailsByAllFilters/{alarmMissionStartDateTime}/{alarmMissionEndDateTime}")
	public List<TemperatureAlarmMissionRuntimeDetailsEntity> fetchInfeedMissionRuntimeDetailsByAllFilters(

			@PathVariable String alarmMissionStartDateTime, @PathVariable String alarmMissionEndDateTime) {

		List<TemperatureAlarmMissionRuntimeDetailsEntity> fetchInfeedMissionRuntimeDetailsByAllFilters = temperatureAlarmMissionRuntimeDetailsServiceImpl
				.findByAllFilters(alarmMissionStartDateTime, alarmMissionEndDateTime);
		return fetchInfeedMissionRuntimeDetailsByAllFilters;
	}
	
	
	
	@GetMapping("/fetchTemperatureAlarmMissionRuntimeDetailsByAllFiltersByAutoMission/{alarmMissionStartDateTime}/{alarmMissionEndDateTime}")
	public List<TemperatureAlarmMissionRuntimeDetailsEntity> findByAllFiltersForAutoMission(String alarmMissionStartDateTime,
			String alarmMissionEndDateTime){
		List<TemperatureAlarmMissionRuntimeDetailsEntity> fetchInfeedMissionRuntimeDetailsByAllFilters = temperatureAlarmMissionRuntimeDetailsServiceImpl
				.findByAllFiltersForAutoMission(alarmMissionStartDateTime, alarmMissionEndDateTime);
		return fetchInfeedMissionRuntimeDetailsByAllFilters;
	}

	@GetMapping("/findByCurrentDateForArea1")
	public ResponseEntity<Object> findByCurrentDateForArea1() throws ParseException {
		return temperatureAlarmMissionRuntimeDetailsServiceImpl.findByCurrentDateForArea1();
	}

	@GetMapping("/findByCurrentDateForArea2")
	public ResponseEntity<Object> findByCurrentDateForArea2() {
		return temperatureAlarmMissionRuntimeDetailsServiceImpl.findByCurrentDateForArea2();
	}

}
