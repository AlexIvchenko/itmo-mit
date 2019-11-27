package itmo.mit.huffman;

import itmo.mit.Analytics;
import itmo.mit.Code;
import itmo.mit.Coder;

import java.util.*;
import java.util.stream.Collectors;

public final class HuffmanCoder implements Coder {
    private final Node root;
    private final Map<Character, Code> codeTable;

    public static HuffmanCoder of(final String input) {
        return new HuffmanCoder(computeFrequencies(input));
    }

    private static Map<Character, Integer> computeFrequencies(final String input) {
        Map<Character, Integer> frequencies = new HashMap<>();
        for (int i = 0; i < input.length(); i++) {
            frequencies.merge(input.charAt(i), 1, Integer::sum);
        }
        return frequencies;
    }

    public HuffmanCoder(Map<Character, Integer> frequencies) {
        if (frequencies.isEmpty()) {
            throw new IllegalArgumentException("must be at least one symbol");
        }
        if (frequencies.size() == 1) {
            Map.Entry<Character, Integer> entry = frequencies.entrySet().iterator().next();
            char symbol = entry.getKey();
            int frequency = entry.getValue();
            LeafNode leaf = new LeafNode(symbol, frequency);
            root = new TreeNode(frequency, leaf, null);
            this.root.buildCode(Code.empty());
            this.codeTable = new HashMap<>();
            this.codeTable.put(symbol, leaf.getCode());
            return;
        }
        PriorityQueue<Node> tree = new PriorityQueue<>(Comparator.comparingInt(Node::getFrequency));
        List<LeafNode> symbols = new ArrayList<>();
        for (final Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            char symbol = entry.getKey();
            int frequency = entry.getValue();
            LeafNode leaf = Node.symbol(symbol, frequency);
            symbols.add(leaf);
            tree.add(leaf);
        }
        while (tree.size() > 1) {
            Node right = tree.poll();
            Node left = tree.poll();
            TreeNode node = TreeNode.merge(left, right);
            tree.add(node);
        }
        this.root = tree.poll();
        assert root != null;
        root.buildCode(Code.empty());
        this.codeTable = symbols.stream()
                .collect(Collectors.toMap(node -> node.symbol, node -> node.code));
    }

    @Override
    public Code encode(final String data) {
        List<Code> codes = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            char symbol = data.charAt(i);
            Code code = this.codeTable.get(symbol);
            if (code == null) {
                throw new IllegalArgumentException("symbol " + symbol + " is not present in haffman tree");
            }
            codes.add(code);
        }
        return Code.join(codes);
    }

    @Override
    public String decode(final Code code) {
        Node current = root;
        StringBuilder decoded = new StringBuilder();
        for (int i = 0; i < code.size(); i++) {
            boolean bit = code.getBitAt(i);
            if (bit) {
                current = current.getRight();
            } else {
                current = current.getLeft();
            }
            if (current.isLeaf()) {
                LeafNode leaf = (LeafNode) current;
                decoded.append(leaf.symbol);
                current = root;
            }
        }
        return decoded.toString();
    }

    public Map<Character, Code> getCodeTable() {
        return Collections.unmodifiableMap(codeTable);
    }

    private static abstract class Node {
        private final int frequency;

        Node(final int frequency) {
            this.frequency = frequency;
        }

        int getFrequency() {
            return frequency;
        }

        abstract void buildCode(final Code code);

        abstract Node getLeft();

        abstract Node getRight();

        abstract boolean isLeaf();

        static LeafNode symbol(final char symbol, final int frequency) {
            return new LeafNode(symbol, frequency);
        }

        static TreeNode merge(final Node left, final Node right) {
            return new TreeNode(left.frequency + right.frequency, left, right);
        }
    }

    private static final class LeafNode extends Node {
        private final char symbol;
        private Code code;

        private LeafNode(final char symbol, final int frequency) {
            super(frequency);
            this.symbol = symbol;
        }

        @Override
        void buildCode(final Code code) {
            this.code = code;
        }

        @Override
        Node getLeft() {
            throw new IllegalStateException("leaf node");
        }

        @Override
        Node getRight() {
            throw new IllegalStateException("leaf node");
        }

        Code getCode() {
            return code;
        }

        int getSymbol() {
            return symbol;
        }

        @Override
        boolean isLeaf() {
            return true;
        }

        @Override
        public String toString() {
            return symbol + "=" + code;
        }
    }

    private static final class TreeNode extends Node {
        private final Node left;
        private final Node right;

        TreeNode(final int frequency, final Node left, final Node right) {
            super(frequency);
            this.left = left;
            this.right = right;
        }

        void buildCode(final Code code) {
            if (left != null) {
                left.buildCode(code.append(false));
            }
            if (right != null) {
                right.buildCode(code.append(true));
            }
        }

        @Override
        Node getLeft() {
            return left;
        }

        @Override
        Node getRight() {
            return right;
        }

        @Override
        boolean isLeaf() {
            return false;
        }
    }

    public static void main(final String... args) {
        while (true) {
            System.out.print("Enter line (no input to exit): ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                break;
            }
            HuffmanCoder coder = HuffmanCoder.of(line);
            Code encoded = coder.encode(line);
            System.out.println("Code Table: " + coder.getCodeTable());
            System.out.println("Encoded: " + encoded);
            String decoded = coder.decode(encoded);
            System.out.println("Decoded: " + decoded);
            System.out.println("Entropy: " + Analytics.computeEntropy(line));
            System.out.println("Entropy bits per symbol: " + (int) Math.ceil(Analytics.computeEntropy(line)));
            System.out.println("Theoretical bits per symbol: " + Analytics.computeTheoreticalBitsPerSymbol(line));
            System.out.println("Mean Huffman bits per symbol: " + 1.0 * encoded.size() / line.length());
        }
    }
}
