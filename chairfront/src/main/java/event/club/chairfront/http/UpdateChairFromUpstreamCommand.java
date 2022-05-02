package event.club.chairfront.http;

/**
 * An "Event that we react to like a command".
 */
public class UpdateChairFromUpstreamCommand {
    // once we get into the world of messages we should rename this file for semantic purposes
    private String requestedSku;
    private String requestedName;
    private String requestedDescription;

    public UpdateChairFromUpstreamCommand(String requestedSku, String requestedName, String requestedDescription) {
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
