using SkylinesTelemetryMod.Data;

namespace SkylinesTelemetryMod.Mapper
{
    internal class VehicleMapper : IMapper<Vehicle, IVehicle>
    {
        public void Convert(Vehicle data, ref IVehicle result)
        {
            result.Speed = data.GetLastFrameVelocity().magnitude * SkylinesGame.SkylinesSpeedScale;
            result.PositionEastward = data.GetLastFramePosition().x;
            result.PositionNorthward = data.GetLastFramePosition().z;
            result.Source = data.m_sourceBuilding;
            result.Target = data.m_sourceBuilding;
            result.VehicleCategory = (long)data.Info.vehicleCategory;
            result.Flags = (long)data.m_flags | ((long)data.m_flags2 << 32);
        }
    }
}