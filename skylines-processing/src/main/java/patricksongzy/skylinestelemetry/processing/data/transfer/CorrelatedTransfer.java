package patricksongzy.skylinestelemetry.processing.data.transfer;

import patricksongzy.skylinestelemetry.processing.data.vehicle.EnrichedVehicle;

public interface CorrelatedTransfer {
    EnrichedTransfer getTransfer();

    void setTransfer(EnrichedTransfer transfer);

    EnrichedVehicle getVehicle();

    void setVehicle(EnrichedVehicle vehicle);

}
