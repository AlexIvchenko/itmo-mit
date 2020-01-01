package itmo.mit.misc;

public final class CrcArithmetic {
    public CrcArithmetic() {
        throw new IllegalStateException("utility class");
    }

    public static long rotateRight(final long value, final int shift, int size) {
        long rotated = 0;
        for (int i = 0; i < size; i++) {
            if (((value >> i) & 1) > 0) {
                rotated |= 1 << ((size + i - shift) % size);
            }
        }
        return rotated;
    }

    public static long rotateLeft(long value, final int shift, int size) {
        long rotated = 0;
        for (int i = 0; i < size; i++) {
            if (((value >> i) & 1) > 0) {
                rotated |= 1 << ((i + shift) % size);
            }
        }
        return rotated;
    }

    public static long polyAdd(final long left, final long right) {
        return left ^ right;
    }

    public static long polyMult(final long left, final long right) {
        long result = 0;
        for (int i = 0; i < 32; i++) {
            if ((left & (1 << i)) > 0) {
                result ^= (right << i);
            }
        }
        return result;
    }

    public static long[] polyDiv(final long dividend, final long divider) {
        long reminder = dividend;
        long quotient = 0;
        for (int i = 31; i >= 0; i--) {
            if (Long.highestOneBit(reminder >> i) == Long.highestOneBit(divider)) {
                reminder ^= divider << i;
                quotient |= 1 << i;
            }
        }

        return new long[]{quotient, reminder};
    }
}
