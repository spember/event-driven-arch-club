package event.club.chair.messaging.messages.inventory;

import event.club.chair.messaging.messages.HeaderInfo;

import java.util.UUID;

@HeaderInfo(aliases = "inventory-purchased")
public class InventoryPurchased extends InventoryManipulationMessage {

    public InventoryPurchased() {
    }

    public InventoryPurchased(String serial, UUID chairId, int price, int revision) {
        super(serial, chairId, price, revision);
    }
}
