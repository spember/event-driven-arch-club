package event.club.admin.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

/**
 * Basic Domain Entity for our products. Represents a high-level 'type' of chair that we sell (as opposed to individual
 * instances of a chair)
 */
@Entity
public class Chair {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private int version = 0;

    // in real life we should create a SKU class instead of a string to control logic (e.g. length, format)
    private String sku = "";
    private String name = "";

    private String description = "";

//    public Chair(UUID id) {
//        this.id = id;
//    }

    protected Chair() {}

    public Chair(int version, String sku, String name, String description) {
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
