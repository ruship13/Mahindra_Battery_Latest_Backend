package com.ats.mahindrabattery.serviceimpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ats.mahindrabattery.entity.AuditTrailDetailsEntity;
import com.ats.mahindrabattery.entity.CurrentPalletStockDetailsEntity;
import com.ats.mahindrabattery.entity.InfeedMissionRuntimeDetailsEntity;
import com.ats.mahindrabattery.entity.MasterPalletInformationEntity;
import com.ats.mahindrabattery.entity.MasterPositionDetailsEntity;
import com.ats.mahindrabattery.entity.MasterUserDetailsEntity;
import com.ats.mahindrabattery.repository.AuditTrailDetailsRepository;
import com.ats.mahindrabattery.repository.CurrentPalletStockDetailsRepository;
import com.ats.mahindrabattery.repository.InfeedMissionRuntimeDetailsRepository;
import com.ats.mahindrabattery.repository.MasterPalletInformationRepository;
import com.ats.mahindrabattery.repository.MasterPositionDetailsRepository;
import com.ats.mahindrabattery.repository.MasterUserDetailsRepository;
import com.ats.mahindrabattery.service.InfeedMissionRuntimeDetailsService;

@Service
public class InfeedMissionRuntimeDetailsServiceImpl implements InfeedMissionRuntimeDetailsService {

	MasterPositionDetailsEntity masterPositionDetailsEntity = new MasterPositionDetailsEntity();

	MasterPalletInformationEntity masterPalletInformationEntity = new MasterPalletInformationEntity();

	@Autowired
	MasterPalletInformationRepository masterPalletInformationRepositoryInstance;

	@Autowired
	private MasterPositionDetailsRepository masterPositionDetailsRepositoryInstance;

	@Autowired
	private InfeedMissionRuntimeDetailsRepository infeedMissionRuntimeDetailsRepository;

	@Autowired
	private AuditTrailDetailsRepository auditTrailDetailsRepository;
	@Autowired
	private CurrentPalletStockDetailsRepository currentPalletStockDetailsRepository;

	@Autowired
	private MasterUserDetailsRepository masterUserDetailsRepository;

