package com.ats.mahindrabattery.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ats.mahindrabattery.entity.BufferDetailsEntity;

public interface BufferDetailsService {
	
	public Page<BufferDetailsEntity> findBufferPalletDetailsOfBEV(Pageable pageable);
	
	public Page<BufferDetailsEntity> findBufferPalletDetailsOfS230(Pageable pageable);
}
