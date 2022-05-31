package event.club.admin.http;

import java.util.UUID;

public class UpdateChairCommand extends CreateChairCommand {

    private UUID id;

    public UpdateChairCommand(UUID id, String requestedSku, String requestedName, String requestedDescription) {
        super(requestedSku, requestedName, requestedDescription);
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
