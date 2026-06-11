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
@Table(name = "ats_wms_duplicate_pallet_code_tag_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuplicatePalletCodeEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DUPLICATE_PALLET_CODE_AREA_ID")
	private int duplicatePalletCodeAreaId;
	
	
	
	@Column(name = "DUPLICATE_PALLET_CODE_AREA_NANE")
	private String duplicatePalletCodeAreaName;
	
	@Column(name = "DUPLICATE_PALLET_CODE_TAG_NAME")
	private String duplicatePalletCodetagName;
	
	@Column(name = "DUPLICATE_PALLET_CODE_UPDATE_VALUE")
	private int duplicatePalletCodeUpdateValue;
	
	

}
