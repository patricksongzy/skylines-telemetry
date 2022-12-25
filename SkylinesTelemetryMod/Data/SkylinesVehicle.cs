using System;

namespace SkylinesTelemetryMod.Data
{
    internal class SkylinesVehicle : IVehicle
    {
        public DateTime Timestamp { get; set; }
        public float Speed { get; set; }
        public float PositionEastward { get; set; }
        public float PositionNorthward { get; set; }
        public ushort Source { get; set; }
        public ushort Target { get; set; }
        public long VehicleCategory { get; set; }
        public long Flags { get; set; }
    }
}
