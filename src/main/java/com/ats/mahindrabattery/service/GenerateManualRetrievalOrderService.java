package com.ats.mahindrabattery.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.ats.mahindrabattery.entity.GenerateManualRetrievalOrderEntity;
import com.ats.mahindrabattery.entity.GenerateRetrivalMissionTypeEntity;

public interface GenerateManualRetrievalOrderService {

	public List<GenerateManualRetrievalOrderEntity> getAllMannualRetrivalDetails();
	
	public List<GenerateManualRetrievalOrderEntity> findAllMannualDispatchOrdersByDate(String startDate,
			String endDate);
	
	public List<GenerateManualRetrievalOrderEntity> findByMannualDispatchNumber(String dispatchOrderNumber);
	
	
	public void updateDispatchTriggered(GenerateRetrivalMissionTypeEntity data);
	
	
	public List<GenerateRetrivalMissionTypeEntity> fetchAllMissionTypeData();


// 26-5-2026
// ADD new method for update currentdate retrival order IsDeleted status to 1
	public ResponseEntity updateCurrentDateRetrivalOrderIsDeleted();

	
	
}
