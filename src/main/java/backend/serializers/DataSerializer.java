package backend.serializers;

import java.io.*;

public class DataSerializer<Type> {
    private String filePath;
    private boolean serializedDataExistence;
    private File serializedData;
    private FileInputStream serializedDataInS;
    private ObjectInputStream serializedDataInO;
    private FileOutputStream serializedDataOutS;
    private ObjectOutputStream serializedDataOutO;

    public DataSerializer(String filePath) {
        this.filePath = filePath;

        try {
            this.serializedData = new File(this.filePath);

            if (this.serializedData.exists() && !this.serializedData.isDirectory()) {
                this.serializedDataExistence = true;
            } else {
                this.serializedDataExistence = false;
            }
        } catch (Exception exception) {
            this.serializedDataExistence = false;
            exception.printStackTrace();
        }
    }

    public boolean isSerializedDataExistence() {
        return serializedDataExistence;
    }

    public void serialize(Type object) {
        try {
            this.serializedDataOutS = new FileOutputStream(this.filePath);
            this.serializedDataOutO = new ObjectOutputStream(this.serializedDataOutS);

            serializedDataOutO.writeObject(object);

            this.serializedDataOutS.close();
            this.serializedDataOutO.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Type deserialize() {
        Type object;

        if (this.serializedDataExistence) {
            try {
                this.serializedDataInS = new FileInputStream(this.filePath);
                this.serializedDataInO = new ObjectInputStream(this.serializedDataInS);

                object = (Type) serializedDataInO.readObject();

                this.serializedDataInS.close();
                this.serializedDataInO.close();
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }

        } else {
            return null;
        }

        return object;
    }
}
