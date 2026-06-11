package com.ats.mahindrabattery.serviceimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.ats.mahindrabattery.entity.AuditTrailDetailsEntity;
import com.ats.mahindrabattery.entity.CurrentPalletStockDetailsEntity;
import com.ats.mahindrabattery.entity.DuplicatePalletCodeEntity;
import com.ats.mahindrabattery.entity.LockUnlockDetailsEntity;
import com.ats.mahindrabattery.entity.MasterPositionDetailsEntity;
import com.ats.mahindrabattery.entity.MasterRackDetailsEntity;
import com.ats.mahindrabattery.entity.MasterRackPositionDetails;
import com.ats.mahindrabattery.repository.AuditTrailDetailsRepository;
import com.ats.mahindrabattery.repository.CurrentPalletStockDetailsRepository;
import com.ats.mahindrabattery.repository.DuplicatePalletCoderepository;
import com.ats.mahindrabattery.repository.LockUnlockDetailsRepository;
import com.ats.mahindrabattery.repository.MasterPositionDetailsRepository;
import com.ats.mahindrabattery.repository.MasterRackDetailsRepository;
import com.ats.mahindrabattery.response.ResponseHandler;
import com.ats.mahindrabattery.service.MasterPositionDetailsService;


import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

@Service

public class MasterPositionDetailsServiceImpl implements MasterPositionDetailsService {

	@Autowired
	private MasterPositionDetailsRepository masterPositionDetailsRepositoryInstance;

	@Autowired
	private MasterRackDetailsRepository masterRackDetailsRepositoryInstance;

	@Autowired
	private CurrentPalletStockDetailsRepository currentPalletStockDetailsRepository;

	@Autowired
	private AuditTrailDetailsRepository auditTrailDetailsRepository;

	@Autowired
	private LockUnlockDetailsRepository lockUnlockDetailsRepository;
	
	
	@Autowired
	private DuplicatePalletCoderepository duplicatePalletCoderepository;

	public List<MasterPositionDetailsEntity> findAll() {
		return masterPositionDetailsRepositoryInstance.findAll();
	}
//	public List<MasterPositionDetailsEntity>findByPositionName(String positionName){
//		return masterPositionDetailsRepositoryInstance.findByPositionName(positionName);
//	}

