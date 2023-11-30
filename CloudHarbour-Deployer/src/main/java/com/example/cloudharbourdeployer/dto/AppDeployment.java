package com.example.cloudharbourdeployer.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@DynamoDBTable(tableName = "AppDeployments")
public class AppDeployment {
    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    String id;
    @DynamoDBAttribute
    String appName;
    @DynamoDBAttribute
    String dockerImage;
    @DynamoDBAttribute
    Integer internalPort;
    @DynamoDBAttribute
    Integer externalPort;
    @DynamoDBAttribute
    String url;
    @DynamoDBAttribute
    String status;
    @DynamoDBAttribute
    String request;
}
