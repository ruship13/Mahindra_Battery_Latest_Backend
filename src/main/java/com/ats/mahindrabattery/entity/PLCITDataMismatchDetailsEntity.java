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
@Table(name = "ats_wms_plc_it_data_mismatch_details")
public class PLCITDataMismatchDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PLC_IT_ID")
	private Integer plcItId;

	@Column(name = "PALLET_CODE")
	private String palletCode;

	@Column(name = "POSITION_ID")
	private Integer positionId;

	@Column(name = "POSITION_NAME")
	private String positionName;

	@Column(name = "IS_DATA_UPDATED")
	private int isDataUpdated;
	
	@Column(name = "CDATETIME")
	private String cDateTime;

}
