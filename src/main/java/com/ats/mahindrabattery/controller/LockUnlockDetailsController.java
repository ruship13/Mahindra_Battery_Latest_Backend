package com.ats.mahindrabattery.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ats.mahindrabattery.entity.MasterPositionDetailsEntity;
import com.ats.mahindrabattery.serviceimpl.LockUnlockDetailsServiceImpl;



@RestController
@RequestMapping("/lockDetails")
@CrossOrigin
public class LockUnlockDetailsController {

	@Autowired
	private LockUnlockDetailsServiceImpl lockUnlockDetailsServiceImpl;

	@GetMapping("/findByLockReport")
	public List<MasterPositionDetailsEntity> findByCdatetimeLockReport() {
		return lockUnlockDetailsServiceImpl.findByLockReport();
	}


	
	@GetMapping("/findByAllFilters/{areaId}/{floorId}")
	public List<MasterPositionDetailsEntity> findByAreaAndFloorFilters(
			@PathVariable int areaId,
			@PathVariable int floorId){
		return lockUnlockDetailsServiceImpl.findByAreaAndFloorFilters(areaId, floorId);
	}

}
