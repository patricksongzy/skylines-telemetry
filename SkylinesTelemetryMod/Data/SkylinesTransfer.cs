using System;

namespace SkylinesTelemetryMod.Data
{
    internal class SkylinesTransfer : ITransfer
    {
        public DateTime Timestamp { get; set; }
        public bool Active { get; set; }
        public int Priority { get; set; }
        public int Amount { get; set; }
        public int PositionEastward { get; set; }
        public int PositionNorthward { get; set; }
        public uint Reason { get; set; }
        public ushort Building { get; set; }
        public ushort Vehicle { get; set; }
        public uint Citizen { get; set; }
    }
}
