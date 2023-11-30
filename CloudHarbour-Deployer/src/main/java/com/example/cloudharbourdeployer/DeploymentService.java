package com.example.cloudharbourdeployer;
import com.example.cloudharbourdeployer.dto.DeploymentRequest;
import com.google.gson.Gson;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeploymentService {

    @Autowired
    DeploymentManager deploymentManager;

    Gson gson = new Gson();
    @SqsListener("deployment-queue.fifo")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
        DeploymentRequest deploymentRequest = gson.fromJson(message, DeploymentRequest.class);
        deploymentManager.processDeploymentMessage(deploymentRequest);
    }
}
