package patricksongzy.skylinestelemetry.processing.stream;

import jnr.ffi.LibraryLoader;
import jnr.ffi.Memory;
import jnr.ffi.Pointer;
import jnr.ffi.Struct;
import jnr.ffi.annotations.In;
import jnr.ffi.annotations.Out;
import jnr.ffi.types.size_t;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import patricksongzy.skylinestelemetry.processing.data.Complex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FourierWindowFunction implements AllWindowFunction<Long, Complex[], TimeWindow> {
    private static final long COMPLEX_SIZE = Struct.size(Complex.class);
    private static final SkylinesFourier fourier;
    static {
        fourier = LibraryLoader.create(SkylinesFourier.class).search(".").load("skylines_fourier_ffi");
    }

    @Override
    public void apply(TimeWindow window, Iterable<Long> values, Collector<Complex[]> out) throws Exception {
        // we use a simple difference-based detrending for now
        List<Double> data = new ArrayList<>();
        Iterator<Long> it = values.iterator();
        long previous = it.next();
        while (it.hasNext()) {
            data.add((double) (it.next() - previous));
        }
        Pointer resultPtr = Memory.allocateDirect(jnr.ffi.Runtime.getRuntime(fourier), COMPLEX_SIZE * data.size());
        fourier.rfft(data.size(), data.stream().mapToDouble(Double::doubleValue).toArray(), resultPtr);
        Complex[] output = new Complex[data.size()];
        for (int i = 0; i < data.size(); i++) {
            output[i] = new Complex(jnr.ffi.Runtime.getSystemRuntime());
            output[i].useMemory(resultPtr.slice((long) i * COMPLEX_SIZE, COMPLEX_SIZE));
        }
        // the sample period is the window size divided by the number of samples in years
        Pointer frequencyResultPtr = Memory.allocateDirect(jnr.ffi.Runtime.getRuntime(fourier), Long.BYTES * data.size());
        fourier.get_real_frequencies(data.size(), 3650.0 / data.size(), frequencyResultPtr);
        out.collect(output);
    }

    public interface SkylinesFourier {
        void rfft(@In @size_t long signal_length, @In double[] input_signal, @Out Pointer output_signal);
        void get_real_frequencies(@In @size_t long signal_length, @In double sample_period, @Out Pointer result_buffer);
    }
}
