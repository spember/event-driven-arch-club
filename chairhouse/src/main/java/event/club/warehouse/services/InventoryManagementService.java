package event.club.warehouse.services;

import event.club.chair.messaging.DomainTopics;
import event.club.chair.messaging.messages.inventory.InventoryAdded;
import event.club.chair.messaging.messages.inventory.InventoryPurchased;
import event.club.chair.messaging.messages.inventory.InventoryRestocked;
import event.club.warehouse.domain.Inventory;
import event.club.warehouse.repositories.ExternalPricingRepository;
import event.club.warehouse.repositories.InventorySerialsOnly;
import event.club.warehouse.repositories.JpaInventoryRepository;
import event.club.warehouse.services.messaging.InternalTopics;
import event.club.warehouse.services.messaging.MessageConsumerService;
import event.club.warehouse.services.messaging.MessageProducerService;
import event.club.warehouse.services.messaging.messages.InitialRecalculationJobCommand;
import event.club.warehouse.services.messaging.messages.RecalculateIndividualPriceCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Responsible for the Inventory
 */
@Service
public class InventoryManagementService {

    private final static Logger log = LoggerFactory.getLogger(InventoryManagementService.class);

    private final static int DEFAULT_PAGE_SIZE = 0;

    private final MessageProducerService producerService;
    private final MessageConsumerService messageConsumerService;
    private final JpaInventoryRepository jpaInventoryRepository;
    private final ExternalPricingRepository externalPricingRepository;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;


    @Autowired
    public InventoryManagementService(MessageProducerService producerService,
                                      MessageConsumerService messageConsumerService,
                                      JpaInventoryRepository jpaInventoryRepository,
                                      ExternalPricingRepository externalPricingRepository
    ) {
        this.producerService = producerService;
        this.messageConsumerService = messageConsumerService;
        this.jpaInventoryRepository = jpaInventoryRepository;
        this.externalPricingRepository = externalPricingRepository;

        // link up the work
        this.messageConsumerService.register(InternalTopics.WAREHOUSE_WORK,
                InitialRecalculationJobCommand.class, this::processRecalculationJob);
        this.messageConsumerService.register(InternalTopics.WAREHOUSE_WORK,
                RecalculateIndividualPriceCommand.class, this::handleIndividualRecalculation);
    }

    public Stream<Inventory> loadAllForChair(UUID chairId, int pageSize) {

        Query query = this.entityManagerFactory.createEntityManager()
                .createQuery("From Inventory where chairId = :chairId");
        query.setParameter("chairId", chairId);
        List<Inventory> totalItems = new ArrayList<>();
        int currentPage = -1;
        // page through the results. It's a bad idea generally to query for an unbounded set of records; although 25
        // is too small of a size wrt to balancing for number of requests, I do it here for illustration.
        // also, I miss jooq. If I had more time I'd set it up.
        while(true) {
            currentPage++;
            query.setFirstResult(currentPage * pageSize);
            query.setMaxResults(pageSize);
            List<Inventory> currentItems = query.getResultList();
            totalItems.addAll(currentItems);
            if (currentItems.size() < pageSize) {
                break;
            }
        }
        return totalItems.stream();
    }

    public Stream<Inventory> loadAllForChair(UUID chairId) {
        return loadAllForChair(chairId, DEFAULT_PAGE_SIZE);
    }

    public Stream<String> loadAllSerialsForChair(UUID chairId) {
        return jpaInventoryRepository.findByChairId(chairId).stream().map(InventorySerialsOnly::getSerial);
    }

    /**
      * Whoops! We forgot to set the price on our inventory. For some reason we decided to price our chairs individually
      * within some range. A side effect of all this is now we need to go and update each individual chair one by one.
      *
      *
      * This method will calculate the price for a given Inventory item and update it in the database. If it already has
      * a price (e.g. you ran this again, because a message was read twice) it will be skipped.
      *
      * @param item
     * */
    public void recalculatePrice(Inventory item) {
        Optional<Integer> updatedPrice = externalPricingRepository.calculatePriceInCents(item);
        if (updatedPrice.isEmpty()) {
            log.warn("Price already set for item {} -> {}", item.getSerial(), item.getCurrentPrice());
        } else {
            item.setCurrentPrice(updatedPrice.get());
            item.setVersion(item.getVersion()+1);
            jpaInventoryRepository.save(item);
            log.info("Price updated for item {} -> {}", item.getSerial(), item.getCurrentPrice());
        }
    }


