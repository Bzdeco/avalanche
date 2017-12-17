package las2etin.output;

import las2etin.las.LASFile;
import las2etin.las.LASReader;
import las2etin.las.vertex.Bounds;
import las2etin.model.Terrain;
import las2etin.model.TerrainBuilder;
import las2etin.model.TerrainSettings;
import las2etin.model.TerrainSettingsBuilder;
import las2etin.tin.Tin;
import las2etin.tin.TinBuilder;
import org.junit.Ignore;
import org.junit.Test;
import testutil.TestUtil;
import tinfour.common.Vertex;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TerrainFormatterTest
{
    @Test
    public void serializationAndDeserializationPreservesTerrainState() throws Exception
    {
        // given
        int numberOfVertices = 100;

        List<Vertex> vertices = TestUtil.generateRandomVertices(numberOfVertices - 2);
        Vertex minVertex = new Vertex(0, 0, 0);
        Vertex maxVertex = new Vertex(1, 1, 0);
        vertices.add(minVertex);
        vertices.add(maxVertex);

        Bounds fakeBounds = new Bounds(minVertex.x, minVertex.y, maxVertex.x, maxVertex.y);

        Tin terrainMesh = new TinBuilder().withVertices(vertices).withBounds(fakeBounds).build();

        int widthInCells = 100;
        int heightInCells = 100;
        TerrainSettings settings = new TerrainSettingsBuilder().withWidthInCells(widthInCells)
                                                               .withHeightInCells(heightInCells)
                                                               .build();
        Terrain terrainToSerialize = new TerrainBuilder(terrainMesh).withSettings(settings).build();

        // when
        Path saveLocation = Paths.get("src/test/resources/terrain.ser");
        Path serializedTerrain = TerrainFormatter.serialize(terrainToSerialize, saveLocation);
        Terrain deserializedTerrain = TerrainFormatter.deserialize(serializedTerrain);

        // then
        assertThat(terrainToSerialize).isEqualTo(deserializedTerrain);
    }

    @Test
    @Ignore("Serialization and deserializtion of real .las files, takes very long")
    public void serializeAndDeserializeTestLasFile() throws Exception
    {
        // given
        LASFile file = LASFile.fromFilePath("src/test/resources/test.las");
        LASReader reader = LASReader.createFor(file);
        List<Vertex> readVertices = reader.getVerticesRecords();
        Bounds bounds = reader.getVerticesBounds();
        Tin terrainMesh = new TinBuilder().withVertices(readVertices).withBounds(bounds).build();
        int widthInCells = 1000;
        int heightInCells = 1000;
        TerrainSettings settings = new TerrainSettingsBuilder().withWidthInCells(widthInCells)
                                                               .withHeightInCells(heightInCells)
                                                               .build();
        Terrain terrainToSerialize = new TerrainBuilder(terrainMesh).withSettings(settings).build();

        // when
        Path saveLocation = Paths.get("src/test/resources/test.ser");
        Path serializedTerrain = TerrainFormatter.serialize(terrainToSerialize, saveLocation);
        Terrain deserializedTerrain = TerrainFormatter.deserialize(serializedTerrain);

        // then
        assertThat(terrainToSerialize).isEqualTo(deserializedTerrain);
    }
}