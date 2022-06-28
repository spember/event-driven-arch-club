package event.club.chair.messaging.messages.inventory;

import event.club.chair.messaging.messages.HeaderInfo;

import java.util.UUID;

@HeaderInfo(aliases = "inventory-restored")
public class InventoryRestocked extends InventoryManipulationMessage {

    public InventoryRestocked() {
    }

    public InventoryRestocked(String serial, UUID chairId, int price, int revision) {
        super(serial, chairId, price, revision);
    }
}
