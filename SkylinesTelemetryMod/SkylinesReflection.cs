namespace SkylinesTelemetryMod
{
    internal static class SkylinesReflection
    {
        internal static TField? GetPrivateField<TField, TSrc>(TSrc src, string name)
        {
            var fieldInfo = typeof(TSrc).GetField(name,
                System.Reflection.BindingFlags.NonPublic | System.Reflection.BindingFlags.Instance);
            if (fieldInfo == null)
            {
                return default;
            }

            return (TField)fieldInfo.GetValue(src);
        }
    }
}
