#include "libskylines_fourier_ffi.h"

#include <stdlib.h>

int main(void) {
    double signal[8] = { 1, 1, 1, 1, 1, 1, 1, 1 };
    double expected[3] = { 3, 0, 0 };
    struct Complex *result = malloc(sizeof(struct Complex) * 8);
    rfft(signal, result, 8);
    for (size_t i = 0; i < 8; ++i) {
        printf("%f %f\n", result[i].re, result[i].im);
    }
    free(result);
}
