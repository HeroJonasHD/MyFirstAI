package ai.neuron;

import java.util.Arrays;

public record Signal(Connection connection, int depth, byte... value) {
    @Override
    public String toString() {
        return "Signal{" +
                "connection=" + connection +
                ", depth=" + depth +
                ", value=" + Arrays.toString(value) +
                '}';
    }
}
