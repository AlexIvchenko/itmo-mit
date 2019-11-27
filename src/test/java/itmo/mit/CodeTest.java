package itmo.mit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CodeTest {
    @Test
    void appendTest() {
        assertThat(Code.empty().append(false).append(true).toBinaryString()).isEqualTo("01");
        assertThat(Code.empty().append(true).append(false).toBinaryString()).isEqualTo("10");
        assertThat(Code.empty().append(false).append(false).append(true).toBinaryString()).isEqualTo("001");
    }
}