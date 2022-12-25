using System;
using SkylinesTelemetryMod.Publisher;
using Spring.Context;

namespace SkylinesTelemetryMod.Collector
{
    public abstract class SkylinesTelemetryCollector<TKey, TValue> : ITelemetryCollector
    {
        private IPublisherService<TKey, TValue>? _publisher;
        private protected IApplicationContext? context;

        public IPublisherService Publisher { set => _publisher = (IPublisherService<TKey, TValue>)value; }
        public IApplicationContext ApplicationContext { set => context = value; }

        public abstract void OnInitialized();

        protected void Publish(string topic, TKey key, TValue value)
        {
            _publisher?.Publish(topic, key, value);
        }

        public Type? GetPublisherType()
        {
            return typeof(SkylinesTelemetryCollector<TKey, TValue>).GetProperty(nameof(Publisher))?.PropertyType;
        }
    }
}
