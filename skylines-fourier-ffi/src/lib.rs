use std::slice;

use skylines_fourier::{fft, complex::Complex};

/// Returns the real-valued FFT on the input signal, where the output is of length signal_length
#[no_mangle]
pub unsafe extern fn rfft(signal_length: usize, input_signal: *const f64, output_buffer: *mut Complex) {
    let result = fft::fourier::rfft(slice::from_raw_parts(input_signal, signal_length).to_vec());
    slice::from_raw_parts_mut(output_buffer, signal_length).copy_from_slice(&result);
}

pub unsafe extern fn get_real_frequencies(signal_length: usize, sample_period: f64, result_buffer: *mut f64) {
    let result = fft::fourier::get_real_frequencies(signal_length, sample_period);
    slice::from_raw_parts_mut(result_buffer, signal_length).copy_from_slice(&result);
}
