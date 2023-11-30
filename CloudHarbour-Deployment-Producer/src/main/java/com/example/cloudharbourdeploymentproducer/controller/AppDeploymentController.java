package com.example.cloudharbourdeploymentproducer.controller;

import com.example.cloudharbourdeploymentproducer.AppDeploymentProducerService;
import com.example.cloudharbourdeploymentproducer.dto.AppDeployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/app")
public class AppDeploymentController {

    @Autowired
    AppDeploymentProducerService appDeploymentProducerService;
    @PostMapping("")
    public ResponseEntity<?> deployApp(@RequestBody AppDeployment appDeployment){
        return appDeploymentProducerService.createAppDeployment(appDeployment);
    }
}
