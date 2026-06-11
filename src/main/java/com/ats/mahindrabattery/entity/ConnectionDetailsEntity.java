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
@Table(name = "ats_wms_connetction_tag_details")
public class ConnectionDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="TAG_ID")
	private int tagId;
	
	
	@Column(name="TAG_NAME")
	private String tagName;


    @Column(name="TAG_VALUE")   
    private int tagvalue;
	

	
	
}
