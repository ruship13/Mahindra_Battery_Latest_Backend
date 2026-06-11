package com.ats.mahindrabattery.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ats.mahindrabattery.entity.ConnectionDetailsEntity;
import com.ats.mahindrabattery.entity.YokogawaTemperatureDetailsEntity;
import com.ats.mahindrabattery.repository.ConnectionDetailsRepository;
import com.ats.mahindrabattery.repository.YokogawaTemperatureDetailsRepository;
import com.ats.mahindrabattery.service.YokogawaTemperatureDetailsService;
import com.ats.mahindrabattery.serviceimpl.YokogawaServiceImpl;

@RestController
@CrossOrigin
@RequestMapping("/yokogawaTempDetails")
public class YokogawaTemperatureDetailsController {
		@Autowired
	private ConnectionDetailsRepository connectionDetailsRepository;

	@Autowired
	private YokogawaTemperatureDetailsRepository yokogawaTemperatureDetailsRepository;

	@Autowired
	private YokogawaTemperatureDetailsService yokogawaTemperatureDetailsService;
	@Autowired
    private  YokogawaServiceImpl service;
	
	

    @GetMapping("/check-connection")
    public String checkConnection() {
    	
    	System.out.println("Check Connection....");
        boolean isConnected = service.checkConnection();
			Optional coneectionDetailsEntity=connectionDetailsRepository.findById(1);

			ConnectionDetailsEntity connectionDetailsEntity2=(ConnectionDetailsEntity) coneectionDetailsEntity.get();

		if(isConnected) {

            if(coneectionDetailsEntity.isPresent()) {
				
				connectionDetailsEntity2.setTagvalue(1);
			
			}
			else{
				connectionDetailsEntity2.setTagvalue(0);
			}
				connectionDetailsRepository.save(connectionDetailsEntity2);

		}
		

 return isConnected ? "CONNECTED" : "DISCONNECTED";
        
      // return "CONNECTED";
    }

	@GetMapping("/fetchTempDetails")
	public List<YokogawaTemperatureDetailsEntity> fetchAllTemperatureDetails() {
		List<YokogawaTemperatureDetailsEntity> findAll = yokogawaTemperatureDetailsRepository.findAll();
		for (int i = 0; i < findAll.size(); i++) {
			String createdDateTime = findAll.get(i).getCreatedDateTime();
			String substring = createdDateTime.substring(0, 10);
			String substring1 = "";
			if (i == findAll.size()) {
				String createdDateTime1 = findAll.get(i + 1).getCreatedDateTime();
				substring1 = createdDateTime1.substring(0, 10);
			}
			if (substring.equals(substring1)) {
				findAll.remove(i);
			}

		}
		return findAll;
		

	}

	@GetMapping("/currentDateTemp")
	public YokogawaTemperatureDetailsEntity fetchTemperatureByCurrentDate() {
		return yokogawaTemperatureDetailsService.getTemperatureDetailsOfCurrentDate();
	}

}
