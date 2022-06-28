package event.club.warehouse.http;

import event.club.warehouse.services.InventoryManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryManagementService inventoryManagementService;

    @Autowired
    public InventoryController(InventoryManagementService inventoryManagementService) {
        this.inventoryManagementService = inventoryManagementService;
    }

    @PostMapping("/inventory/recalculate/{chairId}")
    public void kickoffRecalculation(@PathVariable UUID chairId) {
        log.info("Kicking off recalculation of chair {}", chairId);
        // this all should probably go in some 'job' service but hey
        this.inventoryManagementService.scheduleRecalculationJob(chairId);
    }


    @PostMapping("/inventory")
    public void receiveItem(@RequestBody ReceiveItemCommand command) {
        // chairId, serial
        // register new inventory
        this.inventoryManagementService.storeNewItem(command.getChairId(), command.getSerial());
    }

    // I know that verbs in the urls is not RESTful. this is for illustration.
    @PostMapping("/inventory/reserve/{serial}")
    public void reserveItem(@PathVariable String serial) {
        this.inventoryManagementService.reserveItem(serial);
    }

    @PostMapping("/inventory/restock/{serial}")
    public void restockItem(@PathVariable String serial) {
        this.inventoryManagementService.restockItem(serial);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Could not find a value with that id")
    void onMissing(NoSuchElementException exception) {}
}
