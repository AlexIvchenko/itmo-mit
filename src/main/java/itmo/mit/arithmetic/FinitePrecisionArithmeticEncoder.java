package itmo.mit.arithmetic;

import itmo.mit.Code;
import itmo.mit.Encoder;
import org.eclipse.collections.api.map.primitive.IntIntMap;
import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

import java.util.stream.IntStream;

public final class FinitePrecisionArithmeticEncoder implements Encoder {
    private static final int EOF = 0;
    private final long total;
    private final long half;
    private final long quarter;

    private final IntIntMap symbolToIndex;
    private final IntObjectMap<Segment> segments;

    private long low;
    private long high;
    private int middleRescalings;

    FinitePrecisionArithmeticEncoder(int precision, int[] symbols, int[] frequencies) {
        long whole = (long) Math.pow(2, precision);
        half = whole / 2;
        quarter = whole / 4;
        long low = 0;
        int size = symbols.length;
        long total = 0;
        MutableIntObjectMap<Segment> segments = new IntObjectHashMap<>();
        MutableIntIntMap symbolToIndex = new IntIntHashMap();
        for (int i = 0; i < size; ++i) {
            int frequency = frequencies[i];
            total += frequency;
            Segment segment = new Segment(low, low + frequency);
            low = segment.high;
            segments.put(i, segment);
            symbolToIndex.put(symbols[i], i);
        }
        this.symbolToIndex = symbolToIndex;
        this.segments = segments;
        this.total = total;
        this.low = 0;
        this.high = whole;
    }

    @Override
    public Code encode(final String data) {
        return encodeSymbols(data.chars());
    }

    private Code encodeSymbols(final IntStream symbols) {
        return encodeIndexes(IntStream.concat(symbols.map(symbolToIndex::get), IntStream.of(EOF)));
    }

    private Code encodeIndexes(final IntStream indexes) {
        Code.Builder output = Code.builder();
        indexes.forEach(symbol -> encode(symbol, output));

        middleRescalings++;
        if (low <= quarter) {
            // 2Q
            assert high >= half;
            //     01       01       10       11
            // |--------|--------|--------|--------|
            //        ^                ^
            //        |                |
            //       low              high
            // emit 0{1} * unknowns
            output.append(false);
            output.append(true, middleRescalings);
        } else { // 3Q
            assert low <= half;
            assert high >= 3 * quarter;
            //     01       01       10       11
            // |--------|--------|--------|--------|
            //            ^                    ^
            //            |                    |
            //           low                  high
            // emit 1{0} * unknowns
            output.append(true);
            output.append(false, middleRescalings);
        }

        return output.build();
    }

    private void encode(final int index, final Code.Builder output) {
        Segment segment = segments.get(index);
        long newHigh = low + ((high - low) * segment.high / total);
        long newLow = low + ((high - low) * segment.low / total);
        high = newHigh;
        low = newLow;
        while (high < half || low > half) {
            if (high < half) {
                output.append(false);
                output.append(true, middleRescalings);
                middleRescalings = 0;
                //          0                 1
                // |--------|--------|--------|--------|
                //        ^         ^
                //        |         |
                //       low       high
                low *= 2;
                high *= 2;
            } else {
                output.append(true);
                output.append(false, middleRescalings);
                middleRescalings = 0;
                //          0                 1
                // |--------|--------|--------|--------|
                //                      ^         ^
                //                      |         |
                //                     low       high
                low = 2 * (low - half);
                high = 2 * (high - half);
            }
        }

        // middle rescaling
        while (quarter < low && high < 3 * quarter) {
            assert low <= half;
            assert half <= high;
            //     01       01       10       11
            // |--------|--------|--------|--------|
            //             ^         ^
            //             |         |
            //            low       high
            ++middleRescalings;
            low = 2 * (low - quarter);
            high = 2 * (high - quarter);
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
