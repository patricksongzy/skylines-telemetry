using System;
using ColossalFramework;
using ICities;
using SkylinesTelemetryMod.Publisher;

namespace SkylinesTelemetryMod.Collector
{
    public abstract class SkylinesTelemetryCollector<TKey, TValue> : ThreadingExtensionBase, ITelemetryCollector
    {
        private IPublisherService<TKey, TValue> _publisher;

        public IPublisherService Publisher { set => _publisher = (IPublisherService<TKey, TValue>)value; }
        private protected bool IsPaused => Singleton<SimulationManager>.instance.SimulationPaused;

        protected void Publish(string topic, TKey key, TValue value)
        {
            _publisher?.Publish(topic, key, value);
        }

        public Type GetPublisherType()
        {
            return typeof(SkylinesTelemetryCollector<TKey, TValue>).GetProperty(nameof(Publisher))?.PropertyType;
        }
    }
}
