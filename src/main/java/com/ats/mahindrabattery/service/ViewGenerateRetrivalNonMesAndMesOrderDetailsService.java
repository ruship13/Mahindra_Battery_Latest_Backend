package com.ats.mahindrabattery.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ats.mahindrabattery.entity.ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity;

@Service
public interface ViewGenerateRetrivalNonMesAndMesOrderDetailsService {
	

    List<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity> 
        getAllMannualDispatchOrderMesAndNonMes();

    
   
    List<ViewGenerateRetrivalNonMesAndMesOrderDetailsEntity> 
        findByAllFilters(
            String cDatetimeStart,
            String cDatetimeEnd,
            String dispatchOrderNumber,
            String productVariantCode,
            String shiftName,
            String orderSourceDetails
        );
	
	

}
