using Common.Logging;

namespace SkylinesTelemetryMod.Bindings.Native
{
    internal class SafeKafkaHandle : SafeKafkaPtrHandle
    {
        private readonly ILog _log = LogManager.GetLogger<SafeKafkaBindings>();

        public SafeKafkaHandle() : base(true)
        {
            _log.Debug("Created Kafka handle");
        }

        public override bool ReleaseKafkaHandle(SafeKafkaBindings kafka)
        {
            _log.Debug("Releasing Kafka handle");
            kafka.DestroyHandle(handle);
            return true;
        }
    }
}
