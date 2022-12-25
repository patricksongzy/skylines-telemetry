using System;

namespace SkylinesTelemetryMod.Data
{
    public interface ITelemetryMetadata
    {
    }
    public interface ITelemetryMetadata<TKey> : ITelemetryMetadata
    {
        TKey Key { get; set; }
    }

    public interface ITelemetryData
    {
        DateTime Timestamp { get; set; }
    }
}
