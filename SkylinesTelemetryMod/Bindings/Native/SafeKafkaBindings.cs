using System;
using System.IO;
using System.Runtime.InteropServices;
using static SkylinesTelemetryMod.Bindings.Native.KafkaDefs;

namespace SkylinesTelemetryMod.Bindings.Native
{
    public class SafeKafkaBindings : IDisposable
    {
        private readonly SafeNativeLibraryHandle _library;

        public SafeKafkaBindings(string path)
        {
            Directory.SetCurrentDirectory(path);
            _library = PlatformNative.LoadLibrary("librdkafka.dll");
            if (_library.IsInvalid)
            {
                _library = null;
            }

            _createConf = GetDelegate<rd_kafka_conf_new>();
            _setConf = GetDelegate<rd_kafka_conf_set>();
            _createHandle = GetDelegate<rd_kafka_new>();
            _produceVa = GetDelegate<rd_kafka_produceva>();
            _flush = GetDelegate<rd_kafka_flush>();
            _destroyTopic = GetDelegate<rd_kafka_topic_destroy>();
            _destroyConf = GetDelegate<rd_kafka_conf_destroy>();
            _destroyHandle = GetDelegate<rd_kafka_destroy>();
        }

        private readonly rd_kafka_conf_new _createConf;
        private readonly rd_kafka_conf_set _setConf;
        private readonly rd_kafka_new _createHandle;
        private readonly rd_kafka_produceva _produceVa;
        private readonly rd_kafka_flush _flush;
        private readonly rd_kafka_topic_destroy _destroyTopic;
        private readonly rd_kafka_conf_destroy _destroyConf;
        private readonly rd_kafka_destroy _destroyHandle;

        internal rd_kafka_conf_new CreateConf => _createConf;
        internal rd_kafka_conf_set SetConf => _setConf;
        internal rd_kafka_new CreateHandle => _createHandle;
        internal rd_kafka_produceva ProduceVa => _produceVa;
        internal rd_kafka_flush Flush => _flush;
        internal rd_kafka_topic_destroy DestroyTopic => _destroyTopic;
        internal rd_kafka_conf_destroy DestroyConf => _destroyConf;
        internal rd_kafka_destroy DestroyHandle => _destroyHandle;

        public void Dispose()
        {
            _library?.Close();
        }

        private T GetDelegate<T>()
            where T : Delegate
        {
            return (T)Marshal.GetDelegateForFunctionPointer(PlatformNative.GetProcAddress(_library, typeof(T).Name), typeof(T));
        }
    }
}
