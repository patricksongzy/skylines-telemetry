using System;
using System.Runtime.InteropServices;

namespace SkylinesTelemetryMod.Bindings.Native
{
    internal abstract class SafeKafkaPtrHandle : SafeHandle
    {
        private SafeKafkaBindings _kafka;

        protected SafeKafkaPtrHandle(bool ownsHandle) : base(IntPtr.Zero, ownsHandle)
        {
        }

        public override bool IsInvalid => handle == IntPtr.Zero;

        public SafeKafkaBindings Kafka { set => _kafka = value; }

        public abstract bool ReleaseKafkaHandle(SafeKafkaBindings kafka);

        protected override bool ReleaseHandle()
        {
            return IsInvalid || _kafka == null || ReleaseKafkaHandle(_kafka);
        }
    }
}