package com.example.cloudharbourdeployer.repositories;

import com.example.cloudharbourdeployer.dto.AppDeployment;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableScan
public interface AppDeploymentRepositories extends CrudRepository<AppDeployment, String> {
    Optional<AppDeployment> findAppDeploymentById(String id);
    Optional<AppDeployment> findAppDeploymentByAppName(String appName);
    boolean existsAppDeploymentByAppName(String appName);
}
