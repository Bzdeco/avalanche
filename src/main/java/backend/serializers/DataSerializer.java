package backend.serializers;

import java.io.*;

public class DataSerializer<Type> {
    private File serializedData;

    public DataSerializer(File serializedData) {
        this.serializedData = serializedData;
    }

    public void serialize(Type object) {
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

    public Type deserialize() {
        Type object;

        try {
            FileInputStream serializedDataInS = new FileInputStream(serializedData);
            ObjectInputStream serializedDataInO = new ObjectInputStream(serializedDataInS);

            object = (Type) serializedDataInO.readObject();

            serializedDataInS.close();
            serializedDataInO.close();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }

        return object;
    }
}
