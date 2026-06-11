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
@Table(name = "ats_wms_yokogawa_minmax_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YokogawaTemperatureDetailsEntity {

	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "ID")
	private int temperatureDetailsId;

	@Column(name = "MIN_TEMP")
	private float minTemp;

	@Column(name = "MAX_TEMP")
	private float maxTemp;

	@Column(name = "CDATETIME")
	private String createdDateTime;

}
