using System;
using System.IO;
using System.Runtime.InteropServices;
using Common.Logging;
using static SkylinesTelemetryMod.Bindings.Native.KafkaDefs;

namespace SkylinesTelemetryMod.Bindings.Native
{
    public class SafeKafkaBindings : IDisposable
    {
        private readonly ILog _log = LogManager.GetLogger<SafeKafkaBindings>();
        private readonly SafeNativeLibraryHandle? _library;

        public SafeKafkaBindings(string path)
        {
            _log.Debug("Loading librdkafka");
            Directory.SetCurrentDirectory(path);
            _library = PlatformNative.LoadLibrary("librdkafka.dll");
            if (_library.IsInvalid)
            {
                _library = null;
            }
            _log.Debug("Loaded librdkafka");

            CreateConf = GetDelegate<rd_kafka_conf_new>();
            SetConf = GetDelegate<rd_kafka_conf_set>();
            CreateHandle = GetDelegate<rd_kafka_new>();
            ProduceVa = GetDelegate<rd_kafka_produceva>();
            Flush = GetDelegate<rd_kafka_flush>();
            DestroyTopic = GetDelegate<rd_kafka_topic_destroy>();
            DestroyConf = GetDelegate<rd_kafka_conf_destroy>();
            DestroyHandle = GetDelegate<rd_kafka_destroy>();
        }

        internal rd_kafka_conf_new CreateConf { get; }

        internal rd_kafka_conf_set SetConf { get; }

        internal rd_kafka_new CreateHandle { get; }

        internal rd_kafka_produceva ProduceVa { get; }

        internal rd_kafka_flush Flush { get; }

        internal rd_kafka_topic_destroy DestroyTopic { get; }

        internal rd_kafka_conf_destroy DestroyConf { get; }

        internal rd_kafka_destroy DestroyHandle { get; }

        public void Dispose()
        {
            _log.Debug("Unloading librdkafka");
            _library?.Close();
        }

        private T GetDelegate<T>()
            where T : Delegate
        {
            if (_library != null)
            {
                return (T)Marshal.GetDelegateForFunctionPointer(PlatformNative.GetProcAddress(_library, typeof(T).Name), typeof(T));
            }

            throw new InvalidOperationException("Error: cannot load delegate as library reference is null");
        }
    }
}
