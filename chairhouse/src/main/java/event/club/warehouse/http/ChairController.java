package event.club.warehouse.http;

import event.club.warehouse.domain.Chair;
import event.club.warehouse.services.ChairManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ChairController {
    private static final Logger log = LoggerFactory.getLogger(ChairController.class);

    private final ChairManagementService chairManagementService;

    @Autowired
    ChairController( ChairManagementService service) {
        this.chairManagementService = service;
    }

    @GetMapping("/chairs")
    public List<Chair> index() {
        // in real life we'd do paging and such
        return chairManagementService.list();
    }

    @GetMapping("/chairs/{chairId}")
    public Optional<Chair> get(@PathVariable UUID chairId) {
        Optional<Chair> maybeChair = chairManagementService.get(chairId);
        log.info("Found chair: " + maybeChair);
        return maybeChair;
    }
}
