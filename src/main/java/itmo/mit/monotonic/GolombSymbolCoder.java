package itmo.mit.monotonic;

import itmo.mit.SymbolCoder;
import itmo.mit.stream.BinaryInputStream;
import itmo.mit.stream.BinaryOutputStream;

public final class GolombSymbolCoder implements SymbolCoder {
    private final GolombSymbolEncoder encoder;
    private final GolombSymbolDecoder decoder;

    public GolombSymbolCoder(int moduloBits) {
        this.encoder = new GolombSymbolEncoder(moduloBits);
        this.decoder = new GolombSymbolDecoder(moduloBits);
    }


    @Override
    public int decode(final BinaryInputStream in) {
        return decoder.decode(in);
    }

    @Override
    public void encode(final int symbol, final BinaryOutputStream out) {
        encoder.encode(symbol, out);
    }
}
