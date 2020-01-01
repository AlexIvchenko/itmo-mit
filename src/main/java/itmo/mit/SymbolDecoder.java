package itmo.mit;

import itmo.mit.stream.BinaryInputStream;

public interface SymbolDecoder {
    int decode(BinaryInputStream in);
}
