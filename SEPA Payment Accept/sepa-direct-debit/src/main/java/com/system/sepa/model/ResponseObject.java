package com.system.sepa.model;

public class ResponseObject {
    private String clientSecret;
    private String paymentIntentId;
    private Boolean requiresAction;
    private String error;
    private double captureAmount;
    private String paymentMethodId;

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public Boolean getRequiresAction() {
        return requiresAction;
    }

    public void setRequiresAction(Boolean requiresAction) {
        this.requiresAction = requiresAction;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public double getCaptureAmount() {
        return captureAmount;
    }

    public void setCaptureAmount(double captureAmount) {
        this.captureAmount = captureAmount;
    }
}
