using System.Collections.Generic;
using ColossalFramework;
using SkylinesTelemetryMod.Data;
using SkylinesTelemetryMod.Extension;
using SkylinesTelemetryMod.Mapper;

namespace SkylinesTelemetryMod.Collector.Updater
{
    internal class ServiceVehicleUpdaterService : IThreadingExtensionUpdaterService
    {
        private readonly IMapper<Vehicle, IVehicle> _mapper;
        private readonly ItemClass.Service _service;

        public ServiceVehicleUpdaterService(ItemClass.Service service, IMapper<Vehicle, IVehicle> mapper)
        {
            _service = service;
            _mapper = mapper;
        }

        public uint Interval => 1;
        public UpdateLifecycle Lifecycle => UpdateLifecycle.AfterSimulationFrame;

        public IEnumerable<KeyValuePair<ITelemetryMetadata, ITelemetryData>> GetUpdates()
        {
            var buildingManager = Singleton<BuildingManager>.instance;
            var simulationManager = Singleton<SimulationManager>.instance;
            var vehicleManager = Singleton<VehicleManager>.instance;
            var serviceBuildingIds = buildingManager.GetServiceBuildings(_service);
            foreach (var buildingId in serviceBuildingIds)
            {
                var building = buildingManager.m_buildings.m_buffer[buildingId];
                var vehicleId = building.m_ownVehicles;
                while (vehicleId != 0)
                {
                    var vehicle = vehicleManager.m_vehicles.m_buffer[vehicleId];
                    var key = new TelemetryMetadata<ushort>(vehicleId);
                    IVehicle result = new SkylinesVehicle
                    {
                        Timestamp = simulationManager.GetTimestamp(),
                    };
                    _mapper.Convert(vehicle, ref result);
                    yield return new KeyValuePair<ITelemetryMetadata, ITelemetryData>(key, result);

                    vehicleId = vehicle.m_nextOwnVehicle;
                }
            }
        }
    }
}
