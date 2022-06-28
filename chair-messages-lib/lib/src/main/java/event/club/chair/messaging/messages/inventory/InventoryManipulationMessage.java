package event.club.chair.messaging.messages.inventory;

import event.club.chair.messaging.messages.DomainMessage;

import java.util.UUID;

class InventoryManipulationMessage implements DomainMessage {

    private String serial;
    private UUID chairId;
    private int price;
    private int revision;

    protected InventoryManipulationMessage() {}

    protected InventoryManipulationMessage(String serial, UUID chairId, int price, int revision) {
        this.serial = serial;
        this.chairId = chairId;
        this.price = price;
        this.revision = revision;
    }

    public String getSerial() {
        return serial;
    }

    public UUID getChairId() {
        return chairId;
    }

    public int getPrice() {
        return price;
    }

    public int getRevision() {
        return revision;
    }
}
