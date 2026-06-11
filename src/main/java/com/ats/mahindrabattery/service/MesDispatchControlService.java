
package com.ats.mahindrabattery.service;

import org.springframework.http.ResponseEntity;

import com.ats.mahindrabattery.entity.MesDispatchControlEntity;

public interface MesDispatchControlService {


    public MesDispatchControlEntity getMesDispatchControlDetails();

    

    public ResponseEntity<MesDispatchControlEntity> updateMesDispatchControl(MesDispatchControlEntity request);
   

}