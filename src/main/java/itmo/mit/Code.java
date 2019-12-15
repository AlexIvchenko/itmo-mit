package itmo.mit;

import java.util.BitSet;

public final class Code {
    private static final Code EMPTY = new Code(0);
    private final BitSet code;
    private final int length;

    public static Code empty() {
        return EMPTY;
    }

    public Code(int bits) {
        this(new BitSet(bits), bits);
    }

    private Code(final BitSet code, final int length) {
        this.code = code;
        this.length = length;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Code append(final boolean bit) {
        BitSet newCode = new BitSet(length + 1);
        newCode.or(code);
        newCode.set(length, bit);
        return new Code(newCode, length + 1);
    }

    public int size() {
        return length;
    }

    public boolean getBitAt(final int index) {
        return code.get(index);
    }

    public static Code join(final Iterable<Code> codes) {
        int length = 0;
        for (final Code code : codes) {
            length += code.size();
        }
        BitSet joined = new BitSet(length);
        int position = 0;
        for (final Code code : codes) {
            for (int bitNum = 0; bitNum < code.size(); bitNum++) {
                joined.set(position++, code.code.get(bitNum));
            }
        }
        return new Code(joined, length);
    }

    public String toBinaryString() {
        StringBuilder accumulator = new StringBuilder();
        for (int i = 0; i < length; i++) {
            boolean bit = code.get(i);
            if (bit) {
                accumulator.append('1');
            } else {
                accumulator.append('0');
            }
        }
        return accumulator.toString();
    }

    @Override
    public String toString() {
        return toBinaryString();
    }

    public static final class Builder {
        private BitSet bits = new BitSet();
        private int length = 0;

        public Builder append(final boolean bit) {
            this.bits.set(length, bit);
            this.length++;
            return this;
        }

        public Builder append(final boolean bit, final int repeat) {
            for (int i = 0; i < repeat; i++) {
                this.bits.set(length, bit);
                this.length++;
            }
            return this;
        }

        public Code build() {
            return new Code((BitSet) bits.clone(), length);
        }
    }
}
