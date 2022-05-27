package event.club.chair.messages;

import java.util.UUID;

public class ChairCreated extends ChairManipulationMessage{

    protected ChairCreated() {}

    public ChairCreated(UUID id, int version, String sku, String name, String description) {
        super(id, version, sku, name, description);
    }
}
