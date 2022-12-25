using System;
using Common.Logging;
using Common.Logging.Configuration;
using Common.Logging.Simple;

namespace SkylinesTelemetryMod
{
    internal class SkylinesLogFactory : AbstractSimpleLoggerFactoryAdapter
    {
        public SkylinesLogFactory(NameValueCollection properties) : base(properties)
        {
        }

        protected override ILog CreateLogger(string name, LogLevel level, bool showLevel, bool showDateTime, bool showLogName,
            string dateTimeFormat)
        {
            return new SkylinesLog(name, level, showLevel, showDateTime, showLogName, dateTimeFormat);
        }
    }

    internal class SkylinesLog : AbstractSimpleLogger
    {
        public SkylinesLog(string logName, LogLevel logLevel, bool showlevel, bool showDateTime, bool showLogName, string dateTimeFormat) : base(logName, logLevel, showlevel, showDateTime, showLogName, dateTimeFormat)
        {
        }

        protected override void WriteInternal(LogLevel level, object message, Exception? exception)
        {
            switch (level)
            {
                case LogLevel.Trace:
                case LogLevel.Info:
                case LogLevel.Debug:
                    UnityEngine.Debug.Log(message);
                    break;
                case LogLevel.Warn:
                    UnityEngine.Debug.LogWarning(message);
                    break;
                case LogLevel.Error:
                case LogLevel.Fatal:
                    UnityEngine.Debug.LogError(message);
                    if (exception != null)
                    {
                        UnityEngine.Debug.LogException(exception);
                    }
                    break;
                case LogLevel.All:
                case LogLevel.Off:
                default:
                    break;
            }
        }
    }
}
