using System;
using System.Collections.Generic;
using ColossalFramework;
using SkylinesTelemetryMod.Data;
using SkylinesTelemetryMod.Mapper;

namespace SkylinesTelemetryMod.Extension
{
    internal static class TransferManagerExtension
    {
        private static readonly IMapper<TransferManager.TransferOffer, ITransfer> Mapper = new TransferMapper();

        public static TransferManager.TransferReason 

        public static IEnumerable<KeyValuePair<uint, TransferManager.TransferOffer>> GetOutgoingTransfers(this TransferManager transferManager)
        {
            var outgoingOffers = SkylinesReflection.GetPrivateField<TransferManager.TransferOffer[], TransferManager>(transferManager, "m_outgoingOffers");
            var outgoingCounts = SkylinesReflection.GetPrivateField<ushort[], TransferManager>(transferManager, "m_outgoingCount");
            if (outgoingOffers == null || outgoingCounts == null)
            {
                yield break;
            }

            var reasons = (TransferManager.TransferReason[])Enum.GetValues(typeof(TransferManager.TransferReason));
            for (uint i = 0; i < reasons.Length; i++)
            {
                if (reasons[i] == TransferManager.TransferReason.None)
                {
                    continue;
                }

                for (uint p = 0; p < 8; p++)
                {
                    var block = i * 8 + p;
                    var count = outgoingCounts[block];
                    for (uint j = 0; j < count; j++)
                    {
                        var index = block * 256 + j;
                        var key = new TelemetryMetadata<uint>(index);
                        ITransfer result = new SkylinesTransfer
                        {
                            Timestamp = Singleton<SimulationManager>.instance.GetTimestamp(),
                            Reason = i,
                        };
                        Mapper.Convert(outgoingOffers[index], ref result);
                        yield return new KeyValuePair<ITelemetryMetadata, ITelemetryData>(key, result);
                    }
                }
            }
        }
    }
}
