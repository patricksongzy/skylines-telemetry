namespace SkylinesTelemetryMod.Mapper
{
    public interface IMapper<in TIn, TOut>
    {
        void Convert(TIn data, ref TOut result);
    }
}
