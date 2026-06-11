package com.ats.mahindrabattery.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.ats.mahindrabattery.entity.AuditTrailDetailsEntity;
import com.ats.mahindrabattery.entity.CurrentPalletStockDetailsEntity;
import com.ats.mahindrabattery.entity.ManualOutfeedMissionDetailsEntity;
import com.ats.mahindrabattery.entity.MappingFloorAreaDetailsEntity;
import com.ats.mahindrabattery.entity.MasterPositionDetailsEntity;
import com.ats.mahindrabattery.entity.MasterStationTagDetailsEntity;
import com.ats.mahindrabattery.entity.MasterUserDetailsEntity;
import com.ats.mahindrabattery.entity.TransferPalletMissionRuntimeDetailsEntity;
import com.ats.mahindrabattery.repository.AuditTrailDetailsRepository;
import com.ats.mahindrabattery.repository.CurrentPalletStockDetailsRepository;
import com.ats.mahindrabattery.repository.ManualOutfeedMissionDetailsRepository;
import com.ats.mahindrabattery.repository.MappingFloorAreaDetailsRepository;
import com.ats.mahindrabattery.repository.MasterPositionDetailsRepository;
import com.ats.mahindrabattery.repository.MasterStationTagDetailsRepository;
import com.ats.mahindrabattery.repository.MasterUserDetailsRepository;
import com.ats.mahindrabattery.repository.TransferPalletMissionDetailsRepository;
import com.ats.mahindrabattery.repository.TransferPalletMissionRuntimeDetailsRepository;
import com.ats.mahindrabattery.response.ResponseHandler;
import com.ats.mahindrabattery.service.ManualOutfeedMissionDetailsService;

@Service
public class ManualOutfeedMissionDetailsServiceImpl implements ManualOutfeedMissionDetailsService {

	@Autowired
	ManualOutfeedMissionDetailsRepository manualOutfeedMissionDetailsRepositoryInstance;

	@Autowired
	CurrentPalletStockDetailsRepository currentPalletStockDetailsRepositoryInstance;

	@Autowired
	MasterPositionDetailsRepository masterPositionDetalisRepositoryInstance;

	@Autowired
	MappingFloorAreaDetailsRepository MappingFloorAreaDetailsRepositoryInstance;

	@Autowired
	MasterUserDetailsRepository masterUserDetailsRepository;

	@Autowired
	private AuditTrailDetailsRepository auditTrailDetailsRepository;

	@Autowired
	private TransferPalletMissionRuntimeDetailsRepository transferPalletMissionRuntimeDetailsRepository;

	@Autowired
	private MasterStationTagDetailsRepository masterStationTagDetailsRepository;

	public List<ManualOutfeedMissionDetailsEntity> findAll() {
		return manualOutfeedMissionDetailsRepositoryInstance.findAll();
	}

