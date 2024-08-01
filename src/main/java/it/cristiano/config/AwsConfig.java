package it.cristiano.config;

import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class AwsConfig {

    @Inject
    @ConfigProperty(name = "aws.accessKeyId")
    String accessKeyId;

    @Inject
    @ConfigProperty(name = "aws.secretAccessKey")
    String secretAccessKey;

    @Inject
    @ConfigProperty(name = "aws.region")
    String region;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    @Produces
    @ApplicationScoped
    public Ec2Client ec2Client() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        return Ec2Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
}