package backend.serializers;

import java.io.*;

public class DataSerializer<T> implements Serializer<T> {
    private File serializedData;

    public DataSerializer(File serializedData) {
        this.serializedData = serializedData;
    }

    @Override
    public void serialize(T object) {
        try {
            FileOutputStream serializedDataOutS = new FileOutputStream(serializedData);
            ObjectOutputStream serializedDataOutO = new ObjectOutputStream(serializedDataOutS);

            serializedDataOutO.writeObject(object);

            serializedDataOutS.close();
            serializedDataOutO.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public T deserialize() {
        T object;

        try {
            FileInputStream serializedDataInS = new FileInputStream(serializedData);
            ObjectInputStream serializedDataInO = new ObjectInputStream(serializedDataInS);

            //noinspection unchecked
            object = (T) serializedDataInO.readObject();

            serializedDataInS.close();
            serializedDataInO.close();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }

        return object;
    }
}
