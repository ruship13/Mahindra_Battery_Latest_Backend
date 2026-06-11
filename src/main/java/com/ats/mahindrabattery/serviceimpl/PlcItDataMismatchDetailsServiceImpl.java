package com.ats.mahindrabattery.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ats.mahindrabattery.entity.AuditTrailDetailsEntity;
import com.ats.mahindrabattery.entity.CurrentPalletStockDetailsEntity;
import com.ats.mahindrabattery.entity.InfeedMissionRuntimeDetailsEntity;
import com.ats.mahindrabattery.entity.MasterPalletInformationEntity;
import com.ats.mahindrabattery.entity.MasterPositionDetailsEntity;
import com.ats.mahindrabattery.entity.MasterProductDetailsEntity;
import com.ats.mahindrabattery.entity.MasterProductVariantDetailsEntity;
import com.ats.mahindrabattery.entity.MasterUserDetailsEntity;
import com.ats.mahindrabattery.entity.PLCITDataMismatchDetailsEntity;
import com.ats.mahindrabattery.repository.AuditTrailDetailsRepository;
import com.ats.mahindrabattery.repository.CurrentPalletStockDetailsRepository;
import com.ats.mahindrabattery.repository.MasterPalletInformationRepository;
import com.ats.mahindrabattery.repository.MasterPositionDetailsRepository;
import com.ats.mahindrabattery.repository.MasterProductDetailsRepository;
import com.ats.mahindrabattery.repository.MasterProductVariantDetailsRepository;
import com.ats.mahindrabattery.repository.MasterUserDetailsRepository;
import com.ats.mahindrabattery.repository.PlcItDataMismatchDetailsRepository;
import com.ats.mahindrabattery.response.ResponseHandler;
import com.ats.mahindrabattery.service.PLCITDataMismatchDetailsService;
@Service
public class PlcItDataMismatchDetailsServiceImpl implements PLCITDataMismatchDetailsService {

	@Autowired
	private PlcItDataMismatchDetailsRepository plcItDataMismatchDetailsRepository;

	@Autowired
	private CurrentPalletStockDetailsRepository currentPalletStockDetailsRepository;

	@Autowired
	private MasterPositionDetailsRepository masterPositionDetailsRepository;
	
	
	@Autowired
	private AuditTrailDetailsRepository auditTrailDetailsRepository;
	
	@Autowired
	private MasterProductDetailsRepository masterProductDetailsRepository;
	
	
	@Autowired
	private MasterProductVariantDetailsRepository masterProductVariantDetailsRepository;
	
	@Autowired
	private MasterPalletInformationRepository masterPalletInformationDetailsRepositoryInstance;
	
	
	@Autowired
	private MasterUserDetailsRepository masterUserDetailsRepository;

	@Override
	public ResponseEntity<Object> addPlcItData(PLCITDataMismatchDetailsEntity plcitDataMismatchDetailsEntity) {

		List<CurrentPalletStockDetailsEntity> findByPositionId = currentPalletStockDetailsRepository
				.findByPositionId(plcitDataMismatchDetailsEntity.getPositionId());

		if (!findByPositionId.isEmpty()) {
			findByPositionId.get(0).setPalletCode(plcitDataMismatchDetailsEntity.getPalletCode());
			findByPositionId.get(0).setPalletStatusId(plcitDataMismatchDetailsEntity.getPlcItId());
			currentPalletStockDetailsRepository.save(findByPositionId.get(0));
			plcitDataMismatchDetailsEntity.setIsDataUpdated(1);
			plcItDataMismatchDetailsRepository.save(plcitDataMismatchDetailsEntity);
			MasterPositionDetailsEntity findByPositionId2 = masterPositionDetailsRepository
					.findByPositionId(plcitDataMismatchDetailsEntity.getPositionId());
			findByPositionId2.setPositionIsAllocated(2);
			masterPositionDetailsRepository.save(findByPositionId2);
			return ResponseHandler.generateResponse("Data Updated", HttpStatus.OK, findByPositionId2);

		}

		return null;
	}

