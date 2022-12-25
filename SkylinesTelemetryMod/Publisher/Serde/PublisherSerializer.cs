using System.Text;

namespace SkylinesTelemetryMod.Publisher.Serde
{
    public class PublisherStringSerializer : IPublisherSerializer<string>
    {
        public byte[] Serialize(string data) => Encoding.UTF8.GetBytes(data);
    }

    public class PublisherByteSerializer : IPublisherSerializer<byte[]>
    {
        public byte[] Serialize(byte[] data) => data;
    }
}
