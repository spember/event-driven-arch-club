package event.club.chairfront.services;

import event.club.chairfront.domain.Chair;
import event.club.chairfront.http.UpdateChairFromUpstreamCommand;
import event.club.chairfront.repositories.JpaChairRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChairfrontCatalogService {

    private final static Logger log = LoggerFactory.getLogger(ChairfrontCatalogService.class);

    private final JpaChairRepository chairRepository;

    @Autowired
    public ChairfrontCatalogService(JpaChairRepository chairRepository) {
        this.chairRepository = chairRepository;
        log.info("Initialized the chair service");
    }

    public Optional<Chair> get(UUID chairId) {
        return this.chairRepository.findById(chairId);
    }

    public List<Chair> list() {
        List<Chair> chairs = new ArrayList<>();
        chairs.addAll(this.chairRepository.findAllByUnitsOnHandGreaterThan(0));

        log.info("Returning only those chairs with inv: {}", chairs);
        return chairs;
    }

    public List<Chair> listAll() {
        List<Chair> chairs = new ArrayList<>();
        this.chairRepository.findAll().forEach(chairs::add);
        log.info("Returning all  chairs: {}", chairs);
        return chairs;
    }

    public Chair create(UpdateChairFromUpstreamCommand command) {
        // should throw invalid exceptions or return a -Result class
        log.info("About to save with {}, {}, {}, {}", command.getId(), command.getSku(), command.getName(), command.getDescription());
        Chair target = new Chair(
                command.getId(),
                1,
                command.getSku(),
                command.getName(),
                command.getDescription(),
                0
        );
        try {
            // WOW this is sure taking a long time! MY poor, poor upstream callers
            Thread.sleep(2500);
        } catch(InterruptedException exception) {
            log.error("Could not sleep", exception);
        }
        return this.chairRepository.save(target);

    }

}
