package ai.neuron;

import java.util.*;

public class Neuron {

    public final boolean isStatic;

    private final ArrayList<Connection> connections = new ArrayList<>();
    private final ArrayList<Connection> reverseConnection = new ArrayList<>();

    public final UUID uuid;

    public final byte[] defaultValue;

    public transient byte[] valueStorage;

    public Neuron(UUID uuid, boolean isStatic, byte[] defaultValue) {
        this.uuid = uuid;
        this.isStatic = isStatic;
        this.defaultValue = defaultValue;
        valueStorage = defaultValue.clone();
    }

    public void addConnection(Neuron b, UUID uuid, Connection.Processors... processors) {
        addConnection(new Connection(this,b,uuid, processors));
    }

    public void removeAllConnections() {
        connections.forEach(connection -> connection.getNeuronB().reverseConnection.remove(connection));
        connections.removeIf(connection -> true);
    }

    public Collection<Connection> getConnections() {
        return connections;
    }

    public static void addConnection(Connection connection) {
        connection.getNeuronA().connections.add(connection);
        connection.getNeuronB().reverseConnection.add(connection);
    }

    public static void removeConnection(Connection connection) {
        connection.getNeuronB().reverseConnection.remove(connection);
        connection.getNeuronA().connections.remove(connection);
    }

    @Override
    public String toString() {
        return "Neuron{" +
                ", isStatic=" + isStatic +
                ", uuid=" + uuid +
                ", defaultValue=" + Arrays.toString(defaultValue) +
                ", connections= " + connections +
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
