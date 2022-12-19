using System.Collections.Generic;
using System.IO;
using System.Linq;
using ColossalFramework;
using ColossalFramework.UI;
using ICities;
using JetBrains.Annotations;
using SkylinesTelemetryMod.Collector;
using SkylinesTelemetryMod.Publisher;
using Spring.Context;
using Spring.Context.Support;

namespace SkylinesTelemetryMod
{
    [UsedImplicitly]
    public class SkylinesTelemetryLoading : ILoadingExtension
    {
        private IApplicationContext _context;

        public void OnCreated(ILoading loading)
        {
        }

        public void OnLevelLoaded(LoadMode mode)
        {
            var configurationPath = Path.Combine(SkylinesGame.HomePath(), "services.xml");
            _context = new XmlApplicationContext(configurationPath);
            // Reflection required to access game data
            var threadingExtensions = ((List<IThreadingExtension>)typeof(ThreadingWrapper).GetField("m_ThreadingExtensions", System.Reflection.BindingFlags.NonPublic | System.Reflection.BindingFlags.Instance)?
                .GetValue(Singleton<SimulationManager>.instance.m_ThreadingWrapper));
            threadingExtensions?.OfType<ITelemetryCollector>().ForEach(collector =>
            {
                // Inject the publisher
                collector.Publisher = (IPublisherService)_context.GetObjectsOfType(collector.GetPublisherType()).Values.First();
            });
        }

        public void OnLevelUnloading()
        {
        }

        public void OnReleased()
        {
            // Spring should dispose all objects
            _context.Dispose();
        }
    }
}