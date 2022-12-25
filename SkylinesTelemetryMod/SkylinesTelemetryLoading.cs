using System.Collections.Generic;
using System.IO;
using System.Linq;
using ColossalFramework;
using ColossalFramework.UI;
using Common.Logging;
using Common.Logging.Configuration;
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
        private IApplicationContext? _context;

        public void OnCreated(ILoading loading)
        {
            var properties = new NameValueCollection
            {
                ["dateTimeFormat"] = "yyyy-MM-ddTHH:mm:ss"
            };
            LogManager.Adapter = new SkylinesLogFactory(properties);
        }

        public void OnLevelLoaded(LoadMode mode)
        {
            var configurationPath = Path.Combine(SkylinesGame.HomePath(), "services.xml");
            _context = new XmlApplicationContext(configurationPath);
            // Reflection required to access game data
            var threadingExtensions = SkylinesReflection.GetPrivateField<List<IThreadingExtension>, ThreadingWrapper>(Singleton<SimulationManager>
                    .instance.m_ThreadingWrapper, "m_ThreadingExtensions");
            threadingExtensions?.OfType<ITelemetryCollector>().ForEach(collector =>
            {
                // Inject the dependencies
                collector.ApplicationContext = _context;
                collector.Publisher = (IPublisherService)_context.GetObjectsOfType(collector.GetPublisherType()).Values.First();
                collector.OnInitialized();
            });
        }

        public void OnLevelUnloading()
        {
        }

        public void OnReleased()
        {
            // Spring should dispose all objects
            _context?.Dispose();
        }
    }
}