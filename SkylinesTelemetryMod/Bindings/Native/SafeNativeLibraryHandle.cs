using System;
using System.Runtime.InteropServices;

namespace SkylinesTelemetryMod.Bindings.Native
{
    internal class SafeNativeLibraryHandle : SafeHandle
    {
        public SafeNativeLibraryHandle() : base(IntPtr.Zero, true) { }

        public override bool IsInvalid => handle == IntPtr.Zero;

        protected override bool ReleaseHandle()
        {
            return PlatformNative.FreeLibrary(handle);
        }
    }
}
