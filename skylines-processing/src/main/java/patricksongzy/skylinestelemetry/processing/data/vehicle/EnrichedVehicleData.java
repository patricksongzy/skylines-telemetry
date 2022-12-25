package patricksongzy.skylinestelemetry.processing.data.vehicle;

import patricksongzy.skylinestelemetry.processing.skylines.VehicleCategory;
import patricksongzy.skylinestelemetry.processing.skylines.VehicleFlag;

import java.time.Instant;
import java.util.EnumSet;

public class EnrichedVehicleData implements EnrichedVehicle {
    private int key;
    private Instant timestamp;

    private float speed;
    private float positionEastward;
    private float positionNorthward;
    private int source;
    private int target;
    private int capacity;
    private EnumSet<VehicleCategory> vehicleCategories;
    private EnumSet<VehicleFlag> vehicleFlags;

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
    public float getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public float getPositionEastward() {
        return positionEastward;
    }

    @Override
    public void setPositionEastward(float positionEastward) {
        this.positionEastward = positionEastward;
    }

    @Override
    public float getPositionNorthward() {
        return positionNorthward;
    }

    @Override
    public void setPositionNorthward(float positionNorthward) {
        this.positionNorthward = positionNorthward;
    }

    @Override
    public int getSource() {
        return source;
    }

    @Override
    public void setSource(int source) {
        this.source = source;
    }

    @Override
    public int getTarget() {
        return target;
    }

    @Override
    public void setTarget(int target) {
        this.target = target;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public EnumSet<VehicleCategory> getVehicleCategories() {
        return vehicleCategories;
    }

    @Override
    public void setVehicleCategories(EnumSet<VehicleCategory> vehicleCategories) {
        this.vehicleCategories = vehicleCategories;
    }

    @Override
    public EnumSet<VehicleFlag> getVehicleFlags() {
        return vehicleFlags;
    }

    @Override
    public void setVehicleFlags(EnumSet<VehicleFlag> vehicleFlags) {
        this.vehicleFlags = vehicleFlags;
    }
}
