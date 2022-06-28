package event.club.chairfront.services;

import event.club.chair.messaging.DomainTopics;
import event.club.chair.messaging.messages.ChairCreated;
import event.club.chair.messaging.messages.ChairUpdated;
import event.club.chair.messaging.messages.inventory.InventoryAdded;
import event.club.chair.messaging.messages.inventory.InventoryPurchased;
import event.club.chair.messaging.messages.inventory.InventoryRestocked;
import event.club.chairfront.domain.Chair;
import event.club.chairfront.http.UpdateChairFromUpstreamCommand;
import event.club.chairfront.repositories.JpaChairRepository;
import event.club.chairfront.services.messaging.MessageConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class ChairfrontCatalogService {

    private final static Logger log = LoggerFactory.getLogger(ChairfrontCatalogService.class);

    private final JpaChairRepository chairRepository;
    private final MessageConsumerService consumerService;

    @Autowired
    public ChairfrontCatalogService(JpaChairRepository chairRepository, MessageConsumerService consumerService) {
        this.chairRepository = chairRepository;
        this.consumerService = consumerService;
        log.info("Initialized the chair service");

        this.consumerService.register(DomainTopics.CHAIRS, ChairCreated.class, this::create);
        this.consumerService.register(DomainTopics.CHAIRS, ChairUpdated.class, this::update);

        this.consumerService.register(DomainTopics.INVENTORY, InventoryAdded.class, this::increaseInventory);
        this.consumerService.register(DomainTopics.INVENTORY, InventoryRestocked.class, this::increaseInventory);
        this.consumerService.register(DomainTopics.INVENTORY, InventoryPurchased.class, this::decreaseInventory);

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

    public Chair create(ChairCreated message) {
        return this.chairRepository.save(new Chair(
                message.getId(),
                message.getVersion(),
                message.getSku(),
                message.getName(),
                message.getDescription(),
                0
        ));
    }

    public Chair update(ChairUpdated message) {
        return this.chairRepository.save(new Chair(
                message.getId(),
                message.getVersion(),
                message.getSku(),
                message.getName(),
                message.getDescription(),
                0
        ));
    }

    public void increaseInventory(InventoryAdded message) {
        updateInventory(message.getChairId(), (chair) -> chair.setUnitsOnHand(chair.getUnitsOnHand()+1));
    }

    public void increaseInventory(InventoryRestocked message) {
        updateInventory(message.getChairId(), (chair) -> chair.setUnitsOnHand(chair.getUnitsOnHand()+1));
    }

    public void decreaseInventory(InventoryPurchased message) {
        this.updateInventory(message.getChairId(), (chair) -> chair.setUnitsOnHand(chair.getUnitsOnHand()-1));
    }

    private void updateInventory(UUID chairId, Consumer<Chair> handler) {
        Optional<Chair> maybeChair =  this.chairRepository.findById(chairId);
        maybeChair.ifPresent(chair -> {
            handler.accept(chair);
            this.chairRepository.save(chair);
        });
    }

}
