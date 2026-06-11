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
@AllArgsConstructor
@NoArgsConstructor
@Table(name="ats_wms_mes_connection_status_details")
public class MesConnectionDetailsEntity {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="MES_CONNECTION_ID")
	private int mesConnectionId;
	
	
	@Column(name="CONNECTION_STATUS")
	private int connectionStatus;
	
	
	@Column(name="DB")
	private String db;
	
	@Column(name="UPDATE_DATETIME")
	private String updateDateTime;
	
	
	
}
