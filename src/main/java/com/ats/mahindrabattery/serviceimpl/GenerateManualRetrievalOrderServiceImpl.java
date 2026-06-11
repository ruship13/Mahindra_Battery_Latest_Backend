package com.ats.mahindrabattery.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ats.mahindrabattery.entity.AuditTrailDetailsEntity;
import com.ats.mahindrabattery.entity.BufferDetailsEntity;
import com.ats.mahindrabattery.entity.CurrentPalletStockDetailsEntity;
import com.ats.mahindrabattery.entity.GenerateManualRetrievalOrderEntity;
import com.ats.mahindrabattery.entity.GenerateRetrivalMissionTypeEntity;
import com.ats.mahindrabattery.entity.MasterPositionDetailsEntity;
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
import com.ats.mahindrabattery.repository.MasterPositionDetailsRepository;
import com.ats.mahindrabattery.repository.MasterProductVariantDetailsRepository;
import com.ats.mahindrabattery.repository.MasterStationTagDetailsRepository;
import com.ats.mahindrabattery.repository.MasterUserDetailsRepository;
import com.ats.mahindrabattery.repository.OrderSourceDetailsRepository;
import com.ats.mahindrabattery.response.ResponseHandler;
import com.ats.mahindrabattery.service.GenerateManualRetrievalOrderService;

@Service
public class GenerateManualRetrievalOrderServiceImpl implements GenerateManualRetrievalOrderService {

	@Autowired
	GenerateManualRetrievalOrderRepository generateManualRetrievalOrderRepository;
	
	@Autowired
	GenerateRetrivalMissionTypeRepository generateRetrivalMissionTypeRepository;

	@Autowired
	private CurrentPalletStockDetailsRepository currentPalletStockDetailsRepository;

	@Autowired
	private BufferDetailsRepository bufferDetailsRepository;

	@Autowired
	private CurrentPalletStockDetailsServiceImpl CurrentPalletStockDetailsServiceInstance;

	@Autowired
	private AuditTrailDetailsRepository auditTrailDetailsRepository;

	@Autowired
	private MasterProductVariantDetailsRepository masterProductVariantDetailsRepository;

	@Autowired
	private MasterUserDetailsRepository masterUserDetailsRepository;

	@Autowired
	private MasterPositionDetailsRepository masterPositionDetailsRepository;
	
	@Autowired
    private MasterStationTagDetailsRepository masterStationTagDetailsRepository;
	
	
	@Autowired
	private OrderSourceDetailsRepository orderSourceDetailsRepository;



