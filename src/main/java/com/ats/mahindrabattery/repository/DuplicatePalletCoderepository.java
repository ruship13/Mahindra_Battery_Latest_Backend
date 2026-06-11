package com.ats.mahindrabattery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ats.mahindrabattery.entity.DuplicatePalletCodeEntity;



@Repository
public interface DuplicatePalletCoderepository
        extends JpaRepository<DuplicatePalletCodeEntity, Integer> {

    List<DuplicatePalletCodeEntity> findByDuplicatePalletCodeUpdateValue(int value);
}

