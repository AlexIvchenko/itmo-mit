package itmo.mit.stream;

import itmo.mit.Code;

public final class CodeBinaryOutputStream implements BinaryOutputStream {
    private final Code.Builder builder = Code.builder();

    @Override
    public void emit(final boolean bit) {
        builder.append(bit);
    }

    @Override
    public void emit(final boolean bit, final int repeat) {
        builder.append(bit, repeat);
    }

    public Code commit() {
        return builder.build();
    }
}
