using System;
using ColossalFramework.Threading;
using Common.Logging;
using SkylinesTelemetryMod.Bindings;
using SkylinesTelemetryMod.Publisher.Serde;

namespace SkylinesTelemetryMod.Publisher
{
    public class SkylinesTelemetryPublisherService<TKey, TValue> : IPublisherService<TKey, TValue>, IDisposable
    {
        private readonly ILog _log = LogManager.GetLogger<SkylinesTelemetryPublisherService<TKey, TValue>>();
        private readonly KafkaHandle _kafka;
        private readonly IPublisherSerializer<TKey> _keySerializer;
        private readonly IPublisherSerializer<TValue> _valueSerializer;

        public SkylinesTelemetryPublisherService(KafkaHandle kafka, IPublisherSerializer<TKey> keySerializer, IPublisherSerializer<TValue> valueSerializer)
        {
            _kafka = kafka;
            _keySerializer = keySerializer;
            _valueSerializer = valueSerializer;
            _log.Debug("Created Skylines telemetry publisher service");
        }

        public void Dispose()
        {
            _log.Debug("Disposing Skylines telemetry publisher service");
            _kafka.Dispose();
        }

        public bool Publish(string path, TKey key, TValue value)
        {
            return _kafka.Produce(path, _keySerializer.Serialize(key), _valueSerializer.Serialize(value));
        }

        public Task<bool> PublishAsync(string path, TKey key, TValue value)
        {
            throw new NotImplementedException();
        }
    }
}
