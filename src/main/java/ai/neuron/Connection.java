package ai.neuron;

import ai.Brain;

import java.util.Arrays;
import java.util.UUID;

public class Connection {

    public static double totalProcessingCount;

    private final Processors[] processorTypes;
    private final Brain brain;
    private final Neuron neuronA;
    private final Neuron neuronB;
    private final UUID uuid;
    private double useCount = 0;

    public Connection(Neuron neuronA, Neuron neuronB, UUID uuid, Brain brain, Processors... processorType) {
        if(processorType != null)
            this.processorTypes = processorType;
        else {
            this.processorTypes = new Processors[(int) (Math.random()*3+1)];
            for (int i = 0; i < this.processorTypes.length; i++) {
                this.processorTypes[i] = Processors.randomProcessor();
            }
        }
        this.brain = brain;
        this.neuronA = neuronA;
        this.neuronB = neuronB;
        this.uuid = uuid;


    }

    public Neuron use() {
        useCount++;
        return neuronB;
    }

    public Neuron getNeuronA() {
        return neuronA;
    }

    public Neuron getNeuronB() {
        return neuronB;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getUseCount() {
        return useCount;
    }

    public boolean isProcessable(Signal signal) {
        return signal.depth() <= 30;
    }

    public byte[] processValue(byte[] value) {
        for (Processors processorType : processorTypes) {
            processorType.process(this,value);
        }
        return getNeuronB().valueStorage;
    }

    public Processors[] getProcessorTypes() {
        return processorTypes;
    }

    public enum Processors {
        ADD((connection, bytes) -> {
            byte[] bytesB = connection.getNeuronB().valueStorage;
            if(bytesB.length > bytes.length) {
                for (int i = 0; i < bytes.length; i++) {
                    bytesB[i] += bytes[i];
                }
            } else {
                byte[] bytee = new byte[bytes.length];
                for (int i = 0; i < bytesB.length; i++) {
                    bytee[i] = (byte) (bytesB[i] + bytes[i]);
                }
                System.arraycopy(bytee, bytesB.length, bytee, bytesB.length, bytes.length - bytesB.length);
            }
        }),
        REMOVE((connection, bytes) -> {
            byte[] bytesB = connection.getNeuronB().valueStorage;
            if(bytesB.length > bytes.length) {
                for (int i = 0; i < bytes.length; i++) {
                    bytesB[i] -= bytes[i];
                }
            } else {
                byte[] bytee = new byte[bytes.length];
                for (int i = 0; i < bytesB.length; i++) {
                    bytee[i] = (byte) (bytesB[i] - bytes[i]);
                }
                System.arraycopy(bytee, bytesB.length, bytee, bytesB.length, bytes.length - bytesB.length);
            }

        }),
        MULTIPLY((connection, bytes) -> {
            byte[] bytesB = connection.getNeuronB().valueStorage;
            if(bytesB.length > bytes.length) {
                for (int i = 0; i < bytes.length; i++) {
                    bytesB[i] /= bytes[i];
                }
            } else {
                byte[] bytee = new byte[bytes.length];
                for (int i = 0; i < bytesB.length; i++) {
                    bytee[i] = (byte) (bytesB[i] / bytes[i]);
                }
                System.arraycopy(bytee, bytesB.length, bytee, bytesB.length, bytes.length - bytesB.length);
            }

        }),
        DIVIDE((connection, bytes) -> {
            byte[] bytesB = connection.getNeuronB().valueStorage;
            if(bytesB.length > bytes.length) {
                for (int i = 0; i < bytes.length; i++) {
                    bytesB[i] *= bytes[i];
                }
            } else {
                byte[] bytee = new byte[bytes.length];
                for (int i = 0; i < bytesB.length; i++) {
                    bytee[i] = (byte) (bytesB[i] * bytes[i]);
                }
                System.arraycopy(bytee, bytesB.length, bytee, bytesB.length, bytes.length - bytesB.length);
            }

        });

        private final Processor processor;

        Processors(Processor processor) {
            this.processor = processor;
        }

        void process(Connection connection, byte... bytes) {
            processor.processes(connection, bytes);
        }



        interface Processor {
            void processes(Connection connection, byte... bytes);
        }

        public static Processors randomProcessor() {
            return Processors.values()[Processors.values().length];
        }
    }

    @Override
    public String toString() {
        return "Connection{" +
                "processorType=" + Arrays.toString(processorTypes) +
                //", brain=" + brain.nameCall() +
                ", neuronA=" + neuronA +
                ", neuronB=" + neuronB +
                ", uuid=" + uuid +
                ", useCount=" + useCount +
                '}';
    }
}
