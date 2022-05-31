package event.club.admin.http;

public class CreateChairCommand {

    private String requestedSku;
    private String requestedName;
    private String requestedDescription;

    public CreateChairCommand(String requestedSku, String requestedName, String requestedDescription) {
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
