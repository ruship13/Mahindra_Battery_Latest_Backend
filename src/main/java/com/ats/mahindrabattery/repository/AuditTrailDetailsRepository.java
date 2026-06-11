package com.ats.mahindrabattery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ats.mahindrabattery.entity.AuditTrailDetailsEntity;



public interface AuditTrailDetailsRepository extends JpaRepository<AuditTrailDetailsEntity, Integer>{
	
	@Query(value = "select * from ats_wms_audit_trail_report where datetimeC BETWEEN :startDate AND :endDate",nativeQuery = true)
	public List<AuditTrailDetailsEntity> findAuditTrailDetailsBetweenDates(String startDate, String endDate);
	
	AuditTrailDetailsEntity save(AuditTrailDetailsEntity auditTrailDetailsEntity);
	public List<AuditTrailDetailsEntity> findByDatetimeCBetween(String string, String string2);

	

	
	@Query(value = "select * from ats_wms_audit_trail_report where cast(CDateTime as date) =cast(:startDate as date) ",nativeQuery = true)
	public List<AuditTrailDetailsEntity> findByDatetimeCOrderByAuditIdDesc(String startDate);
	
	@Query(value = "SELECT TOP 1 * FROM ats_wms_audit_trail_report " +
		       "WHERE OPERATORS_ACTION LIKE '%' + :positionName + '%' " +
		       "AND OPERATORS_ACTION LIKE '%Position Locked%' " +
		       "ORDER BY CDATETIME DESC",
		       nativeQuery = true)
		AuditTrailDetailsEntity findLatestLock(@Param("positionName") String positionName);
}
