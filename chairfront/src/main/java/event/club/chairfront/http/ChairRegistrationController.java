package event.club.chairfront.http;

import event.club.chairfront.domain.Chair;
import event.club.chairfront.services.ChairfrontCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChairRegistrationController {

    private static final Logger log = LoggerFactory.getLogger(ChairRegistrationController.class);

    private final ChairfrontCatalogService chairfrontCatalogService;

    @Autowired
    public ChairRegistrationController(ChairfrontCatalogService chairfrontCatalogService) {
        this.chairfrontCatalogService = chairfrontCatalogService;
    }

    /**
     * Endpoint for 'registering' new Chair Types from the admin app.
     *
     * @param command
     * @return
     */
    @PostMapping("/register")
    public Chair create(@RequestBody UpdateChairFromUpstreamCommand command) {
        return this.chairfrontCatalogService.create(command);
    }
}
