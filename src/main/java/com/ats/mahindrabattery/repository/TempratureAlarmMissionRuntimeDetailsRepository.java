package com.ats.mahindrabattery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ats.mahindrabattery.entity.InfeedMissionRuntimeDetailsEntity;
import com.ats.mahindrabattery.entity.TemperatureAlarmMissionRuntimeDetailsEntity;

public interface TempratureAlarmMissionRuntimeDetailsRepository
		extends JpaRepository<TemperatureAlarmMissionRuntimeDetailsEntity, Integer> {

	List<TemperatureAlarmMissionRuntimeDetailsEntity> findByAlarmMissionStatusIn(List<String> asList);

	TemperatureAlarmMissionRuntimeDetailsEntity findTopByOrderByTemperatureAlarmMissionRuntimeDetailsIdDesc();

	List<TemperatureAlarmMissionRuntimeDetailsEntity> findByAlarmMissionIsDeleted(int i);

//	@Query(value = "select * FROM ats_wms_tempreture_alarm_mission_runtime_details where CDATETIME BETWEEN :startDate AND :endDate and ALARM_MISSION_STATUS =:completedStatus", nativeQuery = true)
//	public List<TemperatureAlarmMissionRuntimeDetailsEntity> findTemperatureAlarmMissionRuntimeDetailsBetweenDates(String startDate,
//			String endDate, String completedStatus);

//	List<TemperatureAlarmMissionRuntimeDetailsEntity> findByCDateTimeBetweenAndAlarmMissionStatus(String startDateTime,
//			String endDateTime, String string);

	List<TemperatureAlarmMissionRuntimeDetailsEntity> findByCurrentDateTimeBetweenAndAlarmMissionStatus(String startDateTime,
			String endDateTime, String string);

	List<TemperatureAlarmMissionRuntimeDetailsEntity> findByCurrentDateTimeBetweenAndMissionSourceAndAreaName(String string,
			String string2, String string3, String string4);

	List<TemperatureAlarmMissionRuntimeDetailsEntity> findByAlarmMissionIsDeletedAndAlarmMissionStatusAndMissionSource(
			int i, String string, String string2);

	List<TemperatureAlarmMissionRuntimeDetailsEntity> findByCurrentDateTimeBetweenAndMissionSourceAndAreaNameAndDay(
			String string, String string2, String string3, String string4, String string5);

	List<TemperatureAlarmMissionRuntimeDetailsEntity> findByDay(String string);

	
	

	List<TemperatureAlarmMissionRuntimeDetailsEntity> findByAlarmMissionIsDeletedAndAlarmMissionStatusAndMissionSourceAndCurrentDateTimeContaining(
			int i, String string, String string2, String currentDate);

	List<TemperatureAlarmMissionRuntimeDetailsEntity> findByCurrentDateTimeBetweenAndAlarmMissionStatusAndMissionSource(
			String startDateTime, String endDateTime, String string, String string2);

//	@Modifying
//	@Query("SELECT u FROM TemperatureAlarmMissionRuntimeDetailsEntity u WHERE u.alarmMissionStatus = :alarmMissionStatus1  OR u.alarmMissionStatus= :alarmMissionStatus2")

}
