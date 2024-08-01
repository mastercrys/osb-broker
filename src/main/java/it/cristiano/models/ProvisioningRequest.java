package it.cristiano.models;

import java.util.Map;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ProvisioningRequest {
    // Body Fields
    @NotEmpty(message = "Service ID must not be empty")
    private String serviceId;

    @NotEmpty(message = "Plan ID must not be empty")
    private String planId;

    @NotNull(message = "Context cannot be null")
    private Map<String, Object> context;

    @NotEmpty(message = "Organization GUID must not be empty")
    private String organizationGuid;

    @NotEmpty(message = "Space GUID must not be empty")
    private String spaceGuid;

    private Map<String, Object> parameters;

    private MaintenanceInfo maintenanceInfo;

    // Getters and Setters
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public String getOrganizationGuid() {
        return organizationGuid;
    }

    public void setOrganizationGuid(String organizationGuid) {
        this.organizationGuid = organizationGuid;
    }

    public String getSpaceGuid() {
        return spaceGuid;
    }

    public void setSpaceGuid(String spaceGuid) {
        this.spaceGuid = spaceGuid;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public MaintenanceInfo getMaintenanceInfo() {
        return maintenanceInfo;
    }

    public void setMaintenanceInfo(MaintenanceInfo maintenanceInfo) {
        this.maintenanceInfo = maintenanceInfo;
    }

    // Nested class for MaintenanceInfo
    public static class MaintenanceInfo {
        private String version;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
