package org.example;

import com.google.gson.JsonSyntaxException;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        getClusterConfig();
    }
    public static void getClusterConfig(){
        try {
            String APP_NAME = "nginix";

            ApiClient client = Config.defaultClient();
            Configuration.setDefaultApiClient(client);
            CoreV1Api api = new CoreV1Api();

//            createNamespace(api, APP_NAME);

            if(checkNamespaceExist(api,APP_NAME.toLowerCase())){
//                addPod(api,APP_NAME,"nginx:latest");
//                addService(api,APP_NAME);
                System.out.println("Check Pod Exist :"+checkPodExist(api,APP_NAME));
                System.out.println("Check Service Exist :"+checkServiceExist(api,APP_NAME.toLowerCase()));
            }

//            boolean result = deleteNamespace(api,APP_NAME);
//            System.out.println(result);
//            api.createNamespace(v1Namespace,null,null,null,null);
//            V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
//            for (V1Pod item : list.getItems()) {
//                System.out.println(item.getMetadata().getName();
//            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private static void createNamespace(CoreV1Api coreV1Api, String namespace) {
        V1Namespace v1Namespace = new V1Namespace();
        v1Namespace.setMetadata(new V1ObjectMeta().name(namespace));

        try {
            coreV1Api.createNamespace(v1Namespace, null, null, null);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Namespace created: " + namespace);
    }
    private static boolean checkNamespaceExist(CoreV1Api api,String namespace){
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
    private static boolean deleteNamespace(CoreV1Api api,String namespace){
        try {
            api.deleteNamespace(namespace,null,null,null,null,null,new V1DeleteOptions());
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
    private static void addPod(CoreV1Api api,String appName,String imageName){
        List<V1ContainerPort> containerPortList = new LinkedList<>();
        containerPortList.add(new V1ContainerPort().containerPort(80));
        containerPortList.add(new V1ContainerPort().containerPort(8080));

        V1Container container = new V1Container()
                .name(appName)
                .image(imageName)
                .ports(containerPortList);

        V1Pod pod = new V1Pod()
                .metadata(new V1ObjectMeta()
                        .name(appName)
                        .labels(java.util.Collections.singletonMap("app", appName)))
                .spec(new V1PodSpec()
                        .containers(java.util.Collections.singletonList(container)));

        try {
            V1Pod createdPod = api.createNamespacedPod(appName, pod, null, null, null);
            System.out.println(createdPod.getStatus());
            System.out.println(createdPod);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

    }
    private static void addService(CoreV1Api api,String appName) {
        List<V1ServicePort> v1ServicePorts = new LinkedList<>();
        v1ServicePorts.add(new V1ServicePort().name("http").port(80).targetPort(new IntOrString(80)));
        v1ServicePorts.add(new V1ServicePort().name("http-alt").port(8080).targetPort(new IntOrString(8080)));

        V1Service service = new V1Service()
                .metadata(new V1ObjectMeta().name(appName).namespace(appName))
                .spec(new V1ServiceSpec()
                        .selector(java.util.Collections.singletonMap("app", appName))
                        .type("LoadBalancer")
                        .ports(v1ServicePorts));
        System.out.println("service->"+service.getMetadata());
        System.out.println("service->"+service.getSpec());
        try {
            V1Service createdService = api.createNamespacedService(appName, service, null, null, null);
            System.out.println("CreatedService->"+createdService);
            System.out.println(createdService);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

    }
    private static boolean checkPodExist(CoreV1Api api,String appName){
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
    private static boolean checkServiceExist(CoreV1Api api,String appName){
        V1Service v1Service = null;
        try {
            v1Service = api.readNamespacedService(appName+"-service",appName,null,null,null);
        } catch (ApiException e) {
            if(e.getMessage().contains("Not Found")){
                return false;
            }
            throw new RuntimeException(e);
        }
        System.out.println(v1Service.getStatus());
        return true;
    }
}