	public MasterPositionDetailsEntity findByPositionNameAndPositionIsAllocatedAndEmptyPalletPositionAndPositionIsActive(
			String positionName, int positionIsAllocated, int emptyPalletPosition, int positionIsActive) {
		try {
			return masterPositionDetailsRepositoryInstance
					.findByPositionNameAndPositionIsAllocatedAndEmptyPalletPositionAndPositionIsActive(positionName,
							positionIsAllocated, emptyPalletPosition, positionIsActive);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

//	public List<MasterRackPositionDetails> findByAreaIdAndFloorId(int areaId, int floorId) {
//		// System.out.println("in 123");
//		// System.out.println("Area Id: " + areaId + "Floor Id : " + floorId);
//		List<MasterRackPositionDetails> list = new ArrayList<MasterRackPositionDetails>();
//		List<MasterRackDetailsEntity> rackList = null;
//		List<MasterPositionDetailsEntity> positionList = null;
//		// List<CurrentPalletStockDetailsEntity>currentPalletList=null;
//		try {
//			positionList = masterPositionDetailsRepositoryInstance.findByAreaIdAndFloorIdOrderByPositionId(areaId,
//					floorId);
//			// System.out.println("in positionList ::"+positionList);
//			rackList = masterRackDetailsRepositoryInstance.findByAreaIdAndFloorIdOrderByRackId(areaId, floorId);
//			// System.out.println("rackList ::"+rackList.size());
//			for (int i = 0; i < rackList.size(); i++) {
//				MasterRackPositionDetails obj = new MasterRackPositionDetails();
//				obj.setRackId(rackList.get(i).getRackId());
//				int rackId = rackList.get(i).getRackId();
//
//				List<MasterPositionDetailsEntity> list1 = null;
//				list1 = positionList.stream().filter(data -> data.getRackId() == (rackId))
//						.sorted(Comparator.comparing(MasterPositionDetailsEntity::getPositionId).reversed())
//						.collect(Collectors.toList());
//				// System.out.println("in list1 ::"+list1.size());
//				for (int p = 0; p < list1.size(); p++) {
//					// find by position id
//					List<CurrentPalletStockDetailsEntity> currentPalletList = null;
//					currentPalletList = currentPalletStockDetailsRepository
//							.findByPositionId(list1.get(p).getPositionId());
//
//					// check list size >0
//
//					if (currentPalletList.size() > 0) {
//						// check if materialcode!=na, if true
//						// System.out.println("in
//						// currentpallet::"+currentPalletList.get(0).getProductVariantCode());
//						if (!currentPalletList.get(0).getProductVariantCode().equals("NA")) {
//							if (currentPalletList.get(0).getProductName().equals("BEV")) {
//								list1.get(p).setIsMaterialLoaded(1);
//								list1.get(p).setProductName(currentPalletList.get(0).getProductName());
////								System.out.println("list1 productvariant name::" + list1.get(p).getProductName());
//							} else if (currentPalletList.get(0).getProductName().equals("S230")) {
//								list1.get(p).setIsMaterialLoaded(1);
//								list1.get(p).setProductName(currentPalletList.get(0).getProductName());
////								System.out.println("list1 productvariant name::" + list1.get(p).getProductName());
//							}
//
//						}
//
//						else {
//
//							// System.out.println("in
//							// else::"+currentPalletList.get(0).getProductVariantCode());
//							list1.get(p).setIsMaterialLoaded(0);
//
//						}
//
//					}
//
//				}
//
//				obj.setPosition(list1);
//				list.add(obj);
//
//			}
//			// System.out.println(list);
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		// System.out.println(list);
//		return list;
////		return null;
//	}

//	
//	public List<MasterRackPositionDetails> findByAreaIdAndFloorId(int areaId, int floorId) {
//	    List<MasterRackPositionDetails> list = new ArrayList<>();
//	    
//	    try {
//	        // Fetch the position and rack lists only once
//	        List<MasterRackDetailsEntity> rackList = masterRackDetailsRepositoryInstance.findByAreaIdAndFloorIdOrderByRackId(areaId, floorId);
//	        List<MasterPositionDetailsEntity> positionList = masterPositionDetailsRepositoryInstance.findByAreaIdAndFloorIdOrderByPositionId(areaId, floorId);
//	        
//	        // Create a Map to group positions by rackId for faster lookup
//	        Map<Integer, List<MasterPositionDetailsEntity>> rackIdToPositions = positionList.stream()
//	            .collect(Collectors.groupingBy(MasterPositionDetailsEntity::getRackId));
//	        
//	        // Iterate over each rack
//	        for (MasterRackDetailsEntity rack : rackList) {
//	            MasterRackPositionDetails rackPositionDetails = new MasterRackPositionDetails();
//	            rackPositionDetails.setRackId(rack.getRackId());
//	            List<MasterPositionDetailsEntity> positionsForRack = rackIdToPositions.getOrDefault(rack.getRackId(), Collections.emptyList());
//	            
//	            // Sort the positions by positionId in descending order (like original logic)
//	            positionsForRack.sort(Comparator.comparing(MasterPositionDetailsEntity::getPositionId).reversed());
//	            
//	            // Process each position under the current rack
//	            for (MasterPositionDetailsEntity position : positionsForRack) {
//	                List<CurrentPalletStockDetailsEntity> currentPalletList = currentPalletStockDetailsRepository
//	                        .findByPositionId(position.getPositionId());
//
//	                // If there are any pallets for the position
//	                if (!currentPalletList.isEmpty()) {
//	                    CurrentPalletStockDetailsEntity pallet = currentPalletList.get(0);
//
//	                    // Check material conditions and update position accordingly
//	                    if (!"NA".equals(pallet.getProductVariantCode())) {
//	                        if ("BEV".equals(pallet.getProductName()) || "S230".equals(pallet.getProductName())) {
//	                            position.setIsMaterialLoaded(1);
//	                            position.setProductName(pallet.getProductName());
//	                        }
//	                    } else {
//	                        position.setIsMaterialLoaded(0);
//	                    }
//	                }
//	            }
//
//	            // Set the updated positions in the rack position details object
//	            rackPositionDetails.setPosition(positionsForRack);
//	            list.add(rackPositionDetails);
//	        }
//
//	    } catch (Exception ex) {
//	        ex.printStackTrace();
//	    }
//
//	    return list;
//	}

	public List<MasterRackPositionDetails> findByAreaIdAndFloorId(int areaId, int floorId) {
		List<MasterRackPositionDetails> list = new ArrayList<>();

		try {
			// Fetch the rack and position lists in one go
			List<MasterRackDetailsEntity> rackList = masterRackDetailsRepositoryInstance
					.findByAreaIdAndFloorIdOrderByRackId(areaId, floorId);
			List<MasterPositionDetailsEntity> positionList = masterPositionDetailsRepositoryInstance
					.findByAreaIdAndFloorIdOrderByPositionId(areaId, floorId);

			// Create a map to group positions by rackId
			Map<Integer, List<MasterPositionDetailsEntity>> rackPositionMap = positionList.stream()
					.collect(Collectors.groupingBy(MasterPositionDetailsEntity::getRackId));

			// Fetch all current pallet details at once and map them by positionId
			List<CurrentPalletStockDetailsEntity> currentPalletList = currentPalletStockDetailsRepository
					.findByPositionIdIn(positionList.stream().map(MasterPositionDetailsEntity::getPositionId)
							.collect(Collectors.toList()));

			Map<Integer, CurrentPalletStockDetailsEntity> palletStockMap = currentPalletList.stream()
					.collect(Collectors.toMap(CurrentPalletStockDetailsEntity::getPositionId, Function.identity()));

			// Loop through each rack to build the response
			for (MasterRackDetailsEntity rack : rackList) {
				MasterRackPositionDetails obj = new MasterRackPositionDetails();
				obj.setRackId(rack.getRackId());

				// Get the positions associated with this rack
				List<MasterPositionDetailsEntity> positionsForRack = rackPositionMap.getOrDefault(rack.getRackId(),
						new ArrayList<>());
				Collections.reverse(positionsForRack);
				// Process each position
				for (MasterPositionDetailsEntity position : positionsForRack) {
					CurrentPalletStockDetailsEntity pallet = palletStockMap.get(position.getPositionId());

					// Update position with material loaded status and product name
					if (pallet != null) {
						if (!"NA".equals(pallet.getProductVariantCode())) {
							position.setIsMaterialLoaded(1);
							position.setProductName(pallet.getProductName());
						} else {
							position.setIsMaterialLoaded(0);
						}
					} else {
						position.setIsMaterialLoaded(0); // No pallet info found, set unloaded
					}
				}

				// Set the positions for this rack in the result
				obj.setPosition(positionsForRack);
				list.add(obj);

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return list;
	}
//
//	public List<MasterRackPositionDetails> findByAreaIdAndFloorId(int areaId, int floorId) {
//		//System.out.println("in 123");
//		//System.out.println("Area Id: " + areaId + "Floor Id : " + floorId);
//		List<MasterRackPositionDetails> list = new ArrayList<MasterRackPositionDetails>();
//		List<MasterRackDetailsEntity> rackList = null;
//		List<MasterPositionDetailsEntity> positionList = null;
//		//List<CurrentPalletStockDetailsEntity>currentPalletList=null;
//		try {
//			positionList = masterPositionDetailsRepositoryInstance.findByAreaIdAndFloorIdOrderByPositionId(areaId, floorId);
//		//System.out.println("in positionList ::"+positionList);
//			rackList = masterRackDetailsRepositoryInstance.findByAreaIdAndFloorIdOrderByRackId(areaId, floorId);
//			//System.out.println("rackList ::"+rackList.size());
//			for (int i = 0; i < rackList.size(); i++) {
//				MasterRackPositionDetails obj = new MasterRackPositionDetails();
//				obj.setRackId(rackList.get(i).getRackId());
//				int rackId = rackList.get(i).getRackId();
//
//				List<MasterPositionDetailsEntity> list1 = null;
//				list1 = positionList.stream().filter(data -> data.getRackId() == (rackId))
//						.sorted(Comparator.comparing(MasterPositionDetailsEntity::getPositionId).reversed())
//						.collect(Collectors.toList());
//				//System.out.println("in list1 ::"+list1.size());
//				for(int p=0;p<list1.size();p++) {
//					//find by position id
//					List<CurrentPalletStockDetailsEntity>currentPalletList=null;
//					currentPalletList=currentPalletStockDetailsRepository.findByPositionId(list1.get(p).getPositionId());
//					
//					// check list size >0
//					
//					if(currentPalletList.size()>0) {
//						//check if materialcode!=na, if true
//						//System.out.println("in currentpallet::"+currentPalletList.get(0).getProductVariantCode());
//						if(!currentPalletList.get(0).getProductVariantCode().equals("NA")) {
//							
//							list1.get(p).setIsMaterialLoaded(1);
//							
//						}
//						
//						else {
//							
//							//System.out.println("in else::"+currentPalletList.get(0).getProductVariantCode());
//							list1.get(p).setIsMaterialLoaded(0);
//							
//						}
//						
//						
//					}
//					
//					
//				}
//				
//
//				obj.setPosition(list1);
//				list.add(obj);
//				
//
//			}
//			//System.out.println(list);
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		//System.out.println(list);
//		return list;
////		return null;
//	}

//	public List<MasterRackPositionDetails> findByAreaIdAndFloorId1(int areaId, int floorId) {
//		
//	}

	public List<MasterPositionDetailsEntity> findByAreaId(int areaId) {
		try {
			return masterPositionDetailsRepositoryInstance.findByAreaId(areaId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public ResponseEntity<MasterPositionDetailsEntity> updatIsManualDispatchInMasterPositionDetails(int positionId) {
		MasterPositionDetailsEntity masterPositionDetailsEntity1 = new MasterPositionDetailsEntity();
		try {
			masterPositionDetailsEntity1 = masterPositionDetailsRepositoryInstance.findByPositionId(positionId);

			if (masterPositionDetailsEntity1 != null) {
				masterPositionDetailsEntity1.setIsManualDispatch(1);
				return new ResponseEntity<MasterPositionDetailsEntity>(
						masterPositionDetailsRepositoryInstance.save(masterPositionDetailsEntity1), HttpStatus.OK);
			} else {
				return new ResponseEntity<MasterPositionDetailsEntity>(new MasterPositionDetailsEntity(),
						HttpStatus.NOT_FOUND);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

//	public MasterPositionDetailsEntity updateUnlockSelectedPositionIsActive(
//			MasterPositionDetailsEntity masterPositionDetailsEntity, int positionId, String reason) {
//
//		Date dNow = new Date();
//		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String date = ft.format(dNow);
//
//		MasterPositionDetailsEntity findByPositionId2 = masterPositionDetailsRepositoryInstance
//				.findByPositionId(positionId);
//		if (positionId % 2 == 0) {
//			MasterPositionDetailsEntity findByPositionId = masterPositionDetailsRepositoryInstance
//					.findByPositionId(positionId - 1);
//			findByPositionId.setCDateTime(date);
//			findByPositionId2.setCDateTime(date);
//			findByPositionId.setPositionIsActive(1);
//			findByPositionId2.setPositionIsActive(1);
//			masterPositionDetailsRepositoryInstance.save(findByPositionId);
//			masterPositionDetailsRepositoryInstance.save(findByPositionId2);
//
//			AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
//			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//			String name = authentication.getName();
//			System.out.println(" name :: " + name);
//			auditTrailDetailsEntity.setOperatorActions("Position unlocked by  " + name + " for position  "
//					+ findByPositionId.getPositionName() + "and" + findByPositionId2.getPositionName());
//			auditTrailDetailsEntity.setField("Position unlocked");
//			auditTrailDetailsEntity.setReason(reason);
//			auditTrailDetailsEntity.setUsername(name);
//			auditTrailDetailsEntity.setDatetimeC(date);
//			auditTrailDetailsRepository.save(auditTrailDetailsEntity);
//
//			LockUnlockDetailsEntity findByPositionName = lockUnlockDetailsRepository
//					.findTopByPositionNameOrderByIdDesc(findByPositionId2.getPositionName());
//
//			List<CurrentPalletStockDetailsEntity> findByPositionName3 = currentPalletStockDetailsRepository
//					.findByPositionName(findByPositionId2.getPositionName());
//
//			System.out.println("..." + !findByPositionName.getUnlock().equals("9999-12-31 00:00:00"));
//
//			if (findByPositionName != null && findByPositionName.getUnlock().equals("9999-12-31 00:00:00")) {
//				findByPositionName.setUnlock(date);
//				findByPositionName.setReason(auditTrailDetailsEntity.getReason());
//				findByPositionName.setUsername(name);
//				findByPositionName.setDescription("Position Unlocked for " + findByPositionName3.get(0).getPalletCode());
//				findByPositionName.setPositionName(findByPositionId2.getPositionName());
//				findByPositionName.setCurrentDate(date);
//				lockUnlockDetailsRepository.save(findByPositionName);
//			} else {
//				LockUnlockDetailsEntity lockUnlockDetailsEntity = new LockUnlockDetailsEntity();
//				lockUnlockDetailsEntity.setUnlock(date);
//				lockUnlockDetailsEntity.setCurrentDate(date);
//				lockUnlockDetailsEntity.setUsername(name);
//				lockUnlockDetailsEntity.setDescription("Position Unlocked for " + findByPositionName3.get(0).getPalletCode());
//				lockUnlockDetailsEntity.setReason(auditTrailDetailsEntity.getReason());
//				lockUnlockDetailsEntity.setPositionName(masterPositionDetailsEntity.getPositionName());
//				lockUnlockDetailsRepository.save(lockUnlockDetailsEntity);
//			}
//			LockUnlockDetailsEntity findByPositionName2 = lockUnlockDetailsRepository
//					.findTopByPositionNameOrderByIdDesc(findByPositionId.getPositionName());
//			List<CurrentPalletStockDetailsEntity> findByPositionName4 = currentPalletStockDetailsRepository
//					.findByPositionName(findByPositionId.getPositionName());
//			if (findByPositionName2 != null && findByPositionName2.getUnlock().equals("9999-12-31 00:00:00")) {
//				findByPositionName2.setUnlock(date);
//				findByPositionName2.setReason(auditTrailDetailsEntity.getReason());
//				findByPositionName2.setUsername(name);
//				findByPositionName2.setDescription("Position Unlocked for " + findByPositionName4.get(0).getPalletCode());
//				findByPositionName2.setPositionName(findByPositionId2.getPositionName());
//				findByPositionName2.setCurrentDate(date);
//				lockUnlockDetailsRepository.save(findByPositionName2);
//			}
////			else {
////				LockUnlockDetailsEntity lockUnlockDetailsEntity = new LockUnlockDetailsEntity();
////				lockUnlockDetailsEntity.setUnlock(date);
////				lockUnlockDetailsEntity.setCurrentDate(date);
////				lockUnlockDetailsEntity.setUsername(name);
////				lockUnlockDetailsEntity.setDescription(findByPositionName3.get(0).getPalletCode());
////				lockUnlockDetailsEntity.setReason(auditTrailDetailsEntity.getReason());
////				lockUnlockDetailsEntity.setPositionName(findByPositionId2.getPositionName());
////				lockUnlockDetailsRepository.save(lockUnlockDetailsEntity);
////			}
//		} else {
//			findByPositionId2.setPositionIsActive(1);
//			findByPositionId2.setCDateTime(date);
//			masterPositionDetailsRepositoryInstance.save(findByPositionId2);
//
//			AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
//			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//			String name = authentication.getName();
//			System.out.println(" name :: " + name);
//			auditTrailDetailsEntity.setOperatorActions(
//					"Position unlocked by  " + name + " for position  " + findByPositionId2.getPositionName());
//			auditTrailDetailsEntity.setField("Position unlocked");
//			auditTrailDetailsEntity.setReason(reason);
//			auditTrailDetailsEntity.setUsername(name);
//			auditTrailDetailsEntity.setDatetimeC(date);
//			auditTrailDetailsRepository.save(auditTrailDetailsEntity);
//
//			LockUnlockDetailsEntity findByPositionName = lockUnlockDetailsRepository
//					.findTopByPositionNameOrderByIdDesc(findByPositionId2.getPositionName());
//
//			List<CurrentPalletStockDetailsEntity> findByPositionName3 = currentPalletStockDetailsRepository
//					.findByPositionName(findByPositionId2.getPositionName());
//			if (findByPositionName != null && findByPositionName.getUnlock().equals("9999-12-31 00:00:00")) {
//				findByPositionName.setUnlock(date);
//				findByPositionName.setReason(auditTrailDetailsEntity.getReason());
//				findByPositionName.setUsername(name);
//				findByPositionName.setPositionName(findByPositionId2.getPositionName());
//				findByPositionName.setDescription("Position Unlocked for " + findByPositionName3.get(0).getPalletCode());
//				findByPositionName.setCurrentDate(date);
//				lockUnlockDetailsRepository.save(findByPositionName);
//			} else {
//				LockUnlockDetailsEntity lockUnlockDetailsEntity = new LockUnlockDetailsEntity();
//				lockUnlockDetailsEntity.setUnlock(date);
//				lockUnlockDetailsEntity.setCurrentDate(date);
//				lockUnlockDetailsEntity.setUsername(name);
//				lockUnlockDetailsEntity
//						.setDescription("Position Unlocked for " + findByPositionName3.get(0).getPalletCode());
//				lockUnlockDetailsEntity.setReason(auditTrailDetailsEntity.getReason());
//				lockUnlockDetailsEntity.setPositionName(findByPositionId2.getPositionName());
//				lockUnlockDetailsRepository.save(lockUnlockDetailsEntity);
//			}
//
//		}
//
//		return masterPositionDetailsEntity;
//	}

//	public ResponseEntity<Object> updateLockSelectedPositionIsActive(
//			MasterPositionDetailsEntity masterPositionDetailsEntity, int positionId) {
//		try {
//			Date dNow = new Date();
//			SimpleDateFormat ft = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
//			String date = ft.format(dNow);
//			masterPositionDetailsEntity.setCDateTime(date);
//
//			MasterPositionDetailsEntity masterPositionDetailsEntity2 = masterPositionDetailsRepositoryInstance
//					.findById(positionId).get();
//
//			if (positionId % 2 == 1) {
//				MasterPositionDetailsEntity findByPositionId = masterPositionDetailsRepositoryInstance
//						.findByPositionId(positionId+1);
//				int emptyPalletPosition = findByPositionId.getEmptyPalletPosition();
//				int isMaterialLoaded = findByPositionId.getIsMaterialLoaded();
//
//				if (emptyPalletPosition != 1 && isMaterialLoaded != 0) {
//					masterPositionDetailsEntity2.setPositionIsActive(0);
//					positionId = positionId + 1;
//					MasterPositionDetailsEntity masterPositionDetailsEntity3 = masterPositionDetailsRepositoryInstance
//							.findById(positionId).get();
//					masterPositionDetailsEntity3.setPositionIsActive(0);
//					masterPositionDetailsEntity3.setCDateTime(date);
//					masterPositionDetailsEntity2.setCDateTime(date);
//					masterPositionDetailsRepositoryInstance.save(masterPositionDetailsEntity2);
//					masterPositionDetailsRepositoryInstance.save(masterPositionDetailsEntity3);
//
//				} else {
//					return ResponseHandler.generateResponse("Unable to lock position", HttpStatus.ALREADY_REPORTED,
//							null);
//				}
//
//			} else {
//				masterPositionDetailsEntity2.setPositionIsActive(0);
//				masterPositionDetailsEntity2.setCDateTime(date);
//				masterPositionDetailsRepositoryInstance.save(masterPositionDetailsEntity2);
//				return ResponseHandler.generateResponse("Position locked successfully", HttpStatus.OK, null);
//
//			}
////			return masterPositionDetailsEntity;
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return ResponseHandler.generateResponse("Position locked successfully", HttpStatus.OK, null);
////		return masterPositionDetailsEntity;
//	}

	public MasterPositionDetailsEntity updateUnlockSelectedPositionIsActive(
			MasterPositionDetailsEntity masterPositionDetailsEntity, int positionId, String reason, String comment) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String name = authentication.getName();
			Date dNow = new Date();
			SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = ft.format(dNow); // Current date
			masterPositionDetailsEntity.setCDateTime(date); // Update the change date

			MasterPositionDetailsEntity masterPositionDetailsEntity2 = masterPositionDetailsRepositoryInstance
					.findById(positionId).orElseThrow(() -> new IllegalArgumentException("Position not found"));

			// If position ID is even, unlock the even position and the previous odd
			// position
			if (positionId % 2 == 0) {
				// Unlock the even position (set positionIsActive = 1)
				masterPositionDetailsEntity2.setPositionIsActive(1);
				masterPositionDetailsRepositoryInstance.save(masterPositionDetailsEntity2);

				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
				auditTrailDetailsEntity.setUsername(name);
				auditTrailDetailsEntity.setDatetimeC(date);
				auditTrailDetailsEntity.setReason(reason);
				auditTrailDetailsEntity.setOperatorActions("Position unlocked by  " + name + " for position  "
						+ masterPositionDetailsEntity2.getPositionName());
				auditTrailDetailsEntity.setField("Position unlocked");

				auditTrailDetailsRepository.save(auditTrailDetailsEntity);

				handleLockUnlockDetails(masterPositionDetailsEntity2, auditTrailDetailsEntity, "unlock", date, comment);

				// If the previous odd position is unlocked, insert the entry for the even
				// position
				int adjacentPositionId = positionId - 1;
				MasterPositionDetailsEntity masterPositionDetailsEntity3 = masterPositionDetailsRepositoryInstance
						.findById(adjacentPositionId)
						.orElseThrow(() -> new IllegalArgumentException("Previous position not found"));

				// If previous odd position is unlocked, insert entry for only the even position
				if (masterPositionDetailsEntity3.getPositionIsActive() == 0) {
					masterPositionDetailsEntity3.setPositionIsActive(1);
					masterPositionDetailsRepositoryInstance.save(masterPositionDetailsEntity3);
					// Create audit trail for unlocking the even position
//					AuditTrailDetailsEntity auditTrailDetailsEntity = createAuditTrailEntity(
//							masterPositionDetailsEntity2, null, "unlocked", reason, date);

					// Insert LockUnlockDetailsEntity entry for the even position

					AuditTrailDetailsEntity auditTrailDetailsEntity1 = new AuditTrailDetailsEntity();
					auditTrailDetailsEntity1.setDatetimeC(date);
					auditTrailDetailsEntity1.setUsername(name);
					auditTrailDetailsEntity1.setReason(reason);
					auditTrailDetailsEntity1.setOperatorActions("Position unlocked by  " + name + " for position  "
							+ masterPositionDetailsEntity3.getPositionName());
					auditTrailDetailsEntity1.setField("Position unlocked");
					auditTrailDetailsRepository.save(auditTrailDetailsEntity1);
					handleLockUnlockDetails(masterPositionDetailsEntity3, auditTrailDetailsEntity1, "unlock", date,
							comment);
				}

			} else {
				// Unlock only the odd position
				masterPositionDetailsEntity2.setPositionIsActive(1);
				masterPositionDetailsRepositoryInstance.save(masterPositionDetailsEntity2);

				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
				auditTrailDetailsEntity.setDatetimeC(date);
				auditTrailDetailsEntity.setReason(reason);
				auditTrailDetailsEntity.setUsername(name);
				auditTrailDetailsEntity.setOperatorActions("Position unlocked by  " + name + " for position  "
						+ masterPositionDetailsEntity2.getPositionName());
				auditTrailDetailsEntity.setField("Position unlocked");
				auditTrailDetailsRepository.save(auditTrailDetailsEntity);

				// Create audit trail for unlocking the odd position
//				AuditTrailDetailsEntity auditTrailDetailsEntity = createAuditTrailEntity(masterPositionDetailsEntity2,
//						null, "unlocked", reason, date);

				// Insert LockUnlockDetailsEntity entry for the odd position
				handleLockUnlockDetails(masterPositionDetailsEntity2, auditTrailDetailsEntity, "unlock", date, comment);
			}

			return masterPositionDetailsEntity;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return masterPositionDetailsEntity;
	}

	private void handleLockUnlockDetails(MasterPositionDetailsEntity masterPositionDetailsEntity,
			AuditTrailDetailsEntity auditTrailDetailsEntity, String action, String date, String comment) {

		// Get pallet code for description
		List<CurrentPalletStockDetailsEntity> palletStockDetails = currentPalletStockDetailsRepository
				.findByPositionName(masterPositionDetailsEntity.getPositionName());
		String palletCode = (palletStockDetails.isEmpty()) ? "Unknown" : palletStockDetails.get(0).getPalletCode();

		// Create a new LockUnlockDetailsEntity for each lock/unlock action
		LockUnlockDetailsEntity newLockUnlockDetailsEntity = new LockUnlockDetailsEntity();
		newLockUnlockDetailsEntity.setCurrentDate(date);
		newLockUnlockDetailsEntity.setUsername(auditTrailDetailsEntity.getUsername());
		newLockUnlockDetailsEntity.setDescription(
				action.equals("lock") ? "Position locked for " + palletCode : "Position unlocked for " + palletCode);
		newLockUnlockDetailsEntity.setReason(auditTrailDetailsEntity.getReason());
		newLockUnlockDetailsEntity.setPositionName(masterPositionDetailsEntity.getPositionName());
		newLockUnlockDetailsEntity.setComment(comment);

		// Set lock or unlock date based on the action
//		if (action.equals("lock")) {
//			newLockUnlockDetailsEntity.setLock(date); // Set lock date to current date
//			newLockUnlockDetailsEntity.setUnlock("9999-12-31 00:00:00"); // Default unlocked state
//		} else {
//			newLockUnlockDetailsEntity.setUnlock(date); // Set unlock date to current date
//			newLockUnlockDetailsEntity.setLock("9999-12-31 00:00:00"); // Default locked state
//		}

		// Save the new lock/unlock details
		lockUnlockDetailsRepository.save(newLockUnlockDetailsEntity);
	}

//	public MasterPositionDetailsEntity updateLockSelectedPositionIsActive(
//			MasterPositionDetailsEntity masterPositionDetailsEntity, int positionId, String reason) {
//		try {
//			Date dNow = new Date();
//			SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String date = ft.format(dNow);
//			masterPositionDetailsEntity.setCDateTime(date);
//
//			MasterPositionDetailsEntity masterPositionDetailsEntity2 = masterPositionDetailsRepositoryInstance
//					.findById(positionId).get();
//
//			if (positionId % 2 == 1) {
//				masterPositionDetailsEntity2.setPositionIsActive(0);
//				positionId = positionId + 1;
//				MasterPositionDetailsEntity masterPositionDetailsEntity3 = masterPositionDetailsRepositoryInstance
//						.findById(positionId).get();
//				masterPositionDetailsEntity3.setPositionIsActive(0);
//				masterPositionDetailsRepositoryInstance.save(masterPositionDetailsEntity2);
//				masterPositionDetailsRepositoryInstance.save(masterPositionDetailsEntity3);
//
//				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
//				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//				String name = authentication.getName();
//				System.out.println(" name :: " + name);
//				auditTrailDetailsEntity.setOperatorActions("Position locked by  " + name + " for position  "
//						+ masterPositionDetailsEntity2.getPositionName() + "and"
//						+ masterPositionDetailsEntity3.getPositionName());
//				auditTrailDetailsEntity.setField("Position locked");
//
//				auditTrailDetailsEntity.setReason(reason);
//
//				auditTrailDetailsEntity.setUsername(name);
//				auditTrailDetailsEntity.setDatetimeC(date);
//				auditTrailDetailsRepository.save(auditTrailDetailsEntity);
//
//				LockUnlockDetailsEntity findByPositionName = lockUnlockDetailsRepository
//						.findTopByPositionNameOrderByIdDesc(masterPositionDetailsEntity2.getPositionName());
//
//				List<CurrentPalletStockDetailsEntity> findByPositionName3 = currentPalletStockDetailsRepository
//						.findByPositionName(masterPositionDetailsEntity2.getPositionName());
//
//				if (findByPositionName != null && findByPositionName.getLock().equals("9999-12-31 00:00:00")) {
//					findByPositionName.setLock(date);
//					findByPositionName.setReason(auditTrailDetailsEntity.getReason());
//					findByPositionName.setUsername(name);
//					findByPositionName.setDescription("Position locked for " + findByPositionName3.get(0).getPalletCode());
//					findByPositionName.setPositionName(masterPositionDetailsEntity2.getPositionName());
//					findByPositionName.setCurrentDate(date);
//					findByPositionName.setUnlock("9999-12-31 00:00:00");
//					lockUnlockDetailsRepository.save(findByPositionName);
//				} else {
//					LockUnlockDetailsEntity lockUnlockDetailsEntity = new LockUnlockDetailsEntity();
//					lockUnlockDetailsEntity.setLock(date);
//					lockUnlockDetailsEntity.setCurrentDate(date);
//					lockUnlockDetailsEntity.setUsername(name);
//					lockUnlockDetailsEntity.setDescription("Position locked for " + findByPositionName3.get(0).getPalletCode());
//					lockUnlockDetailsEntity.setReason(auditTrailDetailsEntity.getReason());
//					lockUnlockDetailsEntity.setPositionName(masterPositionDetailsEntity2.getPositionName());
//					lockUnlockDetailsEntity.setUnlock("9999-12-31 00:00:00");
//					lockUnlockDetailsRepository.save(lockUnlockDetailsEntity);
//				}
//				LockUnlockDetailsEntity findByPositionName2 = lockUnlockDetailsRepository
//						.findTopByPositionNameOrderByIdDesc(masterPositionDetailsEntity3.getPositionName());
//				
//
//				List<CurrentPalletStockDetailsEntity> findByPositionName4 = currentPalletStockDetailsRepository
//						.findByPositionName(masterPositionDetailsEntity3.getPositionName());
//				
//				if (findByPositionName2 != null
//						&& findByPositionName2.getUnlock().equalsIgnoreCase("9999-12-31 00:00:00")) {
//					// findByPositionName2.setLock(date);
//					findByPositionName2.setReason(auditTrailDetailsEntity.getReason());
//					findByPositionName2.setUsername(name);
//					findByPositionName2.setDescription("Position locked for " + findByPositionName4.get(0).getPalletCode());
//					findByPositionName2.setPositionName(masterPositionDetailsEntity3.getPositionName());
//					findByPositionName2.setCurrentDate(date);
//					findByPositionName2.setUnlock("9999-12-31 00:00:00");
//					lockUnlockDetailsRepository.save(findByPositionName2);
//				} else {
//					LockUnlockDetailsEntity lockUnlockDetailsEntity = new LockUnlockDetailsEntity();
//					lockUnlockDetailsEntity.setLock(date);
//					lockUnlockDetailsEntity.setCurrentDate(date);
//					lockUnlockDetailsEntity.setUsername(name);
//					lockUnlockDetailsEntity.setDescription("Position locked for " + findByPositionName4.get(0).getPalletCode());
//					lockUnlockDetailsEntity.setReason(auditTrailDetailsEntity.getReason());
//					lockUnlockDetailsEntity.setPositionName(masterPositionDetailsEntity3.getPositionName());
//					lockUnlockDetailsEntity.setUnlock("9999-12-31 00:00:00");
//					lockUnlockDetailsRepository.save(lockUnlockDetailsEntity);
//				}
//
//			} else {
//				masterPositionDetailsEntity2.setPositionIsActive(0);
//				masterPositionDetailsRepositoryInstance.save(masterPositionDetailsEntity2);
//
//				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
//				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//				String name = authentication.getName();
//				System.out.println(" name :: " + name);
//				auditTrailDetailsEntity.setOperatorActions("Position locked by  " + name + " for position  "
//						+ masterPositionDetailsEntity2.getPositionName());
//				auditTrailDetailsEntity.setField("Position locked");
//
//				auditTrailDetailsEntity.setReason(reason);
//
//				auditTrailDetailsEntity.setUsername(name);
//				auditTrailDetailsEntity.setDatetimeC(date);
//				auditTrailDetailsRepository.save(auditTrailDetailsEntity);
//
//				LockUnlockDetailsEntity findByPositionName = lockUnlockDetailsRepository
//						.findTopByPositionNameOrderByIdDesc(masterPositionDetailsEntity2.getPositionName());
//
//				List<CurrentPalletStockDetailsEntity> findByPositionName3 = currentPalletStockDetailsRepository
//						.findByPositionName(masterPositionDetailsEntity2.getPositionName());
//				if (findByPositionName != null
//						&& findByPositionName.getLock().equalsIgnoreCase("9999-12-31 00:00:00")) {
//					findByPositionName.setLock(date);
//					findByPositionName.setReason(auditTrailDetailsEntity.getReason());
//					findByPositionName.setUsername(name);
//					findByPositionName.setPositionName(masterPositionDetailsEntity2.getPositionName());
//					findByPositionName.setDescription("Position locked for " + findByPositionName3.get(0).getPalletCode());
//					findByPositionName.setCurrentDate(date);
//					lockUnlockDetailsRepository.save(findByPositionName);
//				} else {
//					LockUnlockDetailsEntity lockUnlockDetailsEntity = new LockUnlockDetailsEntity();
//					lockUnlockDetailsEntity.setLock(date);
//					lockUnlockDetailsEntity.setCurrentDate(date);
//					lockUnlockDetailsEntity.setUsername(name);
//					lockUnlockDetailsEntity
//							.setDescription("Position locked for " + findByPositionName3.get(0).getPalletCode());
//					lockUnlockDetailsEntity.setReason(auditTrailDetailsEntity.getReason());
//					lockUnlockDetailsEntity.setPositionName(masterPositionDetailsEntity2.getPositionName());
//					lockUnlockDetailsEntity.setUnlock("9999-12-31 00:00:00");
//					lockUnlockDetailsRepository.save(lockUnlockDetailsEntity);
//				}
//
//			}
//			return masterPositionDetailsEntity;
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return masterPositionDetailsEntity;
//	}

	public MasterPositionDetailsEntity updateLockSelectedPositionIsActive(
			MasterPositionDetailsEntity masterPositionDetailsEntity, int positionId, String reason, String comment) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String name = authentication.getName();
			Date dNow = new Date();
			SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = ft.format(dNow); // Current date
			masterPositionDetailsEntity.setCDateTime(date); // Update the change date

			MasterPositionDetailsEntity masterPositionDetailsEntity2 = masterPositionDetailsRepositoryInstance
					.findById(positionId).orElseThrow(() -> new IllegalArgumentException("Position not found"));

			// If position ID is odd, lock the odd position and the next even position
			if (positionId % 2 == 1) {
				// Lock the odd position (set positionIsActive = 0)
				masterPositionDetailsEntity2.setPositionIsActive(0);
				masterPositionDetailsRepositoryInstance.save(masterPositionDetailsEntity2);

				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
				auditTrailDetailsEntity.setDatetimeC(date);
				auditTrailDetailsEntity.setUsername(name);
				auditTrailDetailsEntity.setReason(reason);
				auditTrailDetailsEntity.setOperatorActions("Position locked by  " + name + " for position  "
						+ masterPositionDetailsEntity2.getPositionName());
				auditTrailDetailsEntity.setField("Position locked");
				auditTrailDetailsRepository.save(auditTrailDetailsEntity);

				handleLockUnlockDetails(masterPositionDetailsEntity2, auditTrailDetailsEntity, "lock", date, comment);

				// Lock the next even position
				int adjacentPositionId = positionId + 1;
				MasterPositionDetailsEntity masterPositionDetailsEntity3 = masterPositionDetailsRepositoryInstance
						.findById(adjacentPositionId)
						.orElseThrow(() -> new IllegalArgumentException("Next position not found"));

				if (masterPositionDetailsEntity3.getPositionIsActive() == 1) {
					masterPositionDetailsEntity3.setPositionIsActive(0); // Lock the next even position
					masterPositionDetailsRepositoryInstance.save(masterPositionDetailsEntity3);
					AuditTrailDetailsEntity auditTrailDetailsEntity1 = new AuditTrailDetailsEntity();
					auditTrailDetailsEntity1.setDatetimeC(date);
					auditTrailDetailsEntity1.setUsername(name);
					auditTrailDetailsEntity1.setReason(reason);
					auditTrailDetailsEntity1.setOperatorActions("Position locked by  " + name + " for position  "
							+ masterPositionDetailsEntity3.getPositionName());
					auditTrailDetailsEntity1.setField("Position locked");
					auditTrailDetailsRepository.save(auditTrailDetailsEntity1);
					handleLockUnlockDetails(masterPositionDetailsEntity3, auditTrailDetailsEntity1, "lock", date,
							comment);
				}

				// Create audit trail for both positions
//				AuditTrailDetailsEntity auditTrailDetailsEntity = createAuditTrailEntity(masterPositionDetailsEntity2,
//						masterPositionDetailsEntity3, "locked", reason, date);

				// Insert LockUnlockDetailsEntity entry for both positions

			} else {
				// Lock only the even position
				masterPositionDetailsEntity2.setPositionIsActive(0);
				masterPositionDetailsRepositoryInstance.save(masterPositionDetailsEntity2);

				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
				auditTrailDetailsEntity.setDatetimeC(date);
				auditTrailDetailsEntity.setReason(reason);
				auditTrailDetailsEntity.setUsername(name);
				auditTrailDetailsEntity.setOperatorActions("Position locked by  " + name + " for position  "
						+ masterPositionDetailsEntity2.getPositionName());
				auditTrailDetailsEntity.setField("Position locked");
				auditTrailDetailsRepository.save(auditTrailDetailsEntity);

				handleLockUnlockDetails(masterPositionDetailsEntity2, auditTrailDetailsEntity, "lock", date, comment);

				// Create audit trail for locking the even position
//				AuditTrailDetailsEntity auditTrailDetailsEntity = createAuditTrailEntity(masterPositionDetailsEntity2,
//						null, "locked", reason, date);

				// Insert LockUnlockDetailsEntity entry for the even position

				// If the previous odd position is unlocked, insert the entry for the previous
				// odd position
//				int adjacentPositionId = positionId - 1;
//				MasterPositionDetailsEntity masterPositionDetailsEntity3 = masterPositionDetailsRepositoryInstance
//						.findById(adjacentPositionId)
//						.orElseThrow(() -> new IllegalArgumentException("Previous position not found"));
//
//				if (masterPositionDetailsEntity3.getPositionIsActive() == 1) {
//					// Insert LockUnlockDetailsEntity entry for the previous odd position
//					AuditTrailDetailsEntity auditTrailDetailsEntityForPrev = createAuditTrailEntity(
//							masterPositionDetailsEntity3, null, "locked", reason, date);
//					handleLockUnlockDetails(masterPositionDetailsEntity3, auditTrailDetailsEntityForPrev, "lock", date);
//				}
			}

			return masterPositionDetailsEntity;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return masterPositionDetailsEntity;
	}

	public MasterPositionDetailsEntity UpdatePositionIsEmpty(MasterPositionDetailsEntity masterPositionDetailsEntity,
			int positionId) {

		try {
			masterPositionDetailsRepositoryInstance.findById(positionId).ifPresent(positionData -> {
				positionData.setPositionIsAllocated(0);
				positionData.setEmptyPalletPosition(1);
				masterPositionDetailsRepositoryInstance.save(positionData);
			});
			System.out.println("positionId");
			return masterPositionDetailsEntity;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return masterPositionDetailsEntity;

	}

	public void updatePositionIsAllocated(MasterPositionDetailsEntity masterPositionDetailsEntity, int positionId) {
		masterPositionDetailsRepositoryInstance.findById(positionId).ifPresent(positionDetails -> {
			positionDetails.setPositionIsAllocated(0);
			positionDetails.setEmptyPalletPosition(1);
			positionDetails.setIsManualDispatch(0);
			masterPositionDetailsRepositoryInstance.save(positionDetails);

		});
//		Date dNow = new Date();
//		SimpleDateFormat sdateformat = new SimpleDateFormat("dd MMM yyyy" + " " + "HH:mm:ss");
//		String date = sdateformat.format(dNow);
//		
//		CurrentPalletStockDetailsEntity currentStockDetailsEntityInsrt = new CurrentPalletStockDetailsEntity();
//		
//		currentStockDetailsEntityInsrt = currentPalletStockDetailsRepository.getByPositionId(positionId);
//		if (currentStockDetailsEntityInsrt != null) {
//			currentStockDetailsEntityInsrt.setPalletCode("NA");
//			currentStockDetailsEntityInsrt.setProductName("NA");
//			currentStockDetailsEntityInsrt.setPalletInformationId(0);
//			currentStockDetailsEntityInsrt.setProductVariantCode("NA");
//			currentStockDetailsEntityInsrt.setProductId(0);
//			currentStockDetailsEntityInsrt.setProductVariantId(0);
//			currentStockDetailsEntityInsrt.setProductVariantName("NA");
//			currentStockDetailsEntityInsrt.setPalletStatusId(3);
//			currentStockDetailsEntityInsrt.setSerialNumber(0);
//			currentStockDetailsEntityInsrt.setPalletStatusname("NA");
//			currentStockDetailsEntityInsrt.setQualityStatus("NA");
//			currentStockDetailsEntityInsrt.setAgeingDays(0);
//			currentStockDetailsEntityInsrt.setBatchNumber("NA");
//			currentStockDetailsEntityInsrt.setModelNumber("NA");
//			currentStockDetailsEntityInsrt.setLocation("NA");
//
//			currentStockDetailsEntityInsrt.setIsInfeedMissionGenerated(0);
//			currentStockDetailsEntityInsrt.setIsOutfeedMissionGenerated(0);
//			currentStockDetailsEntityInsrt.setQuantity(0);
//
//			currentStockDetailsEntityInsrt.setProductId(0);
//
//
//			currentStockDetailsEntityInsrt.setLoadDatetime(date);
//			currentPalletStockDetailsRepository.save(currentStockDetailsEntityInsrt);
//		}

		Date dNow = new Date();
		SimpleDateFormat sdateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdateformat.format(dNow);

		MasterPositionDetailsEntity masterPositionDetailsEntity2 = masterPositionDetailsRepositoryInstance
				.findById(positionId).get();

		AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();
		System.out.println(" name :: " + name);
		auditTrailDetailsEntity.setOperatorActions("Position free allocated by  " + name + " for position  "
				+ masterPositionDetailsEntity2.getPositionName());
		auditTrailDetailsEntity.setField("Free allocation");

		auditTrailDetailsEntity.setReason("Free allocation");

		auditTrailDetailsEntity.setUsername(name);
		auditTrailDetailsEntity.setDatetimeC(date);
		auditTrailDetailsRepository.save(auditTrailDetailsEntity);

	}

	public List<MasterPositionDetailsEntity> findByPositionName(String positionName) {
		// TODO Auto-generated method stub
		List<MasterPositionDetailsEntity> data = masterPositionDetailsRepositoryInstance
				.findByPositionName(positionName);
		;
		return data;
//		return null;
	}

//	@Override
//	public ResponseEntity<Object> findEmptyPositionAlarm() {
//		int findByEmptyPalletPosition = masterPositionDetailsRepositoryInstance.findByEmptyPalletPosition();
//		if (findByEmptyPalletPosition == 0) {
//			return ResponseHandler.generateResponse("ASRS capacity has reached its maximum threshold. Please initiate an outfeed operation of first depth position of any available rack to clear space. ", HttpStatus.OK,
//					findByEmptyPalletPosition);
//		} else {
//			return ResponseHandler.generateResponse("ASRS is empty ", HttpStatus.ALREADY_REPORTED,
//					findByEmptyPalletPosition);
//		}
//
//	}

	@Override
	public ResponseEntity<Object> findEmptyPositionAlarm() {
		List<MasterPositionDetailsEntity> emptyPalletPositions = masterPositionDetailsRepositoryInstance
				.findByEmptyPalletPosition();

		if (emptyPalletPositions.isEmpty()) {
			return ResponseHandler.generateResponse(
					"ASRS capacity has reached its maximum threshold. Please initiate an outfeed operation of first depth position of any available rack to clear space.",
					HttpStatus.OK, null);
		} else {
			return ResponseHandler.generateResponse("ASRS is empty", HttpStatus.ALREADY_REPORTED,
					emptyPalletPositions.size());
		}
	}

	@Override
	public ResponseEntity<Object> getAlarmAudio()
			throws UnsupportedAudioFileException, IOException, LineUnavailableException, JavaLayerException {

		List<MasterPositionDetailsEntity> findByIsAlarmRack = masterPositionDetailsRepositoryInstance
				.findByIsAlarmRack(1);

		if (!findByIsAlarmRack.isEmpty()) {
			for (int i = 0; i < findByIsAlarmRack.size(); i++) {
				Date dNow = new Date();
				SimpleDateFormat sdateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date = sdateformat.format(dNow);

				List<MasterPositionDetailsEntity> masterPositionDetailsEntity2 = masterPositionDetailsRepositoryInstance
						.findByPositionName(findByIsAlarmRack.get(i).getPositionName());

				AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				String name = authentication.getName();
				System.out.println(" name :: " + name);
				auditTrailDetailsEntity.setOperatorActions("Yokogawa Alarm generated for position  :  "
						+ masterPositionDetailsEntity2.get(0).getPositionName());
				auditTrailDetailsEntity.setField("Temprature Alarm");

				auditTrailDetailsEntity.setReason("Temprature Alarm");

				auditTrailDetailsEntity.setUsername(name);
				auditTrailDetailsEntity.setDatetimeC(date);
				auditTrailDetailsRepository.save(auditTrailDetailsEntity);
			}

		}

		List<String> list = new ArrayList<>();
		try {
			for (MasterPositionDetailsEntity masterPositionDetailsEntity : findByIsAlarmRack) {
				list.add(masterPositionDetailsEntity.getPositionName());
			}
			if (!findByIsAlarmRack.isEmpty()) {
				String filePath = "D://Electric//alarm.mp3";
//			 String filePath = "E://softwares//alarm.mp3";
//			String filePath = "C://Users//shubhangij//Documents//GitHub//Mahindra_Battery_UI//src//assets//audio//alarm.mp3";
				FileInputStream in = new FileInputStream(filePath);
				AdvancedPlayer player = new AdvancedPlayer(in);
				player.play();
				AudioInputStream aui = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
				Clip clip = AudioSystem.getClip();
				clip.open(aui);
				clip.loop(Clip.LOOP_CONTINUOUSLY);
				clip.start();
				if (findByIsAlarmRack.isEmpty()) {
					clip.stop();
				}
				// clip.loop(10);
				// clip.stop();
				clip.close();

				System.out.println("alarm generated");

			} else {
				return ResponseHandler.generateResponse("Alarm generated at positions " + list,
						HttpStatus.ALREADY_REPORTED, list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseHandler.generateResponse("Alarm generated at positions " + list, HttpStatus.OK, null);
		}
		return ResponseHandler.generateResponse("Alarm generated at positions " + list, HttpStatus.OK, list);
	}

	public List<MasterPositionDetailsEntity> getAlarmData() {

		return masterPositionDetailsRepositoryInstance.findByIsAlarmRack(1); // Fetch positions with active alarms
	}

	public ResponseEntity<Object> getMismatchCell() {
		List<String> mismatchCellNames = masterPositionDetailsRepositoryInstance.findByPositionIsAllocated(2).stream()
				.map(MasterPositionDetailsEntity::getPositionName).collect(Collectors.toList());

		if (mismatchCellNames.isEmpty()) {
			return ResponseHandler.generateResponse("No mismatched cells found.", HttpStatus.ALREADY_REPORTED, null);
		} else {
			return ResponseHandler.generateResponse("Mismatch detected at the following positions :", HttpStatus.OK,
					mismatchCellNames);
		}
	}
	
	
	public ResponseEntity<Object> findDuplicatePalletCode() {

		List<DuplicatePalletCodeEntity> list = duplicatePalletCoderepository.findByDuplicatePalletCodeUpdateValue(1);

		
		if (list.isEmpty()) {
			return ResponseEntity.noContent().build(); 
		}

		String areaName = list.get(0).getDuplicatePalletCodeAreaName();
		String message;

		
		if ("DOCK".equalsIgnoreCase(areaName)) {
			message = "Duplicate pallet code present at " + areaName;
		} else {
			message = "Duplicate pallet code present at " + areaName + " pickup position";
		}

		return ResponseHandler.generateResponse(message, HttpStatus.OK, null);
	}

}