	public ResponseEntity<ManualOutfeedMissionDetailsEntity> addCurrentPalletStockDetailsInManualOutfeed(
			ManualOutfeedMissionDetailsEntity manualOutfeedMissionDetailsEntity) {

		try {

			MasterStationTagDetailsEntity findByPlcTagName = masterStationTagDetailsRepository
					.findByPlcTagName("ATS.WMS_STATION.STACKER_1_DUMP_TANK_HEALTHY");
			if (!"1".equals(findByPlcTagName.getCurrentValue())) {
				return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(new ManualOutfeedMissionDetailsEntity(),
						HttpStatus.ACCEPTED);
			}

			MasterStationTagDetailsEntity findByPlcTagName2 = masterStationTagDetailsRepository
					.findByPlcTagName("ATS.WMS_STATION.STACKER_2_DUMP_TANK_HEALTHY");
			if (!"1".equals(findByPlcTagName2.getCurrentValue())) {
				return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(new ManualOutfeedMissionDetailsEntity(),
						HttpStatus.MULTI_STATUS);
			}

			int positionId = manualOutfeedMissionDetailsEntity.getPositionId();
			MasterPositionDetailsEntity findByPositionId = masterPositionDetalisRepositoryInstance
					.findByPositionId(positionId + 1);

			if (positionId % 2 == 1 && findByPositionId.getIsManualDispatch() == 1) {
				return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(new ManualOutfeedMissionDetailsEntity(),
						HttpStatus.NON_AUTHORITATIVE_INFORMATION);
			}

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String name = authentication.getName();

			List<MasterUserDetailsEntity> findByuserName = masterUserDetailsRepository.findByuserName(name);
			int userId = findByuserName.get(0).getUserId();

			Date dNow = new Date();
			SimpleDateFormat sdateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdateformat.format(dNow);
			List<ManualOutfeedMissionDetailsEntity> manuallist = null;
			List<CurrentPalletStockDetailsEntity> currentlist = null;
			List<MappingFloorAreaDetailsEntity> mappingFloorAreaDetailsEntity = null;
			ManualOutfeedMissionDetailsEntity findByPalletCodeAndIsMissionGenerated = null;
			// MasterPositionDetailsEntity masterPositionDetailsEntity = new
			// MasterPositionDetailsEntity();
			currentlist = currentPalletStockDetailsRepositoryInstance
					.findByPalletCode(manualOutfeedMissionDetailsEntity.getPalletCode());

//			System.out.println("1. mappingFloorAreaDetailsEntity ::" + mappingFloorAreaDetailsEntity);

			if (currentlist.isEmpty()) {
				return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(manualOutfeedMissionDetailsEntity,
						HttpStatus.IM_USED);
			}

			String palletCode = currentlist.get(0).getPalletCode();
			List<TransferPalletMissionRuntimeDetailsEntity> findbyPalletCodeAndTransferMissionStatusIn = transferPalletMissionRuntimeDetailsRepository
					.findByPalletCodeAndTransferMissionStatusIn(palletCode, Arrays.asList("READY", "IN_PROGRESS"));

			List<ManualOutfeedMissionDetailsEntity> manualOutfeedlist = manualOutfeedMissionDetailsRepositoryInstance
					.findByPalletCodeAndIsMissionGenerated(manualOutfeedMissionDetailsEntity.getPalletCode(), 1);
			// check area or floor outfeed is active

			System.out.println("manualOutfeedlist :: " + manualOutfeedlist.size());
			MasterPositionDetailsEntity masterPositionDetailsEntity = masterPositionDetalisRepositoryInstance
					.findByPositionId(currentlist.get(0).getPositionId());
			System.out.println("position " + currentlist.get(0).getPositionId());
			findByPalletCodeAndIsMissionGenerated = manualOutfeedMissionDetailsRepositoryInstance
					.findByPalletInformationDetailsIdAndIsMissionGenerated(
							manualOutfeedMissionDetailsEntity.getPalletInformationDetailsId(), 1);

			if (!findbyPalletCodeAndTransferMissionStatusIn.isEmpty()) {
				return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(new ManualOutfeedMissionDetailsEntity(),
						HttpStatus.NON_AUTHORITATIVE_INFORMATION);
			}
			if (!currentlist.isEmpty() && manualOutfeedlist.isEmpty()
					&& findbyPalletCodeAndTransferMissionStatusIn.isEmpty()) {

				if (findByPalletCodeAndIsMissionGenerated == null
						&& findbyPalletCodeAndTransferMissionStatusIn.isEmpty()) {

					mappingFloorAreaDetailsEntity = MappingFloorAreaDetailsRepositoryInstance
							.findByAreaIdAndFloorId(currentlist.get(0).getAreaId(), currentlist.get(0).getFloorId());
					System.out.println("2. mappingFloorAreaDetailsEntity ::" + mappingFloorAreaDetailsEntity);

					if (mappingFloorAreaDetailsEntity.get(0).getOutfeedIsActive() == 1
							&& masterPositionDetailsEntity.getIsManualDispatch() == 0
							&& masterPositionDetailsEntity.getEmptyPalletPosition() == 0
							&& masterPositionDetailsEntity.getPositionIsActive() == 1) {

						manualOutfeedMissionDetailsEntity
								.setPalletInformationDetailsId(currentlist.get(0).getPalletInformationId());
						manualOutfeedMissionDetailsEntity.setPositionName(currentlist.get(0).getPositionName());
						manualOutfeedMissionDetailsEntity.setPositionId(currentlist.get(0).getPositionId());
						manualOutfeedMissionDetailsEntity.setUserId(userId);
						manualOutfeedMissionDetailsEntity.setUserName(name);
						manualOutfeedMissionDetailsEntity.setLoadDateTime(date);
						manualOutfeedMissionDetailsEntity.setMfgDate(currentlist.get(0).getMfgDate());
						manualOutfeedMissionDetailsEntity.setMfgShift(currentlist.get(0).getMfgShift());
						manualOutfeedMissionDetailsEntity.setSerialNumber(currentlist.get(0).getSerialNumber());
						manualOutfeedMissionDetailsEntity.setIsMissionGenerated(0);
						// manualOutfeedMissionDetailsEntity.setStationId(0);

						// MasterPositionDetailsEntity masterPositionDetailsEntity = new
						// MasterPositionDetailsEntity();
						masterPositionDetailsEntity = masterPositionDetalisRepositoryInstance
								.findByPositionId(currentlist.get(0).getPositionId());
						System.out.println("manual dispatch 0");

//						Date dNow = new Date();
//						SimpleDateFormat sdateformat = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
//						String date = sdateformat.format(dNow);

						manualOutfeedMissionDetailsEntity.setCDateTime(date);
						masterPositionDetalisRepositoryInstance.updateisManualDispatchBypositionId(1,
								masterPositionDetailsEntity.getPositionId());
						System.out.println("manual dispatch 1");

						AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();

						System.out.println(" name :: " + name);
						auditTrailDetailsEntity.setOperatorActions("Dispatch given by  " + name + " for position  "
								+ manualOutfeedMissionDetailsEntity.getPositionName() + " having pallet code "
								+ manualOutfeedMissionDetailsEntity.getPalletCode());
						auditTrailDetailsEntity.setField("Dipsatch");
//							auditTrailDetailsEntity.setAfterValue(0);
//							auditTrailDetailsEntity.setBeforeValue(0);
						auditTrailDetailsEntity.setReason("Manual Dispatch");

						auditTrailDetailsEntity.setUsername(name);
						auditTrailDetailsEntity.setDatetimeC(date);
						auditTrailDetailsRepository.save(auditTrailDetailsEntity);

//						return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(
//								manualOutfeedMissionDetailsRepositoryInstance.save(manualOutfeedMissionDetailsEntity),
//								HttpStatus.OK);
//						 return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(manualOutfeedMissionDetailsEntity,HttpStatus.NON_AUTHORITATIVE_INFORMATION);
					} else if (masterPositionDetailsEntity.getIsManualDispatch() == 1
							&& masterPositionDetailsEntity.getEmptyPalletPosition() == 0) {
						return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(
								new ManualOutfeedMissionDetailsEntity(), HttpStatus.ALREADY_REPORTED);
					} else if (masterPositionDetailsEntity.getEmptyPalletPosition() == 1) {
						// Handle the case where the position is empty
						return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(
								new ManualOutfeedMissionDetailsEntity(), HttpStatus.NOT_ACCEPTABLE);
					} else if (mappingFloorAreaDetailsEntity.get(0).getOutfeedIsActive() == 0) {
						// Handle the case where outfeed is not active
						return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(
								new ManualOutfeedMissionDetailsEntity(), HttpStatus.NO_CONTENT);
					} else if (masterPositionDetailsEntity.getPositionIsActive() == 0) {
						return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(
								new ManualOutfeedMissionDetailsEntity(), HttpStatus.CREATED);
					} else {
						return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(manualOutfeedMissionDetailsEntity,
								HttpStatus.FORBIDDEN);
					}

				} else {
					findByPalletCodeAndIsMissionGenerated.setIsMissionGenerated(0);
					findByPalletCodeAndIsMissionGenerated
							.setPalletCode(manualOutfeedMissionDetailsEntity.getPalletCode());
					manualOutfeedMissionDetailsRepositoryInstance.save(findByPalletCodeAndIsMissionGenerated);

					AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();

					System.out.println(" name :: " + name);
					auditTrailDetailsEntity.setOperatorActions("Dispatch given by  " + name + " for position  "
							+ manualOutfeedMissionDetailsEntity.getPositionName() + " having Serial Number "
							+ manualOutfeedMissionDetailsEntity.getSerialNumber());
					auditTrailDetailsEntity.setField("Dipsatch");
//						auditTrailDetailsEntity.setAfterValue(0);
//						auditTrailDetailsEntity.setBeforeValue(0);
					auditTrailDetailsEntity.setReason("Manual Dispatch");

					auditTrailDetailsEntity.setUsername(name);
					auditTrailDetailsEntity.setDatetimeC(date);
					auditTrailDetailsRepository.save(auditTrailDetailsEntity);
					return new ResponseEntity<>(new ManualOutfeedMissionDetailsEntity(), HttpStatus.OK);
				}

			} else {
				if (masterPositionDetailsEntity.getPositionIsActive() == 1) {
					System.out.println("currentlist.get(0).getPalletInformationId()::"
							+ currentlist.get(0).getPalletInformationId());
					manualOutfeedlist.get(0).setPalletInformationDetailsId(currentlist.get(0).getPalletInformationId());
					manualOutfeedlist.get(0).setCDateTime(date);
					manualOutfeedlist.get(0).setIsMissionGenerated(0);
					manualOutfeedlist.get(0).setPalletCode(currentlist.get(0).getPalletCode());
					manualOutfeedlist.get(0).setPositionId(currentlist.get(0).getPositionId());
					manualOutfeedlist.get(0).setPositionName(currentlist.get(0).getPositionName());
					manualOutfeedlist.get(0).setLoadDateTime(currentlist.get(0).getLoadDatetime());
					manualOutfeedlist.get(0).setMfgDate(currentlist.get(0).getMfgDate());
					manualOutfeedlist.get(0).setMfgShift(currentlist.get(0).getMfgShift());
					manualOutfeedlist.get(0).setSerialNumber(currentlist.get(0).getSerialNumber());
					manualOutfeedlist.get(0).setUserId(userId);
					manualOutfeedlist.get(0).setUserName(name);
					manualOutfeedMissionDetailsRepositoryInstance.save(manualOutfeedlist.get(0));

					masterPositionDetailsEntity = masterPositionDetalisRepositoryInstance
							.findByPositionId(currentlist.get(0).getPositionId());
					masterPositionDetailsEntity.setIsManualDispatch(1);
					masterPositionDetalisRepositoryInstance.save(masterPositionDetailsEntity);

					AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();

					auditTrailDetailsEntity.setOperatorActions("Dispatch given by  " + name + " for position  "
							+ manualOutfeedMissionDetailsEntity.getPositionName() + " having pallet code "
							+ manualOutfeedMissionDetailsEntity.getPalletCode());
					auditTrailDetailsEntity.setField("Dipsatch");
//					auditTrailDetailsEntity.setAfterValue(0);
//					auditTrailDetailsEntity.setBeforeValue(0);
					auditTrailDetailsEntity.setReason("Manual Dispatch");

					auditTrailDetailsEntity.setUsername(name);
					auditTrailDetailsEntity.setDatetimeC(date);
					auditTrailDetailsRepository.save(auditTrailDetailsEntity);

					return new ResponseEntity<>(new ManualOutfeedMissionDetailsEntity(), HttpStatus.OK);
				} else {
					return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(
							new ManualOutfeedMissionDetailsEntity(), HttpStatus.CREATED);
				}
			}

			// Check IsMissionGenerated is 0

			// System.out.println("manual dispatch value
			// "+masterPositionDetailsEntity.getIsManualDispatch());
			// System.out.println("position ID value "+currentlist.get(0).getPositionId());
//			if(masterPositionDetailsEntity.getIsManualDispatch()==1 && masterPositionDetailsEntity.getEmptyPalletPosition()==0)
//			{
//			
//			}
//			else {
//				return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(new ManualOutfeedMissionDetailsEntity(),
//						HttpStatus.ALREADY_REPORTED);
//			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(
				manualOutfeedMissionDetailsRepositoryInstance.save(manualOutfeedMissionDetailsEntity), HttpStatus.OK);
	}

