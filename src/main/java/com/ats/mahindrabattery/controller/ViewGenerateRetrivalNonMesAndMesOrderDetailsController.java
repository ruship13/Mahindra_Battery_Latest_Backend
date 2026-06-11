package com.ats.mahindrabattery.controller;
 
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
 
import com.ats.mahindrabattery.entity.GenerateManualRetrievalOrderEntity;

import com.ats.mahindrabattery.entity.ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity;

import  com.ats.mahindrabattery.service.ViewGenerateRetrivalNonMesAndMesOrderDetailsService;
 
@RestController

@CrossOrigin

@RequestMapping("/ViewMesAndNonMesOrderDetails")

public class ViewGenerateRetrivalNonMesAndMesOrderDetailsController {
 
	@Autowired
private ViewGenerateRetrivalNonMesAndMesOrderDetailsService ViewGenerateRetrivalNonMesAndMesOrderDetailsService;	

	@GetMapping("/getAllMannualDispatchOrderOfMesAndNonMesCurrentDate")

	public List<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity> getAllMannualDispatchOrderMesAndNonMes() {

		System.out.println("Entered into getAllMannualDispatchOrderMesAndNonMes method in controller class");

		List<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity> allMannualDispatchOrder = ViewGenerateRetrivalNonMesAndMesOrderDetailsService

				.getAllMannualDispatchOrderMesAndNonMes();

		return allMannualDispatchOrder;

	}


	@GetMapping("/fetchMannualDispatchDetailsByAllFilters/{cDatetimeStart}/{cDatetimeEnd}/{dispatchOrderNumber}/{productVariantCode}/{shiftName}/{orderSourceDetails}")

	public List<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity> findByAllFiltersDetails(@PathVariable String cDatetimeStart,

			@PathVariable String cDatetimeEnd, @PathVariable String dispatchOrderNumber,

			@PathVariable String productVariantCode, @PathVariable String shiftName,@PathVariable String orderSourceDetails) {

		return ViewGenerateRetrivalNonMesAndMesOrderDetailsService.findByAllFilters(cDatetimeStart, cDatetimeEnd, dispatchOrderNumber,

				productVariantCode, shiftName,orderSourceDetails);

	}


}
 