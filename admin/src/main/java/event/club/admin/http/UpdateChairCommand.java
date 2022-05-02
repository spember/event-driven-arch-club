package event.club.admin.http;

public class UpdateChairCommand {

    private String requestedSku;
    private String requestedName;
    private String requestedDescription;

    public UpdateChairCommand(String requestedSku, String requestedName, String requestedDescription) {
        this.requestedSku = requestedSku;
        this.requestedName = requestedName;
        this.requestedDescription = requestedDescription;
    }

    public String getRequestedSku() {
        return requestedSku;
    }

    public String getRequestedDescription() {
        return requestedDescription;
    }

    public String getRequestedName() {
        return requestedName;
    }
}
