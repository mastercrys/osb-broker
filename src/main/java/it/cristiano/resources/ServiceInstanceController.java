package it.cristiano.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.quarkus.security.Authenticated;
import it.cristiano.filter.OSBHeader;
import it.cristiano.models.ProvisioningRequest;
import it.cristiano.models.ProvisioningUpdateResponse;
import it.cristiano.models.ServiceBindingRequest;
import it.cristiano.models.ServiceBindingResponse;
import it.cristiano.models.ServiceBrokerError;
import it.cristiano.models.ServiceInstanceUpdateRequest;
import it.cristiano.models.ServiceInstanceUpdateRequest.OperationType;
import it.cristiano.services.EC2Service;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import software.amazon.awssdk.services.ec2.model.AttachVolumeResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;

@Path("/v2/service_instances")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@OSBHeader
@Authenticated
public class ServiceInstanceController {

    @Inject
    EC2Service ec2Service;

    @PUT
    @Path("/{instance_id}")
    public Response provisionInstance(@PathParam("instance_id") String instanceId,
            @QueryParam("accepts_incomplete") Boolean accepts_incomplete,
            ProvisioningRequest provisioningRequest) {

        if (accepts_incomplete == null) {
            return Response.status(422)
                    .entity(new ServiceBrokerError("AsyncRequired",
                            "This Service Plan requires client support for asynchronous service operations."))
                    .build();
        }

        String amiId;
        String instanceType;
        Integer minCount;
        Integer maxCount;

        try {
            Map<String, Object> parameters = provisioningRequest.getParameters();
            amiId = (String) parameters.get("amiId");
            instanceType = (String) parameters.get("instanceType");
            minCount = (Integer) parameters.get("minCount");
            maxCount = (Integer) parameters.get("maxCount");

            if (amiId.isBlank() || amiId.isEmpty() || instanceType.isBlank() || instanceType.isEmpty()
                    || minCount == null || maxCount == null) {
                throw new Exception("Invalid parameters. Please specify: amiId, instanceType, minCount, maxCount");
            }
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(new ServiceBrokerError("PROVISIONING-ERROR", e.getMessage())).build();
        }

        try {
            String ec2InstanceId = ec2Service.createInstance(amiId, instanceType, minCount, maxCount);

            ProvisioningUpdateResponse provisioningUpdateResponse = new ProvisioningUpdateResponse();
            provisioningUpdateResponse.setDashboardUrl(ec2InstanceId);
            if (accepts_incomplete) {
                provisioningUpdateResponse.setOperation("test-operation");
            }
            return Response.status(Response.Status.CREATED).entity(provisioningUpdateResponse).build();
        } catch (Ec2Exception ec2e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(new ServiceBrokerError("PROVISIONING-ERROR", ec2e.awsErrorDetails().errorMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{instance_id}")
    public Response deprovisionInstance(@PathParam("instance_id") String instanceId,
            @QueryParam("service_id") String serviceId,
            @QueryParam("plan_id") String planId,
            @QueryParam("accepts_incomplete") Boolean accepts_incomplete) {

        if (accepts_incomplete == null) {
            return Response.status(422)
                    .entity(new ServiceBrokerError("AsyncRequired",
                            "This Service Plan requires client support for asynchronous service operations."))
                    .build();
        }
        try {
            ec2Service.terminateInstance(instanceId);

            if (accepts_incomplete) {
                return Response.status(Status.ACCEPTED).build();
            }
            return Response.ok().build();
        } catch (Ec2Exception e) {
            return Response.status(Status.GONE)
                    .entity(new ServiceBrokerError("DEPROVISIONING-ERROR", e.awsErrorDetails().errorMessage(), true))
                    .build();
        }
    }

    @PATCH
    @Path("/{instance_id}")
    public Response updateInstance(@PathParam("instance_id") String instanceId,
            @QueryParam("accepts_incomplete") Boolean accepts_incomplete,
            ServiceInstanceUpdateRequest serviceInstanceUpdateRequest) {

        if (accepts_incomplete == null) {
            return Response.status(422)
                    .entity(new ServiceBrokerError("AsyncRequired",
                            "This Service Plan requires client support for asynchronous service operations."))
                    .build();
        }

        OperationType operation;
        try {
            Map<String, Object> parameters = serviceInstanceUpdateRequest.getParameters();
            operation = OperationType.valueOf(parameters.get("operation").toString());
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(new ServiceBrokerError("BINDING-ERROR", e.getMessage())).build();
        }
        try {
            if (OperationType.start.equals(operation)) {
                ec2Service.startInstance(instanceId);
            } else if (OperationType.stop.equals(operation)) {
                ec2Service.stopInstance(instanceId);
            } else if (OperationType.reboot.equals(operation)) {
                ec2Service.rebootInstance(instanceId);
            }

            ProvisioningUpdateResponse provisioningUpdateResponse = new ProvisioningUpdateResponse();
            provisioningUpdateResponse.setDashboardUrl(instanceId);

            return Response.ok().entity(provisioningUpdateResponse).build();
        } catch (Ec2Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(new ServiceBrokerError("UPDATE-INSTANCE-ERROR", e.awsErrorDetails().errorMessage(), true,
                            false))
                    .build();
        }
    }

    @PUT
    @Path("/{instance_id}/service_bindings/{binding_id}")
    public Response bindService(@PathParam("instance_id") String instanceId,
            @PathParam("binding_id") String bindingId,
            @QueryParam("accepts_incomplete") Boolean accepts_incomplete,
            ServiceBindingRequest serviceBindingRequest) {

        if (accepts_incomplete == null) {
            return Response.status(422)
                    .entity(new ServiceBrokerError("AsyncRequired",
                            "This Service Plan requires client support for asynchronous service operations."))
                    .build();
        }
        String volumeId;
        String deviceName;

        try {
            Map<String, Object> parameters = serviceBindingRequest.getParameters();
            volumeId = (String) parameters.get("volumeId");
            deviceName = (String) parameters.get("deviceName");

            if (volumeId.isBlank() || volumeId.isEmpty() || deviceName.isBlank() || deviceName.isEmpty()) {
                throw new Exception("Invalid parameters. Please specify: volumeId, deviceName");
            }
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(new ServiceBrokerError("BINDING-ERROR", e.getMessage())).build();
        }
        AttachVolumeResponse attachVolumeResponse = ec2Service.bindService(instanceId, volumeId, deviceName);

        ServiceBindingResponse serviceBindingResponse = new ServiceBindingResponse();
        List<ServiceBindingResponse.VolumeMount> volumeMounts = new ArrayList<>();
        ServiceBindingResponse.VolumeMount volumeMount = new ServiceBindingResponse.VolumeMount();
        ServiceBindingResponse.VolumeMount.Device device = new ServiceBindingResponse.VolumeMount.Device();
        device.setVolumeId(attachVolumeResponse.volumeId());
        volumeMount.setDevice(device);
        volumeMounts.add(volumeMount);

        serviceBindingResponse.setVolumeMounts(volumeMounts);

        return Response.status(Status.CREATED).entity(serviceBindingResponse).build();
    }

    @DELETE
    @Path("/{instance_id}/service_bindings/{binding_id}")
    public Response unbindService(@PathParam("instance_id") String instanceId,
            @PathParam("binding_id") String bindingId, @QueryParam("service_id") String serviceId,
            @QueryParam("plan_id") String planId,
            @QueryParam("accepts_incomplete") Boolean accepts_incomplete) {

        if (accepts_incomplete == null) {
            return Response.status(422)
                    .entity(new ServiceBrokerError("AsyncRequired",
                            "This Service Plan requires client support for asynchronous service operations."))
                    .build();
        }
        ec2Service.unbindService(instanceId, bindingId);
        return Response.ok().build();
    }
}
