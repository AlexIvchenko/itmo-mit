package itmo.mit.stream;

public interface BinaryOutputStream {
    void emit(boolean bit);

    void emit(boolean bit, int repeat);
}
