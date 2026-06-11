package com.ats.mahindrabattery.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ats.mahindrabattery.entity.AgeingDaysReportEntity;
import com.ats.mahindrabattery.entity.CurrentPalletStockDetailsEntity;
import com.ats.mahindrabattery.serviceimpl.AgeingDaysReportServiceImpl;

@RestController
@CrossOrigin
@RequestMapping("/agingDaysReportdetails")
public class AgingDaysReportController {
	@Autowired
	private AgeingDaysReportServiceImpl agingDaysReportServiceImpl;

	@GetMapping("/findAgeingDays")
	public List<CurrentPalletStockDetailsEntity> findAgeingDays() {
		return agingDaysReportServiceImpl.findAgeingDays();
	}
	
//	@GetMapping("/findInventoryAgeingReport")
//	public List<AgeingDaysReportEntity> findInventoryAgeingReport(){
//		List<AgeingDaysReportEntity> findInventoryAgeingReport = agingDaysReportServiceImpl.findInventoryAgeingReport();
//		return findInventoryAgeingReport;
//	}
	
	@GetMapping("/findMaterialAboveAgeingDayaCount")
	public List<AgeingDaysReportEntity> findMaterialAboveAgeingDayaCount() {
		List<AgeingDaysReportEntity> findMaterialAboveAgeingDayaCount = agingDaysReportServiceImpl.findMaterialAboveAgeingDayaCount();
		return findMaterialAboveAgeingDayaCount;
	}
}
