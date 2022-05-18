package event.club.admin.services;

/**
 * Used by internal systems that wish to notify 'subscribers' of changes. In other words, used by internal Observables
 * to perform a lightweight Observer pattern. Implementations should refrain from doing side effects in synchronous
 * operations (e.g. if a Subscriber must do a side effect, ensure that the Observable is async
 *
 * @param <T>
 */
public interface InternalNotificationSubscriber<T> {

    void handle(T value);
}
