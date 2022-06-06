package event.club.warehouse.repositories;

import event.club.warehouse.domain.Inventory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * Simulates some third-party or additional service that is used to generate a price based on chair type.
 *
 * This is a toy, for the purposes of demonstrating how one might construct boundaries  - in the form of Repository
 * objects - for communicating out of the system.
 */
@Service
public class ExternalPricingRepository {

    private final Random random = new Random();

    private final int maxPrice = 50000; // $500, that's a nice chair
    private final int minPrice = 2000; // Not every chair is classy

    /**
     * Queries our pricing service to calculate the price of this particular piece of Inventory. Will return empty if
     * the price is already set on the inventory.
     *
     * @param inventory The item to calculate price for
     * @return an Optional containing the price, in cents. If empty, the result should be ignored
     */
    public Optional<Integer> calculatePriceInCents(Inventory inventory) {
        // we use an optional to signal to the User that this call might fail or be empty. A more robust class could
        // provide better support.
        // (I like rx java Observable or reactive streams for these sorts of things in practice)
        if (inventory.getCurrentPrice() != 0) {
            return Optional.empty();
        }
        try {
            // simulate IO... uh oh this process is also not super-fast
            Thread.sleep(random.nextInt(2500-250) + 250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Optional.of(random.nextInt(maxPrice-minPrice) + minPrice);
    }
}