	public ResponseEntity<Object> createMannualRetrivalDetails(

			GenerateManualRetrievalOrderEntity generateManualRetrievalOrderEntity) {

		int totalquantity = 0;
		int totalBufferQuantity = 0;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();
		List<MasterUserDetailsEntity> findByuserNameAndUserIsDeleted = masterUserDetailsRepository
				.findByuserNameAndUserIsDeleted(name, 0);
		int userId = findByuserNameAndUserIsDeleted.get(0).getUserId();

		MasterPositionDetailsEntity findByPositionId = null;

		List<String> list = new ArrayList<>();

		try {
			List<GenerateRetrivalMissionTypeEntity> entries = generateRetrivalMissionTypeRepository.findAll();
			MasterStationTagDetailsEntity findByPlcTagName = masterStationTagDetailsRepository
					.findByPlcTagName("ATS.WMS_STATION.STACKER_1_DUMP_TANK_HEALTHY");
			if (!"1".equals(findByPlcTagName.getCurrentValue())) {
				
				
				return ResponseHandler.generateResponse("Dump Tank Not healthy for Area-1",
						HttpStatus.CREATED, null);
			}

			MasterStationTagDetailsEntity findByPlcTagName2 = masterStationTagDetailsRepository
					.findByPlcTagName("ATS.WMS_STATION.STACKER_2_DUMP_TANK_HEALTHY");
			if (!"1".equals(findByPlcTagName2.getCurrentValue())) {
				
				
				return ResponseHandler.generateResponse( "Dump Tank Not healthy for Area-2",
						HttpStatus.MULTI_STATUS, null);
			}
			String productVariantCode = generateManualRetrievalOrderEntity.getProductVariantCode();
			List<CurrentPalletStockDetailsEntity> findByproductVariantCode = currentPalletStockDetailsRepository
					.findByproductVariantCode(productVariantCode);

			if (findByproductVariantCode.isEmpty()) {
				List<BufferDetailsEntity> findByproductVariantCode2 = bufferDetailsRepository
						.findByproductVariantCodeAndBufferIsDeleted(productVariantCode, 0);

				for (int j = 0; j < findByproductVariantCode2.size(); j++) {
					int quantity = findByproductVariantCode2.get(j).getQuantity();
					totalBufferQuantity = totalBufferQuantity + quantity;
				}

				Date dNow = new Date();
				SimpleDateFormat ft = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
				String date = ft.format(dNow);
				generateManualRetrievalOrderEntity.setCreatedDatetime(date);
				generateManualRetrievalOrderEntity.setAcutualQuantity(0);
				
				  
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
//				generateManualRetrievalOrderEntity.setIsDispatchStart(0);
				generateManualRetrievalOrderEntity.setIsOrderCancelled(0);
				generateManualRetrievalOrderEntity.setIsOrderDeleted(0);
				generateManualRetrievalOrderEntity.setUserName(name);
				generateManualRetrievalOrderEntity.setUserId(userId);
				generateManualRetrievalOrderEntity.setLoadDatetime(findByproductVariantCode2.get(0).getLoadDateTime());
				generateManualRetrievalOrderEntity.setMfgShift(findByproductVariantCode2.get(0).getMfgShift());
				generateManualRetrievalOrderEntity.setMfgDate(findByproductVariantCode2.get(0).getMfgDate());
				generateManualRetrievalOrderRepository.save(generateManualRetrievalOrderEntity);
				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
				auditTrailDetailsEntity.setOperatorActions(
						"Retrival order generated for " + generateManualRetrievalOrderEntity.getProductVariantCode()
								+ " and Quantity is  " + generateManualRetrievalOrderEntity.getPlannedQuantity());
				auditTrailDetailsEntity.setField("Dipsatch");
				auditTrailDetailsEntity.setAfterValue(0);
				auditTrailDetailsEntity.setBeforeValue(0);
//				auditTrailDetailsEntity.setReason("Bulk Dispatch");
				if(generateManualRetrievalOrderEntity.getOrderSourceDetails().equals("MES")) {
				    auditTrailDetailsEntity.setReason(generateManualRetrievalOrderEntity.getReason());
				} else {
				    auditTrailDetailsEntity.setReason("Bulk Dispatch");
				}

				

				System.out.println(" name :: " + name);
				auditTrailDetailsEntity.setUsername(name);
				auditTrailDetailsEntity.setDatetimeC(date);
				auditTrailDetailsRepository.save(auditTrailDetailsEntity);
				
				
			OrderSourceDetailsEntity orderSourceDetailsEntity=new OrderSourceDetailsEntity();
			orderSourceDetailsEntity.setOrderId(generateManualRetrievalOrderEntity.getDispatchHistoryId());
			orderSourceDetailsEntity.setOrderNumber(generateManualRetrievalOrderEntity.getDispatchOrderNumber());
			orderSourceDetailsEntity.setOrderSourceName(generateManualRetrievalOrderEntity.getOrderSourceDetails());
			orderSourceDetailsEntity.setPartNumber(generateManualRetrievalOrderEntity.getProductVariantCode());
			orderSourceDetailsEntity.setQuantity(generateManualRetrievalOrderEntity.getPlannedQuantity());
			orderSourceDetailsEntity.setIsOrderCancelledFromMes(0);
			orderSourceDetailsEntity.setIsOrderCancelledFromWms(0);
			orderSourceDetailsEntity.setIsOrderDeletedFromWms(0);
			orderSourceDetailsEntity.setSourceCDateTime(date);
			orderSourceDetailsEntity.setVinNumber("NA");
			orderSourceDetailsEntity.setOrderBatchNumber(generateManualRetrievalOrderEntity.getOrderBatchNumber());
			orderSourceDetailsRepository.save(orderSourceDetailsEntity);
				System.out.println("1");
			} else {
				String productVariantCode2 = findByproductVariantCode.get(0).getProductVariantCode();
				List<MasterProductVariantDetailsEntity> findByProductVariantCode3 = masterProductVariantDetailsRepository
						.findByProductVariantCode(productVariantCode2);
				if (findByProductVariantCode3.get(0).getProductVariantIsActive() == 0) {
					return ResponseHandler.generateResponse("Product variant is not active", HttpStatus.IM_USED, null);
				}
				for (int i = 0; i < findByproductVariantCode.size(); i++) {
					findByPositionId = masterPositionDetailsRepository
							.findByPositionId(findByproductVariantCode.get(i).getPositionId());
					MasterPositionDetailsEntity findByPositionIdAndPositionIsActive = masterPositionDetailsRepository
							.findByPositionIdAndPositionIsActive(findByproductVariantCode.get(i).getPositionId(), 0);
					if (findByPositionIdAndPositionIsActive != null) {
						list.add(findByPositionIdAndPositionIsActive.getPositionName());
					}
					if (findByPositionId.getPositionIsActive() == 1) {
						int quantity = findByproductVariantCode.get(i).getQuantity();
						totalquantity = totalquantity + quantity;

					}
				}
				List<BufferDetailsEntity> findByproductVariantCode2 = bufferDetailsRepository
						.findByproductVariantCodeAndBufferIsDeleted(productVariantCode, 0);

				for (int j = 0; j < findByproductVariantCode2.size(); j++) {
					int quantity = findByproductVariantCode2.get(j).getQuantity();
					totalBufferQuantity = totalBufferQuantity + quantity;
				}
				System.out.println("totalquantity::" + totalquantity);
				System.out.println("getPlannedQuantity::" + generateManualRetrievalOrderEntity.getPlannedQuantity());
				if (generateManualRetrievalOrderEntity.getPlannedQuantity() <= totalquantity) {
					System.out.println("**********111");
					Date dNow = new Date();
					SimpleDateFormat ft = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
					String date = ft.format(dNow);
					generateManualRetrievalOrderEntity.setCreatedDatetime(date);
					generateManualRetrievalOrderEntity.setAcutualQuantity(0);
					if (!entries.isEmpty()) {
				        GenerateRetrivalMissionTypeEntity data = entries.get(0); 
				        if (data.getDispatchTriggered() == 0) {
				            generateManualRetrievalOrderEntity.setIsDispatchStart(0);
				            generateManualRetrievalOrderEntity.setDispatchStatus("READY");
				            System.out.println("1");
				        } else {
				            generateManualRetrievalOrderEntity.setIsDispatchStart(1);
				            generateManualRetrievalOrderEntity.setDispatchStatus("IN_PROGRESS");
				            System.out.println("2");
				        }
				    } else {
				        System.out.println("No entry found in ats_wms_generate_retrieval_mission_type table.");
				    }
//					generateManualRetrievalOrderEntity.setIsDispatchStart(0);
					generateManualRetrievalOrderEntity.setIsOrderCancelled(0);
					generateManualRetrievalOrderEntity.setIsOrderDeleted(0);
					generateManualRetrievalOrderEntity.setUserName(name);
					generateManualRetrievalOrderEntity.setUserId(userId);
					generateManualRetrievalOrderEntity
							.setLoadDatetime(findByproductVariantCode.get(0).getLoadDatetime());
//					generateManualRetrievalOrderEntity.setDispatchStatus("READY");
					generateManualRetrievalOrderEntity.setMfgShift(findByproductVariantCode.get(0).getMfgShift());
					generateManualRetrievalOrderEntity.setMfgDate(findByproductVariantCode.get(0).getMfgDate());
					generateManualRetrievalOrderRepository.save(generateManualRetrievalOrderEntity);
					
					OrderSourceDetailsEntity orderSourceDetailsEntity=new OrderSourceDetailsEntity();
					orderSourceDetailsEntity.setOrderId(generateManualRetrievalOrderEntity.getDispatchHistoryId());
					orderSourceDetailsEntity.setOrderNumber(generateManualRetrievalOrderEntity.getDispatchOrderNumber());
					orderSourceDetailsEntity.setOrderSourceName(generateManualRetrievalOrderEntity.getOrderSourceDetails());
					orderSourceDetailsEntity.setPartNumber(generateManualRetrievalOrderEntity.getProductVariantCode());
					orderSourceDetailsEntity.setQuantity(generateManualRetrievalOrderEntity.getPlannedQuantity());
					orderSourceDetailsEntity.setIsOrderCancelledFromMes(0);
					orderSourceDetailsEntity.setIsOrderCancelledFromWms(0);
					orderSourceDetailsEntity.setIsOrderDeletedFromWms(0);
					orderSourceDetailsEntity.setSourceCDateTime(date);
					orderSourceDetailsEntity.setVinNumber("NA");
					orderSourceDetailsEntity.setOrderBatchNumber(generateManualRetrievalOrderEntity.getOrderBatchNumber());

					orderSourceDetailsRepository.save(orderSourceDetailsEntity);
					
					AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
					auditTrailDetailsEntity.setOperatorActions(
							"Retrival order generated for " + generateManualRetrievalOrderEntity.getProductVariantCode()
									+ " and Quantity is  " + generateManualRetrievalOrderEntity.getPlannedQuantity());
					auditTrailDetailsEntity.setField("Dipsatch");
					auditTrailDetailsEntity.setAfterValue(0);
					auditTrailDetailsEntity.setBeforeValue(0);
//					auditTrailDetailsEntity.setReason("Bulk Dispatch");
//					auditTrailDetailsEntity.setReason(generateManualRetrievalOrderEntity.getReason());
					if(generateManualRetrievalOrderEntity.getOrderSourceDetails().equals("MES")) {
						System.out.println("MES");
					    auditTrailDetailsEntity.setReason(generateManualRetrievalOrderEntity.getReason());
					} else {
						System.out.println("NON MES");
					    auditTrailDetailsEntity.setReason("Bulk Dispatch");
					}


					System.out.println(" name :: " + name);
					auditTrailDetailsEntity.setUsername(name);
					auditTrailDetailsEntity.setDatetimeC(date);
					auditTrailDetailsRepository.save(auditTrailDetailsEntity);
					
					
					System.out.println("1");

				} else if (generateManualRetrievalOrderEntity
						.getPlannedQuantity() <= (totalquantity + totalBufferQuantity)) {
					System.out.println("**********2222");
					Date dNow = new Date();
					SimpleDateFormat ft = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
					String date = ft.format(dNow);
					generateManualRetrievalOrderEntity.setCreatedDatetime(date);
					generateManualRetrievalOrderEntity.setAcutualQuantity(0);
					if (!entries.isEmpty()) {
				        GenerateRetrivalMissionTypeEntity data = entries.get(0); 
				        if (data.getDispatchTriggered() == 0) {
				            generateManualRetrievalOrderEntity.setIsDispatchStart(0);
				            generateManualRetrievalOrderEntity.setDispatchStatus("READY");
				            System.out.println("1");
				        } else {
				            generateManualRetrievalOrderEntity.setIsDispatchStart(1);
				            generateManualRetrievalOrderEntity.setDispatchStatus("IN_PROGRESS");
				            System.out.println("2");
				        }
				    } else {
				        System.out.println("No entry found in ats_wms_generate_retrieval_mission_type table.");
				    }
//					generateManualRetrievalOrderEntity.setIsDispatchStart(0);
					generateManualRetrievalOrderEntity.setIsOrderCancelled(0);
					generateManualRetrievalOrderEntity.setIsOrderDeleted(0);
					generateManualRetrievalOrderEntity.setUserName(name);
					generateManualRetrievalOrderEntity.setUserId(userId);
					generateManualRetrievalOrderEntity
							.setLoadDatetime(findByproductVariantCode.get(0).getLoadDatetime());
					generateManualRetrievalOrderEntity.setMfgShift(findByproductVariantCode.get(0).getMfgShift());
					generateManualRetrievalOrderEntity.setMfgDate(findByproductVariantCode.get(0).getMfgDate());
					generateManualRetrievalOrderRepository.save(generateManualRetrievalOrderEntity);
					AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
					auditTrailDetailsEntity.setOperatorActions(
							"Retrival order generated of " + generateManualRetrievalOrderEntity.getProductVariantCode()
									+ "and Quantity is  " + generateManualRetrievalOrderEntity.getPlannedQuantity());
					auditTrailDetailsEntity.setField("Dispatch");
					auditTrailDetailsEntity.setAfterValue(0);
					auditTrailDetailsEntity.setBeforeValue(0);

					if(generateManualRetrievalOrderEntity.getOrderSourceDetails().equals("MES")) {
						auditTrailDetailsEntity.setReason(generateManualRetrievalOrderEntity.getReason());
					}
					else {
						auditTrailDetailsEntity.setReason("Bulk Dispatch");
					}
					System.out.println(" name :: " + name);
					auditTrailDetailsEntity.setUsername(name);
					auditTrailDetailsEntity.setDatetimeC(date);
					auditTrailDetailsRepository.save(auditTrailDetailsEntity);
					
					OrderSourceDetailsEntity orderSourceDetailsEntity=new OrderSourceDetailsEntity();
					orderSourceDetailsEntity.setOrderId(generateManualRetrievalOrderEntity.getDispatchHistoryId());
					orderSourceDetailsEntity.setOrderNumber(generateManualRetrievalOrderEntity.getDispatchOrderNumber());
					orderSourceDetailsEntity.setOrderSourceName(generateManualRetrievalOrderEntity.getOrderSourceDetails());
					orderSourceDetailsEntity.setPartNumber(generateManualRetrievalOrderEntity.getProductVariantCode());
					orderSourceDetailsEntity.setQuantity(generateManualRetrievalOrderEntity.getPlannedQuantity());
					orderSourceDetailsEntity.setIsOrderCancelledFromMes(0);
					orderSourceDetailsEntity.setIsOrderCancelledFromWms(0);
					orderSourceDetailsEntity.setIsOrderDeletedFromWms(0);
					orderSourceDetailsEntity.setSourceCDateTime(date);
					orderSourceDetailsEntity.setVinNumber("NA");
					orderSourceDetailsEntity.setOrderBatchNumber(generateManualRetrievalOrderEntity.getOrderBatchNumber());
					
					orderSourceDetailsRepository.save(orderSourceDetailsEntity);
					System.out.println("2");
					return ResponseHandler.generateResponse("Mannual dispatch sucessfully done", HttpStatus.ACCEPTED,
							generateManualRetrievalOrderEntity);
				} else if ((totalquantity + totalBufferQuantity) == 0) {
					System.out.println("**********333");
					System.out
							.println("totalquantity + totalBufferQuantity :: " + (totalquantity + totalBufferQuantity));

					return ResponseHandler.generateResponse("Mannual dispatch failed due to Quantity available is Zero",
							HttpStatus.INTERNAL_SERVER_ERROR, generateManualRetrievalOrderEntity);
				}

				else {
					System.out.println("**********444");
					return ResponseHandler.generateResponse("Insufficient Quantity available. Quantity is  :    "
							+ (totalquantity + totalBufferQuantity) + " Position " + list + " inactive."
							+ " Do you want to dispatch?", HttpStatus.ALREADY_REPORTED, null);
				}
			}
		} catch (Exception e) {
			System.out.println("**********555");
			return ResponseHandler.generateResponse("Mannual dispatch failed", HttpStatus.INTERNAL_SERVER_ERROR, null);
		}
		return ResponseHandler.generateResponse("Mannual dispatch sucessfully done", HttpStatus.OK,
				generateManualRetrievalOrderEntity);

	}