	@Override
	public List<PLCITDataMismatchDetailsEntity> findByIsDataUpdated() {
		List<PLCITDataMismatchDetailsEntity> findByIsDataUpdated = plcItDataMismatchDetailsRepository.findByIsDataUpdated(0);

		return findByIsDataUpdated;
	}
	
	
	
	
	
	
	
	
	public ResponseEntity<Object> addOrUpdateMasterPalletInformation(
			CurrentPalletStockDetailsEntity currentPalletStockDetailsEntity) {
		List<CurrentPalletStockDetailsEntity> findByPositionId = null;
		try {
			int id = 1;
			Date dNow = new Date();
			SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentDate = ft.format(dNow);

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String name = authentication.getName();
			System.out.println("authentication:" + name);
			int productVariantIsActive = 0;
			List<MasterProductVariantDetailsEntity> findByProductVariantCode3 = masterProductVariantDetailsRepository
					.findByProductVariantCode(currentPalletStockDetailsEntity.getProductVariantCode());
			if (!findByProductVariantCode3.isEmpty()) {
				productVariantIsActive = findByProductVariantCode3.get(0).getProductVariantIsActive();
			}
			// productVariantIsActive =
			// findByProductVariantCode3.get(0).getProductVariantIsActive();

			if (productVariantIsActive == 0 && !currentPalletStockDetailsEntity.getProductVariantCode().equals("NA")) {
				return ResponseHandler.generateResponse("Part Number is not Active", HttpStatus.ALREADY_REPORTED, null);
			}

			List<MasterProductVariantDetailsEntity> findByproductVariantCode = masterProductVariantDetailsRepository
					.findByproductVariantCodeAndProductVariantIsActiveAndProductVariantIsDeleted(
							currentPalletStockDetailsEntity.getProductVariantCode(), 1, 0);

			int positionId = currentPalletStockDetailsEntity.getPositionId();
			List<CurrentPalletStockDetailsEntity> findByPositionId3 = currentPalletStockDetailsRepository
					.findByPositionId(positionId + 1);
			String productVariantCode = findByPositionId3.get(0).getProductVariantCode();

			String productName = findByPositionId3.get(0).getProductName();
			String productName2 = currentPalletStockDetailsEntity.getProductName();

//			if (!"NA".equals(currentPalletStockDetailsEntity.getProductVariantCode()) && findByproductVariantCode == null) {
//				return ResponseHandler.generateResponse("Product variant code not available", HttpStatus.NO_CONTENT,
//						productVariantCode);
//			}

			MasterPositionDetailsEntity findByPositionIdAndPositionIsActive = masterPositionDetailsRepository
					.findByPositionIdAndPositionIsActive(positionId + 1, 0);
			if (findByPositionIdAndPositionIsActive != null && positionId % 2 != 0) {
				if (!Objects.equals(productName, productName2)) {
					return ResponseHandler.generateResponse("Product name does not match previous product name",
							HttpStatus.CREATED, productVariantCode);
				}
				if (findByproductVariantCode.isEmpty()) {
					findByPositionId = currentPalletStockDetailsRepository
							.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
					findByPositionId.get(0).setPalletCode(currentPalletStockDetailsEntity.getPalletCode());

					if (!"NA".equals(currentPalletStockDetailsEntity.getProductVariantCode())) {
						System.out.println("Product variant code not available");
						return ResponseHandler.generateResponse("Product variant code not available",
								HttpStatus.IM_USED, productVariantCode);
					}

					findByPositionId.get(0)
							.setProductVariantCode(currentPalletStockDetailsEntity.getProductVariantCode());
					findByPositionId.get(0).setProductVariantName("NA");
					findByPositionId.get(0).setProductVariantId(0);
//					findByPositionId.get(0).setProductId(0);
					findByPositionId.get(0).setProductName(currentPalletStockDetailsEntity.getProductName());
					MasterProductDetailsEntity findByProductName = masterProductDetailsRepository
							.findByProductName(currentPalletStockDetailsEntity.getProductName());

					findByPositionId.get(0).setProductId(findByProductName.getProductId());
					findByPositionId.get(0).setBatchNumber(currentPalletStockDetailsEntity.getBatchNumber());
					findByPositionId.get(0).setQuantity(currentPalletStockDetailsEntity.getQuantity());
					findByPositionId.get(0).setPalletStatusId(3);
					findByPositionId.get(0).setPalletStatusname("EMPTY");
					findByPositionId.get(0).setSerialNumber(0);
					findByPositionId.get(0).setAgeingDays(0);
					findByPositionId.get(0).setQualityStatus("NA");
					findByPositionId.get(0).setLocation("NA");
					findByPositionId.get(0).setModelNumber("NA");
					findByPositionId.get(0).setIsInfeedMissionGenerated(0);
					findByPositionId.get(0).setIsOutfeedMissionGenerated(0);
					findByPositionId.get(0).setUserName(name);
					List<MasterUserDetailsEntity> findByuserName = masterUserDetailsRepository.findByuserName(name);
					findByPositionId.get(0).setUserId(findByuserName.get(0).getUserId());
					findByPositionId.get(0).setLoadDatetime(currentDate);

					List<MasterPalletInformationEntity> findByPalletCode3 = masterPalletInformationDetailsRepositoryInstance
							.findByPalletCode(currentPalletStockDetailsEntity.getPalletCode());
					if (findByPalletCode3.isEmpty()) {
						MasterPalletInformationEntity findTopByOrderByPalletInformationIdDesc = masterPalletInformationDetailsRepositoryInstance
								.findTopByOrderByPalletInformationIdDesc();
						if (findTopByOrderByPalletInformationIdDesc == null) {
							id = 1;
						} else {
							id = findTopByOrderByPalletInformationIdDesc.getPalletInformationId() + 1;
						}
						MasterPalletInformationEntity masterPalletInformationEntity = new MasterPalletInformationEntity(
								id, currentPalletStockDetailsEntity.getPalletCode(), "NA",
								findByPositionId.get(0).getProductId(), findByPositionId.get(0).getProductVariantId(),
								findByPositionId.get(0).getQuantity(), findByPositionId.get(0).getQualityStatus(),
								findByPositionId.get(0).getPalletStatusId(),
								findByPositionId.get(0).getPalletStatusname(),
								findByPositionId.get(0).getIsInfeedMissionGenerated(),
								findByPositionId.get(0).getIsOutfeedMissionGenerated(), 0, 0, currentDate,
								findByPositionId.get(0).getSerialNumber(),
								findByPositionId.get(0).getProductVariantCode(),
								findByPositionId.get(0).getProductName(),
								findByPositionId.get(0).getProductVariantName(), 0,
								findByPositionId.get(0).getBatchNumber(), findByPositionId.get(0).getModelNumber(),
								findByPositionId.get(0).getLocation(), "NA", "NA", "9999-12-31 00:00:00", "NA",
								findByPositionId.get(0).getLoadDatetime());
						masterPalletInformationDetailsRepositoryInstance.save(masterPalletInformationEntity);

						findByPositionId.get(0)
								.setPalletInformationId(masterPalletInformationEntity.getPalletInformationId());
						currentPalletStockDetailsRepository.save(findByPositionId.get(0));
						
						

					} else {
						findByPalletCode3.get(0).setProductVariantCode(findByPositionId.get(0).getProductVariantCode());
						findByPalletCode3.get(0).setWmsTransferMissionOrderId("NA");
						findByPalletCode3.get(0).setProductId(findByPositionId.get(0).getProductId());
						findByPalletCode3.get(0).setProductVariantId(findByPositionId.get(0).getProductVariantId());
						findByPalletCode3.get(0).setQuantity(findByPositionId.get(0).getQuantity());
						findByPalletCode3.get(0).setQualityStatus(findByPositionId.get(0).getQualityStatus());
						findByPalletCode3.get(0).setPalletStatusId(findByPositionId.get(0).getPalletStatusId());
						findByPalletCode3.get(0).setPalletStatusName(findByPositionId.get(0).getPalletStatusname());
						findByPalletCode3.get(0)
								.setIsInfeedMissionGenerated(findByPositionId.get(0).getIsInfeedMissionGenerated());
						findByPalletCode3.get(0)
								.setIsOutfeedMissionGenerated(findByPositionId.get(0).getIsOutfeedMissionGenerated());
						findByPalletCode3.get(0).setIsTransferManagementMissionGenerated(0);
						findByPalletCode3.get(0).setStationWorkdone(0);
						findByPalletCode3.get(0).setCdatetime(currentDate);
						findByPalletCode3.get(0).setSerialNumber(findByPositionId.get(0).getSerialNumber());
						findByPalletCode3.get(0).setProductName(findByPositionId.get(0).getProductName());
						findByPalletCode3.get(0).setProductVariantName(findByPositionId.get(0).getProductVariantName());
						findByPalletCode3.get(0).setPalletInformationIsDeleted(0);
						findByPalletCode3.get(0).setBatchNumber(findByPositionId.get(0).getBatchNumber());
						findByPalletCode3.get(0).setModelNumber(findByPositionId.get(0).getModelNumber());
						findByPalletCode3.get(0).setLocation(findByPositionId.get(0).getLocation());
						findByPalletCode3.get(0).setVendorCode("NA");
						findByPalletCode3.get(0).setPartIdentificationCode("NA");
						findByPalletCode3.get(0).setMfgDate("NA");
						findByPalletCode3.get(0).setMfgShift("NA");
						findByPalletCode3.get(0).setLoadDateTime(currentDate);

						masterPalletInformationDetailsRepositoryInstance.save(findByPalletCode3.get(0));

						findByPositionId.get(0)
								.setPalletInformationId(findByPalletCode3.get(0).getPalletInformationId());
						currentPalletStockDetailsRepository.save(findByPositionId.get(0));

						masterPositionDetailsRepository
								.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
						
						
						PLCITDataMismatchDetailsEntity findByPositionId4 = plcItDataMismatchDetailsRepository.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
						findByPositionId4.setIsDataUpdated(1);
						findByPositionId4.setCDateTime(currentDate);
						plcItDataMismatchDetailsRepository.save(findByPositionId4);

						masterPositionDetailsRepository.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
						AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
						auditTrailDetailsEntity.setOperatorActions(
								"Data added by " + name + " for position  " + currentPalletStockDetailsEntity.getPositionName()
										+ " having pallet code " + currentPalletStockDetailsEntity.getPalletCode() +" due to mismatch");
						auditTrailDetailsEntity.setField("Data added");
						auditTrailDetailsEntity.setAfterValue(0);
						auditTrailDetailsEntity.setBeforeValue(0);
						auditTrailDetailsEntity.setReason("Pallet added");

						System.out.println(" name :: " + name);
						auditTrailDetailsEntity.setUsername(name);
						auditTrailDetailsEntity.setDatetimeC(currentDate);
						auditTrailDetailsRepository.save(auditTrailDetailsEntity);
					}

				} else {
//					List<MasterProductVariantDetailsEntity> findByproductVariantCodeAndProductVariantIsActiveAndProductVariantIsDeleted = masterProductVariantDetailsRepository
//							.findByproductVariantCodeAndProductVariantIsActiveAndProductVariantIsDeleted(
//									productVariantCode, 1, 0);
//					String productVariantCode2 = findByproductVariantCodeAndProductVariantIsActiveAndProductVariantIsDeleted
//							.get(0).getProductVariantCode();
//					if (findByproductVariantCode.get(0).getProductVariantCode().equalsIgnoreCase(productVariantCode2))
					findByPositionId = currentPalletStockDetailsRepository
							.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
					findByPositionId.get(0).setPalletCode(currentPalletStockDetailsEntity.getPalletCode());
					findByPositionId.get(0)
							.setProductVariantCode(currentPalletStockDetailsEntity.getProductVariantCode());
					findByPositionId.get(0).setBatchNumber(currentPalletStockDetailsEntity.getBatchNumber());
					findByPositionId.get(0).setQuantity(currentPalletStockDetailsEntity.getQuantity());
					findByPositionId.get(0).setPalletStatusId(1);
					findByPositionId.get(0).setPalletStatusname("FULL");
					findByPositionId.get(0).setSerialNumber(0);
					findByPositionId.get(0).setAgeingDays(0);
					findByPositionId.get(0).setQualityStatus(currentPalletStockDetailsEntity.getQualityStatus());
					findByPositionId.get(0).setLocation("NA");
					findByPositionId.get(0).setModelNumber("NA");
					findByPositionId.get(0).setIsInfeedMissionGenerated(0);
					findByPositionId.get(0).setIsOutfeedMissionGenerated(0);
					findByPositionId.get(0).setUserName(name);
					List<MasterUserDetailsEntity> findByuserName = masterUserDetailsRepository.findByuserName(name);
					findByPositionId.get(0).setUserId(findByuserName.get(0).getUserId());
					findByPositionId.get(0).setLoadDatetime(currentDate);
					findByPositionId.get(0).setSerialNumber(currentPalletStockDetailsEntity.getSerialNumber());
					List<MasterProductVariantDetailsEntity> findByProductVariantCode2 = masterProductVariantDetailsRepository
							.findByProductVariantCode(currentPalletStockDetailsEntity.getProductVariantCode());
					findByPositionId.get(0)
							.setProductVariantName(findByProductVariantCode2.get(0).getProductVariantname());
					findByPositionId.get(0).setProductId(findByProductVariantCode2.get(0).getProductId());
					findByPositionId.get(0).setProductVariantId(findByProductVariantCode2.get(0).getProductVariantId());
					findByPositionId.get(0).setProductName(findByProductVariantCode2.get(0).getProductName());

					List<MasterPalletInformationEntity> findByPalletCode3 = masterPalletInformationDetailsRepositoryInstance
							.findByPalletCode(currentPalletStockDetailsEntity.getPalletCode());
					if (findByPalletCode3.isEmpty()) {
						MasterPalletInformationEntity findTopByOrderByPalletInformationIdDesc = masterPalletInformationDetailsRepositoryInstance
								.findTopByOrderByPalletInformationIdDesc();
						if (findTopByOrderByPalletInformationIdDesc == null) {
							id = 1;
						} else {
							id = findTopByOrderByPalletInformationIdDesc.getPalletInformationId() + 1;
						}
						MasterPalletInformationEntity masterPalletInformationEntity = new MasterPalletInformationEntity(
								id, currentPalletStockDetailsEntity.getPalletCode(), "NA",
								findByPositionId.get(0).getProductId(), findByPositionId.get(0).getProductVariantId(),
								findByPositionId.get(0).getQuantity(), findByPositionId.get(0).getQualityStatus(),
								findByPositionId.get(0).getPalletStatusId(),
								findByPositionId.get(0).getPalletStatusname(),
								findByPositionId.get(0).getIsInfeedMissionGenerated(),
								findByPositionId.get(0).getIsOutfeedMissionGenerated(), 0, 0, currentDate,
								findByPositionId.get(0).getSerialNumber(),
								findByPositionId.get(0).getProductVariantCode(),
								findByPositionId.get(0).getProductName(),
								findByPositionId.get(0).getProductVariantName(), 0,
								findByPositionId.get(0).getBatchNumber(), findByPositionId.get(0).getModelNumber(),
								findByPositionId.get(0).getLocation(), "NA", "NA", "9999-12-31 00:00:00", "NA",
								findByPositionId.get(0).getLoadDatetime());
						masterPalletInformationDetailsRepositoryInstance.save(masterPalletInformationEntity);

						findByPositionId.get(0)
								.setPalletInformationId(masterPalletInformationEntity.getPalletInformationId());
						currentPalletStockDetailsRepository.save(findByPositionId.get(0));

					} else {
						findByPalletCode3.get(0).setProductVariantCode(findByPositionId.get(0).getProductVariantCode());
						findByPalletCode3.get(0).setWmsTransferMissionOrderId("NA");
						findByPalletCode3.get(0).setProductId(findByPositionId.get(0).getProductId());
						findByPalletCode3.get(0).setProductVariantId(findByPositionId.get(0).getProductVariantId());
						findByPalletCode3.get(0).setQuantity(findByPositionId.get(0).getQuantity());
						findByPalletCode3.get(0).setQualityStatus(findByPositionId.get(0).getQualityStatus());
						findByPalletCode3.get(0).setPalletStatusId(findByPositionId.get(0).getPalletStatusId());
						findByPalletCode3.get(0).setPalletStatusName(findByPositionId.get(0).getPalletStatusname());
						findByPalletCode3.get(0)
								.setIsInfeedMissionGenerated(findByPositionId.get(0).getIsInfeedMissionGenerated());
						findByPalletCode3.get(0)
								.setIsOutfeedMissionGenerated(findByPositionId.get(0).getIsOutfeedMissionGenerated());
						findByPalletCode3.get(0).setIsTransferManagementMissionGenerated(0);
						findByPalletCode3.get(0).setStationWorkdone(0);
						findByPalletCode3.get(0).setCdatetime(currentDate);
						findByPalletCode3.get(0).setSerialNumber(findByPositionId.get(0).getSerialNumber());
						findByPalletCode3.get(0).setProductName(findByPositionId.get(0).getProductName());
						findByPalletCode3.get(0).setProductVariantName(findByPositionId.get(0).getProductVariantName());
						findByPalletCode3.get(0).setPalletInformationIsDeleted(0);
						findByPalletCode3.get(0).setBatchNumber(findByPositionId.get(0).getBatchNumber());
						findByPalletCode3.get(0).setModelNumber(findByPositionId.get(0).getModelNumber());
						findByPalletCode3.get(0).setSerialNumber(findByPositionId.get(0).getSerialNumber());
						findByPalletCode3.get(0).setLocation(findByPositionId.get(0).getLocation());
						findByPalletCode3.get(0).setVendorCode("NA");
						findByPalletCode3.get(0).setPartIdentificationCode("NA");
						findByPalletCode3.get(0).setMfgDate("NA");
						findByPalletCode3.get(0).setMfgShift("NA");
						findByPalletCode3.get(0).setLoadDateTime(currentDate);
						masterPalletInformationDetailsRepositoryInstance.save(findByPalletCode3.get(0));

						findByPositionId.get(0)
								.setPalletInformationId(findByPalletCode3.get(0).getPalletInformationId());
						currentPalletStockDetailsRepository.save(findByPositionId.get(0));

					}

				}
				MasterPositionDetailsEntity findByPositionId2 = masterPositionDetailsRepository
						.findByPositionId(findByPositionId.get(0).getPositionId());
				findByPositionId2.setEmptyPalletPosition(0);
//				findByPositionId2.setPositionIsAllocated(1);
				findByPositionId2.setPositionIsDeleted(0);
				findByPositionId2.setIsManualDispatch(0);
				findByPositionId2.setPositionIsAllocated(2);
				if (currentPalletStockDetailsEntity.getProductVariantCode().equalsIgnoreCase("NA")
						&& currentPalletStockDetailsEntity.getQuantity() == 0) {
					findByPositionId2.setIsMaterialLoaded(0);
				} else {
					findByPositionId2.setIsMaterialLoaded(1);
				}
				masterPositionDetailsRepository.save(findByPositionId2);

				masterPositionDetailsRepository.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
				
				
				PLCITDataMismatchDetailsEntity findByPositionId4 = plcItDataMismatchDetailsRepository.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
				findByPositionId4.setIsDataUpdated(1);
				findByPositionId4.setCDateTime(currentDate);
				plcItDataMismatchDetailsRepository.save(findByPositionId4);

				masterPositionDetailsRepository.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
				auditTrailDetailsEntity.setOperatorActions(
						"Data added by " + name + " for position  " + currentPalletStockDetailsEntity.getPositionName()
								+ " having pallet code " + currentPalletStockDetailsEntity.getPalletCode() +" due to mismatch");
				auditTrailDetailsEntity.setField("Data added");
				auditTrailDetailsEntity.setAfterValue(0);
				auditTrailDetailsEntity.setBeforeValue(0);
				auditTrailDetailsEntity.setReason("Pallet added");

				System.out.println(" name :: " + name);
				auditTrailDetailsEntity.setUsername(name);
				auditTrailDetailsEntity.setDatetimeC(currentDate);
				auditTrailDetailsRepository.save(auditTrailDetailsEntity);
				return ResponseHandler.generateResponse("Pallet added successfully", HttpStatus.OK,
						auditTrailDetailsEntity);
			}
			if (positionId % 2 == 0) {
				if (findByproductVariantCode.isEmpty()) {
					findByPositionId = currentPalletStockDetailsRepository
							.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
					findByPositionId.get(0).setPalletCode(currentPalletStockDetailsEntity.getPalletCode());

					if (!"NA".equals(currentPalletStockDetailsEntity.getProductVariantCode())) {
						System.out.println("Product variant code not available");
						return ResponseHandler.generateResponse("Product variant code not available",
								HttpStatus.IM_USED, productVariantCode);
					}

					findByPositionId.get(0)
							.setProductVariantCode(currentPalletStockDetailsEntity.getProductVariantCode());
					findByPositionId.get(0).setProductVariantName("NA");
					findByPositionId.get(0).setProductVariantId(0);
//					findByPositionId.get(0).setProductId(0);
					findByPositionId.get(0).setProductName(currentPalletStockDetailsEntity.getProductName());
					MasterProductDetailsEntity findByProductName = masterProductDetailsRepository
							.findByProductName(currentPalletStockDetailsEntity.getProductName());

					findByPositionId.get(0).setProductId(findByProductName.getProductId());
					findByPositionId.get(0).setBatchNumber(currentPalletStockDetailsEntity.getBatchNumber());
					findByPositionId.get(0).setQuantity(currentPalletStockDetailsEntity.getQuantity());
					findByPositionId.get(0).setQualityStatus(currentPalletStockDetailsEntity.getQualityStatus());
					findByPositionId.get(0).setPalletStatusId(3);
					findByPositionId.get(0).setPalletStatusname("EMPTY");
					findByPositionId.get(0).setSerialNumber(currentPalletStockDetailsEntity.getSerialNumber());
					findByPositionId.get(0).setAgeingDays(0);
//					findByPositionId.get(0).setQualityStatus("NA");
					findByPositionId.get(0).setLocation("NA");
					findByPositionId.get(0).setModelNumber("NA");
					findByPositionId.get(0).setIsInfeedMissionGenerated(0);
					findByPositionId.get(0).setIsOutfeedMissionGenerated(0);
					findByPositionId.get(0).setUserName(name);
					List<MasterUserDetailsEntity> findByuserName = masterUserDetailsRepository.findByuserName(name);
					findByPositionId.get(0).setUserId(findByuserName.get(0).getUserId());
					findByPositionId.get(0).setLoadDatetime(currentDate);

					List<MasterPalletInformationEntity> findByPalletCode3 = masterPalletInformationDetailsRepositoryInstance
							.findByPalletCode(currentPalletStockDetailsEntity.getPalletCode());
					if (findByPalletCode3.isEmpty()) {
						MasterPalletInformationEntity findTopByOrderByPalletInformationIdDesc = masterPalletInformationDetailsRepositoryInstance
								.findTopByOrderByPalletInformationIdDesc();
						if (findTopByOrderByPalletInformationIdDesc == null) {
							id = 1;
						} else {
							id = findTopByOrderByPalletInformationIdDesc.getPalletInformationId() + 1;
						}
						MasterPalletInformationEntity masterPalletInformationEntity = new MasterPalletInformationEntity(
								id, currentPalletStockDetailsEntity.getPalletCode(), "NA",
								findByPositionId.get(0).getProductId(), findByPositionId.get(0).getProductVariantId(),
								findByPositionId.get(0).getQuantity(), findByPositionId.get(0).getQualityStatus(),
								findByPositionId.get(0).getPalletStatusId(),
								findByPositionId.get(0).getPalletStatusname(),
								findByPositionId.get(0).getIsInfeedMissionGenerated(),
								findByPositionId.get(0).getIsOutfeedMissionGenerated(), 0, 0, currentDate,
								findByPositionId.get(0).getSerialNumber(),
								findByPositionId.get(0).getProductVariantCode(),
								findByPositionId.get(0).getProductName(),
								findByPositionId.get(0).getProductVariantName(), 0,
								findByPositionId.get(0).getBatchNumber(), findByPositionId.get(0).getModelNumber(),
								findByPositionId.get(0).getLocation(), "NA", "NA", "9999-12-31 00:00:00", "NA",
								findByPositionId.get(0).getLoadDatetime());
						masterPalletInformationDetailsRepositoryInstance.save(masterPalletInformationEntity);

						findByPositionId.get(0)
								.setPalletInformationId(masterPalletInformationEntity.getPalletInformationId());
						currentPalletStockDetailsRepository.save(findByPositionId.get(0));

					} else {
						findByPalletCode3.get(0).setProductVariantCode(findByPositionId.get(0).getProductVariantCode());
						findByPalletCode3.get(0).setWmsTransferMissionOrderId("NA");
						findByPalletCode3.get(0).setProductId(findByPositionId.get(0).getProductId());
						findByPalletCode3.get(0).setProductVariantId(findByPositionId.get(0).getProductVariantId());
						findByPalletCode3.get(0).setQuantity(findByPositionId.get(0).getQuantity());
						findByPalletCode3.get(0).setQualityStatus(findByPositionId.get(0).getQualityStatus());
						findByPalletCode3.get(0).setPalletStatusId(findByPositionId.get(0).getPalletStatusId());
						findByPalletCode3.get(0).setPalletStatusName(findByPositionId.get(0).getPalletStatusname());
						findByPalletCode3.get(0)
								.setIsInfeedMissionGenerated(findByPositionId.get(0).getIsInfeedMissionGenerated());
						findByPalletCode3.get(0)
								.setIsOutfeedMissionGenerated(findByPositionId.get(0).getIsOutfeedMissionGenerated());
						findByPalletCode3.get(0).setIsTransferManagementMissionGenerated(0);
						findByPalletCode3.get(0).setStationWorkdone(0);
						findByPalletCode3.get(0).setCdatetime(currentDate);
						findByPalletCode3.get(0).setSerialNumber(findByPositionId.get(0).getSerialNumber());
						findByPalletCode3.get(0).setProductName(findByPositionId.get(0).getProductName());
						findByPalletCode3.get(0).setProductVariantName(findByPositionId.get(0).getProductVariantName());
						findByPalletCode3.get(0).setPalletInformationIsDeleted(0);
						findByPalletCode3.get(0).setBatchNumber(findByPositionId.get(0).getBatchNumber());
						findByPalletCode3.get(0).setModelNumber(findByPositionId.get(0).getModelNumber());
						findByPalletCode3.get(0).setLocation(findByPositionId.get(0).getLocation());
						findByPalletCode3.get(0).setVendorCode("NA");
						findByPalletCode3.get(0).setPartIdentificationCode("NA");
						findByPalletCode3.get(0).setMfgDate("NA");
						findByPalletCode3.get(0).setMfgShift("NA");
						findByPalletCode3.get(0).setLoadDateTime(currentDate);

						masterPalletInformationDetailsRepositoryInstance.save(findByPalletCode3.get(0));

						findByPositionId.get(0)
								.setPalletInformationId(findByPalletCode3.get(0).getPalletInformationId());
						currentPalletStockDetailsRepository.save(findByPositionId.get(0));

						masterPositionDetailsRepository
								.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
						
						
						PLCITDataMismatchDetailsEntity findByPositionId4 = plcItDataMismatchDetailsRepository.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
						findByPositionId4.setIsDataUpdated(1);
						findByPositionId4.setCDateTime(currentDate);
						plcItDataMismatchDetailsRepository.save(findByPositionId4);

						masterPositionDetailsRepository.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
						AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
						auditTrailDetailsEntity.setOperatorActions(
								"Data added by " + name + " for position  " + currentPalletStockDetailsEntity.getPositionName()
										+ " having pallet code " + currentPalletStockDetailsEntity.getPalletCode() +" due to mismatch");
						auditTrailDetailsEntity.setField("Data added");
						auditTrailDetailsEntity.setAfterValue(0);
						auditTrailDetailsEntity.setBeforeValue(0);
						auditTrailDetailsEntity.setReason("Pallet added");

						System.out.println(" name :: " + name);
						auditTrailDetailsEntity.setUsername(name);
						auditTrailDetailsEntity.setDatetimeC(currentDate);
						auditTrailDetailsRepository.save(auditTrailDetailsEntity);

					}

				} else {
//					List<MasterProductVariantDetailsEntity> findByproductVariantCodeAndProductVariantIsActiveAndProductVariantIsDeleted = masterProductVariantDetailsRepository
//							.findByproductVariantCodeAndProductVariantIsActiveAndProductVariantIsDeleted(
//									productVariantCode, 1, 0);
//					String productVariantCode2 = findByproductVariantCodeAndProductVariantIsActiveAndProductVariantIsDeleted
//							.get(0).getProductVariantCode();
//					if (findByproductVariantCode.get(0).getProductVariantCode().equalsIgnoreCase(productVariantCode2))
					findByPositionId = currentPalletStockDetailsRepository
							.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
					findByPositionId.get(0).setPalletCode(currentPalletStockDetailsEntity.getPalletCode());
					findByPositionId.get(0)
							.setProductVariantCode(currentPalletStockDetailsEntity.getProductVariantCode());
					findByPositionId.get(0).setBatchNumber(currentPalletStockDetailsEntity.getBatchNumber());
					findByPositionId.get(0).setQuantity(currentPalletStockDetailsEntity.getQuantity());
					findByPositionId.get(0).setPalletStatusId(1);
					findByPositionId.get(0).setPalletStatusname("FULL");
					findByPositionId.get(0).setSerialNumber(currentPalletStockDetailsEntity.getSerialNumber());
					findByPositionId.get(0).setAgeingDays(0);
					findByPositionId.get(0).setQualityStatus(currentPalletStockDetailsEntity.getQualityStatus());
					findByPositionId.get(0).setLocation("NA");
					findByPositionId.get(0).setModelNumber("NA");
					findByPositionId.get(0).setIsInfeedMissionGenerated(0);
					findByPositionId.get(0).setIsOutfeedMissionGenerated(0);
					findByPositionId.get(0).setUserName(name);
					List<MasterUserDetailsEntity> findByuserName = masterUserDetailsRepository.findByuserName(name);
					findByPositionId.get(0).setUserId(findByuserName.get(0).getUserId());
					findByPositionId.get(0).setLoadDatetime(currentDate);

					List<MasterProductVariantDetailsEntity> findByProductVariantCode2 = masterProductVariantDetailsRepository
							.findByProductVariantCode(currentPalletStockDetailsEntity.getProductVariantCode());
					findByPositionId.get(0)
							.setProductVariantName(findByProductVariantCode2.get(0).getProductVariantname());
					findByPositionId.get(0).setProductId(findByProductVariantCode2.get(0).getProductId());
					findByPositionId.get(0).setProductVariantId(findByProductVariantCode2.get(0).getProductVariantId());
					findByPositionId.get(0).setProductName(findByProductVariantCode2.get(0).getProductName());

					List<MasterPalletInformationEntity> findByPalletCode3 = masterPalletInformationDetailsRepositoryInstance
							.findByPalletCode(currentPalletStockDetailsEntity.getPalletCode());
					if (findByPalletCode3.isEmpty()) {
						MasterPalletInformationEntity findTopByOrderByPalletInformationIdDesc = masterPalletInformationDetailsRepositoryInstance
								.findTopByOrderByPalletInformationIdDesc();
						if (findTopByOrderByPalletInformationIdDesc == null) {
							id = 1;
						} else {
							id = findTopByOrderByPalletInformationIdDesc.getPalletInformationId() + 1;
						}
						MasterPalletInformationEntity masterPalletInformationEntity = new MasterPalletInformationEntity(
								id, currentPalletStockDetailsEntity.getPalletCode(), "NA",
								findByPositionId.get(0).getProductId(), findByPositionId.get(0).getProductVariantId(),
								findByPositionId.get(0).getQuantity(), findByPositionId.get(0).getQualityStatus(),
								findByPositionId.get(0).getPalletStatusId(),
								findByPositionId.get(0).getPalletStatusname(),
								findByPositionId.get(0).getIsInfeedMissionGenerated(),
								findByPositionId.get(0).getIsOutfeedMissionGenerated(), 0, 0, currentDate,
								findByPositionId.get(0).getSerialNumber(),
								findByPositionId.get(0).getProductVariantCode(),
								findByPositionId.get(0).getProductName(),
								findByPositionId.get(0).getProductVariantName(), 0,
								findByPositionId.get(0).getBatchNumber(), findByPositionId.get(0).getModelNumber(),
								findByPositionId.get(0).getLocation(), "NA", "NA", "9999-12-31 00:00:00", "NA",
								findByPositionId.get(0).getLoadDatetime());
						masterPalletInformationDetailsRepositoryInstance.save(masterPalletInformationEntity);

						findByPositionId.get(0)
								.setPalletInformationId(masterPalletInformationEntity.getPalletInformationId());
						currentPalletStockDetailsRepository.save(findByPositionId.get(0));

					} else {
						findByPalletCode3.get(0).setProductVariantCode(findByPositionId.get(0).getProductVariantCode());
						findByPalletCode3.get(0).setWmsTransferMissionOrderId("NA");
						findByPalletCode3.get(0).setProductId(findByPositionId.get(0).getProductId());
						findByPalletCode3.get(0).setProductVariantId(findByPositionId.get(0).getProductVariantId());
						findByPalletCode3.get(0).setQuantity(findByPositionId.get(0).getQuantity());
						findByPalletCode3.get(0).setQualityStatus(findByPositionId.get(0).getQualityStatus());
						findByPalletCode3.get(0).setPalletStatusId(findByPositionId.get(0).getPalletStatusId());
						findByPalletCode3.get(0).setPalletStatusName(findByPositionId.get(0).getPalletStatusname());
						findByPalletCode3.get(0)
								.setIsInfeedMissionGenerated(findByPositionId.get(0).getIsInfeedMissionGenerated());
						findByPalletCode3.get(0)
								.setIsOutfeedMissionGenerated(findByPositionId.get(0).getIsOutfeedMissionGenerated());
						findByPalletCode3.get(0).setIsTransferManagementMissionGenerated(0);
						findByPalletCode3.get(0).setStationWorkdone(0);
						findByPalletCode3.get(0).setCdatetime(currentDate);
						findByPalletCode3.get(0).setSerialNumber(findByPositionId.get(0).getSerialNumber());
						findByPalletCode3.get(0).setProductName(findByPositionId.get(0).getProductName());
						findByPalletCode3.get(0).setProductVariantName(findByPositionId.get(0).getProductVariantName());
						findByPalletCode3.get(0).setPalletInformationIsDeleted(0);
						findByPalletCode3.get(0).setBatchNumber(findByPositionId.get(0).getBatchNumber());
						findByPalletCode3.get(0).setModelNumber(findByPositionId.get(0).getModelNumber());
						findByPalletCode3.get(0).setLocation(findByPositionId.get(0).getLocation());
						findByPalletCode3.get(0).setVendorCode("NA");
						findByPalletCode3.get(0).setPartIdentificationCode("NA");
						findByPalletCode3.get(0).setMfgDate("NA");
						findByPalletCode3.get(0).setMfgShift("NA");
						findByPalletCode3.get(0).setLoadDateTime(currentDate);
						masterPalletInformationDetailsRepositoryInstance.save(findByPalletCode3.get(0));

						findByPositionId.get(0)
								.setPalletInformationId(findByPalletCode3.get(0).getPalletInformationId());
						currentPalletStockDetailsRepository.save(findByPositionId.get(0));

					}

				}
				MasterPositionDetailsEntity findByPositionId2 = masterPositionDetailsRepository
						.findByPositionId(findByPositionId.get(0).getPositionId());
				findByPositionId2.setEmptyPalletPosition(0);
				findByPositionId2.setPositionIsAllocated(1);
				findByPositionId2.setPositionIsDeleted(0);
				findByPositionId2.setIsManualDispatch(0);
				findByPositionId2.setPositionIsAllocated(2);
				if (currentPalletStockDetailsEntity.getProductVariantCode().equalsIgnoreCase("NA")
						&& currentPalletStockDetailsEntity.getQuantity() == 0) {
					findByPositionId2.setIsMaterialLoaded(0);
				} else {
					findByPositionId2.setIsMaterialLoaded(1);
				}
				masterPositionDetailsRepository.save(findByPositionId2);

				masterPositionDetailsRepository.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
				
				PLCITDataMismatchDetailsEntity findByPositionId4 = plcItDataMismatchDetailsRepository.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
				findByPositionId4.setIsDataUpdated(1);
				findByPositionId4.setCDateTime(currentDate);
				plcItDataMismatchDetailsRepository.save(findByPositionId4);

				masterPositionDetailsRepository.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
				auditTrailDetailsEntity.setOperatorActions(
						"Data added by " + name + " for position  " + findByPositionId2.getPositionName()
								+ " having pallet code " + currentPalletStockDetailsEntity.getPalletCode() +" due to mismatch");
				auditTrailDetailsEntity.setField("Data added");
				auditTrailDetailsEntity.setAfterValue(0);
				auditTrailDetailsEntity.setBeforeValue(0);
				auditTrailDetailsEntity.setReason("Pallet added");

				System.out.println(" name :: " + name);
				auditTrailDetailsEntity.setUsername(name);
				auditTrailDetailsEntity.setDatetimeC(currentDate);
				auditTrailDetailsRepository.save(auditTrailDetailsEntity);

			} else if (currentPalletStockDetailsEntity.getProductVariantCode().equals(productVariantCode)) {
				if (!Objects.equals(productName, productName2)) {
					return ResponseHandler.generateResponse("Product name does not match previous product name",
							HttpStatus.CREATED, null);
				}
				if (currentPalletStockDetailsEntity.getProductVariantCode().equalsIgnoreCase("NA")) {
					findByPositionId = currentPalletStockDetailsRepository
							.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
					findByPositionId.get(0).setPalletCode(currentPalletStockDetailsEntity.getPalletCode());
					findByPositionId.get(0)
							.setProductVariantCode(currentPalletStockDetailsEntity.getProductVariantCode());
					findByPositionId.get(0).setProductVariantName("NA");
					findByPositionId.get(0).setProductVariantId(0);
//					findByPositionId.get(0).setProductId(0);
					findByPositionId.get(0).setProductName(currentPalletStockDetailsEntity.getProductName());
					MasterProductDetailsEntity findByProductName = masterProductDetailsRepository
							.findByProductName(currentPalletStockDetailsEntity.getProductName());

					findByPositionId.get(0).setProductId(findByProductName.getProductId());
					findByPositionId.get(0).setBatchNumber(currentPalletStockDetailsEntity.getBatchNumber());
					findByPositionId.get(0).setQuantity(currentPalletStockDetailsEntity.getQuantity());
					findByPositionId.get(0).setPalletStatusId(3);
					findByPositionId.get(0).setPalletStatusname("EMPTY");
					findByPositionId.get(0).setSerialNumber(currentPalletStockDetailsEntity.getSerialNumber());
					findByPositionId.get(0).setAgeingDays(0);
					findByPositionId.get(0).setQualityStatus("NA");
					findByPositionId.get(0).setLocation("NA");
					findByPositionId.get(0).setModelNumber("NA");
					findByPositionId.get(0).setIsInfeedMissionGenerated(0);
					findByPositionId.get(0).setIsOutfeedMissionGenerated(0);
					findByPositionId.get(0).setUserName(name);
					List<MasterUserDetailsEntity> findByuserName = masterUserDetailsRepository.findByuserName(name);
					findByPositionId.get(0).setUserId(findByuserName.get(0).getUserId());
					findByPositionId.get(0).setLoadDatetime(currentDate);

					List<MasterPalletInformationEntity> findByPalletCode3 = masterPalletInformationDetailsRepositoryInstance
							.findByPalletCode(currentPalletStockDetailsEntity.getPalletCode());
					if (findByPalletCode3.isEmpty()) {
						MasterPalletInformationEntity findTopByOrderByPalletInformationIdDesc = masterPalletInformationDetailsRepositoryInstance
								.findTopByOrderByPalletInformationIdDesc();
						if (findTopByOrderByPalletInformationIdDesc == null) {
							id = 1;
						} else {
							id = findTopByOrderByPalletInformationIdDesc.getPalletInformationId() + 1;
						}
						MasterPalletInformationEntity masterPalletInformationEntity = new MasterPalletInformationEntity(
								id, currentPalletStockDetailsEntity.getPalletCode(), "NA",
								findByPositionId.get(0).getProductId(), findByPositionId.get(0).getProductVariantId(),
								findByPositionId.get(0).getQuantity(), findByPositionId.get(0).getQualityStatus(),
								findByPositionId.get(0).getPalletStatusId(),
								findByPositionId.get(0).getPalletStatusname(),
								findByPositionId.get(0).getIsInfeedMissionGenerated(),
								findByPositionId.get(0).getIsOutfeedMissionGenerated(), 0, 0, currentDate,
								findByPositionId.get(0).getSerialNumber(),
								findByPositionId.get(0).getProductVariantCode(),
								findByPositionId.get(0).getProductName(),
								findByPositionId.get(0).getProductVariantName(), 0,
								findByPositionId.get(0).getBatchNumber(), findByPositionId.get(0).getModelNumber(),
								findByPositionId.get(0).getLocation(), "NA", "NA", "9999-12-31 00:00:00", "NA",
								findByPositionId.get(0).getLoadDatetime());
						masterPalletInformationDetailsRepositoryInstance.save(masterPalletInformationEntity);

						findByPositionId.get(0)
								.setPalletInformationId(masterPalletInformationEntity.getPalletInformationId());
						currentPalletStockDetailsRepository.save(findByPositionId.get(0));

					} else {
						findByPalletCode3.get(0).setProductVariantCode(findByPositionId.get(0).getProductVariantCode());
						findByPalletCode3.get(0).setWmsTransferMissionOrderId("NA");
						findByPalletCode3.get(0).setProductId(findByPositionId.get(0).getProductId());
						findByPalletCode3.get(0).setProductVariantId(findByPositionId.get(0).getProductVariantId());
						findByPalletCode3.get(0).setQuantity(findByPositionId.get(0).getQuantity());
						findByPalletCode3.get(0).setQualityStatus(findByPositionId.get(0).getQualityStatus());
						findByPalletCode3.get(0).setPalletStatusId(findByPositionId.get(0).getPalletStatusId());
						findByPalletCode3.get(0).setPalletStatusName(findByPositionId.get(0).getPalletStatusname());
						findByPalletCode3.get(0)
								.setIsInfeedMissionGenerated(findByPositionId.get(0).getIsInfeedMissionGenerated());
						findByPalletCode3.get(0)
								.setIsOutfeedMissionGenerated(findByPositionId.get(0).getIsOutfeedMissionGenerated());
						findByPalletCode3.get(0).setIsTransferManagementMissionGenerated(0);
						findByPalletCode3.get(0).setStationWorkdone(0);
						findByPalletCode3.get(0).setCdatetime(currentDate);
						findByPalletCode3.get(0).setSerialNumber(findByPositionId.get(0).getSerialNumber());
						findByPalletCode3.get(0).setProductName(findByPositionId.get(0).getProductName());
						findByPalletCode3.get(0).setProductVariantName(findByPositionId.get(0).getProductVariantName());
						findByPalletCode3.get(0).setPalletInformationIsDeleted(0);
						findByPalletCode3.get(0).setBatchNumber(findByPositionId.get(0).getBatchNumber());
						findByPalletCode3.get(0).setModelNumber(findByPositionId.get(0).getModelNumber());
						findByPalletCode3.get(0).setLocation(findByPositionId.get(0).getLocation());
						findByPalletCode3.get(0).setVendorCode("NA");
						findByPalletCode3.get(0).setPartIdentificationCode("NA");
						findByPalletCode3.get(0).setMfgDate("NA");
						findByPalletCode3.get(0).setMfgShift("NA");
						findByPalletCode3.get(0).setLoadDateTime(currentDate);
						masterPalletInformationDetailsRepositoryInstance.save(findByPalletCode3.get(0));

						findByPositionId.get(0)
								.setPalletInformationId(findByPalletCode3.get(0).getPalletInformationId());
						currentPalletStockDetailsRepository.save(findByPositionId.get(0));

						masterPositionDetailsRepository
								.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
						
						PLCITDataMismatchDetailsEntity findByPositionId4 = plcItDataMismatchDetailsRepository.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
						findByPositionId4.setIsDataUpdated(1);
						findByPositionId4.setCDateTime(currentDate);
						plcItDataMismatchDetailsRepository.save(findByPositionId4);

						masterPositionDetailsRepository.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
						AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
						auditTrailDetailsEntity.setOperatorActions(
								"Data added by " + name + " for position  " + currentPalletStockDetailsEntity.getPositionName()
										+ " having pallet code " + currentPalletStockDetailsEntity.getPalletCode() +" due to mismatch");
						auditTrailDetailsEntity.setField("Data added");
						auditTrailDetailsEntity.setAfterValue(0);
						auditTrailDetailsEntity.setBeforeValue(0);
						auditTrailDetailsEntity.setReason("Pallet added");

						System.out.println(" name :: " + name);
						auditTrailDetailsEntity.setUsername(name);
						auditTrailDetailsEntity.setDatetimeC(currentDate);
						auditTrailDetailsRepository.save(auditTrailDetailsEntity);

					}

				} else {
					findByPositionId = currentPalletStockDetailsRepository
							.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
					findByPositionId.get(0).setPalletCode(currentPalletStockDetailsEntity.getPalletCode());
					findByPositionId.get(0)
							.setProductVariantCode(currentPalletStockDetailsEntity.getProductVariantCode());
					findByPositionId.get(0).setBatchNumber(currentPalletStockDetailsEntity.getBatchNumber());
					findByPositionId.get(0).setQuantity(currentPalletStockDetailsEntity.getQuantity());
					findByPositionId.get(0).setPalletStatusId(1);
					findByPositionId.get(0).setPalletStatusname("FULL");
					findByPositionId.get(0).setSerialNumber(currentPalletStockDetailsEntity.getSerialNumber());
					findByPositionId.get(0).setAgeingDays(0);
					findByPositionId.get(0).setQualityStatus(currentPalletStockDetailsEntity.getQualityStatus());
					findByPositionId.get(0).setLocation("NA");
					findByPositionId.get(0).setModelNumber("NA");
					findByPositionId.get(0).setIsInfeedMissionGenerated(0);
					findByPositionId.get(0).setIsOutfeedMissionGenerated(0);
					findByPositionId.get(0).setUserName(name);
					List<MasterUserDetailsEntity> findByuserName = masterUserDetailsRepository.findByuserName(name);
					findByPositionId.get(0).setUserId(findByuserName.get(0).getUserId());
					findByPositionId.get(0).setLoadDatetime(currentDate);

					List<MasterProductVariantDetailsEntity> findByProductVariantCode2 = masterProductVariantDetailsRepository
							.findByProductVariantCode(currentPalletStockDetailsEntity.getProductVariantCode());
					findByPositionId.get(0)
							.setProductVariantName(findByProductVariantCode2.get(0).getProductVariantname());
					findByPositionId.get(0).setProductId(findByProductVariantCode2.get(0).getProductId());
					findByPositionId.get(0).setProductVariantId(findByProductVariantCode2.get(0).getProductVariantId());
					findByPositionId.get(0).setProductName(findByProductVariantCode2.get(0).getProductName());

					List<MasterPalletInformationEntity> findByPalletCode3 = masterPalletInformationDetailsRepositoryInstance
							.findByPalletCode(currentPalletStockDetailsEntity.getPalletCode());
					if (findByPalletCode3.isEmpty()) {
						MasterPalletInformationEntity findTopByOrderByPalletInformationIdDesc = masterPalletInformationDetailsRepositoryInstance
								.findTopByOrderByPalletInformationIdDesc();
						if (findTopByOrderByPalletInformationIdDesc == null) {
							id = 1;
						} else {
							id = findTopByOrderByPalletInformationIdDesc.getPalletInformationId() + 1;
						}
						MasterPalletInformationEntity masterPalletInformationEntity = new MasterPalletInformationEntity(
								id, currentPalletStockDetailsEntity.getPalletCode(), "NA",
								findByPositionId.get(0).getProductId(), findByPositionId.get(0).getProductVariantId(),
								findByPositionId.get(0).getQuantity(), findByPositionId.get(0).getQualityStatus(),
								findByPositionId.get(0).getPalletStatusId(),
								findByPositionId.get(0).getPalletStatusname(),
								findByPositionId.get(0).getIsInfeedMissionGenerated(),
								findByPositionId.get(0).getIsOutfeedMissionGenerated(), 0, 0, currentDate,
								findByPositionId.get(0).getSerialNumber(),
								findByPositionId.get(0).getProductVariantCode(),
								findByPositionId.get(0).getProductName(),
								findByPositionId.get(0).getProductVariantName(), 0,
								findByPositionId.get(0).getBatchNumber(), findByPositionId.get(0).getModelNumber(),
								findByPositionId.get(0).getLocation(), "NA", "NA", "9999-12-31 00:00:00", "NA",
								findByPositionId.get(0).getLoadDatetime());
						masterPalletInformationDetailsRepositoryInstance.save(masterPalletInformationEntity);

						findByPositionId.get(0)
								.setPalletInformationId(masterPalletInformationEntity.getPalletInformationId());
						currentPalletStockDetailsRepository.save(findByPositionId.get(0));

					} else {
						findByPalletCode3.get(0).setProductVariantCode(findByPositionId.get(0).getProductVariantCode());
						findByPalletCode3.get(0).setWmsTransferMissionOrderId("NA");
						findByPalletCode3.get(0).setProductId(findByPositionId.get(0).getProductId());
						findByPalletCode3.get(0).setProductVariantId(findByPositionId.get(0).getProductVariantId());
						findByPalletCode3.get(0).setQuantity(findByPositionId.get(0).getQuantity());
						findByPalletCode3.get(0).setQualityStatus(findByPositionId.get(0).getQualityStatus());
						findByPalletCode3.get(0).setPalletStatusId(findByPositionId.get(0).getPalletStatusId());
						findByPalletCode3.get(0).setPalletStatusName(findByPositionId.get(0).getPalletStatusname());
						findByPalletCode3.get(0)
								.setIsInfeedMissionGenerated(findByPositionId.get(0).getIsInfeedMissionGenerated());
						findByPalletCode3.get(0)
								.setIsOutfeedMissionGenerated(findByPositionId.get(0).getIsOutfeedMissionGenerated());
						findByPalletCode3.get(0).setIsTransferManagementMissionGenerated(0);
						findByPalletCode3.get(0).setStationWorkdone(0);
						findByPalletCode3.get(0).setCdatetime(currentDate);
						findByPalletCode3.get(0).setSerialNumber(findByPositionId.get(0).getSerialNumber());
						findByPalletCode3.get(0).setProductName(findByPositionId.get(0).getProductName());
						findByPalletCode3.get(0).setProductVariantName(findByPositionId.get(0).getProductVariantName());
						findByPalletCode3.get(0).setPalletInformationIsDeleted(0);
						findByPalletCode3.get(0).setBatchNumber(findByPositionId.get(0).getBatchNumber());
						findByPalletCode3.get(0).setModelNumber(findByPositionId.get(0).getModelNumber());
						findByPalletCode3.get(0).setLocation(findByPositionId.get(0).getLocation());
						findByPalletCode3.get(0).setVendorCode("NA");
						findByPalletCode3.get(0).setPartIdentificationCode("NA");
						findByPalletCode3.get(0).setMfgDate("NA");
						findByPalletCode3.get(0).setMfgShift("NA");
						masterPalletInformationDetailsRepositoryInstance.save(findByPalletCode3.get(0));

						findByPositionId.get(0)
								.setPalletInformationId(findByPalletCode3.get(0).getPalletInformationId());
						currentPalletStockDetailsRepository.save(findByPositionId.get(0));

					}

				}
				MasterPositionDetailsEntity findByPositionId2 = masterPositionDetailsRepository
						.findByPositionId(findByPositionId.get(0).getPositionId());
				findByPositionId2.setEmptyPalletPosition(0);
				findByPositionId2.setPositionIsAllocated(1);
				findByPositionId2.setPositionIsDeleted(0);
				findByPositionId2.setIsManualDispatch(0);
				findByPositionId2.setPositionIsAllocated(2);
				if (currentPalletStockDetailsEntity.getProductVariantCode().equalsIgnoreCase("NA")
						&& currentPalletStockDetailsEntity.getQuantity() == 0) {
					findByPositionId2.setIsMaterialLoaded(0);
				} else {
					findByPositionId2.setIsMaterialLoaded(1);
				}
				findByPositionId2.setCDateTime(currentDate);

				masterPositionDetailsRepository.save(findByPositionId2);
				
				
				PLCITDataMismatchDetailsEntity findByPositionId4 = plcItDataMismatchDetailsRepository.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
				findByPositionId4.setIsDataUpdated(1);
				findByPositionId4.setCDateTime(currentDate);
				plcItDataMismatchDetailsRepository.save(findByPositionId4);

				masterPositionDetailsRepository.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
				auditTrailDetailsEntity.setOperatorActions(
						"Data added by " + name + " for position  " + findByPositionId2.getPositionName()
								+ " having pallet code " + currentPalletStockDetailsEntity.getPalletCode() +" due to mismatch");
				auditTrailDetailsEntity.setField("Data added");
				auditTrailDetailsEntity.setAfterValue(0);
				auditTrailDetailsEntity.setBeforeValue(0);
				auditTrailDetailsEntity.setReason("Pallet added");

				System.out.println(" name :: " + name);
				auditTrailDetailsEntity.setUsername(name);
				auditTrailDetailsEntity.setDatetimeC(currentDate);
				auditTrailDetailsRepository.save(auditTrailDetailsEntity);
			} else {
				return ResponseHandler.generateResponse(
						"Product Variant code does not match to next product variant code", HttpStatus.ALREADY_REPORTED,
						null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseHandler.generateResponse("Error adding pallet", HttpStatus.BAD_REQUEST, null);
		}

		return ResponseHandler.generateResponse("Pallet code added successfully", HttpStatus.OK,
				findByPositionId.get(0));
	}
	
	
	
	
	
	
	public List<PLCITDataMismatchDetailsEntity> findByAllFilters(
	      
	        String plcItDataMismatchStartCdatetime, 
	        String plcItDataMismatchEndCdatetime) {

	    List<String> filterList = new ArrayList<>();
	    List<PLCITDataMismatchDetailsEntity> list;


	    if (!plcItDataMismatchStartCdatetime.equals("NA") && !plcItDataMismatchEndCdatetime.equals("NA")) {
	        String startDateTime = plcItDataMismatchStartCdatetime.replace("T", " ");
	        String endDateTime = plcItDataMismatchEndCdatetime.replace("T", " ");

	        list = plcItDataMismatchDetailsRepository
	                .findPlcItDataMismatchDetailsBetweenDates(startDateTime, endDateTime);
	    } else {
	        Date dNow = new Date();
	        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
	        String date = ft.format(dNow);
	        
	        list = plcItDataMismatchDetailsRepository
	                .findPlcItDataMismatchDetailsBetweenDates(date + " 00:00:00", date + " 23:59:59");
	    }

	    

	    if (filterList.isEmpty() && list.isEmpty()) {
	        return null;
	    }

	    return list;
	}
	
	

}
