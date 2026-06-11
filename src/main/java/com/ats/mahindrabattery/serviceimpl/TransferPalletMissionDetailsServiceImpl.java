package com.ats.mahindrabattery.serviceimpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ats.mahindrabattery.entity.AuditTrailDetailsEntity;
import com.ats.mahindrabattery.entity.CurrentPalletStockDetailsEntity;
import com.ats.mahindrabattery.entity.MasterPositionDetailsEntity;
import com.ats.mahindrabattery.entity.TransferPalletMissionDetailsEntity;
import com.ats.mahindrabattery.entity.TransferPalletMissionRuntimeDetailsEntity;
import com.ats.mahindrabattery.repository.AuditTrailDetailsRepository;
import com.ats.mahindrabattery.repository.CurrentPalletStockDetailsRepository;
import com.ats.mahindrabattery.repository.MasterPositionDetailsRepository;
import com.ats.mahindrabattery.repository.TransferPalletMissionDetailsRepository;
import com.ats.mahindrabattery.repository.TransferPalletMissionRuntimeDetailsRepository;
import com.ats.mahindrabattery.response.ResponseHandler;
import com.ats.mahindrabattery.service.TransferPalletMissionDetailsService;

@Service
public class TransferPalletMissionDetailsServiceImpl implements TransferPalletMissionDetailsService {

	@Autowired
	TransferPalletMissionDetailsRepository transferPalletMissionDetailsRepositoryInstance;

	@Autowired
	private MasterPositionDetailsRepository masterPositionDetailsRepositoryInstance;

	@Autowired
	private CurrentPalletStockDetailsRepository currentPalletStockDetailsRepository;
	@Autowired
	private TransferPalletMissionRuntimeDetailsRepository transferPalletMissionRuntimeDetailsRepositoryInstance;

	@Autowired
	private AuditTrailDetailsRepository auditTrailDetailsRepository;

