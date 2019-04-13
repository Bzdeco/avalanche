package las2etin.las;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.tinfour.common.Vertex;
import testutil.TestUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class LASReaderTest
{
    @Test
    void getTrimmedNumberOfVerticesFromFile() throws IOException {
        // given
        LASFile file = mock(LASFile.class, withSettings().stubOnly());
        int numberOfVerticesInFile = 1000;
        int maxNumberOfVertices = 700;
        LASReaderOptions options = new LASReaderOptionsBuilder().withMaxNumberOfVertices(maxNumberOfVertices)
                                                                .withThinningEnabled(false)
                                                                .build();
        VertexLoader loader = mock(VertexLoader.class, withSettings().stubOnly());
        List<Vertex> generatedVertices = TestUtil.generateRandomVertices(numberOfVerticesInFile);
        when(loader.getAllVerticesFromFile()).thenReturn(generatedVertices);
        LASReader reader = new LASReader(file, options, loader);

        // when
        List<Vertex> readVertices = reader.getVerticesRecords();

        // then
        assertThat(readVertices).hasSize(maxNumberOfVertices);
    }

    @Test
    void getFilteredVertices() throws IOException {
        // given
        LASFile file = mock(LASFile.class, withSettings().stubOnly());
        int numberOfVerticesInFile = 1000;
        float thinningFactor = 0.5f;
        int expectedNumberOfVertices = 500;
        float error = 5f;
        LASReaderOptions options = new LASReaderOptionsBuilder().withThinningEnabled(true)
                                                                .withThinningFactor(thinningFactor)
                                                                .build();
        VertexLoader loader = mock(VertexLoader.class, withSettings().stubOnly());
        List<Vertex> generatedVertices = TestUtil.generateRandomVertices(numberOfVerticesInFile);
        when(loader.getAllVerticesFromFile()).thenReturn(generatedVertices);
        LASReader reader = new LASReader(file, options, loader);

        // when
        List<Vertex> readVertices = reader.getVerticesRecords();

        // then
        assertThat(readVertices.size()).isCloseTo(expectedNumberOfVertices, Percentage.withPercentage(error));
    }

    @Test
    @Disabled("Doesn't work?")
    void getUnmodifiedNumberOfVertices() throws IOException {
        // given
        LASFile file = mock(LASFile.class, withSettings().stubOnly());
        int numberOfVerticesInFile = 1000;
        LASReaderOptions options = LASReaderOptions.getDefault();
        VertexLoader loader = mock(VertexLoader.class, withSettings().stubOnly());
        List<Vertex> generatedVertices = TestUtil.generateRandomVertices(numberOfVerticesInFile);
        when(loader.getAllVerticesFromFile()).thenReturn(generatedVertices);
        LASReader reader = new LASReader(file, options, loader);

        // when
        List<Vertex> readVertices = reader.getVerticesRecords();

        // then
        assertThat(readVertices).hasSize(numberOfVerticesInFile);
    }

    private LASFile loadTestLasFile() throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        return LASFile.fromFilePath(classLoader.getResource("test.las").getFile());
    }
}