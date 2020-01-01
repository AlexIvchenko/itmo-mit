package itmo.mit.golay;

import itmo.mit.BlockCoder;
import itmo.mit.misc.CrcArithmetic;

public final class GolayCoder implements BlockCoder {
    private static final int CODEWORD_LENGTH = 23;
    private static final int POLYNOMIAL = 0xC75;
    private static final int X16 = 1 << 16;
    private static final int X17 = 1 << 17;

    @Override
    public long encode(long info) {
        return CrcArithmetic.polyMult(POLYNOMIAL, info);
    }

    @Override
    public long decode(long codeword) {
        int shift = 0;
        long errors;
        while (true) {
            long syndrome = CrcArithmetic.polyDiv(codeword, POLYNOMIAL)[1];
            if (weight(syndrome) <= 3) {
                errors = syndrome;
                break;
            }
            long modifiedSyndrome16 = computeModifiedSyndrome16(syndrome);
            if (weight(modifiedSyndrome16) <= 2) {
                errors = CrcArithmetic.polyAdd(X16, modifiedSyndrome16);
                break;
            }
            long modifiedSyndrome17 = computeModifiedSyndrome17(syndrome);
            if (weight(modifiedSyndrome17) <= 2) {
                errors = CrcArithmetic.polyAdd(X17, modifiedSyndrome17);
                break;
            }
            shift++;
            codeword = CrcArithmetic.rotateRight(codeword, 1, CODEWORD_LENGTH);
        }
        codeword = codeword ^ errors;
        codeword = CrcArithmetic.rotateLeft(codeword, shift, CODEWORD_LENGTH);
        return CrcArithmetic.polyDiv(codeword, POLYNOMIAL)[0];
    }

    private long computeModifiedSyndrome17(final long originalSyndrome) {
        return CrcArithmetic.polyDiv(X17, POLYNOMIAL)[1] ^ originalSyndrome;
    }

    private long computeModifiedSyndrome16(final long originalSyndrome) {
        return CrcArithmetic.polyDiv(X16, POLYNOMIAL)[1] ^ originalSyndrome;
    }

    private int weight(final long codeword) {
        return Long.bitCount(codeword);
    }
}
