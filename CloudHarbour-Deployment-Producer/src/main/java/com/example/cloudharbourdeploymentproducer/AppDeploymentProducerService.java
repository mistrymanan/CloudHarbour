package com.example.cloudharbourdeploymentproducer;

import com.example.cloudharbourdeploymentproducer.dto.AppDeployment;
import com.example.cloudharbourdeploymentproducer.dto.DeploymentRequest;
import com.example.cloudharbourdeploymentproducer.repositories.AppDeploymentRepositories;
import com.google.gson.Gson;
import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AppDeploymentProducerService {

    @Autowired
    SqsTemplate sqsTemplate;

    @Autowired
    AppDeploymentRepositories appDeploymentRepositories;

    public ResponseEntity<?> createAppDeployment(AppDeployment appDeployment){
        appDeployment.setStatus("InProgress");
        if(!appDeploymentRepositories.existsAppDeploymentByAppName(appDeployment.getAppName())){
            AppDeployment appDeployment1 = appDeploymentRepositories.save(appDeployment);
            DeploymentRequest deploymentRequest = new DeploymentRequest(appDeployment1.getId(),"CREATE");
            messageProducer(deploymentRequest);
            return ResponseEntity.ok("App Scheduled To Deploy");
        }
        return ResponseEntity.badRequest().body("App Already Exists, Please Change the name!");
    }
    public void messageProducer(DeploymentRequest deploymentRequest){
        Gson gson = new Gson();
        String jsonString = gson.toJson(deploymentRequest);
        UUID uuid = UUID.randomUUID();
        SendResult<String> result = sqsTemplate.send(to -> to
                .payload(jsonString)
                .header("message-group-id", "1")
                .header("message-deduplication-id", uuid)
        );
        System.out.println("Result From Queue->"+result);
    }
}