	public List<InfeedMissionRuntimeDetailsEntity> getAllInfeedMissionRuntimeDetails() {
		try {
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String currentDateTime = dateFormat.format(date);

      
         List<InfeedMissionRuntimeDetailsEntity> completedList=infeedMissionRuntimeDetailsRepository.getAllInfeedMissionRuntimeDetailsStatusAndBetweenDates("COMPLETED",
			currentDateTime + " " + "00:00:00", currentDateTime + " " + "23:59:59");

			return completedList;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

	public List<InfeedMissionRuntimeDetailsEntity> findByCreatedDateTime(String cDatetime) {
		try {
			List<InfeedMissionRuntimeDetailsEntity> findBycDateTimeInfeedMissionRuntimeDetails = infeedMissionRuntimeDetailsRepository
					.findByCreatedDatetime(cDatetime);
			return findBycDateTimeInfeedMissionRuntimeDetails;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public List<InfeedMissionRuntimeDetailsEntity> findAllInfeedMissionRuntimeDetailsByDate(String startDate,
			String endDate) {
		try {
			List<InfeedMissionRuntimeDetailsEntity> findAllInfeedMissionRuntimeDetailsBetweenDates = infeedMissionRuntimeDetailsRepository
					.getAllInfeedMissionRuntimeDetailsBetweenDates(startDate, endDate);
			return findAllInfeedMissionRuntimeDetailsBetweenDates;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public List<InfeedMissionRuntimeDetailsEntity> getByDate() {
		try {
			Date date = new Date();
			String strDateFormat = "yyyy-MM-dd";
			DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
			String currentDateTime = dateFormat.format(date);
			// System.out.println("currentDateTime::"+currentDateTime);
			return infeedMissionRuntimeDetailsRepository.findInfeedfeedMissionRuntimeDetailsBetweenDates(
					currentDateTime + " " + "00:00:00", currentDateTime + " " + "23:59:59","COMPLETED");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public List<InfeedMissionRuntimeDetailsEntity> findByInfeedMissionId(int id) {
		try {
			List<InfeedMissionRuntimeDetailsEntity> findByInfeedMissionId = infeedMissionRuntimeDetailsRepository
					.findByinfeedMissionId(id);
			return findByInfeedMissionId;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public List<InfeedMissionRuntimeDetailsEntity> fetchInfeedMissionRuntimeDetails() {
		try {
			return infeedMissionRuntimeDetailsRepository.findByInfeedMissionStatus("READY", "IN_PROGRESS");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void updateInfeedMissionRuntimeDetails(InfeedMissionRuntimeDetailsEntity infeedMissionRuntimeDetailsEntity) {

		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = ft.format(dNow);
		System.out.println("currentDate :: " + currentDate);
		int positionId = infeedMissionRuntimeDetailsEntity.getPositionId();
		MasterPositionDetailsEntity findByPositionId = masterPositionDetailsRepositoryInstance
				.findByPositionId(positionId);
		MasterPalletInformationEntity findByPalletInformationId = masterPalletInformationRepositoryInstance
				.findByPalletInformationId(infeedMissionRuntimeDetailsEntity.getPalletInformationId());

		InfeedMissionRuntimeDetailsEntity findByPalletInformationId2 = infeedMissionRuntimeDetailsRepository
				.findByPalletInformationIdAndInfeedMissionStatusIn(infeedMissionRuntimeDetailsEntity.getPalletInformationId(),Arrays.asList("READY","IN_PROGRESS"));

		String infeedMissionStatus = infeedMissionRuntimeDetailsEntity.getInfeedMissionStatus();
		if (infeedMissionStatus.equals("ABORT")) {
			findByPositionId.setIsManualDispatch(0);
			findByPositionId.setPositionIsAllocated(0);
			findByPositionId.setEmptyPalletPosition(1);
			findByPositionId.setCDateTime(currentDate);
			masterPositionDetailsRepositoryInstance.save(findByPositionId);
			findByPalletInformationId.setIsInfeedMissionGenerated(0);
			findByPalletInformationId.setCdatetime(currentDate);
			infeedMissionRuntimeDetailsEntity.setInfeedMissionEndDateTime("1900-01-01 00:00:00");
			infeedMissionRuntimeDetailsEntity.setInfeedMissionStartDateTime("1900-01-01 00:00:00");
			AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String name = authentication.getName();
			System.out.println(" name :: " + name);

			auditTrailDetailsEntity.setOperatorActions(
					"Infeed mission aborted for position " + findByPalletInformationId2.getPositionName()
							+ " having pallet code " + findByPalletInformationId2.getPalletCode() + " by " + name);
			auditTrailDetailsEntity.setField("Infeed mission aborted");
//			auditTrailDetailsEntity.setAfterValue(0);
//			auditTrailDetailsEntity.setBeforeValue(0);
			auditTrailDetailsEntity.setReason("Infeed mission aborted");

			auditTrailDetailsEntity.setUsername(name);
			auditTrailDetailsEntity.setDatetimeC(currentDate);
			auditTrailDetailsRepository.save(auditTrailDetailsEntity);

			masterPalletInformationRepositoryInstance.save(findByPalletInformationId);
		} else if (infeedMissionStatus.equals("COMPLETED")) {
			Date dNow1 = new Date();
			SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentDate1 = ft1.format(dNow1);
			System.out.println("currentDate1 :: " + currentDate1);
			findByPositionId.setIsManualDispatch(0);
			findByPositionId.setPositionIsAllocated(1);
			findByPositionId.setEmptyPalletPosition(0);
			findByPositionId.setCDateTime(currentDate1);
			findByPositionId.setIsAlarmRack(0);
			masterPositionDetailsRepositoryInstance.save(findByPositionId);
			findByPalletInformationId.setIsInfeedMissionGenerated(0);
			findByPalletInformationId.setCdatetime(currentDate1);
			masterPalletInformationRepositoryInstance.save(findByPalletInformationId);
			findByPalletInformationId2.setInfeedMissionEndDateTime(currentDate1);
			System.out.println("End date :: " + findByPalletInformationId2.getInfeedMissionEndDateTime());
			infeedMissionRuntimeDetailsRepository.save(findByPalletInformationId2);
			System.out.println("findByPalletInformationId2 ::" + findByPalletInformationId2);

			List<CurrentPalletStockDetailsEntity> findByPositionId2 = currentPalletStockDetailsRepository
					.findByPositionId(positionId);
			findByPositionId2.get(0).setPalletCode(infeedMissionRuntimeDetailsEntity.getPalletCode());
			findByPositionId2.get(0).setPalletInformationId(infeedMissionRuntimeDetailsEntity.getPalletInformationId());
			findByPositionId2.get(0).setSerialNumber(infeedMissionRuntimeDetailsEntity.getSerialNumber());
			findByPositionId2.get(0).setProductVariantCode(infeedMissionRuntimeDetailsEntity.getProductVariantCode());
			findByPositionId2.get(0).setProductId(infeedMissionRuntimeDetailsEntity.getProductId());
			findByPositionId2.get(0).setProductName(infeedMissionRuntimeDetailsEntity.getProductName());
			findByPositionId2.get(0).setProductVariantId(infeedMissionRuntimeDetailsEntity.getProductVariantId());
			findByPositionId2.get(0).setProductVariantName(infeedMissionRuntimeDetailsEntity.getProductVariantName());
			findByPositionId2.get(0).setPalletStatusId(infeedMissionRuntimeDetailsEntity.getPalletStatusId());
			findByPositionId2.get(0).setPalletStatusname(infeedMissionRuntimeDetailsEntity.getPalletStatusName());
			findByPositionId2.get(0).setAgeingDays(0);
			findByPositionId2.get(0).setQuantity(infeedMissionRuntimeDetailsEntity.getQuantity());
			findByPositionId2.get(0).setQualityStatus("OK");
			findByPositionId2.get(0).setBatchNumber(infeedMissionRuntimeDetailsEntity.getBatchNumber());
			findByPositionId2.get(0).setModelNumber("NA");
			findByPositionId2.get(0).setLocation("MAHINDRA");
			findByPositionId2.get(0).setPositionId(infeedMissionRuntimeDetailsEntity.getPositionId());
			findByPositionId2.get(0).setPositionName(infeedMissionRuntimeDetailsEntity.getPositionName());
			findByPositionId2.get(0)
					.setPositionNumberInRack(infeedMissionRuntimeDetailsEntity.getPositionNumberInRack());
			findByPositionId2.get(0).setRackId(infeedMissionRuntimeDetailsEntity.getRackId());
			findByPositionId2.get(0).setRackName(infeedMissionRuntimeDetailsEntity.getRackName());
			findByPositionId2.get(0).setRackSide(infeedMissionRuntimeDetailsEntity.getRackSide());
			findByPositionId2.get(0).setRackColumn(infeedMissionRuntimeDetailsEntity.getRackColumn());
			findByPositionId2.get(0).setFloorId(infeedMissionRuntimeDetailsEntity.getFloorId());
			findByPositionId2.get(0).setFloorName(infeedMissionRuntimeDetailsEntity.getFloorName());
			findByPositionId2.get(0).setAreaId(infeedMissionRuntimeDetailsEntity.getAreaId());
			findByPositionId2.get(0).setAreaName(infeedMissionRuntimeDetailsEntity.getAreaName());
			findByPositionId2.get(0).setLoadDatetime(infeedMissionRuntimeDetailsEntity.getLoadDateTime());
			findByPositionId2.get(0).setIsOutfeedMissionGenerated(0);
			findByPositionId2.get(0).setIsInfeedMissionGenerated(0);

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String name = authentication.getName();
			List<MasterUserDetailsEntity> findByuserName = masterUserDetailsRepository.findByuserName(name);
			int userId = findByuserName.get(0).getUserId();
			findByPositionId2.get(0).setUserId(userId);
			findByPositionId2.get(0).setUserName(name);
			findByPositionId2.get(0).setVendorCode(infeedMissionRuntimeDetailsEntity.getVendorCode());
			findByPositionId2.get(0)
					.setPartIdentificationCode(infeedMissionRuntimeDetailsEntity.getPartIdentificationCode());
			findByPositionId2.get(0).setMfgDate(infeedMissionRuntimeDetailsEntity.getMfgDate());
			findByPositionId2.get(0).setMfgShift(infeedMissionRuntimeDetailsEntity.getMfgShift());
			findByPositionId2.get(0).setIsAlarmRack(0);
			currentPalletStockDetailsRepository.save(findByPositionId2.get(0));

			AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
//			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//			String name = authentication.getName();
			System.out.println(" name :: " + name);
			auditTrailDetailsEntity.setOperatorActions(
					"Infeed mission completed for position " + findByPalletInformationId2.getPositionName()
							+ " having pallet code " + findByPalletInformationId2.getPalletCode() + " by " + name);
			auditTrailDetailsEntity.setField("Infeed mission completed");
//			auditTrailDetailsEntity.setAfterValue(0);
//			auditTrailDetailsEntity.setBeforeValue(0);
			auditTrailDetailsEntity.setReason("Infeed mission completed");

			auditTrailDetailsEntity.setUsername(name);
			auditTrailDetailsEntity.setDatetimeC(currentDate);
			auditTrailDetailsRepository.save(auditTrailDetailsEntity);
		}
		System.out.println("masterPositionDetailsEntity" + findByPositionId);
		infeedMissionRuntimeDetailsRepository.save(infeedMissionRuntimeDetailsEntity);

	}



	
	public List<InfeedMissionRuntimeDetailsEntity> findByAllFilters(
	        String productName, 
	        String palletStatus, 
	        String infeedMissionStartCdatetime, 
	        String infeedMissionEndCdatetime) {

	    List<String> filterList = new ArrayList<>();
	    List<InfeedMissionRuntimeDetailsEntity> list;

	    if (!productName.equalsIgnoreCase("NA")) {
	        filterList.add("productName");
	    }
	    if (!palletStatus.equalsIgnoreCase("NA")) {
	        filterList.add("palletStatus");
	    }

	    Predicate<InfeedMissionRuntimeDetailsEntity> productNamePred = 
	        data -> data.getProductName().equalsIgnoreCase(productName);
	    Predicate<InfeedMissionRuntimeDetailsEntity> palletStatusPred = 
	        data -> data.getPalletStatusName().equalsIgnoreCase(palletStatus);

	    if (!infeedMissionStartCdatetime.equalsIgnoreCase("NA") && !infeedMissionEndCdatetime.equalsIgnoreCase("NA")) {
	        String startDateTime = infeedMissionStartCdatetime.replace("T", " ");
	        String endDateTime = infeedMissionEndCdatetime.replace("T", " ");

	        list = infeedMissionRuntimeDetailsRepository
	                .findInfeedfeedMissionRuntimeDetailsBetweenDates(startDateTime, endDateTime,"COMPLETED");
	    } else {
	        Date dNow = new Date();
	        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
	        String date = ft.format(dNow);
	        
	        list = infeedMissionRuntimeDetailsRepository
	                .findInfeedfeedMissionRuntimeDetailsBetweenDates(date + " 00:00:00", date + " 23:59:59","COMPLETED");
	    }

	    if (!filterList.isEmpty()) {
	        for (String filter : filterList) {
	            if (filter.equalsIgnoreCase("productName")) {
	                list = list.stream().filter(productNamePred).collect(Collectors.toList());
	            } else if (filter.equalsIgnoreCase("palletStatus")) { // Updated condition
	                list = list.stream().filter(palletStatusPred).collect(Collectors.toList());
	            }
	        }
	    }

	    if (filterList.isEmpty() && list.isEmpty()) {
	        return null;
	    }

	    return list;
	}

}
