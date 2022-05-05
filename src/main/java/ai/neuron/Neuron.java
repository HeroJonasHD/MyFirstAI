package ai.neuron;

import ai.Brain;

import java.util.*;

public class Neuron {

    private final Brain brain;
    public final boolean isStatic;

    private final ArrayList<Connection> connections = new ArrayList<>();
    private final ArrayList<Connection> reverseConnection = new ArrayList<>();

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

    public void addConnection(Neuron b, UUID uuid, Connection.Processors... processors) {
        addConnection(new Connection(this,b,uuid,brain, processors));
    }

    public void removeAllConnections() {
        try {
            connections.forEach(Neuron::removeConnection);
            reverseConnection.forEach(Neuron::removeConnection);
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    public Collection<Connection> getConnections() {
        return connections;
    }

    public static void addConnection(Connection connection) {
        connection.getNeuronA().connections.add(connection);
        connection.getNeuronB().reverseConnection.add(connection);
    }

    public static void removeConnection(Connection connection) {
        try {
            connection.getNeuronB().reverseConnection.remove(connection);
            connection.getNeuronA().connections.remove(connection);
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

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

    public void removeRandomConnection() {
        if(connections.size()>0) {
            Connection connection = connections.get((int) (Math.random() * connections.size()));
            Neuron.removeConnection(connection);
        }
        return;
    }
}
