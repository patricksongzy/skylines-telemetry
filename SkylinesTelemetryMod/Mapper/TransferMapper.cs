using SkylinesTelemetryMod.Data;

namespace SkylinesTelemetryMod.Mapper;

internal class TransferMapper : IMapper<TransferManager.TransferOffer, ITransfer>
{
    public void Convert(TransferManager.TransferOffer data, ref ITransfer result)
    {
        result.Active = data.Active;
        result.Amount = data.Amount;
        result.PositionEastward = data.PositionX;
        result.PositionNorthward = data.PositionZ;
        result.Building = data.Building;
        result.Vehicle = data.Vehicle;
        result.Citizen = data.Citizen;
        result.Priority = data.Priority;
    }
}