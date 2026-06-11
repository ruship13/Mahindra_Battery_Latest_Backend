package com.ats.mahindrabattery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ats.mahindrabattery.entity.ConnectionDetailsEntity;

public interface ConnectionDetailsRepository extends JpaRepository<ConnectionDetailsEntity, Integer> {

    
} 
