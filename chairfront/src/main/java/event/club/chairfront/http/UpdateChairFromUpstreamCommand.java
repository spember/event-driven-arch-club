package event.club.chairfront.http;

import java.util.UUID;

/**
 * An "Event that we react to like a command".
 */
public class UpdateChairFromUpstreamCommand {
    // once we get into the world of messages we should rename this file for semantic purposes
    private UUID id;
    private String sku;
    private String name;
    private String description;

    public UpdateChairFromUpstreamCommand(UUID id, String sku, String name, String description) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
