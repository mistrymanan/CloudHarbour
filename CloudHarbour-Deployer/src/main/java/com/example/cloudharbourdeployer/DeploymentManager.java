package com.example.cloudharbourdeployer;

import com.example.cloudharbourdeployer.dto.AppDeployment;
import com.example.cloudharbourdeployer.dto.DeploymentRequest;
import com.example.cloudharbourdeployer.repositories.AppDeploymentRepositories;
import com.google.gson.JsonSyntaxException;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class DeploymentManager {
    @Autowired
    AppDeploymentRepositories appDeploymentRepositories;
    CoreV1Api coreV1Api;

    public DeploymentManager() {
        ApiClient client = null;
        try {
            client = Config.defaultClient();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Configuration.setDefaultApiClient(client);
        this.coreV1Api = new CoreV1Api();
    }

    public void processDeploymentMessage(DeploymentRequest deploymentRequest) {
//        System.out.println("Exist By AppName=>"+appDeploymentRepositories.existsAppDeploymentByAppName(deploymentInfo.getAppName()));
//        appDeploymentRepositories.save(deploymentInfo);
        if(appDeploymentRepositories.existsById(deploymentRequest.getId())){
            Optional<AppDeployment> deploymentInfo = appDeploymentRepositories.findAppDeploymentById(deploymentRequest.getId());
                if(deploymentInfo.isPresent() && !deploymentInfo.get().getStatus().equals("Deployed") && deploymentRequest.equals("CREATE")){
                    createApp(coreV1Api,deploymentInfo.get());
                }else if(deploymentInfo.isPresent() && deploymentRequest.getRequest().equals("DELETE")){
                    removeDeployment(coreV1Api,deploymentInfo.get());
                }
        }

    }

    private void createApp(CoreV1Api coreV1Api,AppDeployment deploymentInfo){
        if (!checkNamespaceExist(coreV1Api, deploymentInfo.getAppName())) {
            createNamespace(coreV1Api, deploymentInfo.getAppName());
        }

        if (!checkPodExist(coreV1Api, deploymentInfo.getAppName())) {
            addPod(coreV1Api, deploymentInfo);
        }

        if (!checkServiceExist(coreV1Api, deploymentInfo.getAppName())) {
            addService(coreV1Api, deploymentInfo);
            String loadBalancerIp = getServiceLoadBalancerURL(coreV1Api,deploymentInfo.getAppName());
            System.out.println("LoadBalancerIp->"+loadBalancerIp);
            deploymentInfo.setUrl(loadBalancerIp);
            deploymentInfo.setStatus("Deployed");
            appDeploymentRepositories.save(deploymentInfo);
        }
    }
    public void removeDeployment(CoreV1Api coreV1Api, AppDeployment deployment) {
        deleteService(coreV1Api, deployment);
        deletePod(coreV1Api, deployment);
        deleteNamespace(coreV1Api, deployment);
    }
    private void createNamespace(CoreV1Api coreV1Api, String namespace) {
        V1Namespace v1Namespace = new V1Namespace();
        v1Namespace.setMetadata(new V1ObjectMeta().name(namespace));

        try {
            coreV1Api.createNamespace(v1Namespace, null, null, null);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Namespace created: " + namespace);
    }
    private boolean checkNamespaceExist(CoreV1Api api,String namespace){
        V1Namespace v1Namespace = null;
        try {
            v1Namespace = api.readNamespace(namespace,null,true,null);
        } catch (ApiException e) {
            if(e.getMessage().contains("Not Found")){
                return false;
            }
            throw new RuntimeException(e);
        }
        System.out.println(v1Namespace.getStatus());
        return true;
    }
    private boolean deleteNamespace(CoreV1Api api,AppDeployment deployment){
        try {
            api.deleteNamespace(deployment.getAppName(),null,null,null,null,null,new V1DeleteOptions());
            return true;
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        catch (JsonSyntaxException e){
            System.out.println("e.getMessage()"+e.getMessage());
            if(e.getMessage().contains("Expected a string but was BEGIN_OBJECT")){
                return true;
            }
            return false;
        }
    }
    private boolean deleteService(CoreV1Api api,AppDeployment deployment){
        try {
            api.deleteNamespacedService(deployment.getAppName(), deployment.getAppName(), null,null,null,null,null,new V1DeleteOptions());
            return true;
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        catch (JsonSyntaxException e){
            System.out.println("e.getMessage()"+e.getMessage());
            if(e.getMessage().contains("Expected a string but was BEGIN_OBJECT")){
                return true;
            }
            return false;
        }
    }
    private boolean deletePod(CoreV1Api api,AppDeployment deployment){
        try {
            api.deleteNamespacedPod(deployment.getAppName(), deployment.getAppName(), null,null,null,null,null,new V1DeleteOptions());
            return true;
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        catch (JsonSyntaxException e){
            System.out.println("e.getMessage()"+e.getMessage());
            if(e.getMessage().contains("Expected a string but was BEGIN_OBJECT")){
                return true;
            }
            return false;
        }
    }
    private void addPod(CoreV1Api api,AppDeployment appDeployment){
        List<V1ContainerPort> containerPortList = new LinkedList<>();
        containerPortList.add(new V1ContainerPort().containerPort(appDeployment.getInternalPort()));

        V1Container container = new V1Container()
                .name(appDeployment.getAppName())
                .image(appDeployment.getDockerImage())
                .ports(containerPortList);

        V1Pod pod = new V1Pod()
                .metadata(new V1ObjectMeta()
                        .name(appDeployment.getAppName())
                        .labels(java.util.Collections.singletonMap("app", appDeployment.getAppName())))
                .spec(new V1PodSpec()
                        .containers(java.util.Collections.singletonList(container)));

        try {
            V1Pod createdPod = api.createNamespacedPod(appDeployment.getAppName(), pod, null, null, null);
            System.out.println(createdPod.getStatus());
            System.out.println(createdPod);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

    }
    private void addService(CoreV1Api api,AppDeployment appDeployment) {
        List<V1ServicePort> v1ServicePorts = new LinkedList<>();
        v1ServicePorts.add(new V1ServicePort().name("http").port(appDeployment.getExternalPort()).targetPort(new IntOrString(appDeployment.getInternalPort())));

        V1Service service = new V1Service()
                .metadata(new V1ObjectMeta().name(appDeployment.getAppName()).namespace(appDeployment.getAppName()))
                .spec(new V1ServiceSpec()
                        .selector(java.util.Collections.singletonMap("app", appDeployment.getAppName()))
                        .type("LoadBalancer")
                        .ports(v1ServicePorts));
        System.out.println("service->"+service.getMetadata());
        System.out.println("service->"+service.getSpec());
        try {
            V1Service createdService = api.createNamespacedService(appDeployment.getAppName(), service, null, null, null);
            System.out.println("CreatedService->"+createdService);
            System.out.println(createdService);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

    }
    private boolean checkPodExist(CoreV1Api api,String appName){
        V1Pod v1Pod = null;
        try {
            v1Pod = api.readNamespacedPod(appName,appName,null,null,null);
            System.out.println(v1Pod.getStatus().getPhase());
        } catch (ApiException e) {
            if(e.getMessage().contains("Not Found")){
                return false;
            }
            throw new RuntimeException(e);
        }
        System.out.println(v1Pod.getStatus());
        return true;
    }
    private boolean checkServiceExist(CoreV1Api api,String appName){
        V1Service v1Service = null;
        try {
            v1Service = api.readNamespacedService(appName,appName,null,null,null);
        } catch (ApiException e) {
            if(e.getMessage().contains("Not Found")){
                return false;
            }
            throw new RuntimeException(e);
        }
        System.out.println(v1Service.getStatus());
        return true;
    }
    private String getServiceLoadBalancerURL(CoreV1Api api,String appName){
        V1Service v1Service = null;
        try {
            v1Service = api.readNamespacedService(appName,appName,null,null,null);
            System.out.println("v1Service->");
            System.out.println(v1Service);
            System.out.println("v1Status->");
            System.out.println(v1Service.getStatus());
            return v1Service.getSpec().getLoadBalancerIP();
        } catch (ApiException e) {
            if(e.getMessage().contains("Not Found")){
//                return null;
            }
            throw new RuntimeException(e);
        }
//        return true;
    }
}
