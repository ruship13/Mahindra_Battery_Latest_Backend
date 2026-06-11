package com.ats.mahindrabattery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ats.mahindrabattery.entity.MesDispatchControlEntity;
public interface MesDispatchControlRepository extends JpaRepository<MesDispatchControlEntity,Integer> {

}