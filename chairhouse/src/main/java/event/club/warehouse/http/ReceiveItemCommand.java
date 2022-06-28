package event.club.warehouse.http;

import java.util.UUID;

public class ReceiveItemCommand {
    private UUID chairId;
    private String serial;

    public ReceiveItemCommand(UUID chairId, String serial) {
        this.chairId = chairId;
        this.serial = serial;
    }

    public UUID getChairId() {
        return chairId;
    }

    public String getSerial() {
        return serial;
    }
}
