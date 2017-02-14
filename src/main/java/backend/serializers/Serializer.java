package backend.serializers;

/**
 * Created by maciejs on 14.01.17.
 */
public interface Serializer<Type> {
    void serialize(Type object);

    Type deserialize();

    String getFileName();
}
