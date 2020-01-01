package itmo.mit.monotonic;

import itmo.mit.SymbolDecoder;
import itmo.mit.stream.BinaryInputStream;

public final class GolombSymbolDecoder implements SymbolDecoder {
    private final int moduloBits;
    private final int divisor;

    public GolombSymbolDecoder(final int moduloBits) {
        this.moduloBits = moduloBits;
        this.divisor = (int) Math.pow(2, moduloBits);
    }

    @Override
    public int decode(final BinaryInputStream in) {
        // encode prefix
        int quotient = 0;
        while (true) {
            boolean bit = in.poll();
            if (bit) {
                quotient++;
            } else {
                break;
            }
        }

        // encode suffix
        int modulo = 0;
        for (int i = 0; i < moduloBits; i++) {
            modulo *= 2;
            modulo += in.poll() ? 1 : 0;
        }
        return quotient * divisor + modulo;
    }
}
