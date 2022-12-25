using System;
using System.ComponentModel;
using System.Runtime.InteropServices;
using System.Text;
// ReSharper disable InconsistentNaming

namespace SkylinesTelemetryMod.Bindings.Native
{
    public static class KafkaDefs
    {
        [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
        internal delegate SafeKafkaConf rd_kafka_conf_new();
        [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
        internal delegate int rd_kafka_conf_set(SafeKafkaConf conf, string name, string value, StringBuilder errstr, int errstr_size);
        [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
        internal delegate SafeKafkaHandle rd_kafka_new(rd_kafka_type type, SafeKafkaConf conf, StringBuilder errstr, int errstr_size);
        [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
        internal delegate IntPtr rd_kafka_produceva(SafeKafkaHandle rk, rd_kafka_vu[] vus, int cnt);
        [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
        internal delegate int rd_kafka_flush(SafeKafkaHandle rk, int timeout_ms);
        [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
        internal delegate void rd_kafka_topic_destroy(IntPtr rkt);
        [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
        internal delegate void rd_kafka_conf_destroy(IntPtr conf);
        [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
        internal delegate void rd_kafka_destroy(IntPtr rk);

#pragma warning disable IDE1006 // Naming Styles
        [TypeConverter(typeof(EnumConverter))]
        public enum rd_kafka_type
        {
            RD_KAFKA_PRODUCER,
            RD_KAFKA_CONSUMER,
        }

        public enum rd_kafka_vtype
        {
            RD_KAFKA_VTYPE_END,
            RD_KAFKA_VTYPE_TOPIC,
            RD_KAFKA_VTYPE_RKT,
            RD_KAFKA_VTYPE_PARTITION,
            RD_KAFKA_VTYPE_VALUE,
            RD_KAFKA_VTYPE_KEY,
            RD_KAFKA_VTYPE_OPAQUE,
            RD_KAFKA_VTYPE_MSGFLAGS,
            RD_KAFKA_VTYPE_TIMESTAMP,
            RD_KAFKA_VTYPE_HEADER,
            RD_KAFKA_VTYPE_HEADERS,
        }

        public enum rk_msgflags : int
        {
            EMPTY = 0x0,
            RK_MSG_FREE = 0x1,
            RK_MSG_COPY = 0x2,
            RK_MSG_BLOCK = 0x4,
        }

        [StructLayout(LayoutKind.Sequential)]
        public struct rd_kafka_ptr
        {
            public IntPtr data;
            public int size;
        }

        [StructLayout(LayoutKind.Sequential)]
        public struct rd_kafka_header
        {
            public string name;
            public string value;
            public int len;
        }

        [StructLayout(LayoutKind.Explicit, Size = 64)]
        public struct rd_kafka_vdata
        {
            [FieldOffset(0)] public IntPtr topic; // topic name
            [FieldOffset(0)] public int partition; // partition
            [FieldOffset(0)] public rd_kafka_ptr value; // value
            [FieldOffset(0)] public rd_kafka_ptr key; // key
            [FieldOffset(0)] public IntPtr opaque; // opaque
            [FieldOffset(0)] public int flags; // flags
            [FieldOffset(0)] public long timestamp; // timestamp
            [FieldOffset(0)] public IntPtr headers; // header
        }
        public struct rd_kafka_vu
        {
            public rd_kafka_vtype vtype;
            public rd_kafka_vdata vdata;
        }
#pragma warning restore IDE1006 // Naming Styles
    }
}
