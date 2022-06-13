package event.club.warehouse.http;

import event.club.warehouse.services.InventoryManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
