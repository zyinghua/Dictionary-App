package Utils;

import java.util.Arrays;

public enum Operation {
    UNKNOWN (0),
    ADD_WORD(1),
    REMOVE_WORD (2),
    QUERY_WORD (3),
    UPDATE_WORD (4);

    private final int index;

    Operation(int i) {
        this.index = i;
    }

    public int getIndex() {
        return index;
    }

    public static Operation fromIntValue(int value) {
        for (Operation op : Operation.values()) {
            if (op.getIndex() == value) {
                return op;
            }
        }

        return UNKNOWN;
    }
}
