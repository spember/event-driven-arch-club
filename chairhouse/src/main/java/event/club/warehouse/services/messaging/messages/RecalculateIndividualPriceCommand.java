package event.club.warehouse.services.messaging.messages;

import event.club.chair.messaging.messages.DomainMessage;
import event.club.chair.messaging.messages.HeaderInfo;

import java.util.UUID;

@HeaderInfo(aliases = "recalculate-price")
public class RecalculateIndividualPriceCommand implements DomainMessage {

    private String serialNumber;
    private UUID chairId;

    protected RecalculateIndividualPriceCommand() {}

    public RecalculateIndividualPriceCommand(String serialNumber, UUID chairId) {
        this.serialNumber = serialNumber;
        this.chairId = chairId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public UUID getChairId() {
        return chairId;
    }

    public void setChairId(UUID chairId) {
        this.chairId = chairId;
    }
}
