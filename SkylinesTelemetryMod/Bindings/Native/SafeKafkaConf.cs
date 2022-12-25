using Common.Logging;

namespace SkylinesTelemetryMod.Bindings.Native
{
    internal class SafeKafkaConf : SafeKafkaPtrHandle
    {
        private readonly ILog _log = LogManager.GetLogger<SafeKafkaBindings>();

        public SafeKafkaConf() : base(false)
        {
            _log.Debug("Created Kafka conf");
        }

        public override bool ReleaseKafkaHandle(SafeKafkaBindings kafka)
        {
            _log.Debug("Releasing Kafka conf");
            kafka.DestroyConf(handle);
            return true;
        }
    }
}
