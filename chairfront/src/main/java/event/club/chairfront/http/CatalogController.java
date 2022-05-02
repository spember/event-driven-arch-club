package event.club.chairfront.http;

import event.club.chairfront.domain.Chair;
import event.club.chairfront.services.ChairfrontCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class CatalogController {
    private static final Logger log = LoggerFactory.getLogger(CatalogController.class);

    private final ChairfrontCatalogService chairfrontCatalogService;

    @Autowired
    CatalogController(ChairfrontCatalogService service) {
        this.chairfrontCatalogService = service;
    }


    @GetMapping("/catalog")
    public List<Chair> index(@RequestParam(required = false) Boolean includeAll) {
        log.info("Handling catalog request!");
        if (includeAll != null && includeAll) {
            return chairfrontCatalogService.listAll();
        } else {
            return chairfrontCatalogService.list();
        }
    }


    @GetMapping("/catalog/{chairId}")
    public Optional<Chair> get(@PathVariable UUID chairId) {
        Optional<Chair> maybeChair = chairfrontCatalogService.get(chairId);
        log.info("Found chair: " + maybeChair);
        return maybeChair;
    }
}
