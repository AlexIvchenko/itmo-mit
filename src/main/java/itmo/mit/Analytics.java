package itmo.mit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Analytics {
    private Analytics() {
        throw new IllegalStateException("utility class");
    }

    public static int computeTheoreticalBitsPerSymbol(final String data) {
        Set<Character> uniqueChars = new HashSet<>();
        for (int i = 0; i < data.length(); i++) {
            uniqueChars.add(data.charAt(i));
        }
        double bits = Math.log(uniqueChars.size()) / Math.log(2);
        if ((int) bits == bits) {
            return (int) bits;
        } else {
            return (int) bits + 1;
        }
    }

    public static double computeEntropy(final String data) {
        Map<Character, Integer> frequencies = new HashMap<>();
        for (int i = 0; i < data.length(); i++) {
            frequencies.merge(data.charAt(i), 1, Integer::sum);
        }
        return frequencies.values().stream()
                .mapToDouble(frequency -> 1.0 * frequency / data.length())
                .map(probability -> (-probability) * Math.log(probability) / Math.log(2))
                .sum();
    }
}
