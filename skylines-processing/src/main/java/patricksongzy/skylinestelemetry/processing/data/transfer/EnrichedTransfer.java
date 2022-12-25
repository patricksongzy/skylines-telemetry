package patricksongzy.skylinestelemetry.processing.data.transfer;

import patricksongzy.skylinestelemetry.processing.data.TelemetryData;
import patricksongzy.skylinestelemetry.processing.skylines.TransferReason;

public interface EnrichedTransfer extends TelemetryData<Integer> {
    boolean isActive();

    int getPriority();

    void setPriority(int priority);

    int getAmount();

    void setAmount(int amount);

    int getPositionEastward();

    void setPositionEastward(int positionEastward);

    int getPositionNorthward();

    void setPositionNorthward(int positionNorthward);

    TransferReason getReason();

    void setReason(TransferReason reason);

    int getBuilding();

    void setBuilding(int building);

    int getVehicle();

    void setVehicle(int vehicle);

    int getCitizen();

    void setCitizen(int citizen);

    void setIsActive(boolean isActive);
}
