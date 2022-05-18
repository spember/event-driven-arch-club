package event.club.admin.services;

import event.club.admin.domain.Chair;
import event.club.admin.http.UpdateChairCommand;
import event.club.admin.repositories.JpaChairRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Service
public class ChairManagementService {

    private final static Logger log = LoggerFactory.getLogger(ChairManagementService.class);

    private final JpaChairRepository chairRepository;

    private final RestTemplate client = new RestTemplate();

    private final String chairfrontLocation;

    private final ExecutorService threadPool;

    private List<InternalNotificationSubscriber<Chair>> chairNotificationSubscribers = new ArrayList<>();

    @Autowired
    public ChairManagementService(
            JpaChairRepository chairRepository,
            @Value("${chairfront.location}") String chairfront,
            ExecutorService executorService
    ) {
        this.chairRepository = chairRepository;
        this.chairfrontLocation = "http://"+chairfront;
        this.threadPool = executorService;
        log.info("Initialized the chair service");
        log.info("Chairfront is located at {}", chairfront);
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

        threadPool.submit(new ChairPersistTask(target, chairRepository));
        log.debug("Chair persistence work submitted to threadPool");
        return target;
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

    private class ChairPersistTask implements Runnable {

        private final Chair toSave;
        private final JpaChairRepository chairRepository;

        public ChairPersistTask(Chair toSave, JpaChairRepository chairRepository) {
            this.toSave = toSave;
            this.chairRepository = chairRepository;
        }

        @Override
        public void run() {
            Chair saved = this.chairRepository.save(toSave);
            log.info("Chair {} successfully saved, updating downstream...", saved.getId());
            log.info("Calling chairfront at {}", chairfrontLocation);
            ResponseEntity<Boolean> result = client.postForEntity(chairfrontLocation+"/register", saved, Boolean.class);
            log.info("Did we save from chairfront? {}", result.getStatusCode());
            // notify subscribers after the system completes this task
            chairNotificationSubscribers.forEach(subscriber -> subscriber.handle(toSave));
            log.debug("Subscriber notification complete");
        }
    }

}
