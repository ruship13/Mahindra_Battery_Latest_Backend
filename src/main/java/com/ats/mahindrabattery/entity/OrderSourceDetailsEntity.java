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
@Table(name="ats_wms_order_source_details")
public class OrderSourceDetailsEntity {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "WMS_ORDER_SOURCE_DETAILS_ID")
	private int wmsOrderSourceDetailsId;

	@Column(name = "ORDER_ID")
	private int orderId;

	@Column(name = "ORDER_NUMBER")
	private String orderNumber;

	@Column(name = "ORDER_SOURCE_NAME")
	private String orderSourceName;
	
	
	@Column(name="PART_NUMBER")
	private String partNumber;
	
	@Column(name="QUANTITY")
	private int quantity;
	
	@Column(name="IS_ORDER_CANCELLED_FROM_MES")
	private int isOrderCancelledFromMes;
	
	
	@Column(name="IS_ORDER_CANCELLED_FROM_WMS")
	private int isOrderCancelledFromWms;
	
	@Column(name="IS_ORDER_DELETED_FROM_WMS")
	private int isOrderDeletedFromWms;
	
	@Column(name="SOURCE_CDATETIME")
	private String sourceCDateTime;
	
	@Column(name="ORDER_BATCH_NUMBER")
	private String orderBatchNumber;
	
	
	@Column(name="VIN_NUMBER")
	private String vinNumber;
}