    /**
     * Starts the process to recalculate all prices for a given Chair, by its id.
     * Under the hood, this publishes a message so that the caller is not waiting for the job process to start. This
     * is due to the fact that there be a very large number of items and we don't need the caller to wait for this.
     *
     * @param chairId
     */
    public void scheduleRecalculationJob(UUID chairId) {
        log.info("Broadcasting message to begin chair pricing recalculations for {}", chairId);
        producerService.emit(InternalTopics.WAREHOUSE_WORK, new InitialRecalculationJobCommand(chairId));
    }

    /**
     * This method should be run async. Retrieves the serial numbers for all inventory for a chair id and emits more
     * messages;
     *
     * @param command
     */
    public void processRecalculationJob(InitialRecalculationJobCommand command) {
        log.info("Initializing the messages for handling a chair pricing recalculation for chair id {}",
                command.getChairId());
        loadAllSerialsForChair(command.getChairId()) // ideally this would be a 'yield' or done in batches
                .forEach(serial -> producerService.emit(InternalTopics.WAREHOUSE_WORK,
                        new RecalculateIndividualPriceCommand(serial, command.getChairId()))
                );
    }

    /**
     * Does the actual work of recalculating.
     *
     * @param command
     */
    public void handleIndividualRecalculation(RecalculateIndividualPriceCommand command) {
        log.info("Recalculating price for serial {}", command.getSerialNumber());
        Optional<Inventory> maybeInventory = jpaInventoryRepository.findById(command.getSerialNumber());
        if (maybeInventory.isEmpty()) {
            log.warn("Could not find an Inventory with serial {}", command.getSerialNumber());

        } else {
            this.recalculatePrice(maybeInventory.get());
        }
    }


    /**
     * Adds a new item to our warehouse
     *
     *
     * @param chairId
     * @param serial
     */
    public void storeNewItem(UUID chairId, String serial) {
        log.info("Adding new item ({}) to our inventory for  chair id {}", serial, chairId);
        // public Inventory(String serial, UUID chairId, int version, Instant arrived, Instant purchased, Instant shipped) {
        Inventory newItem = new Inventory(serial, chairId, 1, Instant.now(), null, null);
        // recalculating the price also saves the item
        recalculatePrice(newItem);
        // emit
        producerService.emit(DomainTopics.INVENTORY, new InventoryAdded(
                newItem.getSerial(),
                newItem.getChairId(),
                newItem.getCurrentPrice(),
                newItem.getVersion()
        ));

    }


    /**
     * Marks an item as 'purchased' or reserved. It hasn't been shipped yet, but it cannot be 'sold' again.
     *
     */
    public void reserveItem(String serial) {
        updateInventoryIfAvailable(serial, (inventory) -> {
            inventory.setPurchased(Instant.now());
            return inventory;
        }, (saved) -> {
            producerService.emit(DomainTopics.INVENTORY, new InventoryPurchased(
                    saved.getSerial(),
                    saved.getChairId(),
                    saved.getCurrentPrice(),
                    saved.getVersion()

            ));
        });
    }

    /**
     * Called when a Chair has been 'restocked' whether due to order cancellation or returns
     *
     * @param serial
     */
    public void restockItem(String serial) {
        updateInventoryIfAvailable(serial, (inventory) -> {
            inventory.setPurchased(null);
            return inventory;
        }, (saved) -> {
            producerService.emit(DomainTopics.INVENTORY, new InventoryRestocked(
                    saved.getSerial(),
                    saved.getChairId(),
                    saved.getCurrentPrice(),
                    saved.getVersion()
            ));

        });
    }

    private void updateInventoryIfAvailable(String serial,
                                            Function<Inventory, Inventory> updater,
                                            Consumer<Inventory> postSave
    ) {
        Optional<Inventory> maybeInventory =  jpaInventoryRepository.findById(serial);
        // in real life, through an error to trigger a 404, for example
        if (maybeInventory.isEmpty()) {
            throw new NoSuchElementException("Unknown inventory with serial " + serial);
        } else {
            Inventory toSave = updater.apply(maybeInventory.get());
            toSave.setVersion(maybeInventory.get().getVersion()+1);
            jpaInventoryRepository.save(toSave);
            postSave.accept(toSave);
        }
    }
}
