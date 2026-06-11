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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ats_wms_lock_unlock_history_details")
public class LockUnlockDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "LOCK_DETAILS_ID")
	private int id;

	@Column(name = "POSITION_NAME")
	private String positionName;

	@Column(name = "REASON")
	private String reason;

	@Column(name = "USERNAME")
	private String username;

	@Column(name = "CDATETIME")
	private String currentDate;

//	@Column(name = "POSITION_IS_ACTIVE")
//	private int positionIsActive;
//
//	@Column(name = "LOCK")
//	private String lock;
//
//	@Column(name = "UNLOCK")
//	private String unlock;

	@Column(name = "DESCRIPTION")
	private String description;
	
	
	@Column(name = "COMMENT")
	private String comment;
}
