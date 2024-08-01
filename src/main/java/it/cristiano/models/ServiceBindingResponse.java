package it.cristiano.models;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ServiceBindingResponse {
    private BindingMetadata metadata;
    private Map<String, Object> credentials;
    private String syslogDrainUrl;
    private String routeServiceUrl;
    private List<VolumeMount> volumeMounts;
    private List<Endpoint> endpoints;

    // Getters and Setters

    public BindingMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(BindingMetadata metadata) {
        this.metadata = metadata;
    }

    public Map<String, Object> getCredentials() {
        return credentials;
    }

    public void setCredentials(Map<String, Object> credentials) {
        this.credentials = credentials;
    }

    public String getSyslogDrainUrl() {
        return syslogDrainUrl;
    }

    public void setSyslogDrainUrl(String syslogDrainUrl) {
        this.syslogDrainUrl = syslogDrainUrl;
    }

    public String getRouteServiceUrl() {
        return routeServiceUrl;
    }

    public void setRouteServiceUrl(String routeServiceUrl) {
        this.routeServiceUrl = routeServiceUrl;
    }

    public List<VolumeMount> getVolumeMounts() {
        return volumeMounts;
    }

    public void setVolumeMounts(List<VolumeMount> volumeMounts) {
        this.volumeMounts = volumeMounts;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    // Nested class for BindingMetadata
    public static class BindingMetadata {
        private String expiresAt;
        private String renewBefore;

        // Getters and Setters

        public String getExpiresAt() {
            return expiresAt;
        }

        public void setExpiresAt(String expiresAt) {
            this.expiresAt = expiresAt;
        }

        public String getRenewBefore() {
            return renewBefore;
        }

        public void setRenewBefore(String renewBefore) {
            this.renewBefore = renewBefore;
        }
    }

    // Nested class for VolumeMount
    public static class VolumeMount {
        @NotEmpty(message = "Driver must not be empty")
        private String driver;

        @NotEmpty(message = "Container directory must not be empty")
        private String containerDir;

        @NotEmpty(message = "Mode must not be empty")
        private String mode;

        @NotEmpty(message = "Device type must not be empty")
        private String deviceType;

        @NotNull(message = "Device must not be null")
        private Device device;

        // Getters and Setters

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getContainerDir() {
            return containerDir;
        }

        public void setContainerDir(String containerDir) {
            this.containerDir = containerDir;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public Device getDevice() {
            return device;
        }

        public void setDevice(Device device) {
            this.device = device;
        }

        // Nested class for Device
        public static class Device {
            @NotEmpty(message = "Volume ID must not be empty")
            private String volumeId;

            private Map<String, Object> mountConfig;

            // Getters and Setters

            public String getVolumeId() {
                return volumeId;
            }

            public void setVolumeId(String volumeId) {
                this.volumeId = volumeId;
            }

            public Map<String, Object> getMountConfig() {
                return mountConfig;
            }

            public void setMountConfig(Map<String, Object> mountConfig) {
                this.mountConfig = mountConfig;
            }
        }
    }

    // Nested class for Endpoint
    public static class Endpoint {
        @NotEmpty(message = "Host must not be empty")
        private String host;

        @NotNull(message = "Ports must not be null")
        private List<String> ports;

        private String protocol = "tcp";

        // Getters and Setters

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public List<String> getPorts() {
            return ports;
        }

        public void setPorts(List<String> ports) {
            this.ports = ports;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }
    }
}
