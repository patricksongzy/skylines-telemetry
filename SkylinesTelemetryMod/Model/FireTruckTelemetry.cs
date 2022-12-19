using System;

namespace SkylinesTelemetryMod.Model
{
    public class FireTruckTelemetry : VehicleTelemetry
    {
        public FireTruckTelemetry(DateTime timestamp, ushort id, Vehicle vehicle) : base(timestamp, id, vehicle)
        {
        }
    }
}
