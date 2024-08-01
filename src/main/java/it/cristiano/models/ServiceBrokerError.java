package it.cristiano.models;

public class ServiceBrokerError {
    private String error;

    private String decription;

    private boolean instance_usable;

    private boolean update_repeatable;

    public ServiceBrokerError(String error, String decription) {
        this.error = error;
        this.decription = decription;
    }

    public ServiceBrokerError(String error, String decription, boolean instance_usable) {
        this.error = error;
        this.decription = decription;
        this.instance_usable = instance_usable;
    }

    public ServiceBrokerError(String error, String decription, boolean instance_usable, boolean update_repeatable) {
        this.error = error;
        this.decription = decription;
        this.instance_usable = instance_usable;
        this.update_repeatable = update_repeatable;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDecription() {
        return decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    public boolean isInstance_usable() {
        return instance_usable;
    }

    public void setInstance_usable(boolean instance_usable) {
        this.instance_usable = instance_usable;
    }

    public boolean isUpdate_repeatable() {
        return update_repeatable;
    }

    public void setUpdate_repeatable(boolean update_repeatable) {
        this.update_repeatable = update_repeatable;
    }

}
