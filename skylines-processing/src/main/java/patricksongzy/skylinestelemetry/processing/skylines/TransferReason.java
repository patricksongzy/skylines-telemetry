package patricksongzy.skylinestelemetry.processing.skylines;

import java.util.Arrays;

public enum TransferReason {
    GARBAGE(0), // type-service, source-building, target-vehicle
    CRIME(1),
    SICK(2),
    DEAD(3),
    WORKER0(4), // type-cim, source-cim, target-building
    WORKER1(5),
    WORKER2(6),
    WORKER3(7),
    STUDENT1(8),
    STUDENT2(9),
    STUDENT3(10),
    FIRE(11),
    BUS(12),
    OIL(13),
    ORE(14),
    LOGS(15),
    GRAIN(16),
    GOODS(17),
    PASSENGER_TRAIN(18),
    COAL(19),
    FAMILY0(20),
    FAMILY1(21),
    FAMILY2(22),
    FAMILY3(23),
    SINGLE0(24),
    SINGLE1(25),
    SINGLE2(26),
    SINGLE3(27),
    PARTNER_YOUNG(28),
    PARTNER_ADULT(29),
    SHOPPING(30),
    PETROL(31),
    FOOD(32),
    LEAVE_CITY0(33),
    LEAVE_CITY1(34),
    LEAVE_CITY2(35),
    ENTERTAINMENT(36),
    LUMBER(37),
    GARBAGE_MOVE(38),
    METRO_TRAIN(39),
    PASSENGER_PLANE(40),
    PASSENGER_SHIP(41),
    DEAD_MOVE(42),
    DUMMY_CAR(43),
    DUMMY_TRAIN(44),
    DUMMY_SHIP(45),
    DUMMY_PLANE(46),
    SINGLE0B(47),
    SINGLE1B(48),
    SINGLE2B(49),
    SINGLE3B(50),
    SHOPPINGB(51),
    SHOPPINGC(52),
    SHOPPINGD(53),
    SHOPPINGE(54),
    SHOPPINGF(55),
    SHOPPINGG(56),
    SHOPPINGH(57),
    ENTERTAINMENTB(58),
    ENTERTAINMENTC(59),
    ENTERTAINMENTD(60),
    TAXI(61),
    CRIMINAL_MOVE(62),
    TRAM(63),
    SNOW(64),
    SNOW_MOVE(65),
    ROAD_MAINTENANCE(66),
    SICK_MOVE(67),
    FOREST_FIRE(68),
    COLLAPSED(69),
    COLLAPSED2(70),
    FIRE2(71),
    SICK2(72),
    FLOODWATER(73),
    EVACUATEA(74),
    EVACUATEB(75),
    EVACUATEC(76),
    EVACUATED(77),
    EVACUATE_VIPA(78),
    EVACUATE_VIPB(79),
    EVACUATE_VIPC(80),
    EVACUATE_VIPD(81),
    FERRY(82),
    CABLE_CAR(83),
    BLIMP(84),
    MONORAIL(85),
    TOURIST_BUS(86),
    PARK_MAINTENANCE(87),
    TOURISTA(88),
    TOURISTB(89),
    TOURISTC(90),
    TOURISTD(91),
    MAIL(92),
    UNSORTED_MAIL(93),
    SORTED_MAIL(94),
    OUTGOING_MAIL(95),
    INCOMING_MAIL(96),
    ANIMAL_PRODUCTS(97),
    FLOURS(98),
    PAPER(99),
    PLANED_TIMBER(100),
    PETROLEUM(101),
    PLASTICS(102),
    GLASS(103),
    METALS(104),
    LUXURY_PRODUCTS(105),
    GARBAGE_TRANSFER(106),
    PASSENGER_HELICOPTER(107),
    FISH(108),
    TROLLEYBUS(109),
    ELDERCARE(110),
    CHILDCARE(111),
    INTERCITY_BUS(112),
    BIOFUEL_BUS(113),
    CASH(114),
    NONE(255);

    private final int reason;

    TransferReason(int reason) {
        this.reason = reason;
    }

    public static TransferReason getTransferReason(int reason) {
        return Arrays.stream(TransferReason.values()).filter(tr -> tr.reason == reason).findFirst().orElse(NONE);
    }

    public int getReason() {
        return reason;
    }
}