	public ResponseEntity<Object> createMannualRetrivalDetails1(
			GenerateManualRetrievalOrderEntity generateManualRetrievalOrderEntity) {
		int totalquantity = 0;
		int totalBufferQuantity = 0;
		try {
			List<GenerateRetrivalMissionTypeEntity> entries = generateRetrivalMissionTypeRepository.findAll();
			MasterStationTagDetailsEntity findByPlcTagName = masterStationTagDetailsRepository
					.findByPlcTagName("ATS.WMS_STATION.STACKER_1_DUMP_TANK_HEALTHY");
			if (!"1".equals(findByPlcTagName.getCurrentValue())) {
				
				
				return ResponseHandler.generateResponse("Dump Tank Not healthy for Area-1",
						HttpStatus.CREATED, null);
			}

			MasterStationTagDetailsEntity findByPlcTagName2 = masterStationTagDetailsRepository
					.findByPlcTagName("ATS.WMS_STATION.STACKER_2_DUMP_TANK_HEALTHY");
			if (!"1".equals(findByPlcTagName2.getCurrentValue())) {
				
				
				return ResponseHandler.generateResponse( "Dump Tank Not healthy for Area-2",
						HttpStatus.MULTI_STATUS, null);
			}
			String productVariantCode = generateManualRetrievalOrderEntity.getProductVariantCode();
			List<CurrentPalletStockDetailsEntity> findByproductVariantCode = currentPalletStockDetailsRepository
					.findByproductVariantCode(productVariantCode);
			for (int i = 0; i < findByproductVariantCode.size(); i++) {
				int quantity = findByproductVariantCode.get(i).getQuantity();
				totalquantity = totalquantity + quantity;
			}
			List<BufferDetailsEntity> findByproductVariantCode2 = bufferDetailsRepository
					.findByproductVariantCodeAndBufferIsDeleted(productVariantCode, 0);

			for (int j = 0; j < findByproductVariantCode2.size(); j++) {
				int quantity = findByproductVariantCode2.get(j).getQuantity();
				totalBufferQuantity = totalBufferQuantity + quantity;
			}
			Date dNow = new Date();
			SimpleDateFormat ft = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
			String date = ft.format(dNow);

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String name = authentication.getName();
			List<MasterUserDetailsEntity> findByuserNameAndUserIsDeleted = masterUserDetailsRepository
					.findByuserNameAndUserIsDeleted(name, 0);
			int userId = findByuserNameAndUserIsDeleted.get(0).getUserId();

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
			generateManualRetrievalOrderEntity.setIsOrderCancelled(0);
			generateManualRetrievalOrderEntity.setIsOrderDeleted(0);
			generateManualRetrievalOrderEntity.setPlannedQuantity(totalquantity + totalBufferQuantity);
			generateManualRetrievalOrderEntity.setBalanceQuantity(totalquantity + totalBufferQuantity);
			generateManualRetrievalOrderEntity.setUserName(name);
			generateManualRetrievalOrderEntity.setUserId(userId);
			generateManualRetrievalOrderEntity.setLoadDatetime(findByproductVariantCode.get(0).getLoadDatetime());
			generateManualRetrievalOrderEntity.setMfgShift(findByproductVariantCode.get(0).getMfgShift());
			generateManualRetrievalOrderEntity.setMfgDate(findByproductVariantCode.get(0).getMfgDate());
			generateManualRetrievalOrderRepository.save(generateManualRetrievalOrderEntity);
			AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
			auditTrailDetailsEntity.setOperatorActions(
					"Retrival order generated for " + generateManualRetrievalOrderEntity.getProductVariantCode()
							+ "and Quantity is  " + generateManualRetrievalOrderEntity.getPlannedQuantity());
			auditTrailDetailsEntity.setField("Dispatch");
			auditTrailDetailsEntity.setAfterValue(0);
			auditTrailDetailsEntity.setBeforeValue(0);

			if(generateManualRetrievalOrderEntity.getOrderSourceDetails().equals("MES")) {
				auditTrailDetailsEntity.setReason(generateManualRetrievalOrderEntity.getReason());
			}
			else {
				auditTrailDetailsEntity.setReason("Bulk Dispatch");
			}
			System.out.println(" name :: " + name);
			auditTrailDetailsEntity.setUsername(name);
			auditTrailDetailsEntity.setDatetimeC(date);
			auditTrailDetailsRepository.save(auditTrailDetailsEntity);
			
			
			OrderSourceDetailsEntity orderSourceDetailsEntity=new OrderSourceDetailsEntity();
			orderSourceDetailsEntity.setOrderId(generateManualRetrievalOrderEntity.getDispatchHistoryId());
			orderSourceDetailsEntity.setOrderNumber(generateManualRetrievalOrderEntity.getDispatchOrderNumber());
			orderSourceDetailsEntity.setOrderSourceName(generateManualRetrievalOrderEntity.getOrderSourceDetails());
			orderSourceDetailsEntity.setPartNumber(generateManualRetrievalOrderEntity.getProductVariantCode());
			orderSourceDetailsEntity.setQuantity(totalquantity + totalBufferQuantity);
			orderSourceDetailsEntity.setIsOrderCancelledFromMes(0);
			orderSourceDetailsEntity.setIsOrderCancelledFromWms(0);
			orderSourceDetailsEntity.setIsOrderDeletedFromWms(0);
			orderSourceDetailsEntity.setSourceCDateTime(date);
			orderSourceDetailsEntity.setVinNumber("NA");
			orderSourceDetailsEntity.setOrderBatchNumber(generateManualRetrievalOrderEntity.getOrderBatchNumber());
			
			orderSourceDetailsRepository.save(orderSourceDetailsEntity);
			System.out.println("Do you want to dispatch" + (totalquantity + totalBufferQuantity));

		} catch (Exception e) {
			return ResponseHandler.generateResponse("Mannual dispatch failed ", HttpStatus.BAD_REQUEST, null);
		}
		System.out.println("11188888888");
		return ResponseHandler
				.generateResponse(
						"Insufficient Quantity available. Quantity is  :  " + (totalquantity + totalBufferQuantity)
								+ ".  Do you want to dispatch?",
						HttpStatus.CREATED, generateManualRetrievalOrderEntity);
	}



