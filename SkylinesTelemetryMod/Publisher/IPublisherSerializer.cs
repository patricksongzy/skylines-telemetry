namespace SkylinesTelemetryMod.Publisher
{
    public interface IPublisherSerializer<in T>
    {
        byte[] Serialize(T data);
    }
}
