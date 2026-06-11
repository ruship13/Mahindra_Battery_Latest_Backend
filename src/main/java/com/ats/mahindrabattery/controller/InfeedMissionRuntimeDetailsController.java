package com.ats.mahindrabattery.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ats.mahindrabattery.entity.InfeedMissionRuntimeDetailsEntity;
import com.ats.mahindrabattery.serviceimpl.InfeedMissionRuntimeDetailsServiceImpl;

@RestController
@CrossOrigin
@RequestMapping("/infeedmissionruntimedetails")
@EnableCaching
public class InfeedMissionRuntimeDetailsController {

	@Autowired
	private InfeedMissionRuntimeDetailsServiceImpl infeedMissionRuntimeDetailsService;

	@GetMapping("/getall")
//	@Cacheable(cacheNames = "allInfeedMissionRuntimeDetailsCache")
	public List<InfeedMissionRuntimeDetailsEntity> getAllInfeedMissionRuntimeDetails() {
		List<InfeedMissionRuntimeDetailsEntity> allInfeedMissionRuntimeDetails = infeedMissionRuntimeDetailsService
				.getAllInfeedMissionRuntimeDetails();
		return allInfeedMissionRuntimeDetails;
	}

	@GetMapping("/findbycdatetime/{cDateTime}")
	public List<InfeedMissionRuntimeDetailsEntity> findBycDateTime(@PathVariable String cDateTime) {
		List<InfeedMissionRuntimeDetailsEntity> findBycDateTimeInfeedMissionRuntimeDetails = infeedMissionRuntimeDetailsService
				.findByCreatedDateTime(cDateTime);
		return findBycDateTimeInfeedMissionRuntimeDetails;
	}

	@Cacheable(value = "infeedMissionRuntimeCache", key = "#startDate + '_' + #endDate")
	@GetMapping("/findbetweendates/{startDate}/{endDate}")
	public List<InfeedMissionRuntimeDetailsEntity> findAllInfeedMissionRuntimeDetailsBetweenDates(
			@PathVariable String startDate, @PathVariable String endDate) {
		List<InfeedMissionRuntimeDetailsEntity> findAllInfeedMissionRuntimeDetailsBetweenDates = infeedMissionRuntimeDetailsService
				.findAllInfeedMissionRuntimeDetailsByDate(startDate, endDate);
		return findAllInfeedMissionRuntimeDetailsBetweenDates;
	}

	@GetMapping("/getbydate")
	public List<InfeedMissionRuntimeDetailsEntity> getByDate() {
		List<InfeedMissionRuntimeDetailsEntity> getByDate = infeedMissionRuntimeDetailsService.getByDate();
		return getByDate;
	}

	@GetMapping("/findbyinfeedmissionid/{id}")
	public List<InfeedMissionRuntimeDetailsEntity> findByInfeedMissionId(@PathVariable int id) {
		List<InfeedMissionRuntimeDetailsEntity> findByInfeedMissionId = infeedMissionRuntimeDetailsService
				.findByInfeedMissionId(id);
		return findByInfeedMissionId;
	}

	@GetMapping("/fetchInfeedMissionRuntimeDetails")
	public List<InfeedMissionRuntimeDetailsEntity> fetchInfeedMissionRuntimeDetails() {

		return infeedMissionRuntimeDetailsService.fetchInfeedMissionRuntimeDetails();
	}

	@PutMapping("/updateInfeedMissionRuntimeDetailsDetails")
	public void updateInfeedMissionRuntimeDetails(
			@RequestBody InfeedMissionRuntimeDetailsEntity infeedMissionRuntimeDetailsEntity) {

		infeedMissionRuntimeDetailsService.updateInfeedMissionRuntimeDetails(infeedMissionRuntimeDetailsEntity);
	}


	
	
	
	@GetMapping("/fetchInfeedMissionRuntimeDetailsByAllFilters/{productName}/{palletStatus}/{infeedMissionStartCdatetime}/{infeedMissionEndCdatetime}")
	public List<InfeedMissionRuntimeDetailsEntity> fetchInfeedMissionRuntimeDetailsByAllFilters(
	        @PathVariable String productName, 
	        @PathVariable String palletStatus,
	        @PathVariable String infeedMissionStartCdatetime, 
	        @PathVariable String infeedMissionEndCdatetime) {

	    List<InfeedMissionRuntimeDetailsEntity> fetchInfeedMissionRuntimeDetailsByAllFilters = infeedMissionRuntimeDetailsService
	            .findByAllFilters(productName, palletStatus, infeedMissionStartCdatetime, infeedMissionEndCdatetime);
	    return fetchInfeedMissionRuntimeDetailsByAllFilters;
	}


}
