package avalanche.model.serializers;

public interface Serializer<T> {
    void serialize(T object);

    T deserialize();
}
