package event.club.warehouse.services;

import event.club.warehouse.domain.Chair;
import event.club.warehouse.repositories.JpaChairRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Service
public class ChairManagementService {

    private final static Logger log = LoggerFactory.getLogger(ChairManagementService.class);

    private final JpaChairRepository chairRepository;


    private final ExecutorService threadPool;

    private List<InternalNotificationSubscriber<Chair>> chairNotificationSubscribers = new ArrayList<>();

    @Autowired
    public ChairManagementService(
            JpaChairRepository chairRepository,
            ExecutorService executorService
    ) {
        this.chairRepository = chairRepository;
        this.threadPool = executorService;
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


    /**
     * Register a subscriber to be notified when a Chair is manipulated.
     *
     * @param subscriber
     */
    public void register(InternalNotificationSubscriber<Chair> subscriber) {
        // in a real system, the type passed to subscribers should be some sort of context class which contains
        // additional information about the Chair that was changed (e.g. was it just created).
        this.chairNotificationSubscribers.add(subscriber);
    }

}
