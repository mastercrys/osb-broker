package it.cristiano.services;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.AttachVolumeRequest;
import software.amazon.awssdk.services.ec2.model.AttachVolumeResponse;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.DetachVolumeRequest;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.RebootInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesRequest;
import software.amazon.awssdk.services.ec2.waiters.Ec2Waiter;

@ApplicationScoped
public class EC2Service {

        private static final Logger log = Logger.getLogger(EC2Service.class);

        @Inject
        Ec2Client ec2Client;

        public String createInstance(String amiId, String instanceType, int minCount, int maxCount) {
                try {
                        RunInstancesRequest runInstancesRequest = RunInstancesRequest.builder()
                                        .imageId(amiId)
                                        .instanceType(instanceType)
                                        .minCount(minCount)
                                        .maxCount(maxCount)
                                        .build();

                        RunInstancesResponse response = ec2Client.runInstances(runInstancesRequest);
                        if (response == null || !response.hasInstances()) {
                                return "";
                        }

                        String instanceId = response.instances().get(0).instanceId();

                        ec2Client.waiter().waitUntilInstanceRunning(r -> r.instanceIds(instanceId));
                        log.info("Successfully started EC2 Instance " + instanceId);

                        return instanceId;
                } catch (Ec2Exception e) {
                        log.error(e.awsErrorDetails().errorMessage());
                }
                return "";
        }

        public void terminateInstance(String instanceId) {
                Ec2Waiter ec2Waiter = Ec2Waiter.builder()
                                .overrideConfiguration(b -> b.maxAttempts(100))
                                .client(ec2Client)
                                .build();

                TerminateInstancesRequest terminateInstancesRequest = TerminateInstancesRequest.builder()
                                .instanceIds(instanceId)
                                .build();

                ec2Client.terminateInstances(terminateInstancesRequest);

                DescribeInstancesRequest instanceRequest = DescribeInstancesRequest.builder()
                                .instanceIds(instanceId)
                                .build();

                WaiterResponse<DescribeInstancesResponse> waiterResponse = ec2Waiter
                                .waitUntilInstanceTerminated(instanceRequest);
                waiterResponse.matched().response().ifPresent(System.out::println);

                log.info(instanceId + " is terminated!");
        }

        public void startInstance(String instanceId) {
                Ec2Waiter ec2Waiter = Ec2Waiter.builder()
                                .overrideConfiguration(b -> b.maxAttempts(100))
                                .client(ec2Client)
                                .build();

                StartInstancesRequest startInstancesRequest = StartInstancesRequest.builder()
                                .instanceIds(instanceId)
                                .build();

                ec2Client.startInstances(startInstancesRequest);

                DescribeInstancesRequest instanceRequest = DescribeInstancesRequest.builder()
                                .instanceIds(instanceId)
                                .build();

                WaiterResponse<DescribeInstancesResponse> waiterResponse = ec2Waiter
                                .waitUntilInstanceRunning(instanceRequest);
                waiterResponse.matched().response().ifPresent(System.out::println);
                log.info("Successfully started instance " + instanceId);
        }

        public void stopInstance(String instanceId) {
                Ec2Waiter ec2Waiter = Ec2Waiter.builder()
                                .overrideConfiguration(b -> b.maxAttempts(100))
                                .client(ec2Client)
                                .build();

                StopInstancesRequest stopInstancesRequest = StopInstancesRequest.builder()
                                .instanceIds(instanceId)
                                .build();

                ec2Client.stopInstances(stopInstancesRequest);

                DescribeInstancesRequest instanceRequest = DescribeInstancesRequest.builder()
                                .instanceIds(instanceId)
                                .build();

                WaiterResponse<DescribeInstancesResponse> waiterResponse = ec2Waiter
                                .waitUntilInstanceStopped(instanceRequest);
                waiterResponse.matched().response().ifPresent(System.out::println);
                log.info("Successfully stopped instance " + instanceId);
        }

        public void rebootInstance(String instanceId) {
                Ec2Waiter ec2Waiter = Ec2Waiter.builder()
                                .overrideConfiguration(b -> b.maxAttempts(100))
                                .client(ec2Client)
                                .build();

                RebootInstancesRequest rebootInstancesRequest = RebootInstancesRequest.builder()
                                .instanceIds(instanceId)
                                .build();

                ec2Client.rebootInstances(rebootInstancesRequest);

                DescribeInstancesRequest instanceRequest = DescribeInstancesRequest.builder()
                                .instanceIds(instanceId)
                                .build();

                WaiterResponse<DescribeInstancesResponse> waiterResponse = ec2Waiter
                                .waitUntilInstanceRunning(instanceRequest);
                waiterResponse.matched().response().ifPresent(System.out::println);
                log.info("Successfully rebooted instance " + instanceId);
        }

        public AttachVolumeResponse bindService(String instanceId, String volumeId, String deviceName) {
                AttachVolumeRequest attachVolumeRequest = AttachVolumeRequest.builder()
                                .instanceId(instanceId)
                                .volumeId(volumeId)
                                .device(deviceName)
                                .build();

                AttachVolumeResponse attachVolumeResponse = ec2Client.attachVolume(attachVolumeRequest);

                return attachVolumeResponse;
        }

        public void unbindService(String instanceId, String bindingId) {
                DetachVolumeRequest detachVolumeRequest = DetachVolumeRequest.builder()
                                .volumeId(bindingId)
                                .instanceId(instanceId)
                                .build();

                ec2Client.detachVolume(detachVolumeRequest);
        }
}
