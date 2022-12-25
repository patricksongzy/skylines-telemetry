namespace SkylinesTelemetryMod.Data
{
    public interface IVehicle : ITelemetryData
    {
        float Speed { get; set; }
        float PositionEastward { get; set; }
        float PositionNorthward { get; set; }
        ushort Source { get; set; }
        ushort Target { get; set; }
        int TransferSize { get; set; }
        long VehicleCategory { get; set; }
        long Flags { get; set; }
    }
}
