package com.ats.mahindrabattery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ats.mahindrabattery.entity.BufferDetailsEntity;

import com.ats.mahindrabattery.service.BufferDetailsService;

@CrossOrigin
@RestController
@RequestMapping("/bufferDetails")
public class BufferDetailsController {

	
	@Autowired
	private BufferDetailsService bufferserviceInstance;
	
	@GetMapping("/findBufferPalletBEV")
	public Page<BufferDetailsEntity> findListOfBufferPallet(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		System.out.println("data getting..." + bufferserviceInstance.findBufferPalletDetailsOfBEV(pageable));
		return bufferserviceInstance.findBufferPalletDetailsOfBEV(pageable);
	}
	
	
	
	@GetMapping("/findBufferPalletS230")
	public Page<BufferDetailsEntity> findListOfBufferPalletS230(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		System.out.println("data getting..." + bufferserviceInstance.findBufferPalletDetailsOfS230(pageable));
		return bufferserviceInstance.findBufferPalletDetailsOfS230(pageable);
	}

	
	
	
}
