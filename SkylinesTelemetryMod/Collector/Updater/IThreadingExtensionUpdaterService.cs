namespace SkylinesTelemetryMod.Collector.Updater
{
    internal enum UpdateLifecycle { Update, BeforeSimulationTick, BeforeSimulationFrame, AfterSimulationFrame, AfterSimulationTick }

    internal interface IThreadingExtensionUpdaterService : IUpdaterService
    {
        uint Interval { get; }
        UpdateLifecycle Lifecycle { get; }
    }
}
