package itmo.mit.huffman;

import itmo.mit.Code;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HuffmanCoderTest {
    @Test
    void singleSymbol() {
        HuffmanCoder coder = HuffmanCoder.of("aaa");
        assertThat(coder.encode("aaa").toBinaryString()).isEqualTo("000");
    }

    @Test
    void encodeTest() {
        HuffmanCoder coder = HuffmanCoder.of("aaaabbc");
        Code code = coder.encode("aaaabbc");
        assertThat(code.toBinaryString()).isEqualTo("0000101011");
    }

    @Test
    void roundTripTest() {
        String test = "dnsfndbfhwbdhjfbsdkjfbdsbfsdkbfksdbfsdbfksj";
        HuffmanCoder coder = HuffmanCoder.of(test);
        String decoded = coder.decode(coder.encode(test));
        assertThat(test).isEqualTo(decoded);
    }
}