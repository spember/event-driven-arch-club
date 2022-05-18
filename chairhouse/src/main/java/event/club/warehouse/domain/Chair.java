package event.club.warehouse.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

/**
 * Basic Domain Entity for our products. Represents a high-level 'type' of chair that we sell (as opposed to individual
 * instances of a chair). Within the Warehouse, we don't really care too much about about the type itself beyond the
 * sku, though we will track individual 'instances' as inventory
 */
@Entity
public class Chair {

    @Id
    private UUID id;
    private int version = 0;

    // in real life we should create a SKU class instead of a string to control logic (e.g. length, format)
    private String sku = "";
    private String name = "";

    
    protected Chair() {}

    public Chair(int version, String sku, String name, String description) {
        // we generate the ids 'by hand' here, as we want to alert callers to the future id that *will* exist
        // after an async save occurs. In real life we might have a 'ticket' system or other id generator
        this.id = UUID.randomUUID();
        this.version = version;
        this.sku = sku;
        this.name = name;
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
}
