package com.ats.mahindrabattery.controller;

import org.springframework.web.bind.annotation.*;

import com.ats.mahindrabattery.serviceimpl.YokogawaServiceImpl;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // allow Angular
public class YokogawaController {

    private final YokogawaServiceImpl service;

    public YokogawaController(YokogawaServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/check-connection")
    public String checkConnection() {
        boolean isConnected = service.checkConnection();

        return isConnected ? "CONNECTED" : "DISCONNECTED";
    }
}