	public List<GenerateManualRetrievalOrderEntity> getAllMannualRetrivalDetails() {
		try {
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String currentDateTime = dateFormat.format(date);

			// Fetch orders created on the current date with IsOrderDeleted = 0
			List<GenerateManualRetrievalOrderEntity> ordersToday = generateManualRetrievalOrderRepository
					.findBycreatedDatetimeBetweenAndIsOrderDeleted(currentDateTime + " " + "00:00:00",
							currentDateTime + " " + "23:59:59", 0);

			// Fetch orders not created on the current date, with dispatchStatus = READY or
			// IN_PROGRESS, and IsOrderDeleted = 0
			List<GenerateManualRetrievalOrderEntity> ordersOtherThanToday = generateManualRetrievalOrderRepository
					.findOrdersNotCreatedOnCurrentDateWithDispatchStatus(currentDateTime + " " + "00:00:00",
							currentDateTime + " " + "23:59:59", Arrays.asList("READY", "IN_PROGRESS"), 0);

			// Combine both lists
			ordersToday.addAll(ordersOtherThanToday);

//			System.out.println("Orders fetched: " + ordersToday);
			return ordersToday;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}


	public List<GenerateManualRetrievalOrderEntity> findAllMannualDispatchOrdersByDate(String startDate,
			String endDate) {
		try {
			List<GenerateManualRetrievalOrderEntity> findAllInfeedMissionRuntimeDetailsBetweenDates = generateManualRetrievalOrderRepository
					.findGenerateManualRetrievalOrderBetweenDates(startDate, endDate);
			return findAllInfeedMissionRuntimeDetailsBetweenDates;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}



	public List<GenerateManualRetrievalOrderEntity> findByMannualDispatchNumber(String dispatchOrderNumber) {
		try {
			List<GenerateManualRetrievalOrderEntity> findByMannualDispatchNumber = generateManualRetrievalOrderRepository
					.findBydispatchOrderNumber(dispatchOrderNumber);
			return findByMannualDispatchNumber;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public List<GenerateManualRetrievalOrderEntity> findByAllFilters(String cDatetimeStart, String cDatetimeEnd,
			String dispatchOrderNumber, String productVariantCode, String shiftName) {
		List<String> filterList = new ArrayList<>();
		List<GenerateManualRetrievalOrderEntity> list = new ArrayList<>();

		if (!dispatchOrderNumber.equals("NA")) {
			filterList.add("dispatchOrderNumber");
			System.out.println("dispatchOrderNumber::" + dispatchOrderNumber);
		}
		if (!productVariantCode.equals("NA")) {
			filterList.add("productVariantCode");
			System.out.println("productVariantCode::" + productVariantCode);
		}
		if (!shiftName.equals("NA")) {
			filterList.add("shiftName");
			System.out.println("shiftName::" + shiftName);
		}
		Predicate<GenerateManualRetrievalOrderEntity> dispatchOrderNumberPred = data -> data.getDispatchOrderNumber()
				.equals(dispatchOrderNumber);
		Predicate<GenerateManualRetrievalOrderEntity> productVariantCodePred = data -> data.getProductVariantCode()
				.equals(productVariantCode);
		Predicate<GenerateManualRetrievalOrderEntity> shiftNamePred = data -> data.getShiftName().equals(shiftName);

		if (!(cDatetimeStart.equals("NA")) && !(cDatetimeEnd.equals("NA"))) {
			String startDateTime = cDatetimeStart.toString().replace("T", " ");
			String endDateTime = cDatetimeEnd.toString().replace("T", " ");
			System.out.println("startDateTime= " + startDateTime);
			System.out.println("endDateTime= " + endDateTime);
			list = generateManualRetrievalOrderRepository.findBycreatedDatetimeBetweenAndDispatchStatus(startDateTime, endDateTime,"COMPLETED");
			System.out.println("list1:" + list);

		} else {
			Date dateNow = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String date = dateFormat.format(dateNow);
			list = generateManualRetrievalOrderRepository.findBycreatedDatetimeBetweenAndDispatchStatus(date + " " + "00:00:00",
					date + " " + "23:59:59","COMPLETED");
			// System.out.println("list2:" + list);
		}

		if (filterList.size() != 0) {

			for (int i = 0; i < filterList.size(); i++) {

				if (filterList.get(i).equals("dispatchOrderNumber")) {
					list = list.stream().filter(dispatchOrderNumberPred).collect((Collectors.toList()));
					// System.out.println("floor list::"+list.size());
				} else if (filterList.get(i).equals("productVariantCode")) {
					list = list.stream().filter(productVariantCodePred).collect((Collectors.toList()));
					// System.out.println("area list::"+list.size());
				} else if (filterList.get(i).equals("shiftName")) {
					list = list.stream().filter(shiftNamePred).collect((Collectors.toList()));
					// System.out.println("status list::"+list.size());
				}
			}
		}

		if (filterList.size() == 0 && list.size() == 0) {
			list = null;
		}
		return list;

	}

	public void updateDispatchStart(GenerateManualRetrievalOrderEntity data) {

		GenerateManualRetrievalOrderEntity entityToUpdate = generateManualRetrievalOrderRepository
				.findById(data.getDispatchHistoryId()).orElse(null);

		if (entityToUpdate != null) {

			entityToUpdate.setIsDispatchStart(data.getIsDispatchStart());
			entityToUpdate.setDispatchStatus(data.getDispatchStatus());

			generateManualRetrievalOrderRepository.save(entityToUpdate);
			
			
		} else {
			System.out.println("Data not updated");
		}
	}

	public void updateDispatchCanceled(GenerateManualRetrievalOrderEntity data) {

		GenerateManualRetrievalOrderEntity entityToUpdate = generateManualRetrievalOrderRepository
				.findById(data.getDispatchHistoryId())
				.orElseThrow(() -> new ResourceNotFoundException("MasterUserDetailsEntity", "Id",
						data.getDispatchHistoryId()));

//		if (entityToUpdate != null) {

		entityToUpdate.setIsOrderCancelled(data.getIsOrderCancelled());

		generateManualRetrievalOrderRepository.save(entityToUpdate);
//		} else {
//			System.out.println("Data not updated");
//		}
	}

	public List<GenerateManualRetrievalOrderEntity> getAllDispatchOrdernumberDetails() {
		try {
			List<GenerateManualRetrievalOrderEntity> findByIsDispatchStarted = generateManualRetrievalOrderRepository
//					.findAll();
					.findByIsOrderDeleted(0);

					System.out.println("findByIsDispatchStarted::" + findByIsDispatchStarted.size());

			return findByIsDispatchStarted;

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public ResponseEntity<Object> createMannualRetrivalDetailsBySerialNumber(
			List<GenerateManualRetrievalOrderEntity> generateManualRetrievalOrderEntity) {
		try {
			for (int i = 0; i < generateManualRetrievalOrderEntity.size(); i++) {
				List<GenerateManualRetrievalOrderEntity> list = generateManualRetrievalOrderRepository
						.findBySerialNumber(generateManualRetrievalOrderEntity.get(i).getSerialNumber());

//			if (list.size() == 0) {
				Date dNow = new Date();
				SimpleDateFormat ft = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
				String date = ft.format(dNow);
				generateManualRetrievalOrderEntity.get(i).setCreatedDatetime(date);
				generateManualRetrievalOrderEntity.get(i).setAcutualQuantity(0);
//				generateManualRetrievalOrderEntity.setBalanceQuantity(0);
				generateManualRetrievalOrderEntity.get(i).setIsDispatchStart(0);

				generateManualRetrievalOrderRepository.save(generateManualRetrievalOrderEntity.get(i));

				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
				auditTrailDetailsEntity.setOperatorActions("NA");
				auditTrailDetailsEntity.setField("Retrival");
				auditTrailDetailsEntity.setAfterValue(0);
				auditTrailDetailsEntity.setBeforeValue(0);
				auditTrailDetailsEntity.setReason("mannual Retrival");
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				String name = authentication.getName();
				System.out.println(" name :: " + name);
				auditTrailDetailsEntity.setUsername(name);
				auditTrailDetailsEntity.setDatetimeC(date);
				auditTrailDetailsRepository.save(auditTrailDetailsEntity);


			}
			return ResponseHandler.generateResponse("Mannual dispatch sucessfully done", HttpStatus.OK,
					generateManualRetrievalOrderEntity);

		} catch (Exception ex) {
			return ResponseHandler.generateResponse("Error occurred at server side", HttpStatus.INTERNAL_SERVER_ERROR,
					null);

		}

	}



	
	
	public void updateOrderCancelledStatus(int dispatchHistoryId, int isOrderCancelled) {
		GenerateManualRetrievalOrderEntity generateManualRetrievalOrderEntity = generateManualRetrievalOrderRepository
				.findById(dispatchHistoryId).orElse(null);
		List<OrderSourceDetailsEntity> findByOrderId = orderSourceDetailsRepository.findByOrderId(dispatchHistoryId);
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
		String date = ft.format(dNow);
		if (generateManualRetrievalOrderEntity != null) {
		
			generateManualRetrievalOrderEntity.setIsOrderCancelled(1);
			generateManualRetrievalOrderEntity.setDispatchStatus("CANCELLED");
			generateManualRetrievalOrderRepository.save(generateManualRetrievalOrderEntity);
		
		
			findByOrderId.get(0).setIsOrderCancelledFromWms(1);
			orderSourceDetailsRepository.save(findByOrderId.get(0));
			generateManualRetrievalOrderRepository.save(generateManualRetrievalOrderEntity);
			
			
			AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
			auditTrailDetailsEntity.setOperatorActions(generateManualRetrievalOrderEntity.getDispatchOrderNumber()+" Order is Cancelled and Quantity is "+generateManualRetrievalOrderEntity.getPlannedQuantity());
			auditTrailDetailsEntity.setField("Order Cancelled");
			auditTrailDetailsEntity.setAfterValue(0);
			auditTrailDetailsEntity.setBeforeValue(0);
			auditTrailDetailsEntity.setReason("Order Cancelled");
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String name = authentication.getName();
			auditTrailDetailsEntity.setUsername(name);
			auditTrailDetailsEntity.setDatetimeC(date);
			auditTrailDetailsRepository.save(auditTrailDetailsEntity);
		}
	}


	public void updateOrderIsDeleted(int dispatchHistoryId) {
		GenerateManualRetrievalOrderEntity generateManualRetrievalOrderEntity = generateManualRetrievalOrderRepository
				.findById(dispatchHistoryId).orElse(null);
		List<OrderSourceDetailsEntity> findByOrderId = orderSourceDetailsRepository.findByOrderId(dispatchHistoryId);
		
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
		String date = ft.format(dNow);
		
		if (generateManualRetrievalOrderEntity != null) {
			generateManualRetrievalOrderEntity.setIsOrderDeleted(1);
			findByOrderId.get(0).setIsOrderDeletedFromWms(1);
			orderSourceDetailsRepository.save(findByOrderId.get(0));
			generateManualRetrievalOrderRepository.save(generateManualRetrievalOrderEntity);
			
			AuditTrailDetailsEntity auditTrailDetailsEntity=new AuditTrailDetailsEntity();
			auditTrailDetailsEntity.setOperatorActions(generateManualRetrievalOrderEntity.getDispatchOrderNumber()+" Order is Deleted and Quantity is "+generateManualRetrievalOrderEntity.getPlannedQuantity());
			auditTrailDetailsEntity.setField("Order Deleted");
			auditTrailDetailsEntity.setAfterValue(0);
			auditTrailDetailsEntity.setBeforeValue(0);
			auditTrailDetailsEntity.setReason("Order Deleted");
			
			Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
			String name = authentication.getName();
			auditTrailDetailsEntity.setUsername(name);
			auditTrailDetailsEntity.setDatetimeC(date);
			auditTrailDetailsRepository.save(auditTrailDetailsEntity);

		}
	}


	
	
	public void updateDispatchTriggered(GenerateRetrivalMissionTypeEntity data) {
	    Date dNow = new Date();
	    SimpleDateFormat ft = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
	    String date = ft.format(dNow);

	    GenerateRetrivalMissionTypeEntity entityToUpdate = generateRetrivalMissionTypeRepository
	            .findById(data.getDispatchTriggeredId()).orElse(null);

	    if (entityToUpdate != null) {
	    	
	        entityToUpdate.setDispatchTriggered(data.getDispatchTriggered());
	        entityToUpdate.setUserUpdatedTime(date);
	        entityToUpdate.setUserName(data.getUserName());
	        generateRetrivalMissionTypeRepository.save(entityToUpdate);
	    } else {
	        System.out.println("Data not updated");
	    }
	}
	
	
	public List<GenerateRetrivalMissionTypeEntity> fetchAllMissionTypeData() {
		return generateRetrivalMissionTypeRepository.findAll();
	}


	public ResponseEntity updateCurrentDateRetrivalOrderIsDeleted() {
	    Date dNow = new Date();
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    String currentDate = dateFormat.format(dNow);

	    List<GenerateManualRetrievalOrderEntity> ordersToday = generateManualRetrievalOrderRepository
	            .findBycreatedDatetimeBetweenAndIsOrderDeleted(currentDate + " 00:00:00", currentDate + " 23:59:59", 0);


				List<GenerateManualRetrievalOrderEntity> ordersOtherThanToday = generateManualRetrievalOrderRepository
					.findOrdersNotCreatedOnCurrentDateWithDispatchStatus(currentDate + " " + "00:00:00",
							currentDate + " " + "23:59:59", Arrays.asList("READY", "IN_PROGRESS"), 0);

							ordersToday.addAll(ordersOtherThanToday);

	    for (GenerateManualRetrievalOrderEntity order : ordersToday) {
	        order.setIsOrderDeleted(1);
	        generateManualRetrievalOrderRepository.save(order);



	      
	    }
return ResponseHandler.generateResponse(
    "Data Deleted Successfully",
    HttpStatus.OK,
    null
);

	

	}
	

}
