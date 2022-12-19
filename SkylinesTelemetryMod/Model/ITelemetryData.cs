using System;

namespace SkylinesTelemetryMod.Model
{
    public interface ITelemetryData<K>
    {
        DateTime Timestamp { get; }
        K Key { get; }
    }
}
