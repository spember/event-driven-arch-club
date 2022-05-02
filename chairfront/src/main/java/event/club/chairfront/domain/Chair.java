package event.club.chairfront.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

/**
 * Chairfront's understanding of the 'Chair' domain entity.
 * Chairfront needs to maintain some understanding of the inventory levels and whether the thing is in stock. it may
 * also contain information beyond the up stream data, like merchandising / marketing information.
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

    private int unitsOnHand = 0;

    protected Chair() {}

    public Chair(UUID id, int version, String sku, String name, String description, int unitsOnHand) {
        this.id = id;
        this.version = version;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.unitsOnHand = unitsOnHand;
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

    public int getUnitsOnHand() {
        return unitsOnHand;
    }
}
