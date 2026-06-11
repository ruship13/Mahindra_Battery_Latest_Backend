package com.ats.mahindrabattery.serviceimpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ats.mahindrabattery.entity.AgeingDaysReportEntity;
import com.ats.mahindrabattery.entity.CurrentPalletStockDetailsEntity;
import com.ats.mahindrabattery.entity.MasterAgeingDaysDetailsEntity;
import com.ats.mahindrabattery.entity.MasterProductVariantDetailsEntity;
import com.ats.mahindrabattery.repository.CurrentPalletStockDetailsRepository;
import com.ats.mahindrabattery.repository.IMasterAgeingDaysDetailsRepository;
import com.ats.mahindrabattery.repository.MasterProductVariantDetailsRepository;
import com.ats.mahindrabattery.service.AgeingDaysReportService;

@Service
public class AgeingDaysReportServiceImpl implements AgeingDaysReportService {

	int days;


	AgeingDaysReportEntity ageingDaysReportEntity = new AgeingDaysReportEntity();

	@Autowired
	private CurrentPalletStockDetailsRepository currentPalletStockDetailsRepository;

	@Autowired
	private MasterProductVariantDetailsRepository masterProductVariantRepository;

	@Autowired
	private IMasterAgeingDaysDetailsRepository masterAgeingDaysDetailsRepository;

	public List<AgeingDaysReportEntity> findByAllFilters(String productVariantCode) {
		List<String> filterList = new ArrayList<>();
		List<AgeingDaysReportEntity> list = new ArrayList<>();

		if (!productVariantCode.equals("NA")) {
			filterList.add("productVariantCode");
			System.out.println("productVariantCode::" + productVariantCode);
		}

		Predicate<? super AgeingDaysReportEntity> productVariantCodePred = data -> data.getProductvariantCode()
				.equals(productVariantCode);

		if (filterList.size() != 0) {

			for (int i = 0; i < filterList.size(); i++) {

				if (filterList.get(i).equals("productVariantCode")) {
					list = list.stream().filter(productVariantCodePred).collect((Collectors.toList()));
					
				}

			}
		}

		if (filterList.size() == 0 && list.size() == 0) {
			list = null;
		}
		return list;

	}

	@Override
	public List<CurrentPalletStockDetailsEntity> findAgeingDays() {
		try {

			List<CurrentPalletStockDetailsEntity> list = new ArrayList<>();

			List<CurrentPalletStockDetailsEntity> findByProductNameNot = currentPalletStockDetailsRepository
					.findByProductVariantCodeNot("NA");
			for (int i = 0; i < findByProductNameNot.size(); i++) {
				String loadDatetime = findByProductNameNot.get(i).getLoadDatetime();
				String loadDate = loadDatetime.substring(0, 10);
		
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
				LocalDate expiryDate = LocalDate.parse(loadDate, formatter);
				LocalDate currentDate = LocalDate.now();
				days = (int) ChronoUnit.DAYS.between(expiryDate, currentDate);

				currentPalletStockDetailsRepository.updateAgeingDays(days,
						findByProductNameNot.get(i).getCurrentPalletStockDetailsId());

			}
			List<CurrentPalletStockDetailsEntity> findByProductName = currentPalletStockDetailsRepository
					.findByProductVariantCode("NA");
			for (int i = 0; i < findByProductName.size(); i++) {
				currentPalletStockDetailsRepository.updateAgeingDays(0,
						findByProductName.get(i).getCurrentPalletStockDetailsId());
			}
			list.addAll(findByProductName);
			list.addAll(findByProductNameNot);

			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	// find above ageing days material
	public List<AgeingDaysReportEntity> findMaterialAboveAgeingDayaCount() {

		try {

			List<AgeingDaysReportEntity> MaterialAboveDaysDetails = new ArrayList<>();

			

			List<MasterAgeingDaysDetailsEntity> againgDaysList = masterAgeingDaysDetailsRepository.findAll();

			int actualAgeingDays = againgDaysList.get(0).getAgeingDays();

		

			List<MasterProductVariantDetailsEntity> allProductVariants = masterProductVariantRepository
					.findByproductVariantIsDeleted(0);

			

			List<CurrentPalletStockDetailsEntity> currentStockList1 = currentPalletStockDetailsRepository
					.findByProductNameNot("NA");

			for (int i = 0; i < allProductVariants.size(); i++) {



				String pvar = allProductVariants.get(i).getProductVariantCode();



				List<CurrentPalletStockDetailsEntity> productVariantCode1 = currentStockList1.stream()
						.filter(e -> (e.getProductVariantCode().equals(pvar) && e.getAgeingDays() >= 0
								&& e.getAgeingDays() <= 3))
						.collect(Collectors.toList());

				List<CurrentPalletStockDetailsEntity> productVariantCode2 = currentStockList1.stream()
						.filter(e -> (e.getProductVariantCode().equals(pvar) && e.getAgeingDays() >= 4
								&& e.getAgeingDays() <= 7))
						.collect(Collectors.toList());

				List<CurrentPalletStockDetailsEntity> productVariantCode3 = currentStockList1.stream()
						.filter(e -> (e.getProductVariantCode().equals(pvar) && e.getAgeingDays() >= 8
								&& e.getAgeingDays() <= 14))
						.collect(Collectors.toList());

				List<CurrentPalletStockDetailsEntity> productVariantCode4 = currentStockList1.stream()
						.filter(e -> (e.getProductVariantCode().equals(pvar) && e.getAgeingDays() >= 15
								&& e.getAgeingDays() <= 30))
						.collect(Collectors.toList());

				List<CurrentPalletStockDetailsEntity> productVariantCode5 = currentStockList1.stream()
						.filter(e -> (e.getProductVariantCode().equals(pvar) && e.getAgeingDays() > 30))
						.collect(Collectors.toList());



				int range0to3 = productVariantCode1.size();

				int range4to7 = productVariantCode2.size();

				int range8to14 = productVariantCode3.size();

				int range15to30 = productVariantCode4.size();

				int range30plus = productVariantCode5.size();

				int total = range0to3 + range4to7 + range8to14 + range15to30 + range30plus;



				MaterialAboveDaysDetails.add(new AgeingDaysReportEntity(i, allProductVariants.get(i).getProductName(),
						allProductVariants.get(i).getProductVariantCode(),
						allProductVariants.get(i).getProductVariantname(), range0to3, range4to7, range8to14,
						range15to30, range30plus, total));



			}

			return MaterialAboveDaysDetails;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	



}