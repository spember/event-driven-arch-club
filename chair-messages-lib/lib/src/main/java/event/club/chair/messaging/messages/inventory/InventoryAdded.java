package event.club.chair.messaging.messages.inventory;

import event.club.chair.messaging.messages.HeaderInfo;

import java.util.UUID;

@HeaderInfo(aliases = "inventory-added")
public class InventoryAdded extends InventoryManipulationMessage {

    public InventoryAdded() {
    }

    public InventoryAdded(String serial, UUID chairId, int price, int revision) {
        super(serial, chairId, price, revision);
    }
}
