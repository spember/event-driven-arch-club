package event.club.chair.messaging.messages;

import java.util.UUID;

@HeaderInfo(aliases = "chair-created")
public class ChairCreated extends ChairManipulationMessage{

    protected ChairCreated() {}

    public ChairCreated(UUID id, int version, String sku, String name, String description) {
        super(id, version, sku, name, description);
    }
}
