using ColossalFramework.Threading;

namespace SkylinesTelemetryMod.Publisher
{
    public interface IPublisherService
    {
    }

    public interface IPublisherService<in TKey, in TValue> : IPublisherService
    {
        bool Publish(string path, TKey key, TValue value);

        Task<bool> PublishAsync(string path, TKey key, TValue value);
    }
}
