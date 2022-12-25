package patricksongzy.skylinestelemetry.processing.skylines;

import java.util.Arrays;
import java.util.EnumSet;

public enum VehicleCategory {
    NONE(0),
    BICYCLE(1),
    PASSENGER_CAR(2),
    CARGO_TRUCK(3),
    BUS(4),
    TROLLEYBUS(5),
    TAXI(6),
    PASSENGER_PLANE(7),
    CARGO_PLANE(8),
    PRIVATE_PLANE(9),
    PASSENGER_COPTER(10),
    PASSENGER_BALLOON(11),
    PASSENGER_BLIMP(12),
    PASSENGER_SHIP(13),
    CARGO_SHIP(14),
    PASSENGER_FERRY(15),
    FISHING_BOAT(16),
    PASSENGER_TRAIN(17),
    CARGO_TRAIN(18),
    METRO_TRAIN(19),
    MONORAIL(20),
    TRAM(21),
    CABLE_CAR(22),
    AMBULANCE(23),
    HEARSE(24),
    POLICE(25),
    DISASTER(26),
    FIRETRUCK(27),
    POST_TRUCK(28),
    GARBAGE_TRUCK(29),
    MAINTENANCE_TRUCK(30),
    PARK_TRUCK(31),
    SNOW_TRUCK(32),
    VACUUM_TRUCK(33),
    AMBULANCE_COPTER(34),
    FIRE_COPTER(35),
    POLICE_COPTER(36),
    DISASTER_COPTER(37),
    ROCKET(38),
    METEOR(39),
    VORTEX(40),
    BANK_TRUCK(41);

    private final int shift;

    VehicleCategory(int shift) {
        this.shift = shift;
    }

    public static EnumSet<VehicleCategory> getVehicleCategories(long categories) {
        return EnumSet.of(NONE, Arrays.stream(VehicleCategory.values()).filter(v -> ((categories >> v.shift) & 1) == 1)
                .toArray(VehicleCategory[]::new));
    }
}
