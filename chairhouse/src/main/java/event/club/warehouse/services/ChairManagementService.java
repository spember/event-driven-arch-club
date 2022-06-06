package event.club.warehouse.services;

import event.club.chair.messaging.Topics;
import event.club.chair.messaging.messages.ChairCreated;
import event.club.chair.messaging.messages.ChairUpdated;
import event.club.warehouse.domain.Chair;
import event.club.warehouse.repositories.JpaChairRepository;
import event.club.warehouse.services.messaging.MessageConsumerService;
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

    private final MessageConsumerService consumerService;



    @Autowired
    public ChairManagementService(
            JpaChairRepository chairRepository,
            MessageConsumerService consumerService
    ) {
        this.chairRepository = chairRepository;
        this.consumerService = consumerService;
        // register Observers, subscribe to incoming messages

        this.consumerService.register(Topics.CHAIRS, ChairCreated.class, this::handleCreate);
        this.consumerService.register(Topics.CHAIRS, ChairUpdated.class, this::handleUpdate);
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

    public void handleCreate(ChairCreated message) {
        // we need to create a new chair type. No inventory yet to speak of.
        // note that ChairHouse's concept of chair doesn't need all the details.
        chairRepository.save(new Chair(message.getId(), message.getVersion(), message.getSku(), message.getName()));
    }

    public void handleUpdate(ChairUpdated message) {
        if (chairRepository.existsById(message.getId())) {
            chairRepository.save(new Chair(message.getId(), message.getVersion(), message.getSku(), message.getName()));
        } else {
            // this method will be called async and as such direct feedback (i.e. from a user call) may not be obvious
            // this is a perfect place for reporting errors to metrics.
            log.error("Received an update for an unknown chair {}", message.getId());
        }
    }
}
