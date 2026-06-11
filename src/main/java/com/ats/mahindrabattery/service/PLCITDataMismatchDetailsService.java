package com.ats.mahindrabattery.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ats.mahindrabattery.entity.CurrentPalletStockDetailsEntity;
import com.ats.mahindrabattery.entity.PLCITDataMismatchDetailsEntity;

@Service
public interface PLCITDataMismatchDetailsService {

	public ResponseEntity<Object> addPlcItData(PLCITDataMismatchDetailsEntity plcitDataMismatchDetailsEntity);

	public List<PLCITDataMismatchDetailsEntity> findByIsDataUpdated();

	public ResponseEntity<Object> addOrUpdateMasterPalletInformation(
			CurrentPalletStockDetailsEntity currentPalletStockDetailsEntity);

	public List<PLCITDataMismatchDetailsEntity> findByAllFilters(

			String plcItDataMismatchStartCdatetime, String plcItDataMismatchEndCdatetime);

}
