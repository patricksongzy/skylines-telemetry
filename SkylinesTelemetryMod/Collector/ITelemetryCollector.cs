using System;
using SkylinesTelemetryMod.Publisher;
using Spring.Context;

namespace SkylinesTelemetryMod.Collector
{
    public interface ITelemetryCollector
    {
        Type? GetPublisherType();
        IPublisherService Publisher { set; }
        IApplicationContext ApplicationContext { set; }

        void OnInitialized();
    }
}
