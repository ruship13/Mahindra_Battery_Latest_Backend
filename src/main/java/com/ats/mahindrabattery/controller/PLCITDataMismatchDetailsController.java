package com.ats.mahindrabattery.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ats.mahindrabattery.entity.CurrentPalletStockDetailsEntity;
import com.ats.mahindrabattery.entity.InfeedMissionRuntimeDetailsEntity;
import com.ats.mahindrabattery.entity.MasterPositionDetailsEntity;
import com.ats.mahindrabattery.entity.PLCITDataMismatchDetailsEntity;
import com.ats.mahindrabattery.service.PLCITDataMismatchDetailsService;

@RestController
@CrossOrigin
@RequestMapping("/plcItDataMismatch")
public class PLCITDataMismatchDetailsController {
	
	@Autowired
	private PLCITDataMismatchDetailsService plcitDataMismatchDetailsService;
	
	
	@PostMapping("/addPlcItData")
	public ResponseEntity<Object> addPlcItData(@RequestBody PLCITDataMismatchDetailsEntity plcitDataMismatchDetailsEntity){
		return plcitDataMismatchDetailsService.addPlcItData(plcitDataMismatchDetailsEntity);
		
	}
	
	
	@GetMapping("/findByIsDataUpdated")
	public List<PLCITDataMismatchDetailsEntity> findByIsDataUpdated(){
		return plcitDataMismatchDetailsService.findByIsDataUpdated();
	}

	
	
	
	@PostMapping("/addOrUpdateMasterPalletInformationfromDataMismatch")
	public ResponseEntity<Object> addOrUpdateMasterPalletInformation(
			@RequestBody CurrentPalletStockDetailsEntity currentPalletStockDetailsEntity) {
		return plcitDataMismatchDetailsService
				.addOrUpdateMasterPalletInformation(currentPalletStockDetailsEntity);
	}
	
	
	
	
	@GetMapping("/fetchDataMismatchDetailsByAllFilters/{plcItDataMismatchStartCdatetime}/{plcItDataMismatchEndCdatetime}")
	public List<PLCITDataMismatchDetailsEntity> fetchDataMismatchDetailsByAllFilters(
	      
	        @PathVariable String plcItDataMismatchStartCdatetime, 
	        @PathVariable String plcItDataMismatchEndCdatetime) {

	    List<PLCITDataMismatchDetailsEntity> fetchDataMismatchDetailsByAllFilters = plcitDataMismatchDetailsService
	            .findByAllFilters(plcItDataMismatchStartCdatetime, plcItDataMismatchEndCdatetime);
	    return fetchDataMismatchDetailsByAllFilters;
	}
}
