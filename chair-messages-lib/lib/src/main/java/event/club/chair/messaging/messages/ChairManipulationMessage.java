package event.club.chair.messaging.messages;

import java.util.UUID;

/**
 * Contains the common fields and methods used by the various Chair entity messages
 */
class ChairManipulationMessage implements DomainMessage {

    protected UUID id;
    protected int version;
    protected String sku;
    protected String name;
    protected String description;

    protected ChairManipulationMessage() {}

    protected ChairManipulationMessage(UUID id, int version, String sku, String name, String description) {
        this.id = id;
        this.version = version;
        this.sku = sku;
        this.name = name;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
