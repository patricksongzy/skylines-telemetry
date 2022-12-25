package patricksongzy.skylinestelemetry.processing.data.vehicle;

import patricksongzy.skylinestelemetry.processing.data.TelemetryData;
import patricksongzy.skylinestelemetry.processing.skylines.VehicleCategory;
import patricksongzy.skylinestelemetry.processing.skylines.VehicleFlag;

import java.util.EnumSet;

public interface EnrichedVehicle extends TelemetryData<Integer> {
    float getSpeed();

    void setSpeed(float speed);

    float getPositionEastward();

    void setPositionEastward(float positionEastward);

    float getPositionNorthward();

    void setPositionNorthward(float positionNorthward);

    int getSource();

    void setSource(int source);

    int getTarget();

    void setTarget(int target);

    int getCapacity();

    void setCapacity(int capacity);

    EnumSet<VehicleCategory> getVehicleCategories();

    void setVehicleCategories(EnumSet<VehicleCategory> vehicleCategories);

    EnumSet<VehicleFlag> getVehicleFlags();

    void setVehicleFlags(EnumSet<VehicleFlag> vehicleFlags);
}
