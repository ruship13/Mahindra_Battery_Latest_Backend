package com.ats.mahindrabattery.serviceimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ats.mahindrabattery.entity.AuditTrailDetailsEntity;
import com.ats.mahindrabattery.entity.CurrentPalletStockDetailsEntity;
import com.ats.mahindrabattery.entity.InfeedMissionRuntimeDetailsEntity;
import com.ats.mahindrabattery.entity.MasterPositionDetailsEntity;
import com.ats.mahindrabattery.entity.MasterStationTagDetailsEntity;
import com.ats.mahindrabattery.entity.TemperatureAlarmMissionRuntimeDetailsEntity;
import com.ats.mahindrabattery.repository.AuditTrailDetailsRepository;
import com.ats.mahindrabattery.repository.CurrentPalletStockDetailsRepository;
import com.ats.mahindrabattery.repository.MasterPositionDetailsRepository;
import com.ats.mahindrabattery.repository.MasterStationTagDetailsRepository;
import com.ats.mahindrabattery.repository.TempratureAlarmMissionRuntimeDetailsRepository;
import com.ats.mahindrabattery.response.ResponseHandler;
import com.ats.mahindrabattery.service.TemperatureAlarmMissionRuntimeDetailsService;

@Service
public class TemperatureAlarmMissionRuntimeDetailsServiceImpl implements TemperatureAlarmMissionRuntimeDetailsService {

	@Autowired
	TempratureAlarmMissionRuntimeDetailsRepository tempratureAlarmMissionRuntimeDetailsRepository;

	@Autowired
	MasterPositionDetailsRepository masterPositionDetailsRepository;

	@Autowired
	private MasterStationTagDetailsRepository masterStationTagDetailsRepository;

	@Autowired
	private CurrentPalletStockDetailsRepository currentPalletStockDetailsRepository;

	private AuditTrailDetailsRepository auditTrailDetailsRepository;

