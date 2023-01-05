#include <stddef.h>

struct Complex {
    double re;
    double im;
};

void rfft(const double *input_signal, struct Complex *output_buffer, const size_t signal_length);
