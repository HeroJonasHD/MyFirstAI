package ai.neuron;

import ai.Brain;

import java.util.Arrays;
import java.util.UUID;

public class Neuron {

    private final Brain brain;
    public final boolean isStatic;

    public final UUID uuid;

    public final byte[] defaultValue;

    public byte[] valueStorage;

    public Neuron(Brain brain, UUID uuid, boolean isStatic, byte[] defaultValue) {
        this.uuid = uuid;
        this.brain = brain;
        this.isStatic = isStatic;
        this.defaultValue = defaultValue;
        valueStorage = defaultValue.clone();
    }

    @Override
    public String toString() {
        return "Neuron{" +
                //"brain=" + brain.nameCall() +
                ", isStatic=" + isStatic +
                ", uuid=" + uuid +
                ", defaultValue=" + Arrays.toString(defaultValue) +
                ", valueStorage=" + Arrays.toString(valueStorage) +
                '}';
    }
}
