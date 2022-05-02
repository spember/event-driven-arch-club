package event.club.admin.services;

import event.club.admin.domain.Chair;
import event.club.admin.http.UpdateChairCommand;
import event.club.admin.repositories.JpaChairRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChairManagementService {

    private final static Logger log = LoggerFactory.getLogger(ChairManagementService.class);

    private final JpaChairRepository chairRepository;

    @Autowired
    public ChairManagementService(JpaChairRepository chairRepository) {
        this.chairRepository = chairRepository;
        log.info("Initialized the chair service");
    }

    public Optional<Chair> get(UUID chairId) {
        return this.chairRepository.findById(chairId);
    }

    public List<Chair> list() {
        List<Chair> chairs = new ArrayList<>();
        this.chairRepository.findAll().forEach(chairs::add);
        return chairs;
    }

    public Chair create(UpdateChairCommand command) {
        // should throw invalid exceptions or return a -Result class
        log.info("About to save with {}, {}, {}", command.getRequestedSku(), command.getRequestedName(), command.getRequestedDescription());
        Chair target = new Chair(
                1,
                command.getRequestedSku(),
                command.getRequestedName(),
                command.getRequestedDescription()
        );
        try {
            // WOW this is sure taking a long time!
            Thread.sleep(2500);
        } catch(InterruptedException exception) {
            log.error("Could not sleep", exception);
        }
        return this.chairRepository.save(target);

    }

}
