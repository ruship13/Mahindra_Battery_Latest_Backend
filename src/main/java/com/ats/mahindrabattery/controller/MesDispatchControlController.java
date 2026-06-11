package com.ats.mahindrabattery.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ats.mahindrabattery.entity.MesConnectionDetailsEntity;
import com.ats.mahindrabattery.entity.MesDispatchControlEntity;
import com.ats.mahindrabattery.serviceimpl.MesConnectionDetailsServiceImpl;
import com.ats.mahindrabattery.service.MesDispatchControlService;



@RestController
@CrossOrigin
@RequestMapping("/mesDispatchControl")
public class MesDispatchControlController {

@Autowired
private MesDispatchControlService mesDispatchControlService;


      @GetMapping("/fetchMesDispatchControlDetails")
    public MesDispatchControlEntity getMesDispatchControlDetails() {
        
        return mesDispatchControlService.getMesDispatchControlDetails();
    }


    @PutMapping("/updateMesDispatchControl")
    public ResponseEntity<MesDispatchControlEntity> updateMesDispatchControl(@RequestBody MesDispatchControlEntity request) {

System.out.println("Request received: " + request);

        return mesDispatchControlService.updateMesDispatchControl(request);
    }

}