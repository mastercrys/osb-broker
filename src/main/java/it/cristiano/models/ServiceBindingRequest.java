package it.cristiano.models;

import java.util.Map;

import jakarta.validation.constraints.NotEmpty;

public class ServiceBindingRequest {
    private Map<String, Object> context;

    @NotEmpty(message = "Service ID must not be empty")
    private String serviceId;

    @NotEmpty(message = "Plan ID must not be empty")
    private String planId;

    private String appGuid;

    private BindResource bindResource;

    private Map<String, Object> parameters;

    // Getters and Setters

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
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

    public String getAppGuid() {
        return appGuid;
    }

    public void setAppGuid(String appGuid) {
        this.appGuid = appGuid;
    }

    public BindResource getBindResource() {
        return bindResource;
    }

    public void setBindResource(BindResource bindResource) {
        this.bindResource = bindResource;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    // Nested class for BindResource
    public static class BindResource {
        private String appGuid;
        private String route;

        // Getters and Setters

        public String getAppGuid() {
            return appGuid;
        }

        public void setAppGuid(String appGuid) {
            this.appGuid = appGuid;
        }

        public String getRoute() {
            return route;
        }

        public void setRoute(String route) {
            this.route = route;
        }
    }

}
