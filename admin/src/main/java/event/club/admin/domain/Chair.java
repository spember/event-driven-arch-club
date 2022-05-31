package event.club.admin.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

/**
 * Basic Domain Entity for our products. Represents a high-level 'type' of chair that we sell (as opposed to individual
 * instances of a chair)
 */
@Entity
public class Chair {

    @Id
    private UUID id;
    private int version = 0;

    // in real life we should create a SKU class instead of a string to control logic (e.g. length, format)
    private String sku = "";
    private String name = "";

    private String description = "";
    
    protected Chair() {}

    public Chair(int version, String sku, String name, String description) {
        // we generate the ids 'by hand' here, as we want to alert callers to the future id that *will* exist
        // after an async save occurs. In real life we might have a 'ticket' system or other id generator
        this.id = UUID.randomUUID();
        this.version = version;
        this.sku = sku;
        this.name = name;
        this.description = description;
    }

    public UUID getId() {
        return id;
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
