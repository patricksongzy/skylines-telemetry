package patricksongzy.skylinestelemetry.processing.skylines;

import java.util.Arrays;
import java.util.EnumSet;

public enum VehicleFlag {
    CREATED(0),
    DELETED(1),
    SPAWNED(2),
    INVERTED(3),
    TRANSFER_TO_TARGET(4),
    TRANSFER_TO_SOURCE(5),
    EMERGENCY1(6),
    EMERGENCY2(7),
    WAITING_PATH(8),
    STOPPED(9),
    LEAVING(10),
    ARRIVING(11),
    REVERSED(12),
    TAKING_OFF(13),
    FLYING(14),
    LANDING(15),
    WAITING_SPACE(16),
    WAITING_CARGO(17),
    GOING_BACK(18),
    WAITING_TARGET(19),
    IMPORTING(20),
    EXPORTING(21),
    PARKING(22),
    CUSTOM_NAME(23),
    ON_GRAVEL(24),
    WAITING_LOADING(25),
    CONGESTION(26),
    DUMMY_TRAFFIC(27),
    UNDERGROUND(28),
    TRANSITION(29),
    INSIDE_BUILDING(30),
    LEFT_HAND_DRIVE(31),
    FLOATING(32),
    BLOWN(33),
    YIELDING(34),
    END_STOP(35),
    TRANSFER_TO_SERVICE_POINT(36);

    private final int shift;

    VehicleFlag(int shift) {
        this.shift = shift;
    }

    public static EnumSet<VehicleFlag> getVehicleFlags(long flags) {
        return EnumSet.of(CREATED, Arrays.stream(VehicleFlag.values()).filter(v -> ((flags >> v.shift) & 1) == 1)
                .toArray(VehicleFlag[]::new));
    }
}
