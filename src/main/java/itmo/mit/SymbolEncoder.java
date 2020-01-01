package itmo.mit;

import itmo.mit.stream.BinaryOutputStream;

public interface SymbolEncoder {
    void encode(int symbol, BinaryOutputStream out);
}
