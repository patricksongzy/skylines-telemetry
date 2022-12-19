using System;
using System.Runtime.InteropServices;

namespace SkylinesTelemetryMod.Bindings.Native
{
    internal class SafeKafkaConf : SafeKafkaPtrHandle {
        public SafeKafkaConf() : base(false)
        {
        }

        public override bool ReleaseKafkaHandle(SafeKafkaBindings kafka)
        {
            kafka.DestroyConf(handle);
            return true;
        }
    }
}
