package event.club.warehouse.services;

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
import java.util.concurrent.ExecutorService;

@Service
public class ChairManagementService {

    private final static Logger log = LoggerFactory.getLogger(ChairManagementService.class);

    private final JpaChairRepository chairRepository;

    private final MessageConsumerService consumerService;
    private final ExecutorService threadPool;



    @Autowired
    public ChairManagementService(
            JpaChairRepository chairRepository,
            MessageConsumerService consumerService, ExecutorService executorService
    ) {
        this.chairRepository = chairRepository;
        this.consumerService = consumerService;
        this.threadPool = executorService;
        // register Observers, subscribe to incoming messages

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
}
