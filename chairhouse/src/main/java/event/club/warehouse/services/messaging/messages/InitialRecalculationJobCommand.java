package event.club.warehouse.services.messaging.messages;

import event.club.chair.messaging.messages.DomainMessage;
import event.club.chair.messaging.messages.HeaderInfo;

import java.util.UUID;

@HeaderInfo(aliases = "initial-recalc-job")
public class InitialRecalculationJobCommand implements DomainMessage {

    private UUID chairId;

    public InitialRecalculationJobCommand() {
    }

    public InitialRecalculationJobCommand(UUID chairId) {
        this.chairId = chairId;
    }

    public UUID getChairId() {
        return chairId;
    }

    public void setChairId(UUID chairId) {
        this.chairId = chairId;
    }
}
