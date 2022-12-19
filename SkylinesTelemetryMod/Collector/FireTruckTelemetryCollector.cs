using ColossalFramework;
using Newtonsoft.Json;
using SkylinesTelemetryMod.Extension;
using SkylinesTelemetryMod.Model;

namespace SkylinesTelemetryMod.Collector
{
    public class FireTruckTelemetryCollector : SkylinesTelemetryCollector<string, string>
    {
        private readonly SimulationManager _simulationManager;
        private readonly BuildingManager _buildingManager;
        private readonly VehicleManager _vehicleManager;

        public FireTruckTelemetryCollector()
        {
            _simulationManager = Singleton<SimulationManager>.instance;
            _buildingManager = Singleton<BuildingManager>.instance;
            _vehicleManager = Singleton<VehicleManager>.instance;
        }

        public override void OnAfterSimulationTick()
        {
            base.OnAfterSimulationTick();
            if (IsPaused) { return; }
            UpdateVehicles();
        }

        private void UpdateVehicles()
        {
            var fireBuildingIds = _buildingManager.GetServiceBuildings(ItemClass.Service.FireDepartment);
            foreach (var buildingId in fireBuildingIds)
            {
                var building = _buildingManager.m_buildings.m_buffer[buildingId];
                var vehicleId = building.m_ownVehicles;
                while (vehicleId != 0)
                {
                    var vehicle = _vehicleManager.m_vehicles.m_buffer[vehicleId];
                    if (vehicle.Info.vehicleCategory == VehicleInfo.VehicleCategory.FireTruck)
                    {
                        var fireTruckTelemetry = new FireTruckTelemetry(_simulationManager.Timestamp(), vehicleId, vehicle);
                        Publish("in.telemetry.vehicle.firetruck", fireTruckTelemetry.Key.ToString(), JsonConvert.SerializeObject(fireTruckTelemetry));
                    }

                    vehicleId = vehicle.m_nextOwnVehicle;
                }
            }
        }
    }
}
