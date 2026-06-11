package com.ats.mahindrabattery.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ats.mahindrabattery.entity.BufferDetailsEntity;
import com.ats.mahindrabattery.repository.BufferDetailsRepository;
import com.ats.mahindrabattery.service.BufferDetailsService;
@Service
public class BufferDetailsServiceImpl implements BufferDetailsService {

	@Autowired
	private BufferDetailsRepository bufferDetailsRepository;
	
	
	public Page<BufferDetailsEntity> findBufferPalletDetailsOfBEV(Pageable pageable) {
		Page<BufferDetailsEntity> findBufferPallet = bufferDetailsRepository
				.findByBufferIsDeletedAndProductName(0,"BEV",pageable);
		return findBufferPallet;
	}
	
	
	
	public Page<BufferDetailsEntity> findBufferPalletDetailsOfS230(Pageable pageable) {
		Page<BufferDetailsEntity> findBufferPallet = bufferDetailsRepository
				.findByBufferIsDeletedAndProductName(0,"S230",pageable);
		return findBufferPallet;
	}
	
}
