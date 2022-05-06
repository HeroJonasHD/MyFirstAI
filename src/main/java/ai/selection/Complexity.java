package ai.selection;

public record Complexity(int totalConnections, int totalNeurons, int totalSignals, int totalStarts) {

    int getRamComplexity() {
        return totalConnections + totalNeurons + totalSignals;
    }

    int getProcessingComplexity() {
        return totalSignals/totalStarts;
    }

    @Override
    public String toString() {
        return "Complexity{" +
                "totalConnections=" + totalConnections +
                ", totalNeurons=" + totalNeurons +
                ", totalSignals=" + totalSignals +
                ", totalStarts=" + totalStarts +
                ", RamComplexity=" + getRamComplexity() +
                ", ProcessingComplexity=" + getProcessingComplexity() +
                '}';
    }
}
