package com.ats.mahindrabattery.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ats.mahindrabattery.entity.MesConnectionDetailsEntity;
import com.ats.mahindrabattery.serviceimpl.MesConnectionDetailsServiceImpl;

@CrossOrigin
@RequestMapping("mesConnectionDetails")
@RestController
public class MesConnectionDetailsController {
	
	
	@Autowired
	private MesConnectionDetailsServiceImpl mesConnectionDetailsServiceImpl;
	
	
	@GetMapping("/fetchMesConnectionDetails")
	public List<MesConnectionDetailsEntity> fetchMesConnectionDetails() {
		return mesConnectionDetailsServiceImpl.fetchMesConnectionDetails();
	}

}
