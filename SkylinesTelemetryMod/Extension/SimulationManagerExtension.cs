using System;

namespace SkylinesTelemetryMod.Extension
{
    internal static class SimulationManagerExtension
    {
        public static DateTime Timestamp(this SimulationManager simulationManager)
        {
            return simulationManager.m_metaData.m_currentDateTime.ToUniversalTime();
        }
    }
}
