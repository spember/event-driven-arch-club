package event.club.admin.services;

import event.club.admin.domain.Chair;
import event.club.admin.http.CreateChairCommand;
import event.club.admin.http.UpdateChairCommand;
import event.club.admin.repositories.JpaChairRepository;
import event.club.admin.services.messaging.MessageProducerService;
import event.club.chair.messaging.Topics;
import event.club.chair.messaging.messages.ChairCreated;
import event.club.chair.messaging.messages.ChairUpdated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.Option;
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

    private final MessageProducerService producerService;

    private List<InternalNotificationSubscriber<Chair>> chairNotificationSubscribers = new ArrayList<>();

    @Autowired
    public ChairManagementService(
            JpaChairRepository chairRepository,
            @Value("${chairfront.location}") String chairfront,
            ExecutorService executorService,
            MessageProducerService producerService) {
        this.chairRepository = chairRepository;
        this.chairfrontLocation = "http://"+chairfront;
        this.threadPool = executorService;
        this.producerService = producerService;
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
    
    public Chair create(CreateChairCommand command) {
        // should throw invalid exceptions or return a -Result class
        log.info("About to save with {}, {}, {}", command.getRequestedSku(), command.getRequestedName(), command.getRequestedDescription());
        Chair target = new Chair(
                1,
                command.getRequestedSku(),
                command.getRequestedName(),
                command.getRequestedDescription()
        );

        /*
            Now, this seems like moving backward a bit given the previous lessons in making
            the save and http operations to be async.
            It's a tradeoff, but here we persist the new Chair AND THEN publish, in the same block.
            This is slightly slower, but less risky: when the user gets the response, we are
            assured that the chair has been created and the message published.

            We could of course make this async as well, but I thought it was too much for one step.
         */

        Chair saved = this.chairRepository.save(target);
        log.info("Chair {} successfully saved, updating downstream...", saved.getId());
        // once saved, published a message.
        this.producerService.emit(Topics.CHAIRS, new ChairCreated(saved.getId(), saved.getVersion(), saved.getSku(),
                saved.getName(), saved.getDescription()));

        chairNotificationSubscribers.forEach(subscriber -> subscriber.handle(saved));
        return saved;
    }

    @Transactional
    public Optional<Chair> update(UpdateChairCommand command) {
        log.info("Updating chair {}", command.getId());

        Optional<Chair> maybeChair = this.chairRepository.findById(command.getId());
        // should probably throw an error if this fails.
        if (maybeChair.isPresent()) {
            Chair toUpdate = maybeChair.get();
            // this version acts as an Optimistic lock, provided that the database persistence layer
            // refuses to update a row with the same version number
            toUpdate.setVersion(toUpdate.getVersion()+1);
            toUpdate.setDescription(command.getRequestedDescription());
            toUpdate.setName(command.getRequestedName());
            toUpdate.setSku(command.getRequestedSku());

            // we want to this save to ensure that the version number is exactly one more than what's at rest
            // e.g. there's a potential race condition above if two things are trying to update the same resource
            // but that's probably beyond the scope of this toy example.
            this.chairRepository.updateChairInfoById(
                    toUpdate.getId(), toUpdate.getVersion(), toUpdate.getName(), toUpdate.getDescription()
            );
            // if the save fails, it should throw an exception, causing the message production to be skipped
            log.info("Updated chair {}", toUpdate.getId());
            // once saved, publish a message
            this.producerService.emit(Topics.CHAIRS,
                    new ChairUpdated(toUpdate.getId(), toUpdate.getVersion(),
                            toUpdate.getSku(), toUpdate.getName(),
                            toUpdate.getDescription())
            );
            chairNotificationSubscribers.forEach(subscriber -> subscriber.handle(toUpdate));
            return Optional.of(toUpdate);
        }
        return Optional.empty();
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
