package com.ats.mahindrabattery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ats.mahindrabattery.entity.LockUnlockDetailsEntity;

public interface LockUnlockDetailsRepository extends JpaRepository<LockUnlockDetailsEntity, Integer>{

	public List<LockUnlockDetailsEntity> findByCurrentDateBetween(String string, String string2);

	public LockUnlockDetailsEntity findTopByPositionNameOrderByIdDesc(String positionName);

//	List<LockUnlockDetailsEntity> findTopByPositionNameOrderByIdDesc(String positionName);
	@Query(value = "SELECT TOP 1 * FROM ats_wms_lock_unlock_history_details " +
       "WHERE POSITION_NAME = :positionName " +
       "AND DESCRIPTION LIKE '%Position Locked%' " +
       "ORDER BY CDATETIME DESC",
       nativeQuery = true)
LockUnlockDetailsEntity findLatestLock(@Param("positionName") String positionName);


}
