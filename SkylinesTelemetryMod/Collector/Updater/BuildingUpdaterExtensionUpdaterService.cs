using System;
using System.Collections.Generic;
using ColossalFramework;
using SkylinesTelemetryMod.Data;

namespace SkylinesTelemetryMod.Collector.Updater
{
    internal class BuildingUpdaterExtensionUpdaterService : IThreadingExtensionUpdaterService
    {
        private BuildingManager _buildingManager;
        private CitizenManager _citizenManager;

        public BuildingUpdaterExtensionUpdaterService()
        {
            _buildingManager = Singleton<BuildingManager>.instance;
            _citizenManager = Singleton<CitizenManager>.instance;
        }

        public IEnumerable<KeyValuePair<ITelemetryMetadata, ITelemetryData>> GetUpdates()
        {
            for (var i = 1; i < BuildingManager.MAX_BUILDING_COUNT; i++)
            {
                var building = _buildingManager.m_buildings.m_buffer[i];
                if (building.m_flags != Building.Flags.None)
                {
                    var sick = 0;
                    var bodies = 0;
                    var unitIndex = building.m_citizenUnits;
                    while (unitIndex != 0)
                    {
                        var unit = _citizenManager.m_units.m_buffer[unitIndex];
                        for (var j = 0; j < 5; j++)
                        {
                            var citizen = _citizenManager.m_citizens.m_buffer[unit.GetCitizen(j)];
                            if (citizen.Sick)
                            {
                                sick += 1;
                            }

                            if (citizen.Dead)
                            {
                                bodies += 1;
                            }
                        }
                        unitIndex = unit.m_nextUnit;
                    }
                }
            }
            throw new NotImplementedException();
        }

        public Type KeyType => typeof(string);
        public uint Interval => 1;
        public UpdateLifecycle Lifecycle => UpdateLifecycle.AfterSimulationFrame;
    }
}
