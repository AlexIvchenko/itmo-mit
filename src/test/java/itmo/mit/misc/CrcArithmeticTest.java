package itmo.mit.misc;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CrcArithmeticTest {
    @MethodSource("mult")
    @ParameterizedTest
    void polyMultTest(long left, long right, long result) {
        assertThat(CrcArithmetic.polyMult(left, right)).isEqualTo(result);
    }

    private static Stream<Arguments> mult() {
        return Stream.of(
                Arguments.of(0xC75, 0b000000000010, 0b00000000001100011101010),
                Arguments.of(0xC75, 0b100011100110, 0b11001110010100001011110)
        );
    }

    @MethodSource("div")
    @ParameterizedTest
    void polyDivTest(long dividend, long divider, long quotient, long reminder) {
        long[] div = CrcArithmetic.polyDiv(dividend, divider);
        assertThat(div[0]).isEqualTo(quotient);
        assertThat(div[1]).isEqualTo(reminder);
    }

    private static Stream<Arguments> div() {
        return Stream.of(
                Arguments.of(0b10010011100111110001101, 0xC75, 0b111001001111, 0b01000101110)
        );
    }

    @MethodSource("roundTripRotate")
    @ParameterizedTest
    void roundTripRotate(long value, int bits, int size) {
        long leftRotated = CrcArithmetic.rotateLeft(value, bits, size);
        assertThat(CrcArithmetic.rotateRight(leftRotated, bits, size)).isEqualTo(value);
        long rightRotated = CrcArithmetic.rotateRight(value, bits, size);
        assertThat(CrcArithmetic.rotateLeft(rightRotated, bits, size)).isEqualTo(value);
    }

    private static Stream<Arguments> roundTripRotate() {
        return Stream.of(
                Arguments.of(0b0010, 1, 4),
                Arguments.of(0b0010, 2, 4),
                Arguments.of(0b0010, 3, 4)
        );
    }

    @MethodSource("leftRotate")
    @ParameterizedTest
    void leftRotate(long value, int bits, int size, long result) {
        long leftRotated = CrcArithmetic.rotateLeft(value, bits, size);
        assertThat(leftRotated).isEqualTo(result);
    }

    private static Stream<Arguments> leftRotate() {
        return Stream.of(
                Arguments.of(0b0010, 1, 4, 0b0100),
                Arguments.of(0b0010, 2, 4, 0b1000),
                Arguments.of(0b0010, 3, 4, 0b0001)
        );
    }
}