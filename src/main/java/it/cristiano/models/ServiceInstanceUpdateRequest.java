package it.cristiano.models;

import java.util.Map;

public class ServiceInstanceUpdateRequest {
    public enum OperationType {
        start("start"),
        stop("stop"),
        reboot("reboot");

        private final String formatted;

        public String getFormatted() {
            return formatted;
        }

        OperationType(String formatted) {
            this.formatted = formatted;
        }
    }

    private Object context;
    private String serviceId;
    private String planId;
    private Map<String, Object> parameters;
    private PreviousValues previousValues;
    private MaintenanceInfo maintenanceInfo;

    // Getters and Setters

    public Object getContext() {
        return context;
    }

    public void setContext(Object context) {
        this.context = context;
    }

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

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public PreviousValues getPreviousValues() {
        return previousValues;
    }

    public void setPreviousValues(PreviousValues previousValues) {
        this.previousValues = previousValues;
    }

    public MaintenanceInfo getMaintenanceInfo() {
        return maintenanceInfo;
    }

    public void setMaintenanceInfo(MaintenanceInfo maintenanceInfo) {
        this.maintenanceInfo = maintenanceInfo;
    }

    // Nested class for PreviousValues
    public static class PreviousValues {
        private String serviceId;
        private String planId;
        private String organizationId;
        private String spaceId;
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

        public String getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(String organizationId) {
            this.organizationId = organizationId;
        }

        public String getSpaceId() {
            return spaceId;
        }

        public void setSpaceId(String spaceId) {
            this.spaceId = spaceId;
        }

        public MaintenanceInfo getMaintenanceInfo() {
            return maintenanceInfo;
        }

        public void setMaintenanceInfo(MaintenanceInfo maintenanceInfo) {
            this.maintenanceInfo = maintenanceInfo;
        }
    }

    // Nested class for MaintenanceInfo
    public static class MaintenanceInfo {
        private String version;
        private String description;

        // Getters and Setters

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
