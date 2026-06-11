package com.ats.mahindrabattery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ats.mahindrabattery.entity.InfeedMissionRuntimeDetailsEntity;
import com.ats.mahindrabattery.entity.MasterPositionDetailsEntity;
import com.ats.mahindrabattery.entity.PLCITDataMismatchDetailsEntity;

public interface PlcItDataMismatchDetailsRepository extends JpaRepository<PLCITDataMismatchDetailsEntity, Integer>{

	List<PLCITDataMismatchDetailsEntity> findByIsDataUpdated(int i);

	PLCITDataMismatchDetailsEntity findByPositionId(Integer positionId);

	@Query(value = "select * FROM ats_wms_plc_it_data_mismatch_details where CDATETIME BETWEEN :startDate AND :endDate", nativeQuery = true)
	public List<PLCITDataMismatchDetailsEntity> findPlcItDataMismatchDetailsBetweenDates(String startDate,
			String endDate);

}
