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
@Table(name="ats_wms_mes_dispatch_control")
public class MesDispatchControlEntity {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="MES_ID")
	private int mesId;
	
	
	@Column(name="MES_READ")
	private int mesRead;
	
	


	
	
}
