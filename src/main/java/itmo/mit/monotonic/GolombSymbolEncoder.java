package itmo.mit.monotonic;

import itmo.mit.SymbolEncoder;
import itmo.mit.stream.BinaryOutputStream;

public final class GolombSymbolEncoder implements SymbolEncoder {
    private final int moduloBits;
    private final int divisor;

    public GolombSymbolEncoder(final int moduloBits) {
        this.moduloBits = moduloBits;
        this.divisor = (int) Math.pow(2, moduloBits);
    }

    @Override
    public void encode(final int symbol, final BinaryOutputStream out) {
        // encode prefix
        int quotient = (symbol / divisor) + 1;
        out.emit(true, quotient - 1);
        out.emit(false);

        // encode suffix
        int modulo = symbol % divisor;
        for (int i = moduloBits - 1; i >= 0; i--) {
            boolean bit = ((modulo & (1 << i)) > 0);
            out.emit(bit);
        }
    }
}