	public List<TransferPalletMissionDetailsEntity> findAll() {
		try {
			return transferPalletMissionDetailsRepositoryInstance.findAll();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}



	public ResponseEntity<Object> addPalletMovementDetailsInTransferPalletMissionDetails(String currentPositionName,
			String destinationPositionName, int userId, String userName) {
		String substring = currentPositionName.substring(0, 3);
		String substring2 = destinationPositionName.substring(0, 3);

		if (!Objects.equals(substring, substring2)) {
			return ResponseHandler.generateResponse("Pallet Can Not Be Transfer From One Area To Another Area",
					HttpStatus.ACCEPTED, null);
		}

		List<CurrentPalletStockDetailsEntity> findByPositionName3 = currentPalletStockDetailsRepository
				.findByPositionName(currentPositionName);
		String productName = findByPositionName3.get(0).getProductName();
		List<CurrentPalletStockDetailsEntity> findByPositionName4 = currentPalletStockDetailsRepository
				.findByPositionName(destinationPositionName);
		Integer positionId3 = findByPositionName4.get(0).getPositionId();
		List<CurrentPalletStockDetailsEntity> findByPositionId2 = currentPalletStockDetailsRepository
				.findByPositionId(positionId3 + 1);
		String productName2 = findByPositionId2.get(0).getProductName();
//		System.out.println("sss" + ((positionId3 % 2) == 1));
		if (!Objects.equals(productName, productName2) && ((positionId3 % 2) == 1)) {
//			System.out.println("1");
			return ResponseHandler.generateResponse("Product name does not match previous product name",
					HttpStatus.NON_AUTHORITATIVE_INFORMATION, null);
		}

		List<CurrentPalletStockDetailsEntity> findByPositionName = currentPalletStockDetailsRepository
				.findByPositionName(destinationPositionName);
		Integer positionId2 = findByPositionName.get(0).getPositionId();
		List<CurrentPalletStockDetailsEntity> findByPositionName2 = currentPalletStockDetailsRepository
				.findByPositionName(currentPositionName);
		String productVariantCode3 = findByPositionName2.get(0).getProductVariantCode();
		List<CurrentPalletStockDetailsEntity> findByPositionId = currentPalletStockDetailsRepository
				.findByPositionId(positionId2 + 1);
		String productVariantCode1 = findByPositionId.get(0).getProductVariantCode();
		if (positionId2 % 2 == 1 && !Objects.equals(productVariantCode3, productVariantCode1)) {
			return ResponseHandler.generateResponse(
					"Product variant code does not match with previous destination position's product variant code",
					HttpStatus.CREATED, null);
		}

		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = ft.format(dNow);

		List<MasterPositionDetailsEntity> masterPositionDetailsCurrentList = null;
		// System.out.println("currentPositionName ::" + currentPositionName);
		masterPositionDetailsCurrentList = masterPositionDetailsRepositoryInstance
				.findByPositionName(currentPositionName);
		// System.out.println("masterPositionDetailsList::" +
		// masterPositionDetailsCurrentList.size());
		if (masterPositionDetailsCurrentList.size() > 0) {

			if (masterPositionDetailsCurrentList.get(0).getEmptyPalletPosition() == 0
					&& masterPositionDetailsCurrentList.get(0).getPositionIsActive() == 1
					&& masterPositionDetailsCurrentList.get(0).getIsManualDispatch() == 0) {
				List<MasterPositionDetailsEntity> masterPositionDetailsDestList = null;
				masterPositionDetailsDestList = masterPositionDetailsRepositoryInstance
						.findByPositionName(destinationPositionName);
				CurrentPalletStockDetailsEntity currentPalletStockDetailsEntity = currentPalletStockDetailsRepository
						.findById(masterPositionDetailsDestList.get(0).getPositionId() + 1).get();
				String productVariantCode = currentPalletStockDetailsEntity.getProductVariantCode();
				int positionId = masterPositionDetailsCurrentList.get(0).getPositionId();
				CurrentPalletStockDetailsEntity currentPalletStockDetailsEntity1 = currentPalletStockDetailsRepository
						.findById(positionId).get();
				String productVariantCode2 = currentPalletStockDetailsEntity1.getProductVariantCode();
				if (productVariantCode.equals(productVariantCode2) == false
						&& masterPositionDetailsDestList.get(0).getPositionId() / 2 == 0) {
					return ResponseHandler.generateResponse("Product variant code does not matches with previous rack",
							HttpStatus.IM_USED, null);
				} else {

					if (!masterPositionDetailsDestList.isEmpty()) {
						if (masterPositionDetailsDestList.get(0).getEmptyPalletPosition() == 1
								&& masterPositionDetailsDestList.get(0).getPositionIsActive() == 1
								&& masterPositionDetailsDestList.get(0).getIsManualDispatch() == 0) {
							List<MasterPositionDetailsEntity> masterPositionDetailsCheckList = null;
							// System.out.println("rackId"
							// +masterPositionDetailsDestList.get(0).getRackId());
							// System.out.println("positionId"
							// +masterPositionDetailsDestList.get(0).getPositionId());
							masterPositionDetailsCheckList = masterPositionDetailsRepositoryInstance
									.findByRackIdAndPositionIdLessThanAndEmptyPalletPositionAndPositionIsActiveAndIsManualDispatch(
											masterPositionDetailsDestList.get(0).getRackId(),
											masterPositionDetailsDestList.get(0).getPositionId(), 0, 1, 0);
							// System.out.println("masterPositionDetailsCheckList ::" +
							// masterPositionDetailsCheckList.size());
							if (masterPositionDetailsCheckList.isEmpty()) {
								List<CurrentPalletStockDetailsEntity> currentPalletStockList = null;
								currentPalletStockList = currentPalletStockDetailsRepository
										.findByPositionName(currentPositionName);
								if (!currentPalletStockList.isEmpty()) {
									TransferPalletMissionDetailsEntity transferPalletMissionDetailsEntityInstnace = new TransferPalletMissionDetailsEntity();
									List<TransferPalletMissionDetailsEntity> transferPalletMissionList = null;
									List<TransferPalletMissionDetailsEntity> transferPalletMissionList1 = null;
									// System.out.println("transferPalletMissionList::" +
									// transferPalletMissionList);
									transferPalletMissionList = transferPalletMissionDetailsRepositoryInstance
											.findByTransferPositionNameAndIsMissionGenerated(destinationPositionName,
													0);
									transferPalletMissionList1 = transferPalletMissionDetailsRepositoryInstance
											.findByPositionNameAndIsMissionGenerated(currentPositionName, 0);
									// System.out.println("transferPalletMissionList::" +
									// transferPalletMissionList.size());
									// System.out.println("transferPalletMissionList1::" +
									// transferPalletMissionList1.size());
									if (transferPalletMissionList.isEmpty() && transferPalletMissionList1.isEmpty()) {
										List<TransferPalletMissionRuntimeDetailsEntity> transferPalletMissionRuntimepreviousList = null;
										List<TransferPalletMissionRuntimeDetailsEntity> transferPalletMissionRuntimeDetailsList = null;

										transferPalletMissionRuntimepreviousList = transferPalletMissionRuntimeDetailsRepositoryInstance
												.findByPreviousPositionNameAndTransferMissionStatusIn(currentPositionName, "READY", "IN_PROGRESS");
										transferPalletMissionRuntimeDetailsList = transferPalletMissionRuntimeDetailsRepositoryInstance
												.findByPreviousPositionNameAndTransferMissionStatusIn(
														destinationPositionName, "READY", "IN_PROGRESS");
										// System.out.println("size:"+transferPalletMissionRuntimepreviousList.size());
										// System.out.println("size:"+transferPalletMissionRuntimeDetailsList.size());
										if (transferPalletMissionRuntimepreviousList.isEmpty()
												&& transferPalletMissionRuntimeDetailsList.isEmpty()) {
											transferPalletMissionDetailsEntityInstnace
													.setPalletCode(currentPalletStockList.get(0).getPalletCode());
											transferPalletMissionDetailsEntityInstnace.setPalletInformationId(
													currentPalletStockList.get(0).getPalletInformationId());
											transferPalletMissionDetailsEntityInstnace
													.setPositionId(currentPalletStockList.get(0).getPositionId());
											transferPalletMissionDetailsEntityInstnace
													.setPositionName(currentPositionName);
											transferPalletMissionDetailsEntityInstnace.setTransferPositionId(
													masterPositionDetailsDestList.get(0).getPositionId());
											transferPalletMissionDetailsEntityInstnace
													.setTransferPositionName(destinationPositionName);
											transferPalletMissionDetailsEntityInstnace.setIsMissionGenerated(0);
											transferPalletMissionDetailsEntityInstnace.setIsManualMission(1);
											transferPalletMissionDetailsEntityInstnace.setUserId(userId);
											transferPalletMissionDetailsEntityInstnace.setUserName(userName);
											transferPalletMissionDetailsEntityInstnace
													.setCDateTime(LocalDateTime.now().toString());
											transferPalletMissionDetailsRepositoryInstance
													.save(transferPalletMissionDetailsEntityInstnace);

											AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
											Authentication authentication = SecurityContextHolder.getContext()
													.getAuthentication();
											String name = authentication.getName();
											System.out.println(" name :: " + name);
											auditTrailDetailsEntity
													.setOperatorActions("Transfer mission completed from "
															+ transferPalletMissionDetailsEntityInstnace
																	.getPositionName()
															+ " to "
															+ transferPalletMissionDetailsEntityInstnace
																	.getTransferPositionName()
															+ " having pallet code "
															+ transferPalletMissionDetailsEntityInstnace.getPalletCode()
															+ " by " + name);
											auditTrailDetailsEntity.setField("Transfer mission completed");
//										auditTrailDetailsEntity.setAfterValue(0);
//										auditTrailDetailsEntity.setBeforeValue(0);
											auditTrailDetailsEntity.setReason("Transfer mission completed");

											auditTrailDetailsEntity.setUsername(name);
											auditTrailDetailsEntity.setDatetimeC(currentDate);
											auditTrailDetailsRepository.save(auditTrailDetailsEntity);

											return ResponseHandler.generateResponse(
													"Transfered Mission Added sucessfully", HttpStatus.OK,
													transferPalletMissionDetailsEntityInstnace);
//												new ResponseEntity<String>("pallet transfered sucessfully",
//												HttpStatus.OK);
										} else {
											// System.out.println("Mission is in READY or INPROGRESS status");
											return ResponseHandler.generateResponse(
													"Mission is in READY or INPROGRESS status",
													HttpStatus.ALREADY_REPORTED, null);
//												new ResponseEntity<String>("position is ready or in_progress",
//												HttpStatus.ALREADY_REPORTED);
										}

									} else {
										// System.out.println("Mission already generated");
										return ResponseHandler.generateResponse("Mission already generated",
												HttpStatus.ALREADY_REPORTED, null);
//											new ResponseEntity<String>("already ismission generated 0",
//											HttpStatus.ALREADY_REPORTED);

									}
								}
							} else {
								// System.out.println("Pallets present in front side");
								return ResponseHandler.generateResponse("pallets present in front side",
										HttpStatus.ALREADY_REPORTED, null);
//									new ResponseEntity<String>("pallets present in front side",
//									HttpStatus.ALREADY_REPORTED);
							}
						} else {
							// System.out.println("Destination position is not correct");
							return ResponseHandler.generateResponse("Destination position is not correct",
									HttpStatus.NO_CONTENT, null);
//								new ResponseEntity<String>("Destination position is not correct", HttpStatus.NO_CONTENT);
						}

					}
				}
			}
		} else {
			// System.out.println("Given Pallet Can not be transfered");
			return ResponseHandler.generateResponse("Given Pallet Can not be transfered",
					HttpStatus.NON_AUTHORITATIVE_INFORMATION, null);
//						new ResponseEntity<String>("error", HttpStatus.NON_AUTHORITATIVE_INFORMATION);
		}
		return null;

	}



	public List<TransferPalletMissionDetailsEntity> findByCDatetime() {
		Date date = new Date();
		String strDateFormat = "yyyy-MM-dd";
		DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
		String currentDateTime = dateFormat.format(date);
		List<TransferPalletMissionDetailsEntity> findBycDateTime = transferPalletMissionDetailsRepositoryInstance
				.findByCDatetime(currentDateTime);
		return findBycDateTime;
	}

	public List<TransferPalletMissionDetailsEntity> findByAllFilters(String startDate, String endDate) {

		List<String> filterList = new ArrayList<String>();

		List<TransferPalletMissionDetailsEntity> list = new ArrayList<TransferPalletMissionDetailsEntity>();

		if (!(startDate.equals("NA")) && !(endDate.equals("NA"))) {
			// Removing "T" from datatime format
			String startDateTime = startDate.toString().replace("T", " ");
			String endDateTime = endDate.toString().replace("T", " ");

			list = transferPalletMissionDetailsRepositoryInstance.findTransferPalletDetailsBetweenDates(startDateTime,
					endDateTime);
		} else {
			Date dNow = new Date();
			SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
			String date = ft.format(dNow);
			list = transferPalletMissionDetailsRepositoryInstance
					.findTransferPalletDetailsBetweenDates(date + " " + "00:00:00", date + " " + "23:59:59");
		}

		if (filterList.size() == 0 && list.size() == 0) {
			list = null;
		}
		return list;

	}

}
