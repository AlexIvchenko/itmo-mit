package itmo.mit.arithmetic;

import itmo.mit.Code;
import itmo.mit.Encoder;
import org.eclipse.collections.api.map.primitive.IntIntMap;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;

public final class InfinitePrecisionArithmeticEncoder implements Encoder {
    private static final int EOF = 0;
    private final IntIntMap symbolToIndex;
    private final Segment[] segments;

    InfinitePrecisionArithmeticEncoder(int[] symbols, double[] probabilities) {
        double low = 0;
        int size = symbols.length;
        segments = new Segment[size];
        MutableIntIntMap symbolToIndex = new IntIntHashMap();
        for (int i = 0; i < size; ++i) {
            double probability = probabilities[i];
            Segment segment = new Segment(low, low + probability);
            segments[i] = segment;
            low = segment.high;
            symbolToIndex.put(symbols[i], i);
        }
        this.symbolToIndex = symbolToIndex;
    }

    @Override
    public Code encode(final String data) {
        double low = 0;
        double high = 1;
        for (int i = 0; i <= data.length(); ++i) {
            Segment segment;
            if (i < data.length()) {
                char symbol = data.charAt(i);
                segment = segments[symbolToIndex.get(symbol)];
            } else {
                segment = segments[EOF];
            }
            double newHigh = low + (high - low) * segment.high;
            double newLow = low + (high - low) * segment.low;
            high = newHigh;
            low = newLow;
        }

        int numMiddleRescaling = 0;
        Code.Builder output = Code.builder();
        while (high < 0.5 || low > 0.5) {
            if (high < 0.5) {
                // entire [low, high) belongs to the left half.
                // we can emit 0, make rescaling (bit shift) and find out the next bit.
                output.append(false);
                //          0                 1
                // |--------|--------|--------|--------|
                //        ^         ^
                //        |         |
                //       low       high
                low *= 2;
                high *= 2;
            } else if (low > 0.5) {
                // entire [low, high) belongs to the right half.
                // we can emit 1, make rescaling (bit shift) and find out the next bit.
                output.append(true);
                //          0                 1
                // |--------|--------|--------|--------|
                //                      ^         ^
                //                      |         |
                //                     low       high
                low = 2 * (low - 0.5);
                high = 2 * (high - 0.5);
            }
        }

        while (0.25 < low && high < 0.75) {
            assert low <= 0.5;
            assert 0.5 <= high;
            // we can't be sure what to emit at this point.
            // let's make middle rescaling and try to find out on next iteration.
            // it's obvious that we have to emit bit before each rescaling, but we missed to do it now.
            // because of that we track number of the middle rescaling to emit correct number of bits in future.
            //     01       01       10       11
            // |--------|--------|--------|--------|
            //             ^         ^
            //             |         |
            //            low       high
            ++numMiddleRescaling;
            low = 2 * (low - 0.25);
            high = 2 * (high - 0.25);
        }

        if (low <= 0.5) {
            assert high >= 0.75;
            //     01       01       10       11
            // |--------|--------|--------|--------|
            //            ^           ^        ^
            //            |           |        |
            //           low          |       high
            //                        |
            //                        |
            //      this interval belongs to [low, high). Let's take it.
            ++numMiddleRescaling;
            // emit 1{0} * numMiddleRescaling
            output.append(true);
            output.append(false, numMiddleRescaling);
        } else {
            assert low <= 0.25;
            assert high >= 0.5;
            //     01       01       10       11
            // |--------|--------|--------|--------|
            //        ^      ^         ^
            //        |      |         |
            //       low     |        high
            //               |
            //               |
            // this interval belongs to [low, high). Let's take it.
            ++numMiddleRescaling;
            // emit 0{1} * numMiddleRescaling
            output.append(false);
            output.append(true, numMiddleRescaling);
        }
        return output.build();
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
