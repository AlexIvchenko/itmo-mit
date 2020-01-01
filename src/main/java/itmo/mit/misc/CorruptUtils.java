package itmo.mit.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.LongSupplier;
import java.util.stream.LongStream;

public final class CorruptUtils {
    public CorruptUtils() {
        throw new IllegalStateException("utility class");
    }

    public static LongStream bruteForceSingleCorruption(final long codeword, int size) {
        return LongStream.generate(new LongSupplier() {
            private int errorBit = 0;
            @Override
            public long getAsLong() {
                long corrupted = codeword ^ (1 << errorBit);
                errorBit++;
                return corrupted;
            }
        }).limit(size);
    }

    public static LongStream corruptRandomly(final long codeword, int numberOfErrors, int size, int limit) {
        List<Integer> uniqueIndexes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            uniqueIndexes.add(i);
        }
        return LongStream.generate(() -> {
            Collections.shuffle(uniqueIndexes);
            long corrupted = codeword;
            for (int corruptBitIndex = 0; corruptBitIndex < numberOfErrors; ++corruptBitIndex) {
                int corruptBit = uniqueIndexes.get(corruptBitIndex);
                corrupted ^= 1 << corruptBit;
            }
            return corrupted;
        }).limit(limit);
    }
}
