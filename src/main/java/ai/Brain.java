package ai;

import ai.neuron.Connection;
import ai.neuron.Neuron;
import ai.neuron.Signal;
import ai.selection.Complexity;

import java.util.*;

public class Brain {

    private final String name;
    private final int version;
    private final UUID uuid;
    private final HashMap<UUID, Neuron> neurons = new HashMap<>();
    private final ArrayList<UUID> inputNeurons = new ArrayList<>();
    private final ArrayList<UUID> outputNeurons = new ArrayList<>();


    public Brain(boolean autostart, int mutateFactor, Brain parent, String name) {
        this.name = name;
        this.uuid = UUID.randomUUID();
        if (parent == null) {
            version = 0;
            for (int i = 0; i < 11; i++) {
                inputNeurons.add(addNeuron(true).uuid);
                outputNeurons.add(addNeuron(true).uuid);
            }
        } else {
            version = parent.version+1;
            //Load Neurons
            //System.out.println("Counted Neurons: " + parent.neurons.size());
            parent.neurons.forEach((uuid, neuron) -> {
                //System.out.println("add Neuron: " + uuid);
                neurons.put(uuid,new Neuron(uuid,neuron.isStatic,neuron.defaultValue));
            });
            //System.out.println("added " + neurons.size());
            inputNeurons.addAll(parent.inputNeurons);


        }
        for (int i = 0; i < mutateFactor; i++) {
            mutate();
        }
        if(autostart) {
            startBrain();
        }
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public enum Mutation {

        CONNECTOR_ADD,
        CONNECTOR_REMOVE,
        NEURON_ADD,
        NEURON_REMOVE;

        static Mutation randomMutation() {
            return Mutation.values()[(int) (Mutation.values().length*Math.random())];
        }
    }

    public void mutate() {
        mutate(Mutation.randomMutation());
    }

    private void mutate(Mutation mutation_type) {
        switch (mutation_type) {
            case CONNECTOR_ADD -> addRandomConnection();
            case CONNECTOR_REMOVE -> getRandomNeuron().removeRandomConnection();
            case NEURON_ADD -> addNeuron(false);
            case NEURON_REMOVE -> removeNeuron(getRandomNeuron());
        }
    }

    private Neuron getRandomNeuron() {
        if(neurons.values().size() == 0) addNeuron(false);
        return neurons.values().stream().toList().get((int) (neurons.size()*Math.random()));
    }

    public Connection addRandomConnection() {
        return addRandomConnection(getRandomNeuron(), true);
    }

    private Connection addRandomConnection(Neuron neuron, boolean isPrimaryNeuron) {
        return isPrimaryNeuron ? addConnection(neuron,getRandomNeuron()) : addConnection(getRandomNeuron(),neuron);
    }

    private Connection addConnection(Neuron a, Neuron b) {
        Connection connection = new Connection(a,b,UUID.randomUUID());
        return addConnection(connection);
    }

    private Connection addConnection(Connection connection) {
        Neuron.addConnection(connection);
        return connection;
    }


    private Neuron addNeuron(boolean isStatic) {
        return addNeuron(UUID.randomUUID(), isStatic);
    }

    private Neuron addNeuron(UUID uuid, boolean isStatic, byte... defaultValue) {
        Neuron neuron = new Neuron(uuid, isStatic, defaultValue);
        neurons.put(uuid, neuron);
        return neuron;
    }

    private boolean removeNeuron(Neuron neuron) {
        if(neuron.isStatic) return false;
        neuron.removeAllConnections();

        neurons.remove(neuron.uuid);
        return true;
    }


    private void removeConnection(Connection connection) {
        Neuron.removeConnection(connection);
        signals.forEach(signal -> {if(signal.connection() == connection) signals.remove(signal);});
    }

    private final LinkedList<Signal> signals = new LinkedList<>();

    private void addSignalTask(Signal signal) {
        signals.addLast(signal);
    }

    public transient boolean end = false;

    private transient boolean isRunning = false;

    private transient int startCount = 0;

    public void startBrain() {
        if(isRunning) return;
        Neuron[] startNeurons = new Neuron[inputNeurons.size()];
        for (int i = 0; i < inputNeurons.size(); i++) {
            startNeurons[i] = neurons.get(inputNeurons.get(i));
        }
        System.out.println("Start Brain: " + nameCall());
        System.out.println("Processed Successfully: " + process(startNeurons));
    }

    private boolean process(Neuron... startNeurons) {
        if(isRunning) return false;
        isRunning = true;
        startCount++;
        for (Neuron startNeuron : startNeurons) {
            for (Connection connection : startNeuron.getConnections()) {
                addSignalTask(new Signal(connection, 0, startNeuron.valueStorage));
            }
        }

        while(signals.size() > 0 && !end) {
            System.out.println(processSignal(signals.getFirst()));
            signals.removeFirst();
        }
        isRunning = false;
        return true;
    }

    private transient int processedSignals = 0;

    private String processSignal(Signal signal) {
        Neuron neuron = signal.connection().getNeuronB();
        processedSignals++;
        neuron.getConnections().forEach(connection -> {
            if(signal.connection().isProcessable(signal))
                signals.addLast(
                        new Signal(
                                connection,
                                signal.depth()+1,
                                signal.connection().processValue(signal.value())));
        });

        return signal.toString();
    }

    public Collection<Signal> getSignals() {
        return signals;
    }

    public String nameCall() {
        return name + " " + version + " \n" + uuid;
    }

    @Override
    public String toString() {
        return "Brain{" +
                "name='" + name + '\'' +
                ", version=" + version +
                ", uuid=" + uuid +
                ", neurons=" + neurons +
                ", inputNeurons=" + inputNeurons +
                ", outputNeurons=" + outputNeurons +
                //", signals=" + signals +
                ", end=" + end +
                ", isRunning=" + isRunning +
                '}';
    }

    public Complexity getComplexity() {
        final int[] connectionCount = {0};
        neurons.forEach((uuid,neuron) -> {
                connectionCount[0]+=neuron.getConnections().size();
        });
        return new Complexity(neurons.size(), connectionCount[0], processedSignals, startCount);
    }
}
