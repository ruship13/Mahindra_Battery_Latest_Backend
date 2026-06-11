package com.ats.mahindrabattery.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ats.mahindrabattery.entity.InfeedMissionRuntimeDetailsEntity;
import com.ats.mahindrabattery.entity.OutfeedMissionRuntimeDetailsEntity;

@Transactional
public interface OutfeedMissionruntimeDetailsRepository
		extends JpaRepository<OutfeedMissionRuntimeDetailsEntity, Integer> {

	public List<OutfeedMissionRuntimeDetailsEntity> findByCreatedDatetime(String createdDatetime);

	@Query(value = "select * FROM ats_wms_outfeed_mission_runtime_details where OUTFEED_MISSION_END_DATETIME BETWEEN :startDate AND :endDate", nativeQuery = true)
	public List<OutfeedMissionRuntimeDetailsEntity> findOutfeedMissionRuntimeDetailsBetweenDates(String startDate,
			String endDate);
	
	
	@Query(value = "select * FROM ats_wms_outfeed_mission_runtime_details where OUTFEED_MISSION_END_DATETIME BETWEEN :startDate AND :endDate and OUTFEED_MISSION_STATUS =:completedStatus", nativeQuery = true)
	public List<OutfeedMissionRuntimeDetailsEntity> findOutfeedMissionRuntimeDetailsBetweenDates(String startDate,
			String endDate, String completedStatus);

	@Query( "select u from OutfeedMissionRuntimeDetailsEntity u where u.outfeedMissionStatus =:completedStatus AND u.outfeedMissionEndDateTime BETWEEN :startDate AND :endDate")
	public List<OutfeedMissionRuntimeDetailsEntity> getAllOutfeedMissionRuntimeDetailsStatusAndBetweenDates(String completedStatus,
			String startDate, String endDate);
	
	
	
	@Modifying
	@Query("SELECT u FROM OutfeedMissionRuntimeDetailsEntity u WHERE u.outfeedMissionStatus = :outfeedMissionStatus1  OR u.outfeedMissionStatus= :outfeedMissionStatus2")
	List<OutfeedMissionRuntimeDetailsEntity> findByOutfeedMissionStatus(String outfeedMissionStatus1,
			String outfeedMissionStatus2);

	public List<OutfeedMissionRuntimeDetailsEntity> findByOutfeedMissionEndDateTimeBetweenAndOutfeedMissionIsDeleted(
			String startDate, String endDate, int outfeedMissionIsDeleted);

	public List<OutfeedMissionRuntimeDetailsEntity> findByOutfeedMissionEndDateTimeBetweenAndOutfeedMissionIsDeletedAndProductVariantName(
			String startDate, String endDate, int outfeedMissionIsDeleted, String productVariantName);

	public List<OutfeedMissionRuntimeDetailsEntity> findByOutfeedMissionEndDateTimeBetweenAndOutfeedMissionIsDeletedAndProductName(
			String string, String string2, int i, String string3);

	public List<OutfeedMissionRuntimeDetailsEntity> findByOutfeedMissionEndDateTimeBetweenAndOutfeedMissionIsDeletedAndOutfeedMissionStatusAndPalletStatusIdNot(
			String string, String string2, int i, String string3, int j);

	public List<OutfeedMissionRuntimeDetailsEntity> findByOutfeedMissionEndDateTimeBetweenAndOutfeedMissionIsDeletedAndProductNameAndOutfeedMissionStatus(
			String string, String string2, int i, String string3, String string4);

	public List<OutfeedMissionRuntimeDetailsEntity> getByOutfeedMissionIsDeletedAndPalletStatusIdNotAndOutfeedMissionStatus(
			int i, int j, String string);

	public List<OutfeedMissionRuntimeDetailsEntity> findByOutfeedMissionIsDeletedAndOutfeedMissionStatusAndPalletStatusIdNot(
			int i, String string, int j);

	

	public List<OutfeedMissionRuntimeDetailsEntity> findByOutfeedMissionEndDateTimeBetweenAndOutfeedMissionIsDeletedAndProductNameAndPalletStatusIdNot(
			String string, String string2, int i, String string3, int j);

	


//	public List<OutfeedMissionRuntimeDetailsEntity> findBycreatedDatetimeBetweenAndOutfeedMissionIsDeletedAndProductNameAndOutfeedMissionStatusAndPalletStatusIdNot(
//			String string, String string2, int i, String string3, String string4, int j);

	public OutfeedMissionRuntimeDetailsEntity findByPalletInformationIdAndOutfeedMissionStatusIn(
			int palletInformationId, List<String> asList);

	 List<OutfeedMissionRuntimeDetailsEntity> findByOrderId(int orderId);

	public List<OutfeedMissionRuntimeDetailsEntity> findByOutfeedMissionEndDateTimeBetweenAndOutfeedMissionIsDeletedAndProductNameAndOutfeedMissionStatusAndPalletStatusIdNot(
			String string, String string2, int i, String string3, String string4, int j);





	

	

	

}