	public List<TemperatureAlarmMissionRuntimeDetailsEntity> fetchTempratureAlarmMissionRuntimeDetails() {
		try {

			List<TemperatureAlarmMissionRuntimeDetailsEntity> findByAlarmMissionStatus = tempratureAlarmMissionRuntimeDetailsRepository
					.findByAlarmMissionStatusIn(Arrays.asList("READY", "IN_PROGRESS"));
			System.out.println(" findByAlarmMissionStatus::" + findByAlarmMissionStatus);
			return findByAlarmMissionStatus;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

	@Override
	public List<TemperatureAlarmMissionRuntimeDetailsEntity> fetchAllAlarmMission() {

		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		String currentDate = ft.format(dNow);

		return tempratureAlarmMissionRuntimeDetailsRepository
				.findByAlarmMissionIsDeletedAndAlarmMissionStatusAndMissionSourceAndCurrentDateTimeContaining(0,
						"COMPLETED", "MOCK_MISSION", currentDate);
	}

	@Override
	public List<TemperatureAlarmMissionRuntimeDetailsEntity> fetchAllAlarmMissionByAutoMission() {

		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		String currentDate = ft.format(dNow);

		return tempratureAlarmMissionRuntimeDetailsRepository
				.findByAlarmMissionIsDeletedAndAlarmMissionStatusAndMissionSourceAndCurrentDateTimeContaining(0,
						"COMPLETED", "AUTO_MISSION", currentDate);
	}

	@Override
	public ResponseEntity<Object> addMockDrillMissionDetails(String areaName) {

		DayOfWeek dayOfWeek = DayOfWeek.from(LocalDate.now());
		System.out.println("dayOfWeek::" + dayOfWeek.toString());

		if (areaName.equalsIgnoreCase("Area-1")) {
			MasterStationTagDetailsEntity findByPlcTagName = masterStationTagDetailsRepository
					.findByPlcTagName("ATS.WMS_STATION.STACKER_1_DUMP_TANK_HEALTHY");
			if (!"1".equals(findByPlcTagName.getCurrentValue())) {
				return ResponseHandler.generateResponse("Dump tank 1 not empty", HttpStatus.ALREADY_REPORTED,
						findByPlcTagName.getCurrentValue());
			}
		} else if (areaName.equalsIgnoreCase("Area-2")) {
			MasterStationTagDetailsEntity findByPlcTagName = masterStationTagDetailsRepository
					.findByPlcTagName("ATS.WMS_STATION.STACKER_2_DUMP_TANK_HEALTHY");
			if (!"1".equals(findByPlcTagName.getCurrentValue())) {
				return ResponseHandler.generateResponse("Dump tank 2 not empty", HttpStatus.CREATED,
						findByPlcTagName.getCurrentValue());
			}
		}

		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = ft.format(dNow);

		List<MasterPositionDetailsEntity> list = new ArrayList<>();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();

		List<CurrentPalletStockDetailsEntity> findBypalletStatusIdAndPalletStatusname = currentPalletStockDetailsRepository
				.findByPalletStatusIdAndPalletStatusnameAndAreaName(3, "EMPTY", areaName);
		for (CurrentPalletStockDetailsEntity currentPalletStockDetailsEntity : findBypalletStatusIdAndPalletStatusname) {
			List<MasterPositionDetailsEntity> findByPositionName = masterPositionDetailsRepository
					.findByPositionName(currentPalletStockDetailsEntity.getPositionName());
			for (MasterPositionDetailsEntity masterPositionDetailsEntity : findByPositionName) {
				if (masterPositionDetailsEntity.getPositionIsActive() == 1
						&& masterPositionDetailsEntity.getPositionIsAllocated() == 1) {
					list.add(masterPositionDetailsEntity);
				}
			}

		}

		List<CurrentPalletStockDetailsEntity> findByPositionName = currentPalletStockDetailsRepository
				.findByPositionName(list.get(0).getPositionName());

		if (findByPositionName.isEmpty()) {
			return ResponseHandler.generateResponse("Empty Pallet Not Found", HttpStatus.IM_USED, null);
		}
		CurrentPalletStockDetailsEntity currentPalletStockDetailsEntity1 = findByPositionName.get(0);

		TemperatureAlarmMissionRuntimeDetailsEntity findTopByOrderByTemperatureAlarmMissionRuntimeDetailsIdDesc = tempratureAlarmMissionRuntimeDetailsRepository
				.findTopByOrderByTemperatureAlarmMissionRuntimeDetailsIdDesc();

		if (findTopByOrderByTemperatureAlarmMissionRuntimeDetailsIdDesc == null) {
			TemperatureAlarmMissionRuntimeDetailsEntity temperatureAlarmMissionRuntimeDetailsEntity = new TemperatureAlarmMissionRuntimeDetailsEntity(
					1, 1, currentPalletStockDetailsEntity1.getPositionName(), currentDate, "1900-01-01 00:00:00",
					currentPalletStockDetailsEntity1.getPalletInformationId(),
					currentPalletStockDetailsEntity1.getPalletCode(), currentPalletStockDetailsEntity1.getProductId(),
					currentPalletStockDetailsEntity1.getProductName(),
					currentPalletStockDetailsEntity1.getProductVariantId(),
					currentPalletStockDetailsEntity1.getProductVariantName(),
					currentPalletStockDetailsEntity1.getProductVariantCode(),
					currentPalletStockDetailsEntity1.getBatchNumber(),
					currentPalletStockDetailsEntity1.getModelNumber(), currentPalletStockDetailsEntity1.getQuantity(),
					currentPalletStockDetailsEntity1.getAreaId(), areaName,
					currentPalletStockDetailsEntity1.getFloorId(), currentPalletStockDetailsEntity1.getFloorName(),
					currentPalletStockDetailsEntity1.getSerialNumber(),
					currentPalletStockDetailsEntity1.getPositionId(),
					currentPalletStockDetailsEntity1.getPositionName(), currentPalletStockDetailsEntity1.getRackId(),
					currentPalletStockDetailsEntity1.getRackName(), currentPalletStockDetailsEntity1.getRackSide(),
					currentPalletStockDetailsEntity1.getRackColumn(),
					currentPalletStockDetailsEntity1.getPositionNumberInRack(), "READY", currentDate,
					"1900-01-01 00:00:00", "1900-01-01 00:00:00", currentPalletStockDetailsEntity1.getMfgShift(), 0,
					authentication.getName(), "MOCK_MISSION", dayOfWeek.toString());

			tempratureAlarmMissionRuntimeDetailsRepository.save(temperatureAlarmMissionRuntimeDetailsEntity);

			AuditTrailDetailsEntity auditTrailDetailsEntity = new AuditTrailDetailsEntity();
			auditTrailDetailsEntity.setOperatorActions(
					"Mock Drill Mission generated for :  " + currentPalletStockDetailsEntity1.getPositionName());
			auditTrailDetailsEntity.setField("Mock Drill Mission");
			auditTrailDetailsEntity.setAfterValue(0);
			auditTrailDetailsEntity.setBeforeValue(0);
			auditTrailDetailsEntity.setReason("Mock Drill Mission");

			System.out.println(" name :: " + name);
			auditTrailDetailsEntity.setUsername(name);
			auditTrailDetailsEntity.setDatetimeC(currentDate);
			auditTrailDetailsRepository.save(auditTrailDetailsEntity);

			return ResponseHandler.generateResponse("Mission added Succesfully", HttpStatus.OK,
					temperatureAlarmMissionRuntimeDetailsEntity);
		} else {

			TemperatureAlarmMissionRuntimeDetailsEntity temperatureAlarmMissionRuntimeDetailsEntity = new TemperatureAlarmMissionRuntimeDetailsEntity(
					findTopByOrderByTemperatureAlarmMissionRuntimeDetailsIdDesc
							.getTemperatureAlarmMissionRuntimeDetailsId() + 1,
					findTopByOrderByTemperatureAlarmMissionRuntimeDetailsIdDesc.getTemperatureAlarmId() + 1,
					currentPalletStockDetailsEntity1.getPositionName(), currentDate, "1900-01-01 00:00:00",
					currentPalletStockDetailsEntity1.getPalletInformationId(),
					currentPalletStockDetailsEntity1.getPalletCode(), currentPalletStockDetailsEntity1.getProductId(),
					currentPalletStockDetailsEntity1.getProductName(),
					currentPalletStockDetailsEntity1.getProductVariantId(),
					currentPalletStockDetailsEntity1.getProductVariantName(),
					currentPalletStockDetailsEntity1.getProductVariantCode(),
					currentPalletStockDetailsEntity1.getBatchNumber(),
					currentPalletStockDetailsEntity1.getModelNumber(), currentPalletStockDetailsEntity1.getQuantity(),
					currentPalletStockDetailsEntity1.getAreaId(), areaName,
					currentPalletStockDetailsEntity1.getFloorId(), currentPalletStockDetailsEntity1.getFloorName(),
					currentPalletStockDetailsEntity1.getSerialNumber(),
					currentPalletStockDetailsEntity1.getPositionId(),
					currentPalletStockDetailsEntity1.getPositionName(), currentPalletStockDetailsEntity1.getRackId(),
					currentPalletStockDetailsEntity1.getRackName(), currentPalletStockDetailsEntity1.getRackSide(),
					currentPalletStockDetailsEntity1.getRackColumn(),
					currentPalletStockDetailsEntity1.getPositionNumberInRack(), "READY", currentDate,
					"1900-01-01 00:00:00", "1900-01-01 00:00:00", currentPalletStockDetailsEntity1.getMfgShift(), 0,
					authentication.getName(), "MOCK_MISSION", dayOfWeek.toString());

			tempratureAlarmMissionRuntimeDetailsRepository.save(temperatureAlarmMissionRuntimeDetailsEntity);
			return ResponseHandler.generateResponse("Mission added Succesfully", HttpStatus.OK,
					temperatureAlarmMissionRuntimeDetailsEntity);
		}
//		new AuditTrailDetailsEntity(0, currentDate, currentDate, currentDate, 0, 0, 0, areaName, currentDate);

	}

	@Override
	public List<TemperatureAlarmMissionRuntimeDetailsEntity> findByAllFilters(String alarmMissionStartDateTime,
			String alarmMissionEndDateTime) {

		List<String> filterList = new ArrayList<>();
		List<TemperatureAlarmMissionRuntimeDetailsEntity> list;

		if (!alarmMissionStartDateTime.equals("NA") && !alarmMissionEndDateTime.equals("NA")) {
			String startDateTime = alarmMissionStartDateTime.replace("T", " ");
			String endDateTime = alarmMissionEndDateTime.replace("T", " ");

			list = tempratureAlarmMissionRuntimeDetailsRepository
					.findByCurrentDateTimeBetweenAndAlarmMissionStatusAndMissionSource(startDateTime, endDateTime,
							"COMPLETED", "MOCK_MISSION");
		} else {
			Date dNow = new Date();
			SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
			String date = ft.format(dNow);

			list = tempratureAlarmMissionRuntimeDetailsRepository
					.findByCurrentDateTimeBetweenAndAlarmMissionStatusAndMissionSource(date + " 00:00:00",
							date + " 23:59:59", "COMPLETED", "MOCK_MISSION");
		}

		if (filterList.isEmpty() && list.isEmpty()) {
			return null;
		}

		return list;
	}

	@Override
	public List<TemperatureAlarmMissionRuntimeDetailsEntity> findByAllFiltersForAutoMission(
			String alarmMissionStartDateTime, String alarmMissionEndDateTime) {

		List<String> filterList = new ArrayList<>();
		List<TemperatureAlarmMissionRuntimeDetailsEntity> list;

		if (!alarmMissionStartDateTime.equals("NA") && !alarmMissionEndDateTime.equals("NA")) {
			String startDateTime = alarmMissionStartDateTime.replace("T", " ");
			String endDateTime = alarmMissionEndDateTime.replace("T", " ");

			list = tempratureAlarmMissionRuntimeDetailsRepository
					.findByCurrentDateTimeBetweenAndAlarmMissionStatusAndMissionSource(startDateTime, endDateTime,
							"COMPLETED", "AUTO_MISSION");
		} else {
			Date dNow = new Date();
			SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
			String date = ft.format(dNow);

			list = tempratureAlarmMissionRuntimeDetailsRepository
					.findByCurrentDateTimeBetweenAndAlarmMissionStatusAndMissionSource(date + " 00:00:00",
							date + " 23:59:59", "COMPLETED", "AUTO_MISSION");
		}

		if (filterList.isEmpty() && list.isEmpty()) {
			return null;
		}

		return list;
	}

	public ResponseEntity<Object> findByCurrentDateForArea1() {

		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		String currentDate = ft.format(dNow);

		LocalDate today = LocalDate.now();
		LocalDate firstMonday = getFirstMondayOfMonth(today.getYear(), today.getMonthValue());

		LocalTime currentTime = LocalTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		LocalTime dateTime = LocalTime.parse("10:00:00", formatter);

		if (today.equals(firstMonday) && currentTime.isAfter(dateTime)) {
			List<TemperatureAlarmMissionRuntimeDetailsEntity> findBycDateTimeBetweenAndMissionSourceAndAreaName = tempratureAlarmMissionRuntimeDetailsRepository
					.findByCurrentDateTimeBetweenAndMissionSourceAndAreaNameAndDay(currentDate + " " + "00:00:00",
							currentDate + " " + "23:59:59", "MOCK_MISSION", "Area-1", "MONDAY");

			if (findBycDateTimeBetweenAndMissionSourceAndAreaName.isEmpty()) {
				return ResponseHandler.generateResponse("Mock drill test for Area-1 is pending.", HttpStatus.OK,
						findBycDateTimeBetweenAndMissionSourceAndAreaName);
			} else {
				return ResponseHandler.generateResponse("Mock drill test generated for Area-1",
						HttpStatus.ALREADY_REPORTED, findBycDateTimeBetweenAndMissionSourceAndAreaName);
			}
		}
		return null;
	}

	// Utility method to find the first Monday of the given month
	private LocalDate getFirstMondayOfMonth(int year, int month) {
		LocalDate firstDay = LocalDate.of(year, month, 1);
		while (firstDay.getDayOfWeek() != DayOfWeek.MONDAY) {
			firstDay = firstDay.plusDays(1);
		}
		return firstDay;
	}

	public ResponseEntity<Object> findByCurrentDateForArea2() {

		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		String currentDate = ft.format(dNow);

		LocalDate today = LocalDate.now();
		LocalDate firstMonday = getFirstMondayOfMonth(today.getYear(), today.getMonthValue());

		LocalTime currentTime = LocalTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		LocalTime dateTime = LocalTime.parse("10:00:00", formatter);

		if (today.equals(firstMonday) && currentTime.isAfter(dateTime)) {
			List<TemperatureAlarmMissionRuntimeDetailsEntity> findBycDateTimeBetweenAndMissionSourceAndAreaName = tempratureAlarmMissionRuntimeDetailsRepository
					.findByCurrentDateTimeBetweenAndMissionSourceAndAreaNameAndDay(currentDate + " " + "00:00:00",
							currentDate + " " + "23:59:59", "MOCK_MISSION", "Area-2", "MONDAY");

			if (findBycDateTimeBetweenAndMissionSourceAndAreaName.isEmpty()) {
				return ResponseHandler.generateResponse("Mock drill test for Area-2 is pending.", HttpStatus.OK,
						findBycDateTimeBetweenAndMissionSourceAndAreaName);
			} else {
				return ResponseHandler.generateResponse("Mock drill test generated for Area-2",
						HttpStatus.ALREADY_REPORTED, findBycDateTimeBetweenAndMissionSourceAndAreaName);
			}
		}
		return null;
	}

}
