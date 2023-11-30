package com.example.cloudharbourdeployer;

import com.example.cloudharbourdeployer.repositories.AppDeploymentRepositories;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example"})
public class CloudHarbourDeployerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudHarbourDeployerApplication.class, args);
    }

//    @SqsListener("deployment-queue.fifo")
//    public void listen(String message) {
//        System.out.println(message);
//    }

}
