package event.club.admin.http;

import event.club.admin.domain.Chair;
import event.club.admin.services.ChairManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/chairs")
    public Chair create(@RequestBody CreateChairCommand command) {
        return this.chairManagementService.create(command);
    }

    @GetMapping("/chairs/{chairId}")
    public Optional<Chair> get(@PathVariable UUID chairId) {
        Optional<Chair> maybeChair = chairManagementService.get(chairId);
        log.info("Found chair: " + maybeChair);
        return maybeChair;
    }

    @PutMapping("/chairs/{chairId}")
    public Optional<Chair> update(@RequestBody UpdateChairCommand command ) {
        return chairManagementService.update(command);
    }
}
