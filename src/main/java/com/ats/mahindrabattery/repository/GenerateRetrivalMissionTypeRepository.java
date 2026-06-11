package com.ats.mahindrabattery.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ats.mahindrabattery.entity.GenerateRetrivalMissionTypeEntity;
@Repository
public interface GenerateRetrivalMissionTypeRepository extends JpaRepository<GenerateRetrivalMissionTypeEntity, Integer> {
	
}


