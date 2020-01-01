package itmo.mit.monotonic;

import itmo.mit.stream.CodeBinaryOutputStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GolombSymbolEncoderTest {
    @MethodSource("data")
    @ParameterizedTest
    void encodeTest(int moduloBits, int symbol, String expectedCode) {
        GolombSymbolEncoder encoder = new GolombSymbolEncoder(moduloBits);
        CodeBinaryOutputStream out = new CodeBinaryOutputStream();
        encoder.encode(symbol, out);
        assertThat(out.commit().toBinaryString()).isEqualTo(expectedCode);
    }

    private static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of(3, 21, "110101")
        );
    }
}