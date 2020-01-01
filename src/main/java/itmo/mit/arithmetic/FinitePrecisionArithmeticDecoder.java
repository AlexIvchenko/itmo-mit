package itmo.mit.arithmetic;

import itmo.mit.Code;
import itmo.mit.Decoder;

public final class FinitePrecisionArithmeticDecoder implements Decoder {
    private static final int EOF = 0;
    private final int precision;
    private final long total;
    private final long whole;
    private final long half;
    private final long quarter;
    private final Segment[] segments;
    private final int[] symbols;

    FinitePrecisionArithmeticDecoder(int precision, int[] symbols, int[] frequencies) {
        whole = (long) Math.pow(2, precision);
        half = whole / 2;
        quarter = whole / 4;
        long total = 0;
        long low = 0;
        segments = new Segment[symbols.length];
        for (int i = 0; i < symbols.length; i++) {
            total += frequencies[i];
            Segment segment = new Segment(low, low + frequencies[i]);
            segments[i] = segment;
            low = segment.high;
        }

        this.precision = precision;
        this.symbols = symbols;
        this.total = total;
    }

    @Override
    public String decode(final Code code) {
        long value = 0;
        int i;
        for (i = 0; i < precision && i < code.size(); i++) {
            if (code.getBitAt(i)) {
                value += Math.pow(2, precision - (i + 1));
            }
        }
        long low = 0;
        long high = whole;
        StringBuilder output = new StringBuilder();
        while (true) {
            for (int j = 0; j < segments.length; j++) {
                Segment segment = segments[j];
                long newHigh = low + ((high - low) * segment.high / total);
                long newLow = low + ((high - low) * segment.low / total);
                if (newLow <= value && value < newHigh) {
                    if (j == EOF) {
                        return output.toString();
                    }
                    high = newHigh;
                    low = newLow;
                    int symbol = symbols[j];
                    output.append((char) symbol);
                    break;
                }
            }
            while (high < half || low > half) {
                if (high < half) {
                    low *= 2;
                    high *= 2;
                    value *= 2;
                } else {
                    low = 2 * (low - half);
                    high = 2 * (high - half);
                    value = 2 * (value - half);
                }
                if (i < code.size()) {
                    if (code.getBitAt(i)) {
                        value += 1;
                    }
                }
                ++i;
            }
            while (quarter < low && high < 3 * quarter) {
                low = 2 * (low - quarter);
                high = 2 * (high - quarter);
                value = 2 * (value - quarter);
                if (i < code.size()) {
                    if (code.getBitAt(i)) {
                        value += 1;
                    }
                }
                ++i;
            }
        }
    }

    private static final class Segment {
        private final long low;
        private final long high;

        Segment(final long low, final long high) {
            this.low = low;
            this.high = high;
        }
    }
}
