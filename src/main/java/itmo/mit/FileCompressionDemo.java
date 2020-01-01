package itmo.mit;

import itmo.mit.arithmetic.FinitePrecisionArithmeticCoder;
import itmo.mit.huffman.HuffmanCoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileCompressionDemo {
    public static void main(final String... args) throws IOException {
        String content = Files.readString(Path.of("/Users/sir-shurikat/Programming/itmo-mit/src/main/resources/Moby-Dick.txt"));

        System.out.println("Entropy: " + Analytics.computeEntropy(content));
        System.out.println("Entropy bits per symbol: " + (int) Math.ceil(Analytics.computeEntropy(content)));
        System.out.println("Theoretical bits per symbol: " + Analytics.computeTheoreticalBitsPerSymbol(content));

        Map<String, Coder> coders = new HashMap<>();
        coders.put("Huffman", HuffmanCoder.of(content));
        coders.put("Finite-Precision Arithmetic", FinitePrecisionArithmeticCoder.of(46, content));

        for (final Map.Entry<String, Coder> entry : coders.entrySet()) {
            String coderName = entry.getKey();
            Coder coder = entry.getValue();
            Code encoded = coder.encode(content);
            System.out.println(">>> " + coderName);
            String decoded = coder.decode(encoded);
            if (decoded.equals(content)) {
                System.out.println("PASSED! Round-Trip [" + coderName + "]");
            } else {
                System.out.println("FAILED! Round-Trip [" + coderName + "]");
            }
            System.out.println("Mean bits per symbol: " + 1.0 * encoded.size() / content.length());
        }
    }
}
