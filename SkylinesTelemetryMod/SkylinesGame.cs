using ColossalFramework;
using ColossalFramework.Plugins;

namespace SkylinesTelemetryMod
{
    public static class SkylinesGame
    {
        public const float SkylinesSpeedScale = 6.25f;
        public static string HomePath() => Singleton<PluginManager>.instance.FindPluginInfo(typeof(SkylinesGame).Assembly).modPath;
    }
}
