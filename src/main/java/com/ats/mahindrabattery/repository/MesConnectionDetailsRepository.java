package com.ats.mahindrabattery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ats.mahindrabattery.entity.MesConnectionDetailsEntity;


@Repository
public interface MesConnectionDetailsRepository extends JpaRepository<MesConnectionDetailsEntity, Integer>{

}
