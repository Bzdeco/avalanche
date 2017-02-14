package backend.serializers;


import java.io.File;

/**
 * Created by maciejs on 14.01.17.
 */
public class SerializerFactory {
    private static SerializerFactory instance = null;

    private SerializerFactory() {
    }

    public static SerializerFactory getInstance() {
        if (SerializerFactory.instance == null) {
            synchronized (SerializerFactory.class) {
                if (SerializerFactory.instance == null) {
                    SerializerFactory.instance = new SerializerFactory();
                }
            }
        }
        return SerializerFactory.instance;
    }

    public Serializer getSerializer(String serializerType, File serializedData) {
        if (serializerType == null) {
            return null;
        } else if (serializerType.equalsIgnoreCase("FLOAT[][][]")) {
            return new DataSerializer<float[][][]>(serializedData);
        } else if (serializerType.equalsIgnoreCase("DOUBLE[][][]")) {
            return new DataSerializer<double[][][]>(serializedData);
        }
        return null;
    }
}
