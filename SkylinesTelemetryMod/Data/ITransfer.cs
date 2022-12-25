namespace SkylinesTelemetryMod.Data
{
    public interface ITransfer : ITelemetryData
    {
        bool Active { get; set; }
        int Priority { get; set; }
        int Amount { get; set; }
        int PositionEastward { get; set; }
        int PositionNorthward { get; set; }
        uint Reason { get; set; }
        ushort Building { get; set; }
        ushort Vehicle { get; set; }
        uint Citizen { get; set; }
    }
}