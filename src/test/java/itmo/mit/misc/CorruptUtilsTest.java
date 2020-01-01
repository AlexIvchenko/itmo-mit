package itmo.mit.misc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorruptUtilsTest {
    @Test
    void singleCorruptionTest() {
        long[] corruptions = CorruptUtils.bruteForceSingleCorruption(0b100, 3).toArray();
        assertThat(corruptions).containsExactlyInAnyOrder(0b101, 0b110, 0b000);
    }
}