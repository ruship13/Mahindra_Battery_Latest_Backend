package com.ats.mahindrabattery.serviceimpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.Cacheable;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.ats.mahindrabattery.entity.AuditTrailDetailsEntity;
import com.ats.mahindrabattery.entity.BufferDetailsEntity;
import com.ats.mahindrabattery.entity.CurrentPalletStockDetailsEntity;
import com.ats.mahindrabattery.entity.CurrentStockDetails;
import com.ats.mahindrabattery.entity.DashboardDetailsEntity;
import com.ats.mahindrabattery.entity.GenerateManualRetrievalOrderEntity;
import com.ats.mahindrabattery.entity.GenerateRetrivalMissionTypeEntity;
import com.ats.mahindrabattery.entity.InfeedMissionRuntimeDetailsEntity;
import com.ats.mahindrabattery.entity.ManualOutfeedMissionDetailsEntity;
import com.ats.mahindrabattery.entity.MasterPalletInformationEntity;
import com.ats.mahindrabattery.entity.MasterPositionDetailsEntity;
import com.ats.mahindrabattery.entity.MasterProductDetailsEntity;
import com.ats.mahindrabattery.entity.MasterProductVariantDetailsEntity;
import com.ats.mahindrabattery.entity.MasterStationTagDetailsEntity;
import com.ats.mahindrabattery.entity.MasterUserDetailsEntity;
import com.ats.mahindrabattery.entity.OrderSourceDetailsEntity;
import com.ats.mahindrabattery.exception.ResourceNotFoundException;
import com.ats.mahindrabattery.repository.AuditTrailDetailsRepository;
import com.ats.mahindrabattery.repository.BufferDetailsRepository;
import com.ats.mahindrabattery.repository.CurrentPalletStockDetailsRepository;
import com.ats.mahindrabattery.repository.GenerateManualRetrievalOrderRepository;
import com.ats.mahindrabattery.repository.GenerateRetrivalMissionTypeRepository;
import com.ats.mahindrabattery.repository.MasterPalletInformationRepository;
import com.ats.mahindrabattery.repository.MasterPositionDetailsRepository;
import com.ats.mahindrabattery.repository.MasterProductDetailsRepository;
import com.ats.mahindrabattery.repository.MasterProductVariantDetailsRepository;
import com.ats.mahindrabattery.repository.MasterStationTagDetailsRepository;
import com.ats.mahindrabattery.repository.MasterUserDetailsRepository;
import com.ats.mahindrabattery.repository.OrderSourceDetailsRepository;
import com.ats.mahindrabattery.response.ResponseHandler;
import com.ats.mahindrabattery.service.CurrentPalletStockDetailsService;

@Service

public class CurrentPalletStockDetailsServiceImpl implements CurrentPalletStockDetailsService {

	private int bevCurrentStockCount;
	private int s230CurrentStockCount;
	private int emptyPalletCount;

	DashboardDetailsEntity dashboardEntity = new DashboardDetailsEntity();

	CurrentStockDetails currentStockDetails = new CurrentStockDetails();



	@Autowired
	private MasterProductVariantDetailsRepository masterProductVariantDetailsRepository;
	
	@Autowired
	private GenerateRetrivalMissionTypeRepository generateRetrivalMissionTypeRepository;

	@Autowired
	private CurrentPalletStockDetailsRepository currentPalletStockDetailsRepository;

	@Autowired
	private MasterPalletInformationRepository masterPalletInformationDetailsRepositoryInstance;

	@Autowired
	MasterPalletInformationServiceImpl masterPalletInformationServiceImpl;

	@Autowired
	GenerateManualRetrievalOrderRepository generateManualRetrievalOrderRepository;

	MasterProductVariantDetailsEntity masterProductVariantDetailsEntity = new MasterProductVariantDetailsEntity();

	@Autowired
	private MasterProductVariantDetailsRepository masterProductVariantDetailsRepositoryInstance;

	@Autowired
	private MasterPositionDetailsRepository masterPositionDetailsRepository;

	@Autowired
	private BufferDetailsRepository bufferDetailsRepository;

	@Autowired
	private MasterUserDetailsRepository masterUserDetailsRepository;

	@Autowired
	private AuditTrailDetailsRepository auditTrailDetailsRepository;

	@Autowired
	private MasterProductDetailsRepository masterProductDetailsRepository;

	@Autowired
	private MasterStationTagDetailsRepository masterStationTagDetailsRepository;
	
	@Autowired
	private OrderSourceDetailsRepository orderSourceDetailsRepository;

	public Page<CurrentPalletStockDetailsEntity> findAll(Pageable pageable) {
		return currentPalletStockDetailsRepository.findAll(pageable);
	}



	public Page<CurrentPalletStockDetailsEntity> findAllByPalletCodeNotNA(Pageable pageable) {
		return currentPalletStockDetailsRepository.findByPalletCodeNotOrderByAgeingDaysDesc(pageable, "NA");
	}

