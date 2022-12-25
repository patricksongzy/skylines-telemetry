using System;
using System.Runtime.InteropServices;
using System.Text;
using SkylinesTelemetryMod.Bindings.Native;
using static SkylinesTelemetryMod.Bindings.Native.KafkaDefs;

namespace SkylinesTelemetryMod.Bindings
{
    public class KafkaHandle : IDisposable
    {
        private readonly SafeKafkaBindings _kafka;

        internal SafeKafkaHandle KafkaClient { get; }

        public KafkaHandle(SafeKafkaBindings kafka, rd_kafka_type type, KafkaConf conf)
        {
            _kafka = kafka;
            var err = new StringBuilder(512);
            KafkaClient = _kafka.CreateHandle(type, conf.ConfHandle, err, err.Capacity);

            if (KafkaClient.IsInvalid)
            {
                conf.Dispose();
                throw new InvalidOperationException(err.ToString());
            }

            KafkaClient.Kafka = _kafka;
        }

        public bool Produce(string topic, byte[] key, byte[] value)
        {
            var success = false;
            var topicPtr = Marshal.StringToHGlobalAnsi(topic);
            unsafe
            {
                fixed (byte* keyPtr = key)
                {
                    fixed (byte* valuePtr = value)
                    {
                        var vus = new[]
                        {
                            new rd_kafka_vu { vtype = rd_kafka_vtype.RD_KAFKA_VTYPE_TOPIC, vdata = new rd_kafka_vdata { topic = topicPtr } },
                            new rd_kafka_vu { vtype = rd_kafka_vtype.RD_KAFKA_VTYPE_KEY, vdata = new rd_kafka_vdata { key = new rd_kafka_ptr { data = (IntPtr)keyPtr, size = key?.Length ?? 0 } } },
                            new rd_kafka_vu { vtype = rd_kafka_vtype.RD_KAFKA_VTYPE_VALUE, vdata = new rd_kafka_vdata { value = new rd_kafka_ptr { data = (IntPtr)valuePtr, size = value?.Length ?? 0 } } },
                            new rd_kafka_vu { vtype = rd_kafka_vtype.RD_KAFKA_VTYPE_MSGFLAGS, vdata = new rd_kafka_vdata { flags = (int)rk_msgflags.EMPTY } },
                        };
                        var error = _kafka.ProduceVa(KafkaClient, vus, vus.Length);
                        Marshal.FreeHGlobal(topicPtr);
                        success = error == IntPtr.Zero;
                    }
                }
            }

            return success;
        }

        public void Dispose()
        {
            KafkaClient?.Dispose();
            GC.SuppressFinalize(this);
        }
    }
}
