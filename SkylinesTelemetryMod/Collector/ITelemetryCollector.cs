using System;
using SkylinesTelemetryMod.Publisher;

namespace SkylinesTelemetryMod.Collector
{
    public interface ITelemetryCollector
    {
        Type GetPublisherType();
        IPublisherService Publisher { set; }
    }
}
