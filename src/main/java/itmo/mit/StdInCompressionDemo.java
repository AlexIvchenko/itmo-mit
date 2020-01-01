package itmo.mit;

import itmo.mit.arithmetic.FinitePrecisionArithmeticCoder;
import itmo.mit.huffman.HuffmanCoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StdInCompressionDemo {
    public static void main(final String... args) {
        while (true) {
            System.out.print("Enter line (no input to exit): ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                break;
            }

            System.out.println("Entropy: " + Analytics.computeEntropy(line));
            System.out.println("Entropy bits per symbol: " + (int) Math.ceil(Analytics.computeEntropy(line)));
            System.out.println("Theoretical bits per symbol: " + Analytics.computeTheoreticalBitsPerSymbol(line));

            Map<String, Coder> coders = new HashMap<>();
            coders.put("Huffman", HuffmanCoder.of(line));
//            coders.put("Infinite-Precision Arithmetic", ArithmeticCoder.of(line));
            coders.put("Finite-Precision Arithmetic", FinitePrecisionArithmeticCoder.of(31, line));

            for (final Map.Entry<String, Coder> entry : coders.entrySet()) {
                String coderName = entry.getKey();
                Coder coder = entry.getValue();
                Code encoded = coder.encode(line);
                System.out.println(">>> " + coderName);
                System.out.println("Encoded: " + encoded);
                String decoded = coder.decode(encoded);
                System.out.println("Decoded: " + decoded);
                System.out.println("Mean bits per symbol: " + 1.0 * encoded.size() / line.length());
            }
        }
    }
}
