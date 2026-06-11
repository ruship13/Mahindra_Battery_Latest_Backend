package com.ats.mahindrabattery.serviceimpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ats.mahindrabattery.entity.AuditTrailDetailsEntity;
import com.ats.mahindrabattery.entity.CurrentPalletStockDetailsEntity;
import com.ats.mahindrabattery.entity.GenerateManualRetrievalOrderEntity;
import com.ats.mahindrabattery.entity.MasterPalletInformationEntity;
import com.ats.mahindrabattery.entity.MasterPositionDetailsEntity;
import com.ats.mahindrabattery.entity.OutfeedMissionRuntimeDetailsEntity;
import com.ats.mahindrabattery.repository.AuditTrailDetailsRepository;
import com.ats.mahindrabattery.repository.CurrentPalletStockDetailsRepository;
import com.ats.mahindrabattery.repository.GenerateManualRetrievalOrderRepository;
import com.ats.mahindrabattery.repository.MasterPalletInformationRepository;
import com.ats.mahindrabattery.repository.MasterPositionDetailsRepository;
import com.ats.mahindrabattery.repository.OutfeedMissionruntimeDetailsRepository;
import com.ats.mahindrabattery.service.OutfeedMissionRuntimeDetailsService;

@Service
public class OutfeedMissionRuntimeDetailsServiceImpl implements OutfeedMissionRuntimeDetailsService {

	@Autowired
	private OutfeedMissionruntimeDetailsRepository outfeedMissionruntimeDetailsRepository;

	@Autowired
	private MasterPositionDetailsRepository masterPositionDetailsRepositoryInstance;

	@Autowired
	MasterPalletInformationRepository masterPalletInformationRepositoryInstance;

	@Autowired
	private AuditTrailDetailsRepository auditTrailDetailsRepository;

	@Autowired
	private CurrentPalletStockDetailsRepository currentPalletStockDetailsRepository;
	
	
	@Autowired
	private GenerateManualRetrievalOrderRepository generateManualRetrievalOrderRepository;

