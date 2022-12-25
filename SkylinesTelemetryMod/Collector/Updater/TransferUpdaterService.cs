using System.Collections.Generic;
using ColossalFramework;
using SkylinesTelemetryMod.Data;
using SkylinesTelemetryMod.Extension;

namespace SkylinesTelemetryMod.Collector.Updater
{
    internal class TransferUpdaterService : IThreadingExtensionUpdaterService
    {
        private readonly TransferManager _transferManager;

        public TransferUpdaterService()
        {
            _transferManager = Singleton<TransferManager>.instance;
        }

        public uint Interval => 1;
        public UpdateLifecycle Lifecycle => UpdateLifecycle.BeforeSimulationFrame;

        public IEnumerable<KeyValuePair<ITelemetryMetadata, ITelemetryData>> GetUpdates()
        {
            return _transferManager.GetOutgoingTransfers();
        }
    }
}
