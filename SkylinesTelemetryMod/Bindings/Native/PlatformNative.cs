using System;
using System.Runtime.InteropServices;

namespace SkylinesTelemetryMod.Bindings.Native
{
    internal static partial class PlatformNative
    {
        public const string KernelLib = "kernel32";

        [DllImport(KernelLib, SetLastError = true)]
        internal static extern SafeNativeLibraryHandle LoadLibrary(string fileName);
        [DllImport(KernelLib, SetLastError = true)]
        internal static extern IntPtr GetProcAddress(SafeNativeLibraryHandle module, string procedureName);
        [DllImport(KernelLib, SetLastError = true)]
        internal static extern bool FreeLibrary(IntPtr module);
    }
}
