package event.club.admin.orders.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.util.UUID;

@Entity
public class OrderTracker {
    // order has several stages:
    /*
    1. (Creation) Receive payload from Customer, perform minor validation (i.e. ensure fields are not empty)
1. Reserve an inventory item from the Warehouse
1. Process Payment*
1. Send Confirmation Notification*
1. Ship the reserved inventory (in reality this might occur after some time, but our warehouse folks are very fast)
     */

    @Id
    private UUID id;

    private String customerId;
    private String productSerial; // we only sell one at a time!

    private Instant timeInitiated;
    private Instant timeCompleted;

    // just brute-forcing these stages here for now.
    private StageStatus itemReserved = StageStatus.NOT_OBSERVED;
    private Instant itemReservedTime;

    private StageStatus paymentProcessed = StageStatus.NOT_OBSERVED;
    private Instant paymentProcessedTime;

    private StageStatus confirmationSent = StageStatus.NOT_OBSERVED;
    private Instant confirmationSentTime;

    private StageStatus itemShipped = StageStatus.NOT_OBSERVED;
    private Instant itemShippedTime;

    public OrderTracker() {
    }

    public OrderTracker(UUID id, String customerId, String productSerial) {
        this.id = id;
        this.customerId = customerId;
        this.productSerial = productSerial;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProductSerial() {
        return productSerial;
    }

    public void setProductSerial(String productSerial) {
        this.productSerial = productSerial;
    }

    public Instant getTimeInitiated() {
        return timeInitiated;
    }

    public void setTimeInitiated(Instant timeInitiated) {
        this.timeInitiated = timeInitiated;
    }

    public Instant getTimeCompleted() {
        return timeCompleted;
    }

    public void setTimeCompleted(Instant timeCompleted) {
        this.timeCompleted = timeCompleted;
    }

    public StageStatus getItemReserved() {
        return itemReserved;
    }

    public void setItemReserved(StageStatus itemReserved) {
        this.itemReserved = itemReserved;
        this.itemReservedTime = Instant.now();
    }

    public Instant getItemReservedTime() {
        return itemReservedTime;
    }

    public void setItemReservedTime(Instant itemReservedTime) {
        this.itemReservedTime = itemReservedTime;
    }

    public StageStatus getPaymentProcessed() {
        return paymentProcessed;
    }

    public void setPaymentProcessed(StageStatus paymentProcessed) {
        this.paymentProcessed = paymentProcessed;
        this.paymentProcessedTime = Instant.now();
    }

    public Instant getPaymentProcessedTime() {
        return paymentProcessedTime;
    }

    public void setPaymentProcessedTime(Instant paymentProcessedTime) {
        this.paymentProcessedTime = paymentProcessedTime;
    }

    public StageStatus getConfirmationSent() {
        return confirmationSent;
    }

    public void setConfirmationSent(StageStatus confirmationSent) {
        this.confirmationSent = confirmationSent;
        this.confirmationSentTime = Instant.now();
    }

    public Instant getConfirmationSentTime() {
        return confirmationSentTime;
    }

    public void setConfirmationSentTime(Instant confirmationSentTime) {
        this.confirmationSentTime = confirmationSentTime;
    }

    public StageStatus getItemShipped() {
        return itemShipped;
    }

    public void setItemShipped(StageStatus itemShipped) {
        this.itemShipped = itemShipped;
        this.itemShippedTime = Instant.now();
    }

    public Instant getItemShippedTime() {
        return itemShippedTime;
    }

    public void setItemShippedTime(Instant itemShippedTime) {
        this.itemShippedTime = itemShippedTime;
    }
}