	public ResponseEntity<ManualOutfeedMissionDetailsEntity> addSerialNumberStockDetailsInManualOutfeed(
			ManualOutfeedMissionDetailsEntity manualOutfeedMissionDetailsEntity) {

		try {
			MasterStationTagDetailsEntity findByPlcTagName = masterStationTagDetailsRepository
					.findByPlcTagName("ATS.WMS_STATION.STACKER_1_DUMP_TANK_HEALTHY");
			if (!"1".equals(findByPlcTagName.getCurrentValue())) {
				return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(new ManualOutfeedMissionDetailsEntity(),
						HttpStatus.ACCEPTED);
			}

			MasterStationTagDetailsEntity findByPlcTagName2 = masterStationTagDetailsRepository
					.findByPlcTagName("ATS.WMS_STATION.STACKER_2_DUMP_TANK_HEALTHY");
			if (!"1".equals(findByPlcTagName2.getCurrentValue())) {
				return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(new ManualOutfeedMissionDetailsEntity(),
						HttpStatus.MULTI_STATUS);
			}

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String name = authentication.getName();
			List<MasterUserDetailsEntity> findByuserName = masterUserDetailsRepository.findByuserName(name);

			Date dNow = new Date();
			SimpleDateFormat sdateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdateformat.format(dNow);
			List<ManualOutfeedMissionDetailsEntity> manuallist = null;
			CurrentPalletStockDetailsEntity currentlist = null;
			List<MappingFloorAreaDetailsEntity> mappingFloorAreaDetailsEntity = null;
			ManualOutfeedMissionDetailsEntity findByPalletCodeAndIsMissionGenerated = null;
			// MasterPositionDetailsEntity masterPositionDetailsEntity = new
			// MasterPositionDetailsEntity();
			currentlist = currentPalletStockDetailsRepositoryInstance
					.findBySerialNumber(manualOutfeedMissionDetailsEntity.getSerialNumber());
			if (currentlist == null) {
				return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(manualOutfeedMissionDetailsEntity,
						HttpStatus.IM_USED);
			}

			String palletCode = currentlist.getPalletCode();
			List<TransferPalletMissionRuntimeDetailsEntity> findbyPalletCodeAndTransferMissionStatusIn = transferPalletMissionRuntimeDetailsRepository
					.findByPalletCodeAndTransferMissionStatusIn(palletCode, Arrays.asList("READY", "IN_PROGRESS"));

			if (!findbyPalletCodeAndTransferMissionStatusIn.isEmpty()) {
				return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(manualOutfeedMissionDetailsEntity,
						HttpStatus.NON_AUTHORITATIVE_INFORMATION);
			}

			System.out.println("1. mappingFloorAreaDetailsEntity ::" + mappingFloorAreaDetailsEntity);

			List<ManualOutfeedMissionDetailsEntity> manualOutfeedlist = manualOutfeedMissionDetailsRepositoryInstance
					.findByPalletCodeAndIsMissionGenerated(currentlist.getPalletCode(), 1);
			// check area or floor outfeed is active

			System.out.println("manualOutfeedlist :: " + manualOutfeedlist.size());
			MasterPositionDetailsEntity masterPositionDetailsEntity = masterPositionDetalisRepositoryInstance
					.findByPositionId(currentlist.getPositionId());
			System.out.println("position " + currentlist.getPositionId());
			findByPalletCodeAndIsMissionGenerated = manualOutfeedMissionDetailsRepositoryInstance
					.findByPalletInformationDetailsIdAndIsMissionGenerated(
							manualOutfeedMissionDetailsEntity.getPalletInformationDetailsId(), 1);
			if (currentlist != null && manualOutfeedlist.isEmpty()) {

				if (findByPalletCodeAndIsMissionGenerated == null) {

					mappingFloorAreaDetailsEntity = MappingFloorAreaDetailsRepositoryInstance
							.findByAreaIdAndFloorId(currentlist.getAreaId(), currentlist.getFloorId());
					System.out.println("2. mappingFloorAreaDetailsEntity ::" + mappingFloorAreaDetailsEntity);

					if (mappingFloorAreaDetailsEntity.get(0).getOutfeedIsActive() == 1
							&& masterPositionDetailsEntity.getIsManualDispatch() == 0
							&& masterPositionDetailsEntity.getEmptyPalletPosition() == 0
							&& masterPositionDetailsEntity.getPositionIsActive() == 1) {

						manualOutfeedMissionDetailsEntity
								.setPalletInformationDetailsId(currentlist.getPalletInformationId());
						manualOutfeedMissionDetailsEntity.setPositionName(currentlist.getPositionName());
						manualOutfeedMissionDetailsEntity.setPositionId(currentlist.getPositionId());
						manualOutfeedMissionDetailsEntity.setUserId(findByuserName.get(0).getUserId());
						manualOutfeedMissionDetailsEntity.setUserName(name);
						manualOutfeedMissionDetailsEntity.setLoadDateTime(date);
						manualOutfeedMissionDetailsEntity.setMfgDate(currentlist.getMfgDate());
						manualOutfeedMissionDetailsEntity.setMfgShift(currentlist.getMfgShift());
						manualOutfeedMissionDetailsEntity.setIsMissionGenerated(0);
						manualOutfeedMissionDetailsEntity.setSerialNumber(currentlist.getSerialNumber());
						manualOutfeedMissionDetailsEntity.setPalletCode(currentlist.getPalletCode());
						// manualOutfeedMissionDetailsEntity.setStationId(0);

						// MasterPositionDetailsEntity masterPositionDetailsEntity = new
						// MasterPositionDetailsEntity();
						masterPositionDetailsEntity = masterPositionDetalisRepositoryInstance
								.findByPositionId(currentlist.getPositionId());
						System.out.println("manual dispatch 0");

//						Date dNow = new Date();
//						SimpleDateFormat sdateformat = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
//						String date = sdateformat.format(dNow);

						manualOutfeedMissionDetailsEntity.setCDateTime(date);
						masterPositionDetalisRepositoryInstance.updateisManualDispatchBypositionId(1,
								masterPositionDetailsEntity.getPositionId());
						System.out.println("manual dispatch 1");

						AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();

						System.out.println(" name :: " + name);
						auditTrailDetailsEntity.setOperatorActions("Dispatch given by  " + name + " for position  "
								+ manualOutfeedMissionDetailsEntity.getPositionName() + " having Serial Number "
								+ manualOutfeedMissionDetailsEntity.getSerialNumber());
						auditTrailDetailsEntity.setField("Dipsatch");
//							auditTrailDetailsEntity.setAfterValue(0);
//							auditTrailDetailsEntity.setBeforeValue(0);
						auditTrailDetailsEntity.setReason("Manual Dispatch");

						auditTrailDetailsEntity.setUsername(name);
						auditTrailDetailsEntity.setDatetimeC(date);
						auditTrailDetailsRepository.save(auditTrailDetailsEntity);

//						return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(
//								manualOutfeedMissionDetailsRepositoryInstance.save(manualOutfeedMissionDetailsEntity),
//								HttpStatus.OK);
//						 return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(manualOutfeedMissionDetailsEntity,HttpStatus.NON_AUTHORITATIVE_INFORMATION);
					} else if (masterPositionDetailsEntity.getIsManualDispatch() == 1
							&& masterPositionDetailsEntity.getEmptyPalletPosition() == 0) {
						return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(
								new ManualOutfeedMissionDetailsEntity(), HttpStatus.ALREADY_REPORTED);
					} else if (masterPositionDetailsEntity.getEmptyPalletPosition() == 1) {
						// Handle the case where the position is empty
						return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(
								new ManualOutfeedMissionDetailsEntity(), HttpStatus.NOT_ACCEPTABLE);
					} else if (mappingFloorAreaDetailsEntity.get(0).getOutfeedIsActive() == 0) {
						// Handle the case where outfeed is not active
						return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(
								new ManualOutfeedMissionDetailsEntity(), HttpStatus.NO_CONTENT);
					}

					else if (masterPositionDetailsEntity.getPositionIsActive() == 0) {
						return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(
								new ManualOutfeedMissionDetailsEntity(), HttpStatus.CREATED);
					}

					else {
						return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(manualOutfeedMissionDetailsEntity,
								HttpStatus.FORBIDDEN);
					}

				} else {
					findByPalletCodeAndIsMissionGenerated.setIsMissionGenerated(0);
					findByPalletCodeAndIsMissionGenerated
							.setPalletCode(manualOutfeedMissionDetailsEntity.getPalletCode());
					findByPalletCodeAndIsMissionGenerated
							.setSerialNumber(manualOutfeedMissionDetailsEntity.getSerialNumber());
					manualOutfeedMissionDetailsRepositoryInstance.save(findByPalletCodeAndIsMissionGenerated);

					AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();

					System.out.println(" name :: " + name);
					auditTrailDetailsEntity.setOperatorActions("Dispatch given by  " + name + " for position  "
							+ manualOutfeedMissionDetailsEntity.getPositionName() + " having Serial Number "
							+ manualOutfeedMissionDetailsEntity.getSerialNumber());
					auditTrailDetailsEntity.setField("Dipsatch");
//						auditTrailDetailsEntity.setAfterValue(0);
//						auditTrailDetailsEntity.setBeforeValue(0);
					auditTrailDetailsEntity.setReason("Manual Dispatch");

					auditTrailDetailsEntity.setUsername(name);
					auditTrailDetailsEntity.setDatetimeC(date);
					auditTrailDetailsRepository.save(auditTrailDetailsEntity);
					return new ResponseEntity<>(new ManualOutfeedMissionDetailsEntity(), HttpStatus.OK);
				}

			} else {
				if (masterPositionDetailsEntity.getPositionIsActive() == 1) {
					System.out.println(
							"currentlist.get(0).getPalletInformationId()::" + currentlist.getPalletInformationId());
					manualOutfeedlist.get(0).setPalletInformationDetailsId(currentlist.getPalletInformationId());
					manualOutfeedlist.get(0).setCDateTime(date);
					manualOutfeedlist.get(0).setIsMissionGenerated(0);
					manualOutfeedlist.get(0).setPalletCode(currentlist.getPalletCode());
					manualOutfeedlist.get(0).setPositionId(currentlist.getPositionId());
					manualOutfeedlist.get(0).setPositionName(currentlist.getPositionName());
					manualOutfeedlist.get(0).setLoadDateTime(currentlist.getLoadDatetime());
					manualOutfeedlist.get(0).setMfgDate(currentlist.getMfgDate());
					manualOutfeedlist.get(0).setMfgShift(currentlist.getMfgShift());
					manualOutfeedlist.get(0).setSerialNumber(currentlist.getSerialNumber());

					manualOutfeedlist.get(0).setUserId(findByuserName.get(0).getUserId());
					manualOutfeedlist.get(0).setUserName(name);
					manualOutfeedMissionDetailsRepositoryInstance.save(manualOutfeedlist.get(0));

					masterPositionDetailsEntity = masterPositionDetalisRepositoryInstance
							.findByPositionId(currentlist.getPositionId());
					masterPositionDetailsEntity.setIsManualDispatch(1);
					masterPositionDetalisRepositoryInstance.save(masterPositionDetailsEntity);

					AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();

					System.out.println(" name :: " + name);
					auditTrailDetailsEntity.setOperatorActions("Dispatch given by  " + name + " for position  "
							+ manualOutfeedMissionDetailsEntity.getPositionName() + " having Serial Number "
							+ manualOutfeedMissionDetailsEntity.getSerialNumber());
					auditTrailDetailsEntity.setField("Dipsatch");

					auditTrailDetailsEntity.setReason("Manual Dispatch");

					auditTrailDetailsEntity.setUsername(name);
					auditTrailDetailsEntity.setDatetimeC(date);
					auditTrailDetailsRepository.save(auditTrailDetailsEntity);
					return new ResponseEntity<>(new ManualOutfeedMissionDetailsEntity(), HttpStatus.OK);
				} else {
					return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(
							new ManualOutfeedMissionDetailsEntity(), HttpStatus.CREATED);
				}
			}



		} catch (Exception e) {
		
			e.printStackTrace();
			return null;
		}
		return new ResponseEntity<ManualOutfeedMissionDetailsEntity>(
				manualOutfeedMissionDetailsRepositoryInstance.save(manualOutfeedMissionDetailsEntity), HttpStatus.OK);
	}
}