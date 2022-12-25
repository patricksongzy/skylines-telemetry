namespace SkylinesTelemetryMod.Data
{
    internal class TelemetryMetadata<TKey> : ITelemetryMetadata<TKey>
    {
        public TelemetryMetadata(TKey key)
        {
            Key = key;
        }

        public TKey Key { get; set; }
    }
}