	public List<OutfeedMissionRuntimeDetailsEntity> getAllOutfeedMissionRuntimeDetails() {
		try {
			List<OutfeedMissionRuntimeDetailsEntity> findAllOutfeedMissionRuntimeDetails = outfeedMissionruntimeDetailsRepository
					.findAll();
			return findAllOutfeedMissionRuntimeDetails;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public List<OutfeedMissionRuntimeDetailsEntity> findByCreatedDateTime(String createdDatetime) {
		try {
			List<OutfeedMissionRuntimeDetailsEntity> findByCreatedDateTime = outfeedMissionruntimeDetailsRepository
					.findByCreatedDatetime(createdDatetime);
			return findByCreatedDateTime;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public List<OutfeedMissionRuntimeDetailsEntity> findOutfeedNissionRuntimeDetailsBetweenDates(String startDate,
			String endDate) {
		try {
			List<OutfeedMissionRuntimeDetailsEntity> findOutfeedMissionRuntimeDetailsBetweenDates = outfeedMissionruntimeDetailsRepository
					.findOutfeedMissionRuntimeDetailsBetweenDates(startDate, endDate);
			return findOutfeedMissionRuntimeDetailsBetweenDates;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}


	
	
	public List<OutfeedMissionRuntimeDetailsEntity> getByDate() {
	    try {
	        // Step 1: Get today's date range
	        Date date = new Date();
	        String strDateFormat = "yyyy-MM-dd";
	        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
	        String currentDateTime = dateFormat.format(date);

	        String start = currentDateTime + " 00:00:00";
	        String end = currentDateTime + " 23:59:59";

	        // Step 2: Get Outfeed records for today with "COMPLETED" status
	        List<OutfeedMissionRuntimeDetailsEntity> missions =
	            outfeedMissionruntimeDetailsRepository
	                .getAllOutfeedMissionRuntimeDetailsStatusAndBetweenDates("COMPLETED", start, end);

					System.out.println("start::"+start);
					System.out.println("end::"+end);
					System.out.println("missions::"+missions.size());

	        // Step 3: Get all dispatch records (or only those needed if you prefer)
	        List<GenerateManualRetrievalOrderEntity> dispatches = 
	        		generateManualRetrievalOrderRepository.findAll(); // or a filtered method if needed

	        // Step 4: Map dispatchHistoryId to dispatchOrderNumber
	        Map<Integer, String> dispatchOrderMap = dispatches.stream()
	            .collect(Collectors.toMap(
	                GenerateManualRetrievalOrderEntity::getDispatchHistoryId,
	                GenerateManualRetrievalOrderEntity::getDispatchOrderNumber
	            ));

	        // Step 5: Set the dispatchOrderNumber into each Outfeed record
	        for (OutfeedMissionRuntimeDetailsEntity mission : missions) {
	            String dispatchOrderNumber = dispatchOrderMap.get(mission.getOrderId());
	            mission.setDispatchOrderNumber(dispatchOrderNumber);
	        }

	        return missions;
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }

	    return Collections.emptyList();
	}


	
	
	
	public List<OutfeedMissionRuntimeDetailsEntity> fetchOutfeedMissionRuntimeDetails() {
		try {
			return outfeedMissionruntimeDetailsRepository.findByOutfeedMissionStatus("READY", "IN_PROGRESS");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void updateOutfeedMissionRuntimeDetails(
			OutfeedMissionRuntimeDetailsEntity outfeedMissionRuntimeDetailsEntity) {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = ft.format(dNow);

		int positionId = outfeedMissionRuntimeDetailsEntity.getPositionId();
		MasterPositionDetailsEntity findByPositionId = masterPositionDetailsRepositoryInstance
				.findByPositionId(positionId);
		MasterPalletInformationEntity findByPalletInformationId = masterPalletInformationRepositoryInstance
				.findByPalletInformationId(outfeedMissionRuntimeDetailsEntity.getPalletInformationId());
		
	
		String outfeedMissionStatus = outfeedMissionRuntimeDetailsEntity.getOutfeedMissionStatus();
		if (outfeedMissionStatus.equals("ABORT")) {
			findByPositionId.setIsManualDispatch(0);
			findByPositionId.setPositionIsAllocated(1);
			findByPositionId.setEmptyPalletPosition(0);
			findByPositionId.setCDateTime(currentDate);
			masterPositionDetailsRepositoryInstance.save(findByPositionId);
			findByPalletInformationId.setIsOutfeedMissionGenerated(0);

			outfeedMissionRuntimeDetailsEntity.setOutfeedMissionEndDateTime("1900-01-01 00:00:00");
			outfeedMissionRuntimeDetailsEntity.setOutfeedMissionStartDateTime("1900-01-01 00:00:00");
			AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String name = authentication.getName();
			System.out.println(" name :: " + name);
			auditTrailDetailsEntity
					.setOperatorActions("Outfeed mission aborted for position " + findByPositionId.getPositionName()
							+ " having pallet code " + findByPalletInformationId.getPalletCode() + " by " + name);
			auditTrailDetailsEntity.setField("Outfeed mission Aborted");

			auditTrailDetailsEntity.setReason("Outfeed mission Aborted");

			auditTrailDetailsEntity.setUsername(name);
			auditTrailDetailsEntity.setDatetimeC(currentDate);
			auditTrailDetailsRepository.save(auditTrailDetailsEntity);

			masterPalletInformationRepositoryInstance.save(findByPalletInformationId);

		} else if (outfeedMissionStatus.equals("COMPLETED")) {
			findByPositionId.setIsManualDispatch(0);
			findByPositionId.setPositionIsAllocated(0);
			findByPositionId.setEmptyPalletPosition(1);
			findByPositionId.setCDateTime(currentDate);
			masterPositionDetailsRepositoryInstance.save(findByPositionId);
			findByPalletInformationId.setIsOutfeedMissionGenerated(0);
			findByPalletInformationId.setIsInfeedMissionGenerated(0);
			findByPalletInformationId.setCdatetime(currentDate);
			masterPalletInformationRepositoryInstance.save(findByPalletInformationId);
			outfeedMissionRuntimeDetailsEntity.setOutfeedMissionEndDateTime(currentDate);

			AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String name = authentication.getName();
			System.out.println(" name :: " + name);
			auditTrailDetailsEntity
					.setOperatorActions("Outfeed mission Completed for position " + findByPositionId.getPositionName()
							+ " having pallet code " + findByPalletInformationId.getPalletCode() + " by " + name);
			auditTrailDetailsEntity.setField("Outfeed mission Completed");

			auditTrailDetailsEntity.setReason("Outfeed mission Completed");

			auditTrailDetailsEntity.setUsername(name);
			auditTrailDetailsEntity.setDatetimeC(currentDate);
			auditTrailDetailsRepository.save(auditTrailDetailsEntity);

			outfeedMissionruntimeDetailsRepository.save(outfeedMissionRuntimeDetailsEntity);
			System.out.println("outfeedMissionRuntimeDetailsEntity::" + outfeedMissionRuntimeDetailsEntity);

			List<CurrentPalletStockDetailsEntity> findByPositionId2 = currentPalletStockDetailsRepository
					.findByPositionId(positionId);
			findByPositionId2.get(0).setPalletCode("NA");
			findByPositionId2.get(0).setPalletInformationId(0);
			findByPositionId2.get(0).setSerialNumber(0);
			findByPositionId2.get(0).setProductVariantCode("NA");
			findByPositionId2.get(0).setProductId(0);
			findByPositionId2.get(0).setProductName("NA");
			findByPositionId2.get(0).setProductVariantId(0);
			findByPositionId2.get(0).setProductVariantName("NA");
			findByPositionId2.get(0).setPalletStatusId(0);
			findByPositionId2.get(0).setPalletStatusname("NA");
			findByPositionId2.get(0).setAgeingDays(0);
			findByPositionId2.get(0).setQuantity(0);
			findByPositionId2.get(0).setQualityStatus("NA");
			findByPositionId2.get(0).setBatchNumber("NA");
			findByPositionId2.get(0).setModelNumber("NA");
			findByPositionId2.get(0).setLocation("NA");
			findByPositionId2.get(0).setPositionId(outfeedMissionRuntimeDetailsEntity.getPositionId());
			findByPositionId2.get(0).setPositionName(outfeedMissionRuntimeDetailsEntity.getPositionName());
			findByPositionId2.get(0)
					.setPositionNumberInRack(outfeedMissionRuntimeDetailsEntity.getPositionNumberInRack());
			findByPositionId2.get(0).setRackId(outfeedMissionRuntimeDetailsEntity.getRackId());
			findByPositionId2.get(0).setRackName(outfeedMissionRuntimeDetailsEntity.getRackName());
			findByPositionId2.get(0).setRackSide(outfeedMissionRuntimeDetailsEntity.getRackSide());
			findByPositionId2.get(0).setRackColumn(outfeedMissionRuntimeDetailsEntity.getRackColumn());
			findByPositionId2.get(0).setFloorId(outfeedMissionRuntimeDetailsEntity.getFloorId());
			findByPositionId2.get(0).setFloorName(outfeedMissionRuntimeDetailsEntity.getFloorName());
			findByPositionId2.get(0).setAreaId(outfeedMissionRuntimeDetailsEntity.getAreaId());
			findByPositionId2.get(0).setAreaName(outfeedMissionRuntimeDetailsEntity.getAreaName());
			findByPositionId2.get(0).setLoadDatetime("1900-01-01 00:00:00");
			findByPositionId2.get(0).setIsOutfeedMissionGenerated(0);
			findByPositionId2.get(0).setIsInfeedMissionGenerated(0);
			findByPositionId2.get(0).setUserId(0);
			findByPositionId2.get(0).setUserName("NA");
			findByPositionId2.get(0).setVendorCode("NA");
			findByPositionId2.get(0).setPartIdentificationCode("NA");
			findByPositionId2.get(0).setMfgDate("1900-01-01 00:00:00");
			findByPositionId2.get(0).setMfgShift("NA");
			findByPositionId2.get(0).setIsAlarmRack(0);
			currentPalletStockDetailsRepository.save(findByPositionId2.get(0));
		}
		System.out.println("masterPositionDetailsEntity" + findByPositionId);
		outfeedMissionruntimeDetailsRepository.save(outfeedMissionRuntimeDetailsEntity);

	}



	
	
	
	
	public List<OutfeedMissionRuntimeDetailsEntity> findByAllFilters(String productName, String palletStatus,
	        String outfeedMissionCdatetimeStart, String outfeedMissionCdatetimeEnd, String dispatchOrderNumber) {

	    List<String> filterList = new ArrayList<>();
	    List<OutfeedMissionRuntimeDetailsEntity> list;

	    if (!productName.equalsIgnoreCase("NA")) {
	        filterList.add("productName");
	    }
	    if (!palletStatus.equalsIgnoreCase("NA")) {
	        filterList.add("palletStatus");
	    }
	    if (!dispatchOrderNumber.equalsIgnoreCase("NA")) {
	        filterList.add("dispatchOrderNumber");
	    }

	    // Fetch dispatchHistoryId from dispatchOrderNumber if needed
	    Optional<GenerateManualRetrievalOrderEntity> dispatchOrderOpt = generateManualRetrievalOrderRepository
	            .findByDispatchOrderNumber(dispatchOrderNumber);

	    final Optional<Integer> dispatchHistoryIdOpt = dispatchOrderOpt.map(GenerateManualRetrievalOrderEntity::getDispatchHistoryId);

	    Predicate<OutfeedMissionRuntimeDetailsEntity> productNamePred =
	            data -> data.getProductName().equalsIgnoreCase(productName);
	    Predicate<OutfeedMissionRuntimeDetailsEntity> palletStatusPred =
	            data -> data.getPalletStatusName().equalsIgnoreCase(palletStatus);
	    Predicate<OutfeedMissionRuntimeDetailsEntity> dispatchHistoryIdPred =
	            data -> dispatchHistoryIdOpt.map(id -> data.getOrderId() == id).orElse(true);

	    // Get records based on date filters
	    if (!outfeedMissionCdatetimeStart.equals("NA") && !outfeedMissionCdatetimeEnd.equals("NA")) {
	        String startDateTime = outfeedMissionCdatetimeStart.replace("T", " ");
	        String endDateTime = outfeedMissionCdatetimeEnd.replace("T", " ");
	        list = outfeedMissionruntimeDetailsRepository
	                .findOutfeedMissionRuntimeDetailsBetweenDates(startDateTime, endDateTime, "COMPLETED");
	    } else {
	        Date dNow = new Date();
	        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
	        String date = ft.format(dNow);
	        list = outfeedMissionruntimeDetailsRepository
	                .findOutfeedMissionRuntimeDetailsBetweenDates(date + " 00:00:00", date + " 23:59:59", "COMPLETED");
	    }

	    // Apply stream filters
	    for (String filter : filterList) {
	        switch (filter) {
	            case "productName":
	                list = list.stream().filter(productNamePred).collect(Collectors.toList());
	                break;
	            case "palletStatus":
	                list = list.stream().filter(palletStatusPred).collect(Collectors.toList());
	                break;
	            case "dispatchOrderNumber":
	                list = list.stream().filter(dispatchHistoryIdPred).collect(Collectors.toList());
	                break;
	        }
	    }

	    // Enrich the results with dispatchOrderNumber
	    List<GenerateManualRetrievalOrderEntity> allDispatches = generateManualRetrievalOrderRepository.findAll();
	    Map<Integer, String> dispatchMap = allDispatches.stream()
	            .collect(Collectors.toMap(
	                    GenerateManualRetrievalOrderEntity::getDispatchHistoryId,
	                    GenerateManualRetrievalOrderEntity::getDispatchOrderNumber
	            ));

	    for (OutfeedMissionRuntimeDetailsEntity entity : list) {
	        entity.setDispatchOrderNumber(dispatchMap.get(entity.getOrderId()));
	    }

	    return list;
	}




}
