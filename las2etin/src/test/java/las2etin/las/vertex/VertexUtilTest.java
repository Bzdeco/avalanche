package las2etin.las.vertex;

import org.junit.jupiter.api.Test;
import org.tinfour.common.Vertex;
import testutil.TestUtil;
import org.assertj.core.data.Percentage;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class VertexUtilTest
{

    @Test
    void thinnedNumberOfVerticesRoughlyConformsToThinningFactor()
    {
        // given
        int originalNumberOfVertices = 1000;
        List<Vertex> randomVertices = TestUtil.generateRandomVertices(originalNumberOfVertices);
        ThinningVertexFilter filter = new ThinningVertexFilter(0.7f);
        int expectedNumberOfVertices = 700;

        // when
        List<Vertex> filteredVertices = VertexUtil.filter(randomVertices, filter);

        // then
        double error = 5f; // this is quite fragile, may result occasionally in failed tests
        assertThat(filteredVertices.size()).isCloseTo(expectedNumberOfVertices, Percentage.withPercentage(error));
    }

    @Test
    void trimNumberOfVerticesToExactValue()
    {
        // given
        int originalNumberOfVertices = 1000;
        List<Vertex> randomVertices = TestUtil.generateRandomVertices(originalNumberOfVertices);
        int expectedNumberOfVertices = 850;

        // when
        List<Vertex> trimmedListOfVertices = VertexUtil.trimNumberOfVertices(randomVertices, expectedNumberOfVertices);

        // then
        assertThat(trimmedListOfVertices).hasSize(expectedNumberOfVertices);
    }
}