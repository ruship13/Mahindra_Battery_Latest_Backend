package com.ats.mahindrabattery.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="ats_wms_generate_retrieval_mission_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateRetrivalMissionTypeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DISPATCH_TRIGGERED_ID")
	private int dispatchTriggeredId;

	@Column(name = "DISPATCH_TRIGGERRED")
	private int dispatchTriggered;

	@Column(name = "USER_UPDATED_TIME")
	private String userUpdatedTime;

	@Column(name = "USER_NAME")
	private String userName;
	
}
