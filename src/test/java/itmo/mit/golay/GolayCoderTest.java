package itmo.mit.golay;

import itmo.mit.misc.CorruptUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GolayCoderTest {
    @MethodSource("noErrors")
    @ParameterizedTest
    void encodeNoErrors(long info, long encoded) {
        GolayCoder coder = new GolayCoder();
        long codeword = coder.encode(info);
        assertThat(codeword).isEqualTo(encoded);
    }

    private static Stream<Arguments> noErrors() {
        return Stream.of(
                Arguments.of(0b000000000010, 0b00000000001100011101010),
                Arguments.of(0b100011100110, 0b11001110010100001011110)
        );
    }

    @MethodSource("roundTripNoErrors")
    @ParameterizedTest
    void roundTripNoErrors(long info) {
        GolayCoder coder = new GolayCoder();
        long codeword = coder.encode(info);
        long encoded = coder.decode(codeword);
        assertThat(encoded).isEqualTo(info);
    }

    private static Stream<Arguments> roundTripNoErrors() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(0b100011100110),
                Arguments.of(0b00000000001)
        );
    }

    @MethodSource("decodeOneError")
    @ParameterizedTest
    void decodeOneError(long codeword, long info) {
        GolayCoder coder = new GolayCoder();
        long encoded = coder.decode(codeword);
        assertThat(encoded).isEqualTo(info);
    }

    private static Stream<Arguments> decodeOneError() {
        GolayCoder coder = new GolayCoder();
        return ThreadLocalRandom.current().longs(0, 0x1000)
                .limit(50)
                .mapToObj(info -> new long[] {coder.encode(info), info})
                .flatMap(data -> CorruptUtils.bruteForceSingleCorruption(data[0], 23)
                        .mapToObj(codeword -> Arguments.of(codeword, data[1])));
    }

    @MethodSource("decodeTwoErrors")
    @ParameterizedTest
    void decodeTwoErrors(long codeword, long info) {
        GolayCoder coder = new GolayCoder();
        long encoded = coder.decode(codeword);
        assertThat(encoded).isEqualTo(info);
    }

    private static Stream<Arguments> decodeTwoErrors() {
        GolayCoder coder = new GolayCoder();
        return generate(coder, 2, 100, 10);
    }

    @MethodSource("decodeThreeErrors")
    @ParameterizedTest
    void decodeThreeErrors(long codeword, long info) {
        GolayCoder coder = new GolayCoder();
        long encoded = coder.decode(codeword);
        assertThat(encoded).isEqualTo(info);
    }

    private static Stream<Arguments> decodeThreeErrors() {
        GolayCoder coder = new GolayCoder();
        return generate(coder, 3, 100, 10);
    }

    private static Stream<Arguments> generate(GolayCoder coder, int numOfErrors, int limitInfo, int limitPermutations) {
        return ThreadLocalRandom.current().longs(0, 0x1000)
                .limit(limitInfo)
                .mapToObj(info -> new long[] {coder.encode(info), info})
                .flatMap(data -> CorruptUtils.corruptRandomly(data[0], numOfErrors, 23, limitPermutations)
                        .mapToObj(codeword -> Arguments.of(codeword, data[1])));
    }
}