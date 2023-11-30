package com.example.cloudharbourdeploymentproducer.repositories;

import com.example.cloudharbourdeploymentproducer.dto.AppDeployment;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@EnableScan
public interface AppDeploymentRepositories extends CrudRepository<AppDeployment, String> {
    Optional<AppDeployment> findAppDeploymentById(String id);
    Optional<AppDeployment> findAppDeploymentByAppName(String appName);
    boolean existsAppDeploymentByAppName(String appName);
}
