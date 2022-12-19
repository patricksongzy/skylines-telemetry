using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SkylinesTelemetryMod.Bindings.Native;
using Spring.Stereotype;

namespace SkylinesTelemetryMod.Bindings
{
    [Component]
    public class KafkaConf : IDisposable
    {
        internal SafeKafkaConf ConfHandle { get; }

        /// <summary>
        /// RdKafka takes ownership of the config after a Kafka handle is created, meaning the handle is not owned.
        /// The handle must only be destroyed if and only if there is an error in creating the Kafka handle.
        /// </summary>
        public KafkaConf(SafeKafkaBindings kafka, Dictionary<string, string> conf)
        {
            ConfHandle = kafka.CreateConf();
            ConfHandle.Kafka = kafka;

            var err = new StringBuilder(512);
            var success = conf.Keys.Select(key => kafka.SetConf(ConfHandle, key, conf[key], err, err.Capacity))
                .All(res => res == 0);

            if (!success)
            {
                ConfHandle.Dispose();
                throw new InvalidOperationException(err.ToString());
            }
        }

        public void Dispose()
        {
            ConfHandle?.Dispose();
            GC.SuppressFinalize(this);
        }
    }
}
