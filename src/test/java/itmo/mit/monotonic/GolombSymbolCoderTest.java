package itmo.mit.monotonic;

import itmo.mit.stream.CodeBinaryInputStream;
import itmo.mit.stream.CodeBinaryOutputStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Java6Assertions.assertThat;

class GolombSymbolCoderTest {
    @MethodSource("data")
    @ParameterizedTest
    void roundTripTest(int moduloBits, int symbol) {
        GolombSymbolCoder coder = new GolombSymbolCoder(moduloBits);
        CodeBinaryOutputStream out = new CodeBinaryOutputStream();
        coder.encode(symbol, out);
        CodeBinaryInputStream in = new CodeBinaryInputStream(out.commit());
        int roundTripSymbol = coder.decode(in);
        assertThat(roundTripSymbol).isEqualTo(symbol);
    }

    private static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of(3, 21)
        );
    }
}