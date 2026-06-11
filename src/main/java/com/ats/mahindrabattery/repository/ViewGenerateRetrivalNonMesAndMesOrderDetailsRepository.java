package com.ats.mahindrabattery.repository;
 
import java.util.List;
 
import javax.transaction.Transactional;
 
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
 
import com.ats.mahindrabattery.entity.GenerateManualRetrievalOrderEntity;

import com.ats.mahindrabattery.entity.ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity;
 
 
@Transactional

public interface ViewGenerateRetrivalNonMesAndMesOrderDetailsRepository extends JpaRepository<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity, Integer> {
 
	List<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity> findBycreatedDatetimeBetweenAndIsOrderDeleted(String string,

			String string2, int i);
 
	@Query("SELECT e FROM ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity e WHERE e.createdDatetime NOT BETWEEN :startDatetime AND :endDatetime AND e.dispatchStatus IN :dispatchStatuses AND e.isOrderDeleted = :isOrderDeleted")

	List<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity> findOrdersNotCreatedOnCurrentDateWithDispatchStatus(

	        @Param("startDatetime") String startDatetime,

	        @Param("endDatetime") String endDatetime,

	        @Param("dispatchStatuses") List<String> dispatchStatuses,

	        @Param("isOrderDeleted") int isOrderDeleted);
 
	List<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity> findBycreatedDatetimeBetweenAndDispatchStatus(String startDateTime, String endDateTime, String dispatchOrderNumber);
 
	
 
}
 