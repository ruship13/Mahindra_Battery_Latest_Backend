package com.ats.mahindrabattery.serviceimpl;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ats.mahindrabattery.entity.MasterPositionDetailsEntity;
import com.ats.mahindrabattery.repository.LockUnlockDetailsRepository;
import com.ats.mahindrabattery.repository.MasterPositionDetailsRepository;
import com.ats.mahindrabattery.service.LockUnlockDetailsService;


@Service
public class LockUnlockDetailsServiceImpl implements LockUnlockDetailsService {

	@Autowired
	private LockUnlockDetailsRepository lockUnlockDetailsRepository;
	
	
	@Autowired
	private MasterPositionDetailsRepository masterPositionDetailsRepository;

public List<MasterPositionDetailsEntity> findByLockReport() {
		
		List<MasterPositionDetailsEntity> findByCDatetimeBetween = masterPositionDetailsRepository
				.findByPositionIsActive(0);
		return findByCDatetimeBetween;
	}
	
	
	
	
	public List<MasterPositionDetailsEntity> findByAreaAndFloorFilters(
	        int areaId,
	        int floorId) {

	    Instant start = Instant.now();

	    // Fetch base data
	    // ONLY POSITION_IS_ACTIVE = 0
	    List<MasterPositionDetailsEntity> list =
	            masterPositionDetailsRepository.findByPositionIsActive(0);

	    // Apply Area filter if provided
	    if (areaId > 0) {
	        list = list.stream()
	                .filter(data -> data.getAreaId() == areaId)
	                .collect(Collectors.toList());
	    }

	    // Apply Floor filter if provided
	    if (floorId > 0) {
	        list = list.stream()
	                .filter(data -> data.getFloorId() == floorId)
	                .collect(Collectors.toList());
	    }

	    // If no data found
	    if (list.isEmpty()) {
	        return null;
	    }

	    Instant end = Instant.now();
	    double timeTakenSeconds =
	            java.time.Duration.between(start, end).toNanos() / 1_000_000_000.0;

	    System.out.println("Time taken for Master Position filter: "
	            + String.format("%.3f", timeTakenSeconds) + " seconds");

	    return list;
	}
}
