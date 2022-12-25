using System;

namespace SkylinesTelemetryMod.Extension
{
    internal static class SimulationManagerExtension
    {
        public static DateTime GetTimestamp(this SimulationManager simulationManager)
        {
            return simulationManager.m_metaData.m_currentDateTime.ToUniversalTime();
        }
    }
}
