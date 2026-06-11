package com.ats.mahindrabattery.serviceimpl;

import com.ats.mahindrabattery.entity.MesDispatchControlEntity;
import com.ats.mahindrabattery.service.MesDispatchControlService;
import com.ats.mahindrabattery.repository.MesDispatchControlRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MesDispatchControlServiceImpl implements MesDispatchControlService {

         @Autowired
    private MesDispatchControlRepository mesDispatchControlRepository;


 public MesDispatchControlEntity getMesDispatchControlDetails(){

    return mesDispatchControlRepository.findAll().stream().findFirst().orElse(null);
 }

  public ResponseEntity<MesDispatchControlEntity> updateMesDispatchControl(MesDispatchControlEntity request) {

    MesDispatchControlEntity mesDispatchControlEntity = mesDispatchControlRepository.findAll().stream().findFirst().orElse(null);

    if(mesDispatchControlEntity != null){
        mesDispatchControlEntity.setMesRead(request.getMesRead());
       
       
    
        mesDispatchControlEntity = mesDispatchControlRepository.save(mesDispatchControlEntity);
        return ResponseEntity.ok(mesDispatchControlEntity);
    }
    return ResponseEntity.notFound().build();
 }

}