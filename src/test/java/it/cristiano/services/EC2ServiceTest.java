package it.cristiano.services;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.AttachVolumeRequest;
import software.amazon.awssdk.services.ec2.model.AttachVolumeResponse;
import software.amazon.awssdk.services.ec2.model.DetachVolumeRequest;
import software.amazon.awssdk.services.ec2.model.DetachVolumeResponse;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesRequest;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesResponse;

@QuarkusTest
public class EC2ServiceTest {

    @Inject
    EC2Service ec2Service;

    @Inject
    Ec2Client ec2Client;

    @Inject
    @ConfigProperty(name = "broker.tests.volumeId")
    String volumeId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ec2Client = Mockito.mock(Ec2Client.class);
    }

    @Test
    void testAll() {
        String instanceId = testCreateInstance();
        testBindService(instanceId);
        testUnbindService(instanceId);
        testTerminateInstance(instanceId);
    }

    private String testCreateInstance() {
        RunInstancesResponse response = RunInstancesResponse.builder().build();
        when(ec2Client.runInstances(any(RunInstancesRequest.class))).thenReturn(response);

        String instanceId = ec2Service.createInstance("ami-05842291b9a0bd79f", "t2.micro", 1, 1);
        assertNotNull(instanceId);
        assertNotEquals("", instanceId);

        return instanceId;
    }

    private void testTerminateInstance(String instanceId) {
        TerminateInstancesResponse response = TerminateInstancesResponse.builder().build();
        when(ec2Client.terminateInstances(any(TerminateInstancesRequest.class))).thenReturn(response);

        ec2Service.terminateInstance(instanceId);

    }

    private void testBindService(String instanceId) {
        AttachVolumeResponse response = AttachVolumeResponse.builder().build();
        when(ec2Client.attachVolume(any(AttachVolumeRequest.class))).thenReturn(response);

        AttachVolumeResponse result = ec2Service.bindService(instanceId, volumeId, "/dev/sdf");
        assertNotNull(result);
    }

    private void testUnbindService(String instanceId) {
        DetachVolumeResponse response = DetachVolumeResponse.builder().build();
        when(ec2Client.detachVolume(any(DetachVolumeRequest.class))).thenReturn(response);

        ec2Service.unbindService(instanceId, volumeId);
    }
}
