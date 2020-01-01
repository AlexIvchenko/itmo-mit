package itmo.mit.stream;

import itmo.mit.Code;

public final class CodeBinaryInputStream implements BinaryInputStream {
    private final Code code;
    private int position;

    public CodeBinaryInputStream(final Code code) {
        this.code = code;
        this.position = 0;
    }

    @Override
    public boolean poll() {
        return code.getBitAt(position++);
    }
}
