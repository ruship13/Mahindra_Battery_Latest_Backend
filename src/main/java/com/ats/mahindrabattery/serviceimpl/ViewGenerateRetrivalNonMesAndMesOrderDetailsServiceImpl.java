package com.ats.mahindrabattery.serviceimpl;
 
import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.Arrays;

import java.util.Date;

import java.util.List;

import java.util.function.Predicate;

import java.util.stream.Collectors;
 
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
 
import com.ats.mahindrabattery.entity.ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity;

import com.ats.mahindrabattery.repository.ViewGenerateRetrivalNonMesAndMesOrderDetailsRepository;

import com.ats.mahindrabattery.service.ViewGenerateRetrivalNonMesAndMesOrderDetailsService;
 
@Service

public class ViewGenerateRetrivalNonMesAndMesOrderDetailsServiceImpl

		implements ViewGenerateRetrivalNonMesAndMesOrderDetailsService {
 
	@Autowired

	private ViewGenerateRetrivalNonMesAndMesOrderDetailsRepository viewGenerateRetrivalNonMesAndMesOrderDetailsRepository;
 
	public List<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity> getAllMannualDispatchOrderMesAndNonMes() {

		try {

			System.out.println("Entered into getAllMannualDispatchOrderMesAndNonMes method in serviceimpl class");

			Date date = new Date();

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			String currentDateTime = dateFormat.format(date);
 
			// Fetch orders created on the current date with IsOrderDeleted = 0

			List<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity> ordersToday = viewGenerateRetrivalNonMesAndMesOrderDetailsRepository

					.findBycreatedDatetimeBetweenAndIsOrderDeleted(currentDateTime + " " + "00:00:00",

							currentDateTime + " " + "23:59:59", 0);

							System.out.println("Orders fetched for today: " + ordersToday);
 
			// Fetch orders not created on the current date, with dispatchStatus = READY or

			// IN_PROGRESS, and IsOrderDeleted = 0

			List<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity> ordersOtherThanToday = viewGenerateRetrivalNonMesAndMesOrderDetailsRepository

					.findOrdersNotCreatedOnCurrentDateWithDispatchStatus(currentDateTime + " " + "00:00:00",

							currentDateTime + " " + "23:59:59", Arrays.asList("READY", "IN_PROGRESS"), 0);

							System.out.println("Orders fetched other day: " + ordersOtherThanToday);

 
			// Combine both lists

			ordersToday.addAll(ordersOtherThanToday);
 
			
			return ordersToday;
 
		} catch (Exception ex) {

			ex.printStackTrace();

		}

		return null;

	}
 
	
 
	public List<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity> findByAllFilters(String cDatetimeStart, String cDatetimeEnd,

			String dispatchOrderNumber, String productVariantCode, String shiftName,String orderSourceDetails) {

		List<String> filterList = new ArrayList<>();

		List<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity> list = new ArrayList<>();
 
		if (!dispatchOrderNumber.equalsIgnoreCase("NA")) {

			filterList.add("dispatchOrderNumber");

			System.out.println("dispatchOrderNumber::" + dispatchOrderNumber);

		}

		if (!productVariantCode.equalsIgnoreCase("NA")) {

			filterList.add("productVariantCode");

			System.out.println("productVariantCode::" + productVariantCode);

		}

		if (!shiftName.equalsIgnoreCase("NA")) {

			filterList.add("shiftName");

			System.out.println("shiftName::" + shiftName);

		}

		if(!orderSourceDetails.equalsIgnoreCase("NA")) {

			filterList.add("orderSourceDetails");

		}

		Predicate<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity> dispatchOrderNumberPred = data -> data.getDispatchOrderNumber()

				.equalsIgnoreCase(dispatchOrderNumber);

		Predicate<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity> productVariantCodePred = data -> data.getProductVariantCode()

				.equalsIgnoreCase(productVariantCode);

		Predicate<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity> shiftNamePred = data -> data.getShiftName().equalsIgnoreCase(shiftName);

		Predicate<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity> orderSourceDetailsPred = data -> data.getOrderSourceDetails().equalsIgnoreCase(orderSourceDetails);
 
		if (!(cDatetimeStart.equalsIgnoreCase("NA")) && !(cDatetimeEnd.equalsIgnoreCase("NA"))) {

			String startDateTime = cDatetimeStart.toString().replace("T", " ");

			String endDateTime = cDatetimeEnd.toString().replace("T", " ");

			System.out.println("startDateTime= " + startDateTime);

			System.out.println("endDateTime= " + endDateTime);

			list = viewGenerateRetrivalNonMesAndMesOrderDetailsRepository.findBycreatedDatetimeBetweenAndDispatchStatus(startDateTime, endDateTime,"COMPLETED");

			System.out.println("list1:" + list.size());
 
		} else {

			Date dateNow = new Date();

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			String date = dateFormat.format(dateNow);

			list = viewGenerateRetrivalNonMesAndMesOrderDetailsRepository.findBycreatedDatetimeBetweenAndDispatchStatus(date + " " + "00:00:00",

					date + " " + "23:59:59","COMPLETED");

			// System.out.println("list2:" + list);

		}
 
		if (filterList.size() != 0) {
 
			for (int i = 0; i < filterList.size(); i++) {
 
				if (filterList.get(i).equalsIgnoreCase("dispatchOrderNumber")) {

					list = list.stream().filter(dispatchOrderNumberPred).collect((Collectors.toList()));

					// System.out.println("floor list::"+list.size());

				} else if (filterList.get(i).equalsIgnoreCase("productVariantCode")) {

					list = list.stream().filter(productVariantCodePred).collect((Collectors.toList()));

					// System.out.println("area list::"+list.size());

				} else if (filterList.get(i).equalsIgnoreCase("shiftName")) {

					list = list.stream().filter(shiftNamePred).collect((Collectors.toList()));

					// System.out.println("status list::"+list.size());

				}

				else if(filterList.get(i).equalsIgnoreCase("orderSourceDetails")) {

					list=list.stream().filter(orderSourceDetailsPred).collect((Collectors.toList()));

				}

			}

		}
 
		if (filterList.size() == 0 && list.size() == 0) {

			list = null;

		}
System.out.println("final list size after applying filters::" + list.size());
		return list;
 
	}
 
}
 