package itmo.mit.arithmetic;

import itmo.mit.Code;
import itmo.mit.Coder;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;

/**
 * This is an implementation of theoretical algorithm called infinite-precision arithmetic coding.
 * It supposed that all additions and multiplications over floating point numbers are done in exactly in infinite precision.
 * Because of that this coder is interesting only for academic purposes.
 * It can encode and decode correctly few symbols because of finite-precision floating point arithmetic.
 */
public final class InfinitePrecisionArithmeticCoder implements Coder {
    private static final int EOF = 0;
    private final InfinitePrecisionArithmeticEncoder encoder;
    private final InfinitePrecisionArithmeticDecoder decoder;

    public static InfinitePrecisionArithmeticCoder of(final String input) {
        MutableIntIntMap symbolToIndex = new IntIntHashMap();
        symbolToIndex.put(0, 0);
        int index = 1;
        for (int i = 0; i < input.length(); i++) {
            char symbol = input.charAt(i);
            if (!symbolToIndex.containsKey(symbol)) {
                symbolToIndex.put(symbol, index++);
            }
        }
        double[] probabilities = new double[symbolToIndex.size()];
        int[] symbols = new int[symbolToIndex.size()];
        probabilities[EOF] = 1;
        symbols[EOF] = 0;
        for (int i = 0; i < input.length(); i++) {
            char symbol = input.charAt(i);
            probabilities[symbolToIndex.get(symbol)]++;
            symbols[symbolToIndex.get(symbol)] = symbol;
        }
        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] /= input.length() + 1;
        }
        return of(symbols, probabilities);
    }

    public static InfinitePrecisionArithmeticCoder of(int[] symbols, double[] probabilities) {
        InfinitePrecisionArithmeticEncoder encoder = new InfinitePrecisionArithmeticEncoder(symbols, probabilities);
        InfinitePrecisionArithmeticDecoder decoder = new InfinitePrecisionArithmeticDecoder(symbols, probabilities);
        return new InfinitePrecisionArithmeticCoder(encoder, decoder);
    }

    private InfinitePrecisionArithmeticCoder(final InfinitePrecisionArithmeticEncoder encoder,
                                             final InfinitePrecisionArithmeticDecoder decoder) {
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
