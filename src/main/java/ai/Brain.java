package ai;

import ai.neuron.Connection;
import ai.neuron.Neuron;
import ai.neuron.Signal;

import java.util.*;

public class Brain {

    private final String name;
    private final int version;
    private final UUID uuid;
    private final HashMap<UUID, Neuron> neurons = new HashMap<>();
    private final HashMap<Neuron, ArrayList<Connection>> connections = new HashMap<>();
    private final HashMap<Neuron, ArrayList<Connection>> reverseConnections = new HashMap<>();
    private final ArrayList<Neuron> inputNeurons = new ArrayList<>();
    private final ArrayList<Neuron> outputNeurons = new ArrayList<>();


    public Brain(boolean autostart, int mutateFactor, Brain parent, String name) {
        this.name = name;
        this.uuid = UUID.randomUUID();
        if (parent == null) {
            version = 0;
            for (int i = 0; i < 11; i++) {
                inputNeurons.add(addNeuron(true));
                outputNeurons.add(addNeuron(true));
            }
        } else {
            version = parent.version+1;
            //Load Neurons
            //System.out.println("Counted Neurons: " + parent.neurons.size());
            parent.neurons.forEach((uuid, neuron) -> {
                //System.out.println("add Neuron: " + uuid);
                neurons.put(uuid,new Neuron(this,uuid,neuron.isStatic,neuron.defaultValue));
            });
            //System.out.println("added " + neurons.size());
            parent.inputNeurons.forEach(neuron -> inputNeurons.add(neurons.get(neuron.uuid)));
            parent.outputNeurons.forEach(neuron -> outputNeurons.add(neurons.get(neuron.uuid)));


            parent.connections.forEach((neuron, connections1) -> {
                Neuron neuronA = neurons.get(neuron.uuid);
                connections1.forEach(connection -> {
                    Neuron neuronB = neurons.get(connection.getNeuronB().uuid);
                    addConnection(new Connection(neuronA,neuronB,connection.getUuid(),this, connection.getProcessorTypes()));
                });
            });

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

        CONNECTOR,
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
            case CONNECTOR -> addRandomConnection();

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
        Connection connection = new Connection(a,b,UUID.randomUUID(),this);
        return addConnection(connection);
    }

    private Connection addConnection(Connection connection) {
        System.out.println("add Connection: " + connection);
        if(!connections.containsKey(connection.getNeuronA())) connections.put(connection.getNeuronA(), new ArrayList<>());
        connections.get(connection.getNeuronA()).add(connection);
        if(!reverseConnections.containsKey(connection.getNeuronB())) reverseConnections.put(connection.getNeuronB(), new ArrayList<>());
        reverseConnections.get(connection.getNeuronB()).add(connection);
        return connection;
    }

    private Neuron addNeuron(boolean isStatic) {
        return addNeuron(UUID.randomUUID(), isStatic);
    }

    private Neuron addNeuron(UUID uuid, boolean isStatic, byte... defaultValue) {
        Neuron neuron = new Neuron(this,uuid, isStatic, defaultValue);
        neurons.put(uuid, neuron);
        return neuron;
    }

    private boolean removeNeuron(Neuron neuron) {
        if(neuron.isStatic) return false;
        removeAllConnections(neuron);

        neurons.remove(neuron.uuid);
        return true;
    }

    private void removeAllConnections(Neuron neuron) {
        if(reverseConnections.containsKey(neuron)) {
            if(connections.get(neuron) != null)
            for (Connection connection : reverseConnections.get(neuron)) {
                removeConnection(connection);
            }
            reverseConnections.remove(neuron);
        }
        if(connections.containsKey(neuron)) {
            if(connections.get(neuron) != null)
            for (Connection connection : connections.get(neuron)) {
                removeConnection(connection);
            }
            connections.remove(neuron);
        }


    }

    private void removeConnection(Connection connection) {
        connections.getOrDefault(connection.getNeuronA(), new ArrayList<>()).remove(connection);
        reverseConnections.getOrDefault(connection.getNeuronB(), new ArrayList<>()).remove(connection);
        signals.forEach(signal -> {if(signal.connection() == connection) signals.remove(signal);});
    }

    private final LinkedList<Signal> signals = new LinkedList<>();

    private void addSignalTask(Signal signal) {
        signals.addLast(signal);
    }

    public void startBrain() {
        if(isRunning) return;
        Neuron[] startNeurons = new Neuron[inputNeurons.size()];
        for (int i = 0; i < inputNeurons.size(); i++) {
            startNeurons[i] = inputNeurons.get(i);
        }
        System.out.println("Start Brain: " + nameCall());
        System.out.println("Processed Successfully: " + process(startNeurons));
    }

    public boolean end;

    private boolean isRunning = false;

    private boolean process(Neuron... startNeurons) {
        if(isRunning) return false;
        isRunning = true;
        for (Neuron startNeuron : startNeurons) {
            for (Connection connection : connections.getOrDefault(startNeuron, new ArrayList<>())) {
                addSignalTask(new Signal(connection, 0, startNeuron.valueStorage));
            }
        }

        while(signals.size() > 0 && !end) {
            processSignal(signals.getFirst());
        }
        isRunning = false;
        return true;
    }

    private void processSignal(Signal signal) {
        Neuron neuron = signal.connection().getNeuronB();
        connections.get(neuron).forEach(connection -> {
            if(connection.isProcessable(signal))
            addSignalTask(new Signal(connection,
                    signal.depth()+1,
                    connection.processValue(signal.value())));
        });
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
                //", uuid=" + uuid +
                //", neurons=" + neurons +
                ", connections=" + connections +
                ", reverseConnections=" + reverseConnections +
                //", inputNeurons=" + inputNeurons +
                //", outputNeurons=" + outputNeurons +
                ", signals=" + signals +
                ", end=" + end +
                ", isRunning=" + isRunning +
                '}';
    }
}
