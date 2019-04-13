package testutil;

import org.tinfour.common.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class TestUtil
{
    public static List<Vertex> generateRandomVertices(int numberOfVertices)
    {
        List<Vertex> generatedVertices = new ArrayList<>();
        IntStream.range(0, numberOfVertices).forEach(index -> generatedVertices.add(generateRandomVertex(index)));
        return generatedVertices;
    }

    public static Vertex generateRandomVertex(int vertexIndex)
    {
        Random generator = new Random();
        return new Vertex(generator.nextDouble(), generator.nextDouble(), generator.nextDouble(), vertexIndex);
    }
}
