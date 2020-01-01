package itmo.mit.arithmetic;

import itmo.mit.Code;
import itmo.mit.Coder;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;

public final class FinitePrecisionArithmeticCoder implements Coder {
    private static final int EOF = 0;
    private final FinitePrecisionArithmeticEncoder encoder;
    private final FinitePrecisionArithmeticDecoder decoder;

    public static FinitePrecisionArithmeticCoder of(final int precision, final String input) {
        MutableIntIntMap symbolToIndex = new IntIntHashMap();
        symbolToIndex.put(0, 0);
        int index = 1;
        for (int i = 0; i < input.length(); i++) {
            char symbol = input.charAt(i);
            if (!symbolToIndex.containsKey(symbol)) {
                symbolToIndex.put(symbol, index++);
            }
        }
        int[] frequencies = new int[symbolToIndex.size()];
        int[] symbols = new int[symbolToIndex.size()];
        frequencies[EOF] = 1;
        symbols[EOF] = 0;
        for (int i = 0; i < input.length(); i++) {
            char symbol = input.charAt(i);
            frequencies[symbolToIndex.get(symbol)]++;
            symbols[symbolToIndex.get(symbol)] = symbol;
        }
        return of(precision, symbols, frequencies);
    }

    public static FinitePrecisionArithmeticCoder of(int precision, int[] symbols, int[] frequencies) {
        FinitePrecisionArithmeticEncoder encoder = new FinitePrecisionArithmeticEncoder(precision, symbols, frequencies);
        FinitePrecisionArithmeticDecoder decoder = new FinitePrecisionArithmeticDecoder(precision, symbols, frequencies);
        return new FinitePrecisionArithmeticCoder(encoder, decoder);
    }

    private FinitePrecisionArithmeticCoder(final FinitePrecisionArithmeticEncoder encoder,
                                           final FinitePrecisionArithmeticDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    @Override
    public String decode(final Code code) {
        return decoder.decode(code);
    }

    @Override
    public Code encode(final String data) {
        return encoder.encode(data);
    }
}
