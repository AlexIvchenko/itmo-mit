package itmo.mit.arithmetic;

import itmo.mit.Code;
import itmo.mit.Decoder;

public final class InfinitePrecisionArithmeticDecoder implements Decoder {
    private static final int EOF = 0;
    private final Segment[] segments;
    private final int[] symbols;

    InfinitePrecisionArithmeticDecoder(int[] symbols, double[] probabilities) {
        this.segments = new Segment[symbols.length];
        double low = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            Segment segment = new Segment(low, low + probabilities[i]);
            low = segment.high;
            segments[i] = segment;
        }
        this.symbols = symbols;
    }

    @Override
    public String decode(final Code code) {
        double value = computeEncodedRealValue(code);
        StringBuilder output = new StringBuilder();
        while (true) {
            for (int j = 0; j < segments.length; j++) {
                Segment segment = segments[j];
                if (segment.low <= value && value < segment.high) {
                    if (j == EOF) {
                        return output.toString();
                    }
                    char symbol = (char) symbols[j];
                    output.append(symbol);
                    value = (value - segment.low) / (segment.high - segment.low);
                }
            }
        }
    }

    private double computeEncodedRealValue(final Code code) {
        double pow = 0.5;
        double value = 0.0;
        for (int bit = 0; bit < code.size(); bit++) {
            if (code.getBitAt(bit)) {
                value += pow;
            }
            pow /= 2;
        }
        return value;
    }

    private static final class Segment {
        private final double low;
        private final double high;

        Segment(final double low, final double high) {
            this.low = low;
            this.high = high;
        }
    }
}
