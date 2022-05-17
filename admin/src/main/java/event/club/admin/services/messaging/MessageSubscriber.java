package event.club.admin.services.messaging;

public interface MessageSubscriber<T> {

    void handle(T value);
}
