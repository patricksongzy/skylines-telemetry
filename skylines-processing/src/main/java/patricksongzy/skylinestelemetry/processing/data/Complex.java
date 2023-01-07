package patricksongzy.skylinestelemetry.processing.data;

import jnr.ffi.Struct;

public final class Complex extends Struct {
    public Struct.Double re;
    public Struct.Double im;
    public Complex(final jnr.ffi.Runtime runtime) {
        super(runtime);
        re = new Double();
        im = new Double();
    }
}
