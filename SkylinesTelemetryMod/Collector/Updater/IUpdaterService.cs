using System.Collections.Generic;
using SkylinesTelemetryMod.Data;

namespace SkylinesTelemetryMod.Collector.Updater
{
    internal interface IUpdaterService
    {
        IEnumerable<KeyValuePair<ITelemetryMetadata, ITelemetryData>> GetUpdates();
    }
}
