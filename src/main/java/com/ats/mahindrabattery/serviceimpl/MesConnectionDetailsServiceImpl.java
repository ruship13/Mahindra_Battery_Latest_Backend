package com.ats.mahindrabattery.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ats.mahindrabattery.entity.MesConnectionDetailsEntity;
import com.ats.mahindrabattery.repository.MesConnectionDetailsRepository;

@Service
public class MesConnectionDetailsServiceImpl{
	
	
	@Autowired
	private MesConnectionDetailsRepository mesConnectionDetailsRepository;
	
	
	
	public List<MesConnectionDetailsEntity> fetchMesConnectionDetails() {
		 List<MesConnectionDetailsEntity> findAll = mesConnectionDetailsRepository.findAll();
		 return findAll;
	}
	
	 

}
