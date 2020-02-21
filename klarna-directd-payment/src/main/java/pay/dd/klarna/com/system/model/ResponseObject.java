package pay.dd.klarna.com.system.model;

public class ResponseObject {
    private Boolean requiresAction;
    private String error;

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
}
