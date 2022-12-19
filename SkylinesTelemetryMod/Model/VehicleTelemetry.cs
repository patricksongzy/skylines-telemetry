using System;

namespace SkylinesTelemetryMod.Model
{
    public abstract class VehicleTelemetry : ITelemetryData<ushort>
    {
        public DateTime Timestamp { get; }
        public ushort Key { get; }

        public float Speed { get; }
        public float X { get; }
        public float Y { get; }
        public ushort Source { get; }
        public ushort Target { get; }
        public long Flags { get; }

        protected VehicleTelemetry(DateTime timestamp, ushort id, Vehicle vehicle)
        {
            Timestamp = timestamp;
            Key = id;
            X = vehicle.GetLastFramePosition().x;
            Y = vehicle.GetLastFramePosition().z;
            Speed = vehicle.GetLastFrameVelocity().magnitude * SkylinesGame.SkylinesSpeedScale;
            Source = vehicle.m_sourceBuilding;
            Target = vehicle.m_targetBuilding;
            Flags = (long)vehicle.m_flags | ((long)vehicle.m_flags2 << 32);
        }
    }
}
