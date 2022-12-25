namespace SkylinesTelemetryMod.Publisher.Serde
{
    public interface IPublisherSerializer<in T>
    {
        byte[] Serialize(T data);
    }
}
