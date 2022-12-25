package patricksongzy.skylinestelemetry.processing.data.transfer;

import patricksongzy.skylinestelemetry.processing.skylines.TransferReason;

import java.time.Instant;

public class EnrichedTransferData implements EnrichedTransfer {
    private int key;
    private Instant timestamp;
    private boolean isActive;
    private int priority;
    private int amount;
    private int positionEastward;
    private int positionNorthward;
    private TransferReason reason;
    private int building;
    private int vehicle;
    private int citizen;

    @Override
    public Integer getKey() {
        return key;
    }

    @Override
    public void setKey(Integer key) {
        this.key = key;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public int getPositionEastward() {
        return positionEastward;
    }

    @Override
    public void setPositionEastward(int positionEastward) {
        this.positionEastward = positionEastward;
    }

    @Override
    public int getPositionNorthward() {
        return positionNorthward;
    }

    @Override
    public void setPositionNorthward(int positionNorthward) {
        this.positionNorthward = positionNorthward;
    }

    @Override
    public TransferReason getReason() {
        return reason;
    }

    @Override
    public void setReason(TransferReason reason) {
        this.reason = reason;
    }

    @Override
    public int getBuilding() {
        return building;
    }

    @Override
    public void setBuilding(int building) {
        this.building = building;
    }

    @Override
    public int getVehicle() {
        return vehicle;
    }

    @Override
    public void setVehicle(int vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public int getCitizen() {
        return citizen;
    }

    @Override
    public void setCitizen(int citizen) {
        this.citizen = citizen;
    }

    @Override
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
