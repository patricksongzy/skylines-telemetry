using System.Collections.Generic;
using System.Linq;
using ColossalFramework;
using ColossalFramework.Threading;
using Common.Logging;
using ICities;
using SkylinesTelemetryMod.Collector.Updater;
using SkylinesTelemetryMod.Extension;

namespace SkylinesTelemetryMod.Collector
{
    public class ThreadingExtensionCollector : SkylinesTelemetryCollector<string, string>, IThreadingExtension
    {
        private readonly ILog _log = LogManager.GetLogger<ThreadingExtensionCollector>();
        private readonly SimulationManager _simulationManager;
        private bool _isInitialized = false;
        private IDictionary<string, IThreadingExtensionUpdaterService>? _updaters;

        private bool IsPaused => _simulationManager.SimulationPaused;

        public ThreadingExtensionCollector()
        {
            _simulationManager = Singleton<SimulationManager>.instance;
        }

        public void OnCreated(IThreading threading)
        {
        }

        public override void OnInitialized()
        {
            _isInitialized = true;
            _updaters = context?.GetObject<IDictionary<string, IThreadingExtensionUpdaterService>>();
            _log.Debug("Threading extension collector initialized");
        }

        public void OnReleased()
        {
        }

        public void OnUpdate(float realTimeDelta, float simulationTimeDelta)
        {
            PublishUpdates((uint)_simulationManager.GetTimestamp().Second, UpdateLifecycle.Update);
        }

        public void OnBeforeSimulationTick()
        {
            PublishUpdates(_simulationManager.m_currentTickIndex, UpdateLifecycle.BeforeSimulationTick);
        }

        public void OnBeforeSimulationFrame()
        {
            PublishUpdates(_simulationManager.m_currentFrameIndex, UpdateLifecycle.BeforeSimulationFrame);
        }

        public void OnAfterSimulationFrame()
        {
            PublishUpdates(_simulationManager.m_currentFrameIndex, UpdateLifecycle.AfterSimulationFrame);
        }

        public void OnAfterSimulationTick()
        {
            PublishUpdates(_simulationManager.m_currentTickIndex, UpdateLifecycle.AfterSimulationTick);
        }

        private void PublishUpdates(uint tick, UpdateLifecycle lifecycle)
        {
            if (IsPaused || !_isInitialized) { return; }
            var tasks = _updaters?.Where(updater => tick % updater.Value.Interval == 0 && updater.Value.Lifecycle == lifecycle).Select(updater => Task.Create(() =>
            {
                foreach (var telemetry in updater.Value.GetUpdates())
                {
                    var key = SimpleJson.SerializeObject(telemetry.Key);
                    var value = SimpleJson.SerializeObject(telemetry.Value);
                    Publish(updater.Key, key, value);
                }
            }).Run()).ToArray() ?? new Task[0];
            Task.WaitAll(tasks);
        }
    }
}
