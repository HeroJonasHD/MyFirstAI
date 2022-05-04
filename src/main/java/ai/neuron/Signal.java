package ai.neuron;

public record Signal(Connection connection, int depth, byte... value) {
}
