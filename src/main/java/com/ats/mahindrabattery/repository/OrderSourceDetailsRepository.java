package com.ats.mahindrabattery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ats.mahindrabattery.entity.OrderSourceDetailsEntity;


@Repository
public interface OrderSourceDetailsRepository extends JpaRepository<OrderSourceDetailsEntity, Integer> {

	List<OrderSourceDetailsEntity> findByOrderId(int dispatchHistoryId);

	

}
