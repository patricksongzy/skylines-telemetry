using System;
using System.Runtime.InteropServices;

namespace SkylinesTelemetryMod.Bindings.Native
{
    internal class SafeKafkaHandle : SafeKafkaPtrHandle
    {
        public SafeKafkaHandle() : base(true)
        {
        }

        public override bool ReleaseKafkaHandle(SafeKafkaBindings kafka)
        {
            kafka.DestroyHandle(handle);
            return true;
        }
    }
}
