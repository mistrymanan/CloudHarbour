package com.example.cloudharbourdeploymentproducer.controller;

import com.example.cloudharbourdeploymentproducer.AppDeploymentProducerService;
import com.example.cloudharbourdeploymentproducer.dto.AppDeployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/apps")
public class AppDeploymentController {

    @Autowired
    AppDeploymentProducerService appDeploymentProducerService;

    @CrossOrigin(origins = "*")
    @PostMapping("")
    public ResponseEntity<?> deployApp(@RequestBody AppDeployment appDeployment){
        return appDeploymentProducerService.createAppDeployment(appDeployment);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("{deploymentId}")
    public ResponseEntity<?> deleteDeployment(@PathVariable String deploymentId){
        return appDeploymentProducerService.deleteAppDeployment(deploymentId);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("")
    public ResponseEntity<?> getAllDeployments(){
        return appDeploymentProducerService.getAllDeployment();
    }
    @CrossOrigin(origins = "*")
    @GetMapping("{deploymentId}")
    public ResponseEntity<?> getDeployment(@PathVariable String deploymentId){
        return appDeploymentProducerService.getSingleDeployment(deploymentId);
    }
}
