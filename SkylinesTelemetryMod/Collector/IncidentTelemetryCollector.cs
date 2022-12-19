using System.Text;
using ColossalFramework;

namespace SkylinesTelemetryMod.Collector
{
    public class IncidentTelemetryCollector : SkylinesTelemetryCollector<string, string>
    {
        private readonly SimulationManager _simulationManager;

        public IncidentTelemetryCollector()
        {
            _simulationManager = Singleton<SimulationManager>.instance;
        }

        public override void OnBeforeSimulationFrame()
        {
            base.OnBeforeSimulationFrame();
            if (IsPaused) { return; }
            UpdateTransfers();
        }

        private void UpdateTransfers()
        {
            var reason = TransferManager.GetFrameReason((int)_simulationManager.m_currentFrameIndex + 1);
            Publish("in.telemetry.transfer.fire", "reason", reason.ToString());
            switch (reason)
            {
                case TransferManager.TransferReason.Fire:
                    break;
                default:
                    break;
            }
        }
    }
}
