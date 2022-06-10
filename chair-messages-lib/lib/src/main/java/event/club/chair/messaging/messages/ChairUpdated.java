package event.club.chair.messaging.messages;

import java.util.UUID;

@HeaderInfo(aliases = "chair-updated")
public class ChairUpdated extends ChairManipulationMessage {

    public ChairUpdated() {
    }

    public ChairUpdated(UUID id, int version, String sku, String name, String description) {
        super(id, version, sku, name, description);
    }
}