	public List<CurrentPalletStockDetailsEntity> fetchAllByPalletCodeNotNA(Pageable pageable) {
		return currentPalletStockDetailsRepository.findByPalletCodeNotOrderByAgeingDaysDesc("NA");
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


			if (productVariantIsActive == 0 && !currentPalletStockDetailsEntity.getProductVariantCode().equals("NA")) {
				return ResponseHandler.generateResponse("Part Number is not Active", HttpStatus.ALREADY_REPORTED, null);
			}

			List<MasterProductVariantDetailsEntity> findByproductVariantCode = masterProductVariantDetailsRepositoryInstance
					.findByproductVariantCodeAndProductVariantIsActiveAndProductVariantIsDeleted(
							currentPalletStockDetailsEntity.getProductVariantCode(), 1, 0);

			int positionId = currentPalletStockDetailsEntity.getPositionId();
			List<CurrentPalletStockDetailsEntity> findByPositionId3 = currentPalletStockDetailsRepository
					.findByPositionId(positionId + 1);
			String productVariantCode = findByPositionId3.get(0).getProductVariantCode();

			String productName = findByPositionId3.get(0).getProductName();
			String productName2 = currentPalletStockDetailsEntity.getProductName();



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
						AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
						auditTrailDetailsEntity.setOperatorActions("Data added by " + name + " for position  "
								+ currentPalletStockDetailsEntity.getPositionName() + " having pallet code "
								+ currentPalletStockDetailsEntity.getPalletCode());
						auditTrailDetailsEntity.setField("Data added");
						auditTrailDetailsEntity.setAfterValue(0);
						auditTrailDetailsEntity.setBeforeValue(0);
						auditTrailDetailsEntity.setReason("Data added");

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

				if (currentPalletStockDetailsEntity.getProductVariantCode().equalsIgnoreCase("NA")
						&& currentPalletStockDetailsEntity.getQuantity() == 0) {
					findByPositionId2.setIsMaterialLoaded(0);
				} else {
					findByPositionId2.setIsMaterialLoaded(1);
				}
				masterPositionDetailsRepository.save(findByPositionId2);

				masterPositionDetailsRepository.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
				auditTrailDetailsEntity.setOperatorActions(
						"Data added by " + name + " for position  " + findByPositionId2.getPositionName()
								+ " having pallet code " + currentPalletStockDetailsEntity.getPalletCode());
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
						AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
						auditTrailDetailsEntity.setOperatorActions("Data added by " + name + " for position  "
								+ currentPalletStockDetailsEntity.getPositionName() + " having pallet code "
								+ currentPalletStockDetailsEntity.getPalletCode());
						auditTrailDetailsEntity.setField("Data added");
						auditTrailDetailsEntity.setAfterValue(0);
						auditTrailDetailsEntity.setBeforeValue(0);
						auditTrailDetailsEntity.setReason("Data added");

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

				if (currentPalletStockDetailsEntity.getProductVariantCode().equalsIgnoreCase("NA")
						&& currentPalletStockDetailsEntity.getQuantity() == 0) {
					findByPositionId2.setIsMaterialLoaded(0);
				} else {
					findByPositionId2.setIsMaterialLoaded(1);
				}
				masterPositionDetailsRepository.save(findByPositionId2);

				masterPositionDetailsRepository.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
				auditTrailDetailsEntity.setOperatorActions(
						"Data added by " + name + " for position  " + findByPositionId2.getPositionName()
								+ " having pallet code " + currentPalletStockDetailsEntity.getPalletCode());
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
						AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
						auditTrailDetailsEntity.setOperatorActions("Data added by " + name + " for position  "
								+ currentPalletStockDetailsEntity.getPositionName() + " having pallet code "
								+ currentPalletStockDetailsEntity.getPalletCode());
						auditTrailDetailsEntity.setField("Data added");
						auditTrailDetailsEntity.setAfterValue(0);
						auditTrailDetailsEntity.setBeforeValue(0);
						auditTrailDetailsEntity.setReason("Data added");

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

				if (currentPalletStockDetailsEntity.getProductVariantCode().equalsIgnoreCase("NA")
						&& currentPalletStockDetailsEntity.getQuantity() == 0) {
					findByPositionId2.setIsMaterialLoaded(0);
				} else {
					findByPositionId2.setIsMaterialLoaded(1);
				}
				findByPositionId2.setCDateTime(currentDate);

				masterPositionDetailsRepository.save(findByPositionId2);

				masterPositionDetailsRepository.findByPositionId(currentPalletStockDetailsEntity.getPositionId());
				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
				auditTrailDetailsEntity.setOperatorActions(
						"Data added by " + name + " for position  " + findByPositionId2.getPositionName()
								+ " having pallet code " + currentPalletStockDetailsEntity.getPalletCode());
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

	public List<CurrentPalletStockDetailsEntity> findAllCurrentPalletStockDetails() {
		try {
			List<CurrentPalletStockDetailsEntity> currentPalletStockDetails = currentPalletStockDetailsRepository
					.findAll();

			return currentPalletStockDetails;

		} catch (Exception ex) {

			ex.printStackTrace();
		}
		return null;
	}

	public CurrentPalletStockDetailsEntity findbycurrentPalletStockDetailsId(int currentPalletStockDetailsId) {
		try {
			CurrentPalletStockDetailsEntity findByCurrentPalletStockDetailsId = currentPalletStockDetailsRepository
					.findByCurrentPalletStockDetailsId(currentPalletStockDetailsId);

			return findByCurrentPalletStockDetailsId;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

	public List<CurrentPalletStockDetailsEntity> findByQuantity(int quantity) {
		try {
			List<CurrentPalletStockDetailsEntity> findByQuantity = currentPalletStockDetailsRepository
					.findByQuantity(quantity);

			return findByQuantity;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}


	public List<CurrentPalletStockDetailsEntity> findAllByPositionName(String positionName) {
		try {

			return currentPalletStockDetailsRepository.findAllByPositionName(positionName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}


	public List<CurrentPalletStockDetailsEntity> findBypalletCode(String palletCode) {
		try {

			return currentPalletStockDetailsRepository.findByPalletCode(palletCode);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public List<CurrentPalletStockDetailsEntity> findBySerialNumber(int serialNumber) {
		try {

			return currentPalletStockDetailsRepository.getBySerialNumber(serialNumber);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}


	public List<CurrentPalletStockDetailsEntity> findByproductName(String productName) {
		try {
			
			List<CurrentPalletStockDetailsEntity> currentPalletStockDetailsByProductName = currentPalletStockDetailsRepository
					.findByproductName(productName);
			return currentPalletStockDetailsByProductName;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public List<CurrentPalletStockDetailsEntity> findByproductVariantCode(String productVariantCode) {
		try {
			List<CurrentPalletStockDetailsEntity> currentPalletStockDetailsByProductVariantCode = currentPalletStockDetailsRepository
					.findByproductVariantCode(productVariantCode);
			return currentPalletStockDetailsByProductVariantCode;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}


	public CurrentPalletStockDetailsEntity deleteByProductVariantCodeAndPalletCodeAndCurrentPalletStockDetailsId(
			String productVariantCode, String palletCode, int currentPalletStockDetailsId) {
		// System.out.println("productVariantCode :: " + currentPalletStockDetailsId);
		try {
			return deleteByProductVariantCodeAndPalletCodeAndCurrentPalletStockDetailsId(productVariantCode, palletCode,
					currentPalletStockDetailsId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

//	@Cacheable(key = "#areaName")
	public List<CurrentPalletStockDetailsEntity> findByAreaName(String areaName) {
		try {

			return currentPalletStockDetailsRepository.findByAreaName(areaName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

//	@Cacheable(key = "#positionId")
	public List<CurrentPalletStockDetailsEntity> findByPositionId(int positionId) {
		try {
//			System.out.println("called by findByPositionId() from DB");
			return currentPalletStockDetailsRepository.findByPositionId(positionId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public CurrentPalletStockDetailsEntity updateQuantityAndPalletCodeAndproductVariantCodeByPositionId(
			CurrentPalletStockDetailsEntity currentPalletStockDetailsEntity, int quantity, int positionId,
			String palletCode, String productVariantCode) {

		try {
			CurrentPalletStockDetailsEntity findByQuantityAndPositionIdAndPalletCodeAndProductVariantCode = currentPalletStockDetailsRepository
					.findByQuantityAndPositionIdAndPalletCodeAndProductVariantCode(quantity, positionId, palletCode,
							productVariantCode);
			findByQuantityAndPositionIdAndPalletCodeAndProductVariantCode
					.setQuantity(currentPalletStockDetailsEntity.getQuantity());

			findByQuantityAndPositionIdAndPalletCodeAndProductVariantCode
					.setPalletCode(currentPalletStockDetailsEntity.getPalletCode());
			findByQuantityAndPositionIdAndPalletCodeAndProductVariantCode
					.setProductVariantCode(currentPalletStockDetailsEntity.getProductVariantCode());
			findByQuantityAndPositionIdAndPalletCodeAndProductVariantCode
					.setPositionId(currentPalletStockDetailsEntity.getPositionId());

			return currentPalletStockDetailsRepository
					.save(findByQuantityAndPositionIdAndPalletCodeAndProductVariantCode);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

	public CurrentPalletStockDetailsEntity deleteByCurrentPalletStockDetailsId(int currentPalletStockDetailsId) {
	
		try {
			CurrentPalletStockDetailsEntity findByCurrentPalletStockDetailsId = currentPalletStockDetailsRepository
					.findByCurrentPalletStockDetailsId(currentPalletStockDetailsId);
			currentPalletStockDetailsRepository.delete(findByCurrentPalletStockDetailsId);
			return findByCurrentPalletStockDetailsId;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}


	public List<CurrentPalletStockDetailsEntity> findByLoadDateTime() {
		try {
			Date date = new Date();
			String strDateFormat = "yyyy-MM-dd";
			DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
			String currentDateTime = dateFormat.format(date);
		

			return currentPalletStockDetailsRepository.getAllCurrentPalletStockDetailsBetweenDates(
					currentDateTime + " " + "00:00:00", currentDateTime + " " + "23:59:59");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public CurrentPalletStockDetailsEntity updateCurrentPalletStockDetails(int currentPalletStockDetailsId,
			CurrentPalletStockDetailsEntity currentPalletStockDetailsEntity) {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = ft.format(dNow);
		System.out.println("currentDate :: " + currentDate);
		CurrentPalletStockDetailsEntity currentStockDetails = currentPalletStockDetailsRepository
				.findById(currentPalletStockDetailsId)
				.orElseThrow(() -> new ResourceNotFoundException("CurrentPalletStockDetailsEntity", "Id",
						currentPalletStockDetailsId));

		currentStockDetails.setQualityStatus(currentPalletStockDetailsEntity.getQualityStatus());

		System.out.println("Quality approved status=" + currentStockDetails.getQualityStatus());

		AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();
		System.out.println(" name :: " + name);
		if (currentStockDetails.getQualityStatus().equals("NOK")) {
			auditTrailDetailsEntity.setOperatorActions(
					"Quality Status changed from OK to NOK for  " + currentStockDetails.getPositionName()
							+ " having pallet code " + currentStockDetails.getPalletCode() + " by " + name);
			auditTrailDetailsEntity.setField("Quality Status Changed");

			auditTrailDetailsEntity.setReason("Quality Status Changed");

			auditTrailDetailsEntity.setUsername(name);
			auditTrailDetailsEntity.setDatetimeC(currentDate);
			auditTrailDetailsRepository.save(auditTrailDetailsEntity);
		} else {
			auditTrailDetailsEntity.setOperatorActions(
					"Quality Status changed from NOK to OK for  " + currentStockDetails.getPositionName()
							+ " having pallet code " + currentStockDetails.getPalletCode() + " by " + name);
			auditTrailDetailsEntity.setField("Quality Status Changed");

			auditTrailDetailsEntity.setReason("Quality Status Changed");

			auditTrailDetailsEntity.setUsername(name);
			auditTrailDetailsEntity.setDatetimeC(currentDate);
			auditTrailDetailsRepository.save(auditTrailDetailsEntity);
		}

		currentPalletStockDetailsRepository.save(currentStockDetails);

		MasterPalletInformationEntity findByPalletInformationId = masterPalletInformationDetailsRepositoryInstance
				.findByPalletInformationId(currentStockDetails.getPalletInformationId());
		findByPalletInformationId.setQualityStatus(currentStockDetails.getQualityStatus());
		masterPalletInformationDetailsRepositoryInstance.save(findByPalletInformationId);
		return currentStockDetails;
	}



//prcaticw
	public List<CurrentPalletStockDetailsEntity> findByAllFilters(String startDate, String endDate,
			String productVariantCode, String floorName, String areaName, String productName, String palletStatusname) {
		List<String> filterList = new ArrayList<>();
		List<CurrentPalletStockDetailsEntity> list = new ArrayList<>();
		System.out.println("productName2222222222" + productName);
		if (!productVariantCode.equals("NA")) {
			filterList.add("productVariantCode");
		}
		if (!areaName.equals("NA")) {
			filterList.add("areaName");
		}
		if (!floorName.equals("NA")) {
			filterList.add("floorName");
		}
		if (!productName.equals("NA")) {
			filterList.add("productName");
		}
		if (!palletStatusname.equals("NA")) {
			filterList.add("palletStatusname");
		}

		Predicate<CurrentPalletStockDetailsEntity> predProductVariantCode = data -> data.getProductVariantCode()
				.equals(productVariantCode);
		Predicate<CurrentPalletStockDetailsEntity> predArea = data -> data.getAreaName().equals(areaName);
		Predicate<CurrentPalletStockDetailsEntity> predFloor = data -> data.getFloorName().equals(floorName);
		Predicate<CurrentPalletStockDetailsEntity> predProductName = data -> data.getProductName().equals(productName);
		Predicate<CurrentPalletStockDetailsEntity> predPalletStatusname = data -> data.getPalletStatusname()
				.equals(palletStatusname);

		if (!startDate.equals("NA") && !endDate.equals("NA")) {
			String startDateTime = startDate.replace("T", " ");
			String endDateTime = endDate.replace("T", " ");
			list = currentPalletStockDetailsRepository.findByLoadDatetimeBetweenAndPalletCodeNot(startDateTime,
					endDateTime, "NA");
		} else {
			list = currentPalletStockDetailsRepository.findByPalletCodeNotOrderByAgeingDaysDesc("NA");
		}

		if (filterList.size() != 0) {
			for (String filter : filterList) {
				switch (filter) {
				case "productVariantCode":
					list = list.stream().filter(predProductVariantCode).collect(Collectors.toList());
					break;
				case "areaName":
					list = list.stream().filter(predArea).collect(Collectors.toList());
					break;
				case "floorName":
					list = list.stream().filter(predFloor).collect(Collectors.toList());
					break;
				case "productName":
					list = list.stream().filter(predProductName).collect(Collectors.toList());
					break;
				case "palletStatusname":
					list = list.stream().filter(predPalletStatusname).collect(Collectors.toList());
					break;
				}

			}
		}

		return list;
	}



	public List<CurrentPalletStockDetailsEntity> findbyloaddatetime() {
		try {
			Date date = new Date();
			String strDateFormat = "yyyy-MM-dd";
			DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
			String currentDateTime = dateFormat.format(date);
		
			return currentPalletStockDetailsRepository.getAllCurrentPalletStockDetailsBetweenDates(
					currentDateTime + " " + "00:00:00", currentDateTime + " " + "23:59:59");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public ResponseEntity<Object> addCurrentPalletStockDetails(
			CurrentPalletStockDetailsEntity currentPalletStockDetailsEntity) {
		List<MasterProductVariantDetailsEntity> productVirantlist = null;
		List<MasterPalletInformationEntity> palletInformationList = null;
		try {
			palletInformationList = masterPalletInformationDetailsRepositoryInstance
					.findByPalletCode(currentPalletStockDetailsEntity.getPalletCode());


			currentPalletStockDetailsEntity
					.setPalletInformationId(palletInformationList.get(0).getPalletInformationId());

			List<CurrentPalletStockDetailsEntity> currentpalletList = currentPalletStockDetailsRepository
					.findByPalletCodeAndProductVariantCode(currentPalletStockDetailsEntity.getPalletCode(), "NA");

			if (currentpalletList.size() > 0) {
				currentPalletStockDetailsEntity
						.setCurrentPalletStockDetailsId(currentpalletList.get(0).getCurrentPalletStockDetailsId());
			}

			productVirantlist = masterProductVariantDetailsRepositoryInstance
					.findByProductVariantCode(currentPalletStockDetailsEntity.getProductVariantCode());



			if (productVirantlist.size() > 0) {
				currentPalletStockDetailsEntity.setProductVariantName(productVirantlist.get(0).getProductVariantname());
				currentPalletStockDetailsEntity.setProductVariantId(productVirantlist.get(0).getProductVariantId());
				currentPalletStockDetailsEntity.setProductId(productVirantlist.get(0).getProductId());
				currentPalletStockDetailsEntity.setProductName(productVirantlist.get(0).getProductName());

				currentPalletStockDetailsEntity.setAreaName("NA");
				currentPalletStockDetailsEntity.setFloorName("NA");
				currentPalletStockDetailsEntity.setRackName("NA");
				currentPalletStockDetailsEntity.setRackSide("NA");
				currentPalletStockDetailsEntity.setPositionName("NA");

				Date dNow = new Date();
				SimpleDateFormat ft = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
				String date = ft.format(dNow);
				currentPalletStockDetailsEntity.setLoadDatetime(date);
				if (currentpalletList.size() > 0) {
					currentPalletStockDetailsRepository.updateNApalletData(
							currentPalletStockDetailsEntity.getProductVariantId(),
							currentPalletStockDetailsEntity.getProductVariantCode(),
							currentPalletStockDetailsEntity.getProductVariantName(),
							currentPalletStockDetailsEntity.getQuantity(),
							currentPalletStockDetailsEntity.getLoadDatetime(),
							currentPalletStockDetailsEntity.getUserId(), currentPalletStockDetailsEntity.getUserName(),
							currentPalletStockDetailsEntity.getCurrentPalletStockDetailsId());


				} else {
					
					currentPalletStockDetailsRepository.save(currentPalletStockDetailsEntity);


					return ResponseHandler.generateResponse("Material loaded sucessfully", HttpStatus.OK, null);
				}
			} else {
				return ResponseHandler.generateResponse("Material is not available in master material details",
						HttpStatus.BAD_REQUEST, null);
			}
		} catch (Exception ex) {
			
			return ResponseHandler.generateResponse("Error occurred at server side", HttpStatus.INTERNAL_SERVER_ERROR,
					null);
		}
		return null;

	}

	
	

//	public ResponseEntity<Object> updateCurrentStockDetails(
//			CurrentPalletStockDetailsEntity currentPalletStockDetailsEntity) {
//		if (currentPalletStockDetailsEntity != null) {
//			List<MasterProductVariantDetailsEntity> findByProductVariantCode = masterProductVariantDetailsRepository
//					.findByProductVariantCode(currentPalletStockDetailsEntity.getProductVariantCode());
//			if ("NA".equals(currentPalletStockDetailsEntity.getProductVariantCode())
//					|| findByProductVariantCode.size() == 0) {
//				return ResponseHandler.generateResponse("Product variant code not available", HttpStatus.IM_USED, null);
//			}
//
//			if (findByProductVariantCode.get(0).getProductVariantIsActive() == 0) {
//				return ResponseHandler.generateResponse("Part Number is not Active", HttpStatus.ALREADY_REPORTED, null);
//			}
//
//			int positionId = currentPalletStockDetailsEntity.getPositionId();
//			
//			if (positionId % 2 == 0) {
//				int positionId1 = positionId - 1;
//				List<CurrentPalletStockDetailsEntity> findByPositionId = currentPalletStockDetailsRepository
//						.findByPositionId(positionId1);
//				List<CurrentPalletStockDetailsEntity> findByPositionId2 = currentPalletStockDetailsRepository
//						.findByPositionId(positionId);
//				
//				if (!Objects.equals(findByPositionId.get(0).getProductVariantCode(), findByPositionId2.get(0)
//						.getProductVariantCode())) {
//					return ResponseHandler.generateResponse("Product variant does not match.", HttpStatus.ACCEPTED,
//							null);
//				}
//				if (!Objects.equals(findByPositionId.get(0).getProductName(), findByPositionId2.get(0).getProductName())) {
//					return ResponseHandler.generateResponse("Product name does not match.", HttpStatus.CREATED, null);
//
//				}
//				currentPalletStockDetailsEntity
//						.setProductVariantId(findByProductVariantCode.get(0).getProductVariantId());
//				currentPalletStockDetailsEntity
//						.setProductVariantName(findByProductVariantCode.get(0).getProductVariantname());
//
//				currentPalletStockDetailsEntity.setProductName(findByProductVariantCode.get(0).getProductName());
//				currentPalletStockDetailsEntity.setProductId(findByProductVariantCode.get(0).getProductId());
//
//				Date dNow = new Date();
//				SimpleDateFormat ft = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
//				String date = ft.format(dNow);
//				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
//				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//				String name = authentication.getName();
//				auditTrailDetailsEntity.setOperatorActions(
//						"Position updated " + currentPalletStockDetailsEntity.getPositionName() + " by " + name
//								+ " having pallet code " + currentPalletStockDetailsEntity.getPalletCode());
//				auditTrailDetailsEntity.setField("Data updated from 2D Layout");
//
//				auditTrailDetailsEntity.setReason("Updation");
//
//				auditTrailDetailsEntity.setUsername(name);
//				auditTrailDetailsEntity.setDatetimeC(date);
//				auditTrailDetailsRepository.save(auditTrailDetailsEntity);
//				currentPalletStockDetailsRepository.save(currentPalletStockDetailsEntity);
//				return ResponseHandler.generateResponse("Data Updated Successfully.", HttpStatus.OK,
//						currentPalletStockDetailsEntity);
//			} else {
//				int positionId1 = positionId + 1;
//				List<CurrentPalletStockDetailsEntity> findByPositionId = currentPalletStockDetailsRepository
//						.findByPositionId(positionId1);
//				List<CurrentPalletStockDetailsEntity> findByPositionId2 = currentPalletStockDetailsRepository
//						.findByPositionId(positionId);
//
//				
//				
//				if (!Objects.equals(findByPositionId.get(0).getProductVariantCode(), findByPositionId2.get(0)
//						.getProductVariantCode())) {
//					return ResponseHandler.generateResponse("Product variant does not match.", HttpStatus.ACCEPTED,
//							null);
//				}
//				if (!Objects.equals(findByPositionId.get(0).getProductName(), findByPositionId2.get(0).getProductName())) {
//					return ResponseHandler.generateResponse("Product name does not match.", HttpStatus.CREATED, null);
//
//				}
//
//				currentPalletStockDetailsEntity
//						.setProductVariantId(findByProductVariantCode.get(0).getProductVariantId());
//				currentPalletStockDetailsEntity
//						.setProductVariantName(findByProductVariantCode.get(0).getProductVariantname());
//
//				currentPalletStockDetailsEntity.setProductName(findByProductVariantCode.get(0).getProductName());
//				currentPalletStockDetailsEntity.setProductId(findByProductVariantCode.get(0).getProductId());
//
//				Date dNow = new Date();
//				SimpleDateFormat ft = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
//				String date = ft.format(dNow);
//				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
//				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//				String name = authentication.getName();
//				auditTrailDetailsEntity.setOperatorActions(
//						"Position updated " + currentPalletStockDetailsEntity.getPositionName() + " by " + name
//								+ " having pallet code " + currentPalletStockDetailsEntity.getPalletCode());
//				auditTrailDetailsEntity.setField("Data updated from 2D Layout");
//
//				auditTrailDetailsEntity.setReason("Updation");
//
//				auditTrailDetailsEntity.setUsername(name);
//				auditTrailDetailsEntity.setDatetimeC(date);
//				auditTrailDetailsRepository.save(auditTrailDetailsEntity);
//				currentPalletStockDetailsRepository.save(currentPalletStockDetailsEntity);
//				return ResponseHandler.generateResponse("Data Updated Successfully.", HttpStatus.OK,
//						currentPalletStockDetailsEntity);
//
//			}
//
//		} else {
//			return ResponseHandler.generateResponse("Data not Updated.", HttpStatus.ALREADY_REPORTED, null);
//		}
//	}

	
	
	public ResponseEntity<Object> updateCurrentStockDetails(
	        CurrentPalletStockDetailsEntity currentPalletStockDetailsEntity) {

	    if (currentPalletStockDetailsEntity == null) {
	        return ResponseHandler.generateResponse("Data not Updated.", HttpStatus.ALREADY_REPORTED, null);
	    }

	    List<MasterProductVariantDetailsEntity> findByProductVariantCode =
	            masterProductVariantDetailsRepository.findByProductVariantCode(
	                    currentPalletStockDetailsEntity.getProductVariantCode());

	    if ("NA".equals(currentPalletStockDetailsEntity.getProductVariantCode())
	            || findByProductVariantCode.isEmpty()) {
	        return ResponseHandler.generateResponse("Product variant code not available",
	                HttpStatus.IM_USED, null);
	    }

	    if (findByProductVariantCode.get(0).getProductVariantIsActive() == 0) {
	        return ResponseHandler.generateResponse("Part Number is not Active",
	                HttpStatus.ALREADY_REPORTED, null);
	    }

	    int currentPos = currentPalletStockDetailsEntity.getPositionId();
	    int oppositePos = (currentPos % 2 == 0) ? currentPos - 1 : currentPos + 1;

	    List<CurrentPalletStockDetailsEntity> currentPositionData =
	            currentPalletStockDetailsRepository.findByPositionId(currentPos);

	    List<CurrentPalletStockDetailsEntity> oppositePositionData =
	            currentPalletStockDetailsRepository.findByPositionId(oppositePos);

	    // Validate only when opposite position contains a valid product palletStatusId == 1
	    if (!oppositePositionData.isEmpty() 
	            && oppositePositionData.get(0).getPalletStatusId() != null 
	            && oppositePositionData.get(0).getPalletStatusId() == 1) {

	        if (!Objects.equals(oppositePositionData.get(0).getProductVariantCode(),
	                currentPositionData.get(0).getProductVariantCode())) {
	            return ResponseHandler.generateResponse("Product variant does not match.",
	                    HttpStatus.ACCEPTED, null);
	        }

	        if (!Objects.equals(oppositePositionData.get(0).getProductName(),
	                currentPositionData.get(0).getProductName())) {
	            return ResponseHandler.generateResponse("Product name does not match.",
	                    HttpStatus.CREATED, null);
	        }
	    }

	    // Set Updated Product Details
	    MasterProductVariantDetailsEntity variantInfo = findByProductVariantCode.get(0);

	    currentPalletStockDetailsEntity.setProductVariantId(variantInfo.getProductVariantId());
	    currentPalletStockDetailsEntity.setProductVariantName(variantInfo.getProductVariantname());
	    currentPalletStockDetailsEntity.setProductId(variantInfo.getProductId());
	    currentPalletStockDetailsEntity.setProductName(variantInfo.getProductName());

	    // Audit Trail
	    Date dNow = new Date();
	    SimpleDateFormat ft = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
	    String datetime = ft.format(dNow);

	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String userName = authentication.getName();

	    AuditTrailDetailsEntity auditTrail = new AuditTrailDetailsEntity();
	    auditTrail.setOperatorActions("Position updated " + currentPalletStockDetailsEntity.getPositionName()
	            + " by " + userName + " having pallet code " + currentPalletStockDetailsEntity.getPalletCode());
	    auditTrail.setField("Data updated from 2D Layout");
	    auditTrail.setReason("Updation");
	    auditTrail.setUsername(userName);
	    auditTrail.setDatetimeC(datetime);

	    auditTrailDetailsRepository.save(auditTrail);
	    
	    
	    MasterPalletInformationEntity findByPalletInformationId = masterPalletInformationDetailsRepositoryInstance
				.findByPalletInformationId(currentPalletStockDetailsEntity.getPalletInformationId());
		findByPalletInformationId.setQualityStatus(currentPalletStockDetailsEntity.getQualityStatus());
		masterPalletInformationDetailsRepositoryInstance.save(findByPalletInformationId);
		
		currentPalletStockDetailsRepository.save(currentPalletStockDetailsEntity);
	
	    return ResponseHandler.generateResponse("Data Updated Successfully.", HttpStatus.OK,
	            currentPalletStockDetailsEntity);
	}

	
	
	
	
	public ResponseEntity<Object> deletCurrentStockDetailsByPalletCode(int positionId) {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
		String date = ft.format(dNow);
		CurrentPalletStockDetailsEntity currentStockDetailsEntityInsrt = new CurrentPalletStockDetailsEntity();
		List<CurrentPalletStockDetailsEntity> findByPositionId = currentPalletStockDetailsRepository
				.findByPositionId(positionId);
		if (findByPositionId != null) {
			for (int i = 0; i < findByPositionId.size(); i++) {
				// findByPositionId.get(i).setCurrentPalletStockDetailsId(0);
				findByPositionId.get(i).setPalletCode("NA");
				findByPositionId.get(i).setPalletInformationId(0);
				findByPositionId.get(i).setSerialNumber(0);
				findByPositionId.get(i).setProductVariantId(0);
				findByPositionId.get(i).setProductVariantCode("NA");
				findByPositionId.get(i).setProductVariantName("NA");
				findByPositionId.get(i).setProductId(0);
				findByPositionId.get(i).setProductName("NA");
				findByPositionId.get(i).setPalletStatusId(0);
				findByPositionId.get(i).setPalletStatusname("EMPTY");
				findByPositionId.get(i).setAgeingDays(0);
				findByPositionId.get(i).setQuantity(0);
				findByPositionId.get(i).setQualityStatus("NA");
				findByPositionId.get(i).setBatchNumber("NA");
				findByPositionId.get(i).setModelNumber("NA");
				findByPositionId.get(i).setLocation("NA");
				// findByPositionId.get(i).setPositionName("NA");
				findByPositionId.get(i).setLoadDatetime(date);
				findByPositionId.get(i).setExpiryDate("9999-12-31 00:00:00.0000000");
				findByPositionId.get(i).setUserId(0);
				findByPositionId.get(i).setUserName("NA");
				currentPalletStockDetailsRepository.save(findByPositionId.get(i));
			}

			return ResponseHandler.generateResponse("Material deleted sucessfully", HttpStatus.OK, null);

		}
		return ResponseHandler.generateResponse("error occured", HttpStatus.BAD_REQUEST, null);
	}

	@SuppressWarnings("null")
	public int findBEVCurrentStockDetails() {
		try {

			List<CurrentPalletStockDetailsEntity> findByProductName = currentPalletStockDetailsRepository
					.findByProductNameAndPalletCodeNot("BEV", "NA");
			bevCurrentStockCount = (int) findByProductName.stream().filter(e -> e.getPalletStatusId() != 3).count();
			currentStockDetails.setBevCurrentStockCount(bevCurrentStockCount);
			dashboardEntity.setBevCurrentStockCount(bevCurrentStockCount);
			return dashboardEntity.getBevCurrentStockCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (Integer) null;
	}

	@SuppressWarnings("null")
	public int findS230CurrentStockDetails() {
		try {

			List<CurrentPalletStockDetailsEntity> findByProductName = currentPalletStockDetailsRepository
					.findByProductNameAndPalletCodeNot("S230", "NA");
			s230CurrentStockCount = (int) findByProductName.stream().filter(e -> e.getPalletStatusId() != 3).count();
			currentStockDetails.setS230CurrentStockCount(s230CurrentStockCount);
			dashboardEntity.setS230CurrentStockCount(s230CurrentStockCount);
			return dashboardEntity.getS230CurrentStockCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (Integer) null;
	}


	@Override
	public Page<CurrentPalletStockDetailsEntity> findByBEV(Pageable pageable) {
		return currentPalletStockDetailsRepository
				.findByProductNameAndPalletCodeNotAndPalletStatusIdOrderByAgeingDaysDesc(pageable, "BEV", "NA", 1);
	}

	@Override
	public Page<CurrentPalletStockDetailsEntity> findByS230(Pageable pageable) {
		return currentPalletStockDetailsRepository
				.findByProductNameAndPalletCodeNotAndPalletStatusIdOrderByAgeingDaysDesc(pageable, "S230", "NA", 1);
	}

	@Override
	public List<CurrentPalletStockDetailsEntity> findByOkAndBev(Pageable pageable) {
		List<CurrentPalletStockDetailsEntity> findByProductNameAndQualityStatusAndPalletStatusIdNot = currentPalletStockDetailsRepository
				.findByProductNameAndQualityStatusAndPalletStatusIdNot(pageable, "BEV", "OK", 3);
		return findByProductNameAndQualityStatusAndPalletStatusIdNot;
	}

	@Override
	public List<CurrentPalletStockDetailsEntity> findByOkAndS230(Pageable pageable) {
		List<CurrentPalletStockDetailsEntity> findByProductNameAndQualityStatusAndPalletStatusIdNot = currentPalletStockDetailsRepository
				.findByProductNameAndQualityStatusAndPalletStatusIdNot(pageable, "S230", "OK", 3);
		return findByProductNameAndQualityStatusAndPalletStatusIdNot;
	}

	@Override
	public List<CurrentPalletStockDetailsEntity> findByNOkAndBEV(Pageable pageable) {
		List<CurrentPalletStockDetailsEntity> findByProductNameAndQualityStatusAndPalletStatusIdNot = currentPalletStockDetailsRepository
				.findByProductNameAndQualityStatusAndPalletStatusIdNot(pageable, "BEV", "NOK", 3);
		return findByProductNameAndQualityStatusAndPalletStatusIdNot;
	}

	@Override
	public List<CurrentPalletStockDetailsEntity> findByNOkAndS230(Pageable pageable) {
		List<CurrentPalletStockDetailsEntity> findByProductNameAndQualityStatusAndPalletStatusIdNot = currentPalletStockDetailsRepository
				.findByProductNameAndQualityStatusAndPalletStatusIdNot(pageable, "S230", "NOK", 3);
		return findByProductNameAndQualityStatusAndPalletStatusIdNot;
	}

	public Page<CurrentPalletStockDetailsEntity> findByQualityStatus(String palletStatusname, String qualityStatus,
			Pageable pageable) {
		Page<CurrentPalletStockDetailsEntity> findByQualityStatus = currentPalletStockDetailsRepository
				.findByPalletStatusnameAndQualityStatusAndProductNameOrderByAgeingDaysDesc("FULL", qualityStatus, "BEV",
						pageable);
		return findByQualityStatus;
	}

	public Page<CurrentPalletStockDetailsEntity> findByQualityStatusS230(String palletStatusname, String qualityStatus,
			Pageable pageable) {
		Page<CurrentPalletStockDetailsEntity> findByQualityStatus = currentPalletStockDetailsRepository
				.findByPalletStatusnameAndQualityStatusAndProductNameOrderByAgeingDaysDesc("FULL", qualityStatus,
						"S230", pageable);
		return findByQualityStatus;
	}



	public ResponseEntity<Object> findByserialNumberBetween(int serialNumber1, int serialNumber2,
			String dispatchOrderNumber, int shiftId, String shiftName, String productName,String orderBatchNumber) {
		GenerateManualRetrievalOrderEntity generateManualRetrievalOrderEntity = new GenerateManualRetrievalOrderEntity();
		Set<Integer> set1 = new HashSet<>();
		Set<String> set2 = new HashSet<>();
		try {
			List<GenerateRetrivalMissionTypeEntity> entries = generateRetrivalMissionTypeRepository.findAll();
			List<MasterProductDetailsEntity> findByproductName2 = masterProductDetailsRepository
					.findByproductName(productName);
			int productId = findByproductName2.get(0).getProductId();

			MasterStationTagDetailsEntity findByPlcTagName = masterStationTagDetailsRepository
					.findByPlcTagName("ATS.WMS_STATION.STACKER_1_DUMP_TANK_HEALTHY");
			if (!"1".equals(findByPlcTagName.getCurrentValue())) {

				return ResponseHandler.generateResponse(productName + "Dump Tank Not healthy for Area-2",
						HttpStatus.IM_USED, null);
			}

			MasterStationTagDetailsEntity findByPlcTagName2 = masterStationTagDetailsRepository
					.findByPlcTagName("ATS.WMS_STATION.STACKER_2_DUMP_TANK_HEALTHY");
			if (!"1".equals(findByPlcTagName2.getCurrentValue())) {

				return ResponseHandler.generateResponse(productName + "Dump Tank Not healthy for Area-2",
						HttpStatus.MULTI_STATUS, null);
			}

			List<CurrentPalletStockDetailsEntity> findByProductName = currentPalletStockDetailsRepository
					.findByProductName(productName);
			if (findByProductName.isEmpty()) {
				return ResponseHandler.generateResponse(productName + "Product Name not found",
						HttpStatus.ALREADY_REPORTED, generateManualRetrievalOrderEntity);
			}

			List<CurrentPalletStockDetailsEntity> findByserialNumberBetween = currentPalletStockDetailsRepository
					.findByserialNumberBetweenAndProductName(serialNumber1, serialNumber2, productName);

			if (findByserialNumberBetween.isEmpty()) {
				for (int i = serialNumber1; i < serialNumber2; i++) {
					set1.add(i);
				}
				return ResponseHandler.generateResponse(
						"Mannual dispatch failed due to Quantity available is Zero" + "\n"
								+ "These serial numbers are not found : " + "\n" + set1 + "\n",
						HttpStatus.ALREADY_REPORTED, generateManualRetrievalOrderEntity);
			}
			Map<Integer, String> map = new HashMap<>();


			for (int x = serialNumber1; x <= serialNumber2; x++) {
				List<CurrentPalletStockDetailsEntity> findByserialNumber = currentPalletStockDetailsRepository
						.findByserialNumber(x);
				if (findByserialNumber.isEmpty()) {
					set1.add(x);
				}
				for (CurrentPalletStockDetailsEntity currentPalletStockDetailsEntity : findByserialNumber) {
					String positionName = currentPalletStockDetailsEntity.getPositionName();
					List<MasterPositionDetailsEntity> findByPositionName = masterPositionDetailsRepository
							.findByPositionName(positionName);
					for (MasterPositionDetailsEntity masterPositionDetailsEntity : findByPositionName) {
						if (masterPositionDetailsEntity.getPositionIsActive() == 0) {
							set2.add(masterPositionDetailsEntity.getPositionName());
						}
					}
				}
			}

			Iterator<Integer> iter = set1.iterator();
			while (iter.hasNext()) {
				Integer s = iter.next();
				List<BufferDetailsEntity> findByserialNumber = bufferDetailsRepository
						.findByserialNumberAndProductName(s, productName);
				boolean found = false;
				for (BufferDetailsEntity bufferDetailsEntity : findByserialNumber) {
					if (s.equals(bufferDetailsEntity.getSerialNumber())) {
						found = true;
						break;
					}
				}

			}

			System.out.println("set1::" + set1);

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String name = authentication.getName();
		

			int plannedQuantity = serialNumber2 - serialNumber1 + 1;
			int totalquantity = 0;
			int totalBufferQuantity = 0;
			String key = "";
			int value = 0;
			for (int i = 0; i < findByserialNumberBetween.size(); i++) {
				String productVariantCode = findByserialNumberBetween.get(i).getProductVariantCode();
				List<MasterProductVariantDetailsEntity> findByproductVariantCode = masterProductVariantDetailsRepositoryInstance
						.findByproductVariantCode(productVariantCode);

				List<MasterProductVariantDetailsEntity> collect = findByproductVariantCode.stream()
						.filter(e -> e.getProductVariantIsActive() == 0).collect(Collectors.toList());

				for (int j = 0; j < collect.size(); j++) {
					List<CurrentPalletStockDetailsEntity> findByproductVariantCode2 = currentPalletStockDetailsRepository
							.findByproductVariantCodeAndSerialNumberBetween(collect.get(j).getProductVariantCode(),
									serialNumber1, serialNumber2);



					for (CurrentPalletStockDetailsEntity currentPalletStockDetailsEntity : findByproductVariantCode2) {
						map.put(currentPalletStockDetailsEntity.getSerialNumber(),
								currentPalletStockDetailsEntity.getProductVariantCode());
						System.out.println("map::" + map);
						
					}
				}

				for (int k = 0; k < findByproductVariantCode.size(); k++) {
					if (findByproductVariantCode.get(k).getProductVariantIsActive() == 1) {

						List<MasterPositionDetailsEntity> findByPositionName = masterPositionDetailsRepository
								.findByPositionName(findByserialNumberBetween.get(i).getPositionName());
						for (MasterPositionDetailsEntity masterPositionDetailsEntity : findByPositionName) {
							if (masterPositionDetailsEntity.getPositionIsActive() == 1) {
								System.out.println("current stock active::"
										+ findByproductVariantCode.get(k).getProductVariantCode());
								int quantity = findByserialNumberBetween.get(i).getQuantity();
								totalquantity = totalquantity + quantity;
							}
								
						}

					}
				}
			}

			List<BufferDetailsEntity> findByproductVariantCode2 = bufferDetailsRepository
					.findBySerialNumberBetweenAndBufferIsDeletedAndProductName(serialNumber1, serialNumber2, 0,
							productName);

			for (int j = 0; j < findByproductVariantCode2.size(); j++) {
				String productVariantCode = findByserialNumberBetween.get(j).getProductVariantCode();

				List<MasterProductVariantDetailsEntity> findByproductVariantCode = masterProductVariantDetailsRepositoryInstance
						.findByproductVariantCode(productVariantCode);
				List<MasterProductVariantDetailsEntity> collect = findByproductVariantCode.stream()
						.filter(e -> e.getProductVariantIsActive() == 0).collect(Collectors.toList());
				for (int k = 0; k < collect.size(); k++) {
					List<BufferDetailsEntity> findByproductVariantCode3 = bufferDetailsRepository
							.findByproductVariantCodeAndSerialNumberBetween(collect.get(k).getProductVariantCode(),
									serialNumber1, serialNumber2);

					for (BufferDetailsEntity bufferDetailsEntity : findByproductVariantCode3) {
						map.put(bufferDetailsEntity.getSerialNumber(), bufferDetailsEntity.getProductVariantCode());
						
					}
				}

				for (int l = 0; l < findByproductVariantCode.size(); l++) {
					if (findByproductVariantCode.get(l).getProductVariantIsActive() == 1) {
						System.out.println("buffer active " + findByproductVariantCode.get(l).getProductVariantCode());
						int quantity = findByproductVariantCode2.get(j).getQuantity();
						totalBufferQuantity = totalBufferQuantity + quantity;
					}
				}
			}
			if (plannedQuantity <= totalquantity) {
				Date dNow = new Date();
				SimpleDateFormat ft = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
				String date = ft.format(dNow);
				generateManualRetrievalOrderEntity.setCreatedDatetime(date);
				generateManualRetrievalOrderEntity.setPlannedQuantity(totalquantity + totalBufferQuantity);
				generateManualRetrievalOrderEntity.setBalanceQuantity(totalquantity + totalBufferQuantity);
				generateManualRetrievalOrderEntity.setAcutualQuantity(0);
//				generateManualRetrievalOrderEntity.setIsDispatchStart(0);
				if (!entries.isEmpty()) {
			        GenerateRetrivalMissionTypeEntity data = entries.get(0); 
			        if (data.getDispatchTriggered() == 0) {
			            generateManualRetrievalOrderEntity.setIsDispatchStart(0);
			            generateManualRetrievalOrderEntity.setDispatchStatus("READY");
			        } else {
			            generateManualRetrievalOrderEntity.setIsDispatchStart(1);
			            generateManualRetrievalOrderEntity.setDispatchStatus("IN_PROGRESS");
			        }
			    } else {
			        System.out.println("No entry found in ats_wms_generate_retrieval_mission_type table.");
			    }
				generateManualRetrievalOrderEntity.setDispatchOrderNumber(dispatchOrderNumber);
				generateManualRetrievalOrderEntity.setProductVariantCode("NA");
				generateManualRetrievalOrderEntity.setShiftName(shiftName);
				generateManualRetrievalOrderEntity.setProductName(productName);
				generateManualRetrievalOrderEntity.setProductId(productId);
				generateManualRetrievalOrderEntity.setProductVariantName("NA");
//				generateManualRetrievalOrderEntity.setDispatchStatus("READY");
				generateManualRetrievalOrderEntity.setShiftId(shiftId);
				generateManualRetrievalOrderEntity.setUserName(name);
				generateManualRetrievalOrderEntity.setLoadDatetime(date); // current time added
				generateManualRetrievalOrderEntity.setMfgDate(date);
				generateManualRetrievalOrderEntity.setMfgShift(shiftName);
				List<MasterUserDetailsEntity> findByuserName = masterUserDetailsRepository.findByuserName(name);
				int userId = findByuserName.get(0).getUserId();
				generateManualRetrievalOrderEntity.setUserId(userId);
				generateManualRetrievalOrderRepository.save(generateManualRetrievalOrderEntity);
				
				
				OrderSourceDetailsEntity orderSourceDetailsEntity=new OrderSourceDetailsEntity();
				orderSourceDetailsEntity.setOrderId(generateManualRetrievalOrderEntity.getDispatchHistoryId());
				orderSourceDetailsEntity.setOrderNumber(generateManualRetrievalOrderEntity.getDispatchOrderNumber());
				orderSourceDetailsEntity.setOrderSourceName(generateManualRetrievalOrderEntity.getOrderSourceDetails());
				orderSourceDetailsEntity.setPartNumber("NA");
				orderSourceDetailsEntity.setQuantity(totalquantity + totalBufferQuantity);
				orderSourceDetailsEntity.setIsOrderCancelledFromMes(0);
				orderSourceDetailsEntity.setIsOrderCancelledFromWms(0);
				orderSourceDetailsEntity.setIsOrderDeletedFromWms(0);
				orderSourceDetailsEntity.setSourceCDateTime(date);
				orderSourceDetailsEntity.setOrderBatchNumber(orderBatchNumber);
				orderSourceDetailsEntity.setOrderSourceName("NON-MES");
				orderSourceDetailsEntity.setVinNumber("NA");
				orderSourceDetailsRepository.save(orderSourceDetailsEntity);
				
				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
				auditTrailDetailsEntity.setOperatorActions("Retrival order generated of Dispatch Number  :  "
						+ generateManualRetrievalOrderEntity.getDispatchOrderNumber() + " and Quantity is  "
						+ generateManualRetrievalOrderEntity.getPlannedQuantity());
				auditTrailDetailsEntity.setField("Dispatch");
				auditTrailDetailsEntity.setAfterValue(0);
				auditTrailDetailsEntity.setBeforeValue(0);
				auditTrailDetailsEntity.setReason("Bulk Dispatch");

				auditTrailDetailsEntity.setUsername(name);
				auditTrailDetailsEntity.setDatetimeC(date);
				auditTrailDetailsRepository.save(auditTrailDetailsEntity);
				System.out.println("1");

			} else if (plannedQuantity <= (totalquantity + totalBufferQuantity)) {
				Date dNow = new Date();
				SimpleDateFormat ft = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
				String date = ft.format(dNow);
				generateManualRetrievalOrderEntity.setCreatedDatetime(date);
				generateManualRetrievalOrderEntity.setPlannedQuantity(totalquantity + totalBufferQuantity);
				generateManualRetrievalOrderEntity.setBalanceQuantity(totalquantity + totalBufferQuantity);
				generateManualRetrievalOrderEntity.setAcutualQuantity(0);
//				generateManualRetrievalOrderEntity.setIsDispatchStart(0);
				if (!entries.isEmpty()) {
			        GenerateRetrivalMissionTypeEntity data = entries.get(0); 
			        if (data.getDispatchTriggered() == 0) {
			            generateManualRetrievalOrderEntity.setIsDispatchStart(0);
			            generateManualRetrievalOrderEntity.setDispatchStatus("READY");
			        } else {
			            generateManualRetrievalOrderEntity.setIsDispatchStart(1);
			            generateManualRetrievalOrderEntity.setDispatchStatus("IN_PROGRESS");
			        }
			    } else {
			        System.out.println("No entry found in ats_wms_generate_retrieval_mission_type table.");
			    }
				generateManualRetrievalOrderEntity.setDispatchOrderNumber(dispatchOrderNumber);
				generateManualRetrievalOrderEntity.setProductVariantCode("NA");
				generateManualRetrievalOrderEntity.setShiftName(shiftName);
				generateManualRetrievalOrderEntity.setProductName(productName);
				generateManualRetrievalOrderEntity.setProductId(productId);
				generateManualRetrievalOrderEntity.setProductVariantName("NA");
//				generateManualRetrievalOrderEntity.setDispatchStatus("READY");

//			generateManualRetrievalOrderEntity.setIsOrderCancelled(1);
				generateManualRetrievalOrderEntity.setShiftId(shiftId);
				generateManualRetrievalOrderEntity.setLoadDatetime(date); // current time added
				generateManualRetrievalOrderEntity.setMfgDate(date);
				generateManualRetrievalOrderEntity.setMfgShift(shiftName);
				generateManualRetrievalOrderRepository.save(generateManualRetrievalOrderEntity);
				
				OrderSourceDetailsEntity orderSourceDetailsEntity=new OrderSourceDetailsEntity();
				orderSourceDetailsEntity.setOrderId(generateManualRetrievalOrderEntity.getDispatchHistoryId());
				orderSourceDetailsEntity.setOrderNumber(generateManualRetrievalOrderEntity.getDispatchOrderNumber());
				orderSourceDetailsEntity.setOrderSourceName(generateManualRetrievalOrderEntity.getOrderSourceDetails());
				orderSourceDetailsEntity.setPartNumber("NA");
				orderSourceDetailsEntity.setQuantity(totalquantity + totalBufferQuantity);
				orderSourceDetailsEntity.setIsOrderCancelledFromMes(0);
				orderSourceDetailsEntity.setIsOrderCancelledFromWms(0);
				orderSourceDetailsEntity.setIsOrderDeletedFromWms(0);
				orderSourceDetailsEntity.setSourceCDateTime(date);
				orderSourceDetailsEntity.setOrderBatchNumber(orderBatchNumber);
				orderSourceDetailsEntity.setOrderSourceName("NON-MES");
				orderSourceDetailsEntity.setVinNumber("NA");
				orderSourceDetailsRepository.save(orderSourceDetailsEntity);
				
				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
				auditTrailDetailsEntity.setOperatorActions("Retrival order generated of Dispatch Number  :  "
						+ generateManualRetrievalOrderEntity.getDispatchOrderNumber() + " and Quantity is  "
						+ generateManualRetrievalOrderEntity.getPlannedQuantity());

				auditTrailDetailsEntity.setField("Dispatch");
				auditTrailDetailsEntity.setAfterValue(0);
				auditTrailDetailsEntity.setBeforeValue(0);
				auditTrailDetailsEntity.setReason("Bulk Dispatch");

				auditTrailDetailsEntity.setUsername(name);
				auditTrailDetailsEntity.setDatetimeC(date);
				auditTrailDetailsRepository.save(auditTrailDetailsEntity);
				System.out.println("2");

				return ResponseHandler.generateResponse("Mannual dispatch sucessfully done", HttpStatus.ACCEPTED,
						generateManualRetrievalOrderEntity);
			} else if ((totalquantity + totalBufferQuantity) == 0) {
				System.out.println("totalquantity + totalBufferQuantity :: " + (totalquantity + totalBufferQuantity));

				return ResponseHandler.generateResponse(
						"Mannual dispatch failed due to Quantity available is Zero" + "\n"
								+ "Following Part No. with Serial No. are Inactive." + "\n"
								+ "Part No. \t Serial No.  \n" + map + "\n" + "These serial numbers are not found : "
								+ "\n" + set1 + "\n" + "These positions are inactive : " + "\n" + set2,
						HttpStatus.ALREADY_REPORTED, generateManualRetrievalOrderEntity);
			}

			else {


				return ResponseHandler.generateResponse("Insufficient Quantity. Available quantity is: "
						+ (totalquantity + totalBufferQuantity) + "\n" + "Do you want to dispatch?" + "\n"
						+ "Following Part No. with Serial No. are Inactive." + "\n" + "Part No. \t Serial No.  \n" + map
						+ "\n" + "These serial numbers are not found : " + "\n" + set1 + "\n"
						+ "These positions are inactive : " + "\n" + set2, HttpStatus.CREATED, null);

			}

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseHandler.generateResponse("Unable to add", HttpStatus.INTERNAL_SERVER_ERROR,
					generateManualRetrievalOrderEntity);
		}
		return ResponseHandler.generateResponse("Mannual dispatch sucessfully done", HttpStatus.OK,
				generateManualRetrievalOrderEntity);
	}

	public ResponseEntity<Object> findByserialNumberBetween1(int serialNumber1, int serialNumber2,
			String dispatchOrderNumber, int shiftId, String shiftName, String productName,String orderBatchNumber) {
		int totalquantity = 0;
		int totalBufferQuantity = 0;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();
		System.out.println(" name :: " + name);
		GenerateManualRetrievalOrderEntity generateManualRetrievalOrderEntity = new GenerateManualRetrievalOrderEntity();
		try {
			List<GenerateRetrivalMissionTypeEntity> entries = generateRetrivalMissionTypeRepository.findAll();
			List<MasterProductDetailsEntity> findByproductName2 = masterProductDetailsRepository
					.findByproductName(productName);
			int productId = findByproductName2.get(0).getProductId();

			MasterStationTagDetailsEntity findByPlcTagName = masterStationTagDetailsRepository
					.findByPlcTagName("ATS.WMS_STATION.STACKER_1_DUMP_TANK_HEALTHY");
			if (!"1".equals(findByPlcTagName.getCurrentValue())) {

				return ResponseHandler.generateResponse(productName + "Dump Tank Not healthy for Area-2",
						HttpStatus.IM_USED, null);
			}

			MasterStationTagDetailsEntity findByPlcTagName2 = masterStationTagDetailsRepository
					.findByPlcTagName("ATS.WMS_STATION.STACKER_2_DUMP_TANK_HEALTHY");
			if (!"1".equals(findByPlcTagName2.getCurrentValue())) {

				return ResponseHandler.generateResponse(productName + "Dump Tank Not healthy for Area-2",
						HttpStatus.MULTI_STATUS, null);
			}

			List<CurrentPalletStockDetailsEntity> findByserialNumberBetween = currentPalletStockDetailsRepository
					.findByserialNumberBetweenAndProductName(serialNumber1, serialNumber2, productName);
			for (int i = 0; i < findByserialNumberBetween.size(); i++) {
				String productVariantCode = findByserialNumberBetween.get(i).getProductVariantCode();
				List<MasterProductVariantDetailsEntity> findByproductVariantCode = masterProductVariantDetailsRepositoryInstance
						.findByproductVariantCode(productVariantCode);
				for (int k = 0; k < findByproductVariantCode.size(); k++) {
					if (findByproductVariantCode.get(k).getProductVariantIsActive() == 1) {

						int quantity = findByserialNumberBetween.get(i).getQuantity();
						totalquantity = totalquantity + quantity;
					}
				}
			}
			List<BufferDetailsEntity> findByproductVariantCode2 = bufferDetailsRepository
					.findBySerialNumberBetweenAndBufferIsDeletedAndProductName(serialNumber1, serialNumber2, 0,
							productName);

			for (int j = 0; j < findByproductVariantCode2.size(); j++) {
				String productVariantCode = findByserialNumberBetween.get(j).getProductVariantCode();
				List<MasterProductVariantDetailsEntity> findByproductVariantCode = masterProductVariantDetailsRepositoryInstance
						.findByproductVariantCode(productVariantCode);
				for (int l = 0; l < findByproductVariantCode.size(); l++) {
					if (findByproductVariantCode.get(l).getProductVariantIsActive() == 1) {
						int quantity = findByproductVariantCode2.get(j).getQuantity();
						totalBufferQuantity = totalBufferQuantity + quantity;
					}
				}
			}
			Date dNow = new Date();
			SimpleDateFormat ft = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
			String date = ft.format(dNow);
			generateManualRetrievalOrderEntity.setCreatedDatetime(date);
			generateManualRetrievalOrderEntity.setAcutualQuantity(0);
//			generateManualRetrievalOrderEntity.setIsDispatchStart(0);
			if (!entries.isEmpty()) {
		        GenerateRetrivalMissionTypeEntity data = entries.get(0); 
		        if (data.getDispatchTriggered() == 0) {
		            generateManualRetrievalOrderEntity.setIsDispatchStart(0);
		            generateManualRetrievalOrderEntity.setDispatchStatus("READY");
		        } else {
		            generateManualRetrievalOrderEntity.setIsDispatchStart(1);
		            generateManualRetrievalOrderEntity.setDispatchStatus("IN_PROGRESS");
		        }
		    } else {
		        System.out.println("No entry found in ats_wms_generate_retrieval_mission_type table.");
		    }
			generateManualRetrievalOrderEntity.setPlannedQuantity(totalquantity + totalBufferQuantity);
			generateManualRetrievalOrderEntity.setBalanceQuantity(totalquantity + totalBufferQuantity);
			generateManualRetrievalOrderEntity.setDispatchOrderNumber(dispatchOrderNumber);
			generateManualRetrievalOrderEntity.setProductVariantCode("NA");
			generateManualRetrievalOrderEntity.setShiftName(shiftName);
			generateManualRetrievalOrderEntity.setProductName(productName);
			generateManualRetrievalOrderEntity.setProductId(productId);
			generateManualRetrievalOrderEntity.setProductVariantName("NA");
//			generateManualRetrievalOrderEntity.setDispatchStatus("READY");
			generateManualRetrievalOrderEntity.setUserName(name);
			List<MasterUserDetailsEntity> findByuserName = masterUserDetailsRepository.findByuserName(name);
			int userId = findByuserName.get(0).getUserId();
			generateManualRetrievalOrderEntity.setUserId(userId);
//			generateManualRetrievalOrderEntity.setIsOrderCancelled(1);
			generateManualRetrievalOrderEntity.setShiftId(shiftId);
			generateManualRetrievalOrderEntity.setLoadDatetime(date); // current time added
			generateManualRetrievalOrderEntity.setMfgDate(date);
			generateManualRetrievalOrderEntity.setMfgShift(shiftName);
			generateManualRetrievalOrderRepository.save(generateManualRetrievalOrderEntity);
			
			OrderSourceDetailsEntity orderSourceDetailsEntity=new OrderSourceDetailsEntity();
			orderSourceDetailsEntity.setOrderId(generateManualRetrievalOrderEntity.getDispatchHistoryId());
			orderSourceDetailsEntity.setOrderNumber(generateManualRetrievalOrderEntity.getDispatchOrderNumber());
			orderSourceDetailsEntity.setPartNumber("NA");
			orderSourceDetailsEntity.setQuantity(totalquantity + totalBufferQuantity);
			orderSourceDetailsEntity.setIsOrderCancelledFromMes(0);
			orderSourceDetailsEntity.setIsOrderCancelledFromWms(0);
			orderSourceDetailsEntity.setIsOrderDeletedFromWms(0);
			orderSourceDetailsEntity.setSourceCDateTime(date);
			orderSourceDetailsEntity.setOrderBatchNumber(orderBatchNumber);
			orderSourceDetailsEntity.setOrderSourceName("NON-MES");
			orderSourceDetailsEntity.setVinNumber("NA");
			orderSourceDetailsRepository.save(orderSourceDetailsEntity);
			
			AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
			auditTrailDetailsEntity.setOperatorActions("Retrival order generated of Dispatch Number  : "
					+ generateManualRetrievalOrderEntity.getDispatchOrderNumber() + " and Quantity is "
					+ generateManualRetrievalOrderEntity.getPlannedQuantity());
			auditTrailDetailsEntity.setField("Dispatch");

			auditTrailDetailsEntity.setAfterValue(0);
			auditTrailDetailsEntity.setBeforeValue(0);
			auditTrailDetailsEntity.setReason("Bulk Dispatch");

			auditTrailDetailsEntity.setUsername(name);
			auditTrailDetailsEntity.setDatetimeC(date);
			auditTrailDetailsRepository.save(auditTrailDetailsEntity);
			System.out.println("Do you want to dispatch" + (totalquantity + totalBufferQuantity));

		} catch (Exception e) {
			return ResponseHandler.generateResponse("Mannual dispatch failed ", HttpStatus.BAD_REQUEST, null);
		}
		return ResponseHandler.generateResponse("Insufficient Quantity available Quantity  :  "
				+ (totalquantity + totalBufferQuantity) + " Do you want to dispatch ?", HttpStatus.CREATED,
				generateManualRetrievalOrderEntity);
	}

	public ResponseEntity<List<CurrentPalletStockDetailsEntity>> findBypalletCode1(String palletCode) {
		try {
			List<CurrentPalletStockDetailsEntity> findByPalletCode = currentPalletStockDetailsRepository
					.findByPalletCode(palletCode);
			if (!findByPalletCode.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Return 204 No Content status
			} else {
				return new ResponseEntity<>(findByPalletCode, HttpStatus.OK);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<List<MasterProductDetailsEntity>> findByProductName(String productName) {
		try {
			MasterProductDetailsEntity findByProductName = masterProductDetailsRepository
					.findByProductName(productName);
			if (findByProductName == null) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} else {
				return new ResponseEntity<>(HttpStatus.OK);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public Page<CurrentPalletStockDetailsEntity> findEmptyPalletList(Pageable pageable) {
		Page<CurrentPalletStockDetailsEntity> findEmptyPallet = currentPalletStockDetailsRepository
				.findByProductNameAndProductVariantCodeAndQuantityAndPalletStatusIdAndPalletCodeNot("BEV", "NA", 0, 3,
						"NA", pageable);
		return findEmptyPallet;
	}

	public Page<CurrentPalletStockDetailsEntity> findEmptyPalletListOfS230(Pageable pageable) {
		Page<CurrentPalletStockDetailsEntity> findEmptyPalletS230 = currentPalletStockDetailsRepository
				.findByProductNameAndProductVariantCodeAndQuantityAndPalletStatusIdAndPalletCodeNot("S230", "NA", 0, 3,
						"NA", pageable);
		return findEmptyPalletS230;
	}



	

}
