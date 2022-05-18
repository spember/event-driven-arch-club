package event.club.warehouse.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents an instance of a chair in our database
 */
@Entity
public class Inventory {

    @Id
    private String serial;
    private UUID chairId;
    private int version = 0;
    private Instant arrived;
    private Instant purchased;
    private Instant shipped;

    protected Inventory() {};

    public Inventory(String serial, UUID chairId, int version, Instant arrived, Instant purchased, Instant shipped) {
        this.serial = serial;
        this.chairId = chairId;
        this.version = version;
        this.arrived = arrived;
        this.purchased = purchased;
        this.shipped = shipped;
    }

    public String getSerial() {
        return serial;
    }


    public UUID getChairId() {
        return chairId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Instant getArrived() {
        return arrived;
    }

    public void setArrived(Instant arrived) {
        this.arrived = arrived;
    }

    public Instant getPurchased() {
        return purchased;
    }

    public void setPurchased(Instant purchased) {
        this.purchased = purchased;
    }

    public Instant getShipped() {
        return shipped;
    }

    public void setShipped(Instant shipped) {
        this.shipped = shipped;
    }
